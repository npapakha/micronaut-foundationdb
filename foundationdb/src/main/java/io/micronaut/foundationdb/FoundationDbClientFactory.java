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
package io.micronaut.foundationdb;

import com.apple.foundationdb.ApiVersion;
import com.apple.foundationdb.Database;
import com.apple.foundationdb.FDB;
import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.inject.qualifiers.Qualifiers;
import jakarta.inject.Singleton;

import java.util.Optional;
import java.util.concurrent.ExecutorService;

/**
 * Factory for creating FoundationDB {@link Database} instances.
 *
 * @author Nikolai Papakha
 */
@Factory
class FoundationDbClientFactory {

    /**
     * Creates a FoundationDB {@link Database} instance based on the provided configuration.
     *
     * @param config      The {@link FoundationDbClientConfig} containing settings for the database
     * @param beanContext The {@link BeanContext} used for resolving the executor service
     * @return The {@link Database}
     */
    @Bean(preDestroy = "close")
    @Singleton
    Database createDatabase(FoundationDbClientConfig config, BeanContext beanContext) {
        if (config.getNativeLibraryPath() != null) {
            System.setProperty("FDB_LIBRARY_PATH_FDB_C", config.getNativeLibraryPath());
        }
        ExecutorService executor = Optional.ofNullable(config.getExecutor())
            .flatMap(name -> beanContext.findBean(ExecutorService.class, Qualifiers.byName(name))).orElse(FDB.DEFAULT_EXECUTOR);
        return FDB.selectAPIVersion(ApiVersion.LATEST).open(config.getClusterFilePath(), executor);
    }
}
