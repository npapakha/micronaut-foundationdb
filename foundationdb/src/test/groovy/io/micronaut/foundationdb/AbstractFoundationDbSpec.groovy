package io.micronaut.foundationdb

import io.micronaut.foundationdb.test.FoundationDbContainer
import io.micronaut.test.support.TestPropertyProvider
import spock.lang.Specification


abstract class AbstractFoundationDbSpec extends Specification implements TestPropertyProvider {

    static FoundationDbContainer foundationDb = new FoundationDbContainer()
    static {
        foundationDb.startNewSingleMemory()
    }

    @Override
    Map<String, String> getProperties() {
        return [
                "foundationdb.cluster-file-path": foundationDb.getClusterFilePath()
        ]
    }
}
