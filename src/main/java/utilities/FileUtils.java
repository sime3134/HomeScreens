package utilities;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FileUtils {

    private FileUtils() {
    }

    public static void createDirectoryIfNotExists(String directoryPath) throws IOException {
        Path path = Paths.get(directoryPath);

        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new IOException("Could not create directory: " + path, e);
            }
        }
    }
    public static void copyIfNotExists(String sourcePath, String destinationPath) throws IOException {
        Path destination = Paths.get(destinationPath);

        //TODO: check if file exists and is the same. which is most demanding?
        try (InputStream resourceStream = FileUtils.class.getResourceAsStream(sourcePath)) {
            if (resourceStream == null) {
                throw new IOException("Resource not found: " + sourcePath);
            }
            Files.copy(resourceStream, destination, REPLACE_EXISTING);
        }
    }
}
