package io.micronaut.foundationdb.metrics

import com.apple.foundationdb.Database
import com.apple.foundationdb.EventKeeper
import io.micrometer.core.instrument.MeterRegistry
import io.micronaut.foundationdb.AbstractFoundationDbSpec
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject

@MicronautTest
class MicrometerEventKeeperSpec extends AbstractFoundationDbSpec {

    @Inject
    private Database db

    @Inject
    private MeterRegistry registry

    @Inject
    private EventMetadataProvider metadataProvider

    def 'test all known count events are registered'() {
        given:
        def knownEvents = EventKeeper.Events.values().findAll { !it.isTimeEvent() } as EventKeeper.Event[]

        expect:
        knownEvents.every { registry.find(metadataProvider.getMeterName(it)).counter() != null}
    }

    def 'test all known time events are registered'() {
        given:
        def knownEvents = EventKeeper.Events.values().findAll { it.isTimeEvent() } as EventKeeper.Event[]

        expect:
        knownEvents.every { registry.find(metadataProvider.getMeterName(it)).timer() != null}
    }

    def 'test counter meter gets incremented'() {
        given:
        def key = 'THE_KEY'.bytes
        def value = 'THE_VALUE'.bytes

        and:
        def counter = registry.find('foundationdb.jni.call').counter()
        def prevValue = counter.count()

        when:
        db.run { it.set(key, value) }

        then:
        counter.count() > prevValue

        cleanup:
        db.run { it.clear(key) }
    }
}
