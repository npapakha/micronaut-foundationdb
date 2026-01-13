package io.micronaut.foundationdb

import io.micronaut.foundationdb.test.FoundationDb
import io.micronaut.test.support.TestPropertyProvider
import spock.lang.Specification


abstract class AbstractFoundationDbSpec extends Specification implements TestPropertyProvider {

    @Override
    Map<String, String> getProperties() {
        return FoundationDb.getProperties()
    }
}
