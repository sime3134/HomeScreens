package utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class EnvironmentVars {

    private static final String ENV_FILE_PATH = System.getProperty("user.home") + "/HomeScreens/.env";
    private static final Map<String, String> ENVIRONMENT_VARIABLES = new HashMap<>();

    private EnvironmentVars(){}

    public static void load() throws IOException {
        Path path = Paths.get(ENV_FILE_PATH);
        if(!Files.exists(path)) {
            throw new IOException("Environment variables file not found");
        }
        try (Stream<String> stream = Files.lines(path)) {
            stream.forEach(EnvironmentVars::processLine);
        } catch (IOException e) {
            throw new IOException("Error loading environment variables", e);
        }
    }

    private static void processLine(String line) {
        int delimiterPosition = line.indexOf("=");
        if (delimiterPosition > 0) {
            String name = line.substring(0, delimiterPosition);
            String value = line.substring(delimiterPosition + 1);
            ENVIRONMENT_VARIABLES.put(name, value);
        }
    }

    public static String getValue(String name) {
        return ENVIRONMENT_VARIABLES.get(name);
    }
}
