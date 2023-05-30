package utilities;

public class Paths {

    private Paths() {}
    public static final String API_PREFIX = "api/v1";
    public static final String ROOT_FOLDER_PATH = System.getProperty("user.home") + "/HomeScreens";
    public static final String APP_FOLDER_PATH = ROOT_FOLDER_PATH + "/apps";
    public static final String SCRIPT_FOLDER_PATH = ROOT_FOLDER_PATH + "/scripts";

    public static final String LOG_FOLDER_PATH = ROOT_FOLDER_PATH + "/logs";
    public static final String ENV_FILE_PATH = ROOT_FOLDER_PATH + "/.env";
}
