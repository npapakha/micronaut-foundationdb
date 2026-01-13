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

import io.micronaut.core.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Copies a native library from the classpath to a temporary directory.
 *
 * @author Nikolai Papakha
 */
public final class NativeLibraryInstaller {

    private final Path tempDir;

    /**
     * Creates an instance of {@code NativeLibraryInstaller} which manages a temporary
     * directory to store native libraries.
     *
     * @param prefix the prefix of the temporary directory
     * @throws IOException if an I/O error occurs while creating the directory
     */
    public NativeLibraryInstaller(@NonNull String prefix) throws IOException {
        this.tempDir = Files.createTempDirectory(prefix);
    }

    /**
     * Copies a resource from the classpath to a target temporary directory.
     *
     * @param resourceName the name of the resource to copy
     * @return the {@link Path} to the file in the temporary directory
     * @throws IOException if an I/O error occurs during the resource copying
     */
    public Path install(@NonNull String resourceName) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            Objects.requireNonNull(inputStream, "Cannot find resource");
            Path fileName = Path.of(resourceName).getFileName();
            Path outputPath = tempDir.resolve(fileName);
            Files.copy(inputStream, outputPath, REPLACE_EXISTING);
            outputPath.toFile().deleteOnExit();
            return outputPath;
        }
    }
}
