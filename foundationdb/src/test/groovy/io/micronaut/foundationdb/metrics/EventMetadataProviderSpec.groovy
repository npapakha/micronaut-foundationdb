package io.micronaut.foundationdb.metrics

import io.micronaut.foundationdb.AbstractFoundationDbSpec
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject


@MicronautTest
class EventMetadataProviderSpec extends AbstractFoundationDbSpec {

    @Inject
    private EventMetadataProvider metadataProvider

    def 'test meter name for unknown event'() {
        given:
        def meterName = metadataProvider.getMeterName { 'UNKNOWN_EVENT' }
        expect:
        meterName == 'foundationdb.unknown.event'
    }

    def 'test generic count description'() {
        given:
        def meterName = metadataProvider.getCountDescription { 'UNKNOWN_EVENT' }
        expect:
        meterName == 'The number of times the UNKNOWN_EVENT event occurred'
    }

    def 'test generic timer description'() {
        given:
        def meterName = metadataProvider.getTimerDescription { 'UNKNOWN_EVENT' }
        expect:
        meterName == 'The time taken to perform the UNKNOWN_EVENT event'
    }
}
