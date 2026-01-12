package io.micronaut.build.internal.foundationdb;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


public abstract class BaseDownloadClientTask extends DefaultTask {

    protected static final List<String> ARCHITECTURES = List.of("x86_64", "aarch64"); //

    protected static final String BASE_URL = "https://github.com/apple/foundationdb/releases/download";

    @Input
    public abstract Property<String> getVersion();

    @OutputDirectory
    public abstract DirectoryProperty getOutputDirectory();

    protected Path getTargetPath(String arch) throws IOException {
        Path outputPath = getOutputDirectory().getAsFile().get().toPath();
        Path targetPath = outputPath.resolve(arch);
        Files.createDirectories(targetPath);
        return targetPath;
    }
}
