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

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Nullable;


/**
 * FoundationDB client configuration.
 *
 * @author Nikolai Papakha
 */
@Requires(property = FoundationDbClientConfig.PREFIX)
@ConfigurationProperties(value = FoundationDbClientConfig.PREFIX)
public class FoundationDbClientConfig {

    /**
     * The prefix to use for FoundationDB configuration.
     */
    public static final String PREFIX = "foundationdb";

    @Nullable
    private String clusterFilePath;

    @Nullable
    private String nativeLibraryPath;

    @Nullable
    private String executor;

    /**
     * Default constructor.
     */
    public FoundationDbClientConfig() {
    }

    /**
     * Gets the path to the FoundationDB cluster file.
     *
     * @return The path to the cluster file
     */
    @Nullable
    public String getClusterFilePath() {
        return clusterFilePath;
    }

    /**
     * Sets the path to the FoundationDB cluster file.
     *
     * @param clusterFilePath The path to the cluster file
     */
    public void setClusterFilePath(String clusterFilePath) {
        this.clusterFilePath = clusterFilePath;
    }

    /**
     * Gets the path to the FoundationDB native client library file.
     *
     * @return The path to the native client library
     */
    @Nullable
    public String getNativeLibraryPath() {
        return nativeLibraryPath;
    }

    /**
     * Sets the path of the FoundationDB native client library file.
     *
     * @param nativeLibraryPath The path to the native client library
     */
    public void setNativeLibraryPath(String nativeLibraryPath) {
        this.nativeLibraryPath = nativeLibraryPath;
    }

    /**
     * Gets the name of the executor service bean.
     *
     * @return The name of the executor bean
     */
    @Nullable
    public String getExecutor() {
        return executor;
    }

    /**
     * Sets the name of the executor service bean to use.
     *
     * @param executor The name of the executor bean
     */
    public void setExecutor(@Nullable String executor) {
        this.executor = executor;
    }
}
