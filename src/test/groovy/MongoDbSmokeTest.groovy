import com.mongodb.BasicDBObject
import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import spock.lang.Specification

import static org.assertj.core.api.Assertions.fail

class MongoDbSmokeTest extends Specification {

    def 'MongoDb is healthy'() {
        given: 'I connect to the MongoDB Media Store'
        def mongoConnectionUri = System.getenv 'MONGODB_CONNECTION_URI'
        if (!mongoConnectionUri) {
            fail 'MONGODB_CONNECTION_URI environment variable was not set'
        }

        def mongoClient = new MongoClient(new MongoClientURI(mongoConnectionUri))
        def database = mongoClient.getDatabase 'hub_metadata'

        when: 'I run the buildInfo command'
        def command = database.runCommand(new BasicDBObject('buildInfo', 1))

        then: 'the version string is present in the response object'
        command.getString('version').length() != 0
    }

}