package io.micronaut.build.internal.foundationdb;

import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecOperations;

import javax.inject.Inject;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


public abstract class DownloadMacOsClientTask extends BaseDownloadClientTask {

    @Inject
    protected abstract ExecOperations getExecOperations();

    @TaskAction
    public void download() throws Exception {
        for (String arch : ARCHITECTURES) {
            download(arch);
            extract(arch);
            copy(arch);
            clean(arch);
        }
    }

    private void download(String arch) throws Exception {
        try (InputStream inputStream = new URI(getUrl(arch)).toURL().openStream()) {
            Path downloadPath = getDownloadPath(arch);
            Path outputPath = downloadPath.resolve("FoundationDB.pkg");
            Files.copy(inputStream, outputPath, REPLACE_EXISTING);
        }
    }

    // https://github.com/apple/foundationdb/releases/download/7.3.69/FoundationDB-7.3.69_arm64.pkg
    private String getUrl(String arch) {
        String version = getVersion().get();
        String mappedArch = switch (arch) {
            case "x86_64" -> "x86_64";
            case "aarch64" -> "arm64";
            default -> throw new IllegalArgumentException("Unknown arch");
        };
        return BASE_URL + '/' + version + "/FoundationDB-" + version + '_' + mappedArch + ".pkg";
    }

    private void extract(String arch) throws Exception {
        Path downloadPath = getDownloadPath(arch);
        Path clientsPath = downloadPath.resolve("FoundationDB-clients.pkg");
        exec(downloadPath, "xar", "-xf", "FoundationDB.pkg");
        exec(clientsPath, "tar", "-xzf", "Payload", "-C", ".");
    }

    private void copy(String arch) throws Exception {
        Path targetPath = getTargetPath(arch).resolve("libfdb_c.dylib");
        Path clientsPath = getDownloadPath(arch).resolve("FoundationDB-clients.pkg");
        Path libPath = Path.of("usr", "local", "lib", "libfdb_c.dylib");
        Files.copy(clientsPath.resolve(libPath), targetPath, REPLACE_EXISTING);
    }

    private void clean(String arch) throws Exception {
        Path targetPath = getTargetPath(arch);
        Path downloadPath = getDownloadPath(arch);
        exec(targetPath, "rm", "-rf", downloadPath.toString());
    }

    private void exec(Path dir, String... cmd) {
        getExecOperations().exec(spec -> {
            spec.setWorkingDir(dir);
            spec.commandLine(Arrays.asList(cmd));
        });
    }

    private Path getDownloadPath(String arch) throws Exception {
        Path targetPath = getTargetPath(arch);
        Path downloadPath = targetPath.resolve("download");
        Files.createDirectories(downloadPath);
        return downloadPath;
    }
}
