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
package io.micronaut.foundationdb.client;

import io.micronaut.context.annotation.Context;
import io.micronaut.context.condition.OperatingSystem;
import io.micronaut.context.env.CachedEnvironment;
import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.nio.file.Path;


/**
 * Installer for FoundationDB native client library.
 *
 * @author Nikolai Papakha
 */
@Context
class FoundationDbClientInstaller {

    static final String CLIENT_PROPERTY_NAME = "FDB_LIBRARY_PATH_FDB_C";
    static final String CLIENT_LIBRARY_NAME = "fdb_c";

    @PostConstruct
    void install() throws IOException {
        NativeLibraryInstaller installer = new NativeLibraryInstaller("foundationdb-client");
        String resourceName = getResourceName(CLIENT_LIBRARY_NAME);
        Path installed = installer.install(resourceName);
        System.setProperty(CLIENT_PROPERTY_NAME, installed.toString());
    }

    private String getResourceName(String libName) {
        Path path = Path.of("lib", getOs(), getArch(), System.mapLibraryName(libName));
        return path.toString();
    }

    private String getOs() {
        OperatingSystem os = OperatingSystem.getCurrent();
        return switch (os.getFamily()) {
            case LINUX -> "linux";
            case MAC_OS -> "osx";
            default -> throw new IllegalStateException("Unknown or unsupported arch os");
        };
    }

    private String getArch() {
        var arch = CachedEnvironment.getProperty("os.arch");
        return switch (arch) {
            case "x86_64", "amd64" -> "x86_64";
            case "aarch64", "arm64" -> "aarch64";
            default -> throw new IllegalStateException("Unknown or unsupported arch");
        };
    }
}
