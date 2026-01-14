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
package io.micronaut.foundationdb.health;

import com.apple.foundationdb.Database;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import io.micronaut.health.HealthStatus;
import io.micronaut.json.JsonMapper;
import io.micronaut.management.endpoint.health.HealthEndpoint;
import io.micronaut.management.health.indicator.HealthIndicator;
import io.micronaut.management.health.indicator.HealthResult;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Health indicator for FoundationDB.
 *
 * @author Nikolai Papakha
 */
@Requires(beans = HealthEndpoint.class)
@Requires(property = HealthEndpoint.PREFIX + ".foundationdb.enabled", notEquals = StringUtils.FALSE)
@Singleton
public class FoundationDbHealthIndicator implements HealthIndicator {

    private static final String NAME = "foundationdb";

    private static final Logger LOG = LoggerFactory.getLogger(FoundationDbHealthIndicator.class);

    private final Database database;

    private final JsonMapper jsonMapper;

    /**
     * Constructs a new {@code FoundationDbHealthIndicator} instance.
     *
     * @param database   The FoundationDB database
     * @param jsonMapper The {@code JsonMapper}
     */
    public FoundationDbHealthIndicator(Database database, JsonMapper jsonMapper) {
        this.database = database;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public Publisher<HealthResult> getResult() {
        CompletableFuture<byte[]> clientStatus = database.getClientStatus();
        return Mono.fromCompletionStage(
            clientStatus
                .thenApply(this::deserialize)
                .thenApply(this::buildHealthResult)
                .exceptionally(this::buildErrorResult)
        );
    }

    private FoundationDbClientStatus deserialize(byte[] byteArray) {
        try {
            return jsonMapper.readValue(byteArray, FoundationDbClientStatus.class);
        } catch (IOException e) {
            LOG.error("Failed to deserialize client status", e);
            throw new RuntimeException("Failed to deserialize client status", e);
        }
    }

    private HealthResult buildHealthResult(FoundationDbClientStatus clientStatus) {
        HealthStatus status = Boolean.TRUE.equals(clientStatus.getHealthy()) ? HealthStatus.UP : HealthStatus.DOWN;
        return HealthResult.builder(NAME, status).details(clientStatus).build();
    }

    private HealthResult buildErrorResult(Throwable throwable) {
        return HealthResult.builder(NAME, HealthStatus.DOWN).exception(throwable).build();
    }
}
