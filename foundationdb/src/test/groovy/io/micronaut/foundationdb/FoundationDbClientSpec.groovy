package io.micronaut.foundationdb

import com.apple.foundationdb.Database
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject

@MicronautTest
class FoundationDbClientSpec extends AbstractFoundationDbSpec {

    @Inject
    private Database db

    def 'database is injected'() {
        expect:
        db != null
    }

    def 'returns value after it is set'() {
        given:
        byte[] key = "THE_KEY".bytes
        byte[] value = "THE_VALUE".bytes

        when:
        db.run { it.set(key, value) }

        then:
        db.run { it.get(key) }.join() == value

        cleanup:
        db.run { it.clear(key) }
    }

    def 'returns null when key does not exist'() {
        given:
        byte[] unknownKey = "UNKNOWN".bytes
        expect:
        db.run { it.get(unknownKey) }.join() == null
    }
}
