/*
 * Copyright 2017-2026 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.foundationdb.test;

import java.util.Map;


public final class FoundationDb {

    private static FoundationDbContainer container;

    public static Map<String, String> getProperties() {
        return getProperties("configure new single memory");
    }

    public static Map<String, String> getProperties(String initCmd) {
        if (container == null) {
            container = new FoundationDbContainer();
            container.start();
            container.fdbCliExec(initCmd);
        }
        return Map.of(
            "foundationdb.cluster-file", container.getClusterFile()
        );
    }
}
