import com.mashape.unirest.http.Unirest
import com.microsoft.azure.storage.CloudStorageAccount
import spock.lang.Specification

import static java.lang.String.format
import static org.assertj.core.api.Assertions.fail

class AzureBlobStoreSmokeTest extends Specification {
    private static final String AZURE_CONTAINER_NAME = "content-items"
    private static final String IMAGE_FILE_NAME = 'hub-smoke-tests-1-pixel.png'

    def 'Azure blob store is healthy'() {
        given: 'I connect to the Azure blob store'
        def azureConnectionUri = System.getenv 'AZURE_BLOB_STORE_CONNECTION_URI'
        if (!azureConnectionUri) {
            fail 'AZURE_BLOB_STORE_CONNECTION_URI environment variable was not set'
        }

        def azurePublicUrlBase = System.getenv 'AZURE_BLOB_STORE_PUBLIC_URL_BASE'
        if (!azurePublicUrlBase) {
            fail 'AZURE_BLOB_STORE_PUBLIC_URL_BASE environment variable was not set'
        }

        def blobClient = CloudStorageAccount.parse(azureConnectionUri).createCloudBlobClient()
        def container = blobClient.getContainerReference(AZURE_CONTAINER_NAME)

        when: 'I upload a file and then access its public URL'
        def file = new File(getClass().getResource(IMAGE_FILE_NAME).toURI())
        container.getBlockBlobReference(IMAGE_FILE_NAME).upload(new FileInputStream(file), file.size())

        def uri = format("%s/%s/%s", azurePublicUrlBase, AZURE_CONTAINER_NAME, IMAGE_FILE_NAME)
        def imageResponse = Unirest.get(uri).asString()

        then: 'I get a HTTP status OK response'
        imageResponse.getStatus() == 200
    }

}
