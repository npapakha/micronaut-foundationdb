package io.micronaut.build.internal.foundationdb;

import org.gradle.api.tasks.TaskAction;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


public abstract class DownloadLinuxClientTask extends BaseDownloadClientTask {

    @TaskAction
    public void download() throws Exception {
        for (String arch : ARCHITECTURES) {
            download(arch);
        }
    }

    private void download(String arch) throws Exception {
        try (InputStream inputStream = new URI(getUrl(arch)).toURL().openStream();) {
            Path targetPath = getTargetPath(arch);
            Files.copy(inputStream, targetPath.resolve("libfdb_c.so"), REPLACE_EXISTING);
        }
    }

    // https://github.com/apple/foundationdb/releases/download/7.3.69/libfdb_c.x86_64.so
    private String getUrl(String arch) {
        String version = getVersion().get();
        return BASE_URL + '/' + version + "/libfdb_c." + arch + ".so";
    }
}
