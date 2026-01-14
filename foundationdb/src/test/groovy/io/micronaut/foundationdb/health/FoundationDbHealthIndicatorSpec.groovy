package io.micronaut.foundationdb.health

import io.micronaut.foundationdb.AbstractFoundationDbSpec
import io.micronaut.health.HealthStatus
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import reactor.core.publisher.Mono

import static java.util.concurrent.TimeUnit.SECONDS
import static org.awaitility.Awaitility.await


@MicronautTest
class FoundationDbHealthIndicatorSpec extends AbstractFoundationDbSpec {

    @Inject
    private FoundationDbHealthIndicator healthIndicator

    def 'test health status is up'() {
        given:
        def status = Mono.from(healthIndicator.getResult()).block()

        expect:
        status.status == HealthStatus.UP
    }

    def 'test health status is down'() {
        when:
        foundationDb.stop()

        then:
        await().atMost(5, SECONDS).until {
            def status = Mono.from(healthIndicator.getResult()).block()
            status.status == HealthStatus.DOWN
        }

        cleanup:
        foundationDb.startNewSingleMemory()
    }
}
