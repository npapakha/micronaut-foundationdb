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

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Serdeable
@JsonIgnoreProperties(ignoreUnknown = true)
class FoundationDbClientStatus {

    @JsonAlias({"ClusterID"})
    private String clusterId;

    @JsonAlias({"Healthy"})
    private Boolean healthy;

    @JsonAlias({"Connections"})
    private List<ConnectionStatus> connections;

    String getClusterId() {
        return clusterId;
    }

    void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    Boolean getHealthy() {
        return healthy;
    }

    void setHealthy(Boolean healthy) {
        this.healthy = healthy;
    }

    List<ConnectionStatus> getConnections() {
        return connections;
    }

    void setConnections(List<ConnectionStatus> connections) {
        this.connections = connections;
    }

    @Serdeable
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ConnectionStatus {

        @JsonAlias({"Address"})
        private String address;

        @JsonAlias({"Status"})
        private String status;

        String getAddress() {
            return address;
        }

        void setAddress(String address) {
            this.address = address;
        }

        String getStatus() {
            return status;
        }

        void setStatus(String status) {
            this.status = status;
        }
    }
}
