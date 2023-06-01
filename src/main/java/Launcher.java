import com.mongodb.client.MongoDatabase;
import io.javalin.http.staticfiles.Location;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import repository.DisplayRepository;
import repository.MongoDisplayRepository;
import service.*;
import utilities.DatabaseManager;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;
import repository.MongoPageRepository;
import repository.PageRepository;
import utilities.FileUtils;
import utilities.EnvironmentVars;
import utilities.Paths;

import java.io.IOException;

public class Launcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);
    private static Javalin app = null;
    private static DisplayService displayService;

    public static void main(String[] args) {
        ApiConfigurator apiConfigurator;
        WebSocketConfigurator webSocketConfigurator;
        loadEnvironmentVariables();

        createDirectories();

        copyScriptAndHomepage();

        try {
            MongoDatabase database = DatabaseManager.getDatabase();

            if (database != null) {
                LOGGER.info("Database connected successfully");
                DisplayRepository displayRepository = new MongoDisplayRepository(database);
                displayService = new DisplayServiceImpl(displayRepository);
                apiConfigurator = new ApiConfiguratorImpl(displayService);
                webSocketConfigurator = new WebSocketConfigurator(displayService);

                TemplateEngine templateEngine = getTemplateEngine();

                app = launchApp(templateEngine, webSocketConfigurator, apiConfigurator);
                createPages(database);
            }else {
                LOGGER.error("Error connecting to database, closing application");
                cleanUpResources();
            }
        } catch (Exception e) {
            LOGGER.error("Error starting application", e);
            cleanUpResources();
        }
        LOGGER.info("App started successfully");

        Runtime.getRuntime().addShutdownHook(new Thread(Launcher::cleanUpResources));
    }

    private static void createPages(MongoDatabase database) throws IOException {
        PageRepository pageRepository = new MongoPageRepository(database);
        PageService pageService = new MongoPageService(pageRepository);
        PageCreator pageCreator = new PageCreator();
        pageCreator.createPagesFromDirectories(Paths.ROOT_FOLDER_PATH, app, pageService);
        LOGGER.info("Pages created successfully");
    }

    private static void copyScriptAndHomepage() {
        try {
            FileUtils.copyIfNotExists("/homescreens.js", Paths.SCRIPT_FOLDER_PATH + "/homescreens.js");
            FileUtils.copyIfNotExists("/home/index.html", Paths.APP_FOLDER_PATH + "/home/index.html");
            FileUtils.copyIfNotExists("/home/style.css", Paths.APP_FOLDER_PATH + "/home/style.css");
            FileUtils.copyIfNotExists("/home/scripts.js", Paths.APP_FOLDER_PATH + "/home/scripts.js");
        } catch (IOException e) {
            LOGGER.error("Error copying script and default homepage", e);
            cleanUpResources();
        }
        LOGGER.info("Script and homepage copied successfully to {}", Paths.SCRIPT_FOLDER_PATH);
    }

    private static void loadEnvironmentVariables() {
        try {
            EnvironmentVars.load();
        } catch (IOException e) {
            LOGGER.error("Error loading environment variables", e);
            cleanUpResources();
        }
        LOGGER.info("Environment variables loaded successfully");
    }

    private static void createDirectories() {
        try {
            FileUtils.createDirectoryIfNotExists(Paths.APP_FOLDER_PATH);
            FileUtils.createDirectoryIfNotExists(Paths.APP_FOLDER_PATH + "/home");
            FileUtils.createDirectoryIfNotExists(Paths.LOG_FOLDER_PATH);
            FileUtils.createDirectoryIfNotExists(Paths.SCRIPT_FOLDER_PATH);
        }catch (IOException e) {
            LOGGER.error("Error creating directories", e);
            cleanUpResources();
        }
        LOGGER.info("Folders created successfully");
    }

    private static void cleanUpResources() {
        DatabaseManager.close();
        if(app != null) {
            if(displayService != null) {
                displayService.closeAllSessions();
            }
            app.close();
        }
    }

    @NotNull
    private static Javalin launchApp(TemplateEngine templateEngine,
                                     WebSocketConfigurator webSocketConfigurator,
                                     ApiConfigurator apiConfigurator) {
        Javalin app = Javalin.create(config -> {
            JavalinThymeleaf.init(templateEngine);
            config.staticFiles.add(Paths.ROOT_FOLDER_PATH, Location.EXTERNAL);
        });

        webSocketConfigurator.configure(app);
        apiConfigurator.configure(app);

        app.start(Integer.parseInt(EnvironmentVars.getValue("PORT")));
        return app;
    }

    @NotNull
    private static TemplateEngine getTemplateEngine() {
        FileTemplateResolver templateResolver = new FileTemplateResolver();
        templateResolver.setPrefix(Paths.APP_FOLDER_PATH + "/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCacheable(false);
        templateResolver.setCheckExistence(true);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine;
    }
}
