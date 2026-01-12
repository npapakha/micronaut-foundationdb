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

import io.micronaut.core.annotation.NonNull;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;


/**
 * A container class for running FoundationDB instances in a Docker environment.
 */
public class FoundationDbContainer extends GenericContainer<FoundationDbContainer> {

    public static final String IMAGE_TAG = "7.4.3";

    public static final String IMAGE_NAME = "foundationdb/foundationdb";

    private static final DockerImageName DOCKER_IMAGE = DockerImageName.parse(IMAGE_NAME).withTag(IMAGE_TAG);

    private final int clientPort = findFreePort();

    private String clusterFile;

    public FoundationDbContainer() {
        this(DOCKER_IMAGE);
    }

    public FoundationDbContainer(@NonNull DockerImageName dockerImageName) {
        super(dockerImageName);
        addEnv("FDB_NETWORKING_MODE", "host");
        addEnv("FDB_PORT", String.valueOf(clientPort));
        addFixedExposedPort(clientPort, clientPort);
        setWaitStrategy(Wait.forLogMessage(".*FDBD joined cluster.*\\n", 1));
    }

    public int getClientPort() {
        return clientPort;
    }

    public String getClusterFile() {
        if (clusterFile != null) {
            return clusterFile;
        }
        try {
            ExecResult result = execInContainer("printenv", "FDB_CLUSTER_FILE");
            String remotePath = result.getStdout().trim();
            Path tmpDir = Files.createTempDirectory("foundationdb-test");
            Path localPath = tmpDir.resolve("fdb.cluster");
            copyFileFromContainer(remotePath, localPath.toString());
            localPath.toFile().deleteOnExit();
            clusterFile = localPath.toString();
            return clusterFile;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ExecResult fdbCliExec(String cmd) {
        try {
            return execInContainer("/usr/bin/fdbcli", "--exec", cmd);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static int findFreePort() {
        try {
            try (ServerSocket socket = new ServerSocket(0)) {
                socket.setReuseAddress(true);
                return socket.getLocalPort();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
