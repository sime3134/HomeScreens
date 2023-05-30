import com.mongodb.client.MongoDatabase;
import io.javalin.http.staticfiles.Location;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;
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

    public static void main(String[] args) {
        DisplayService displayService = new DisplayServiceImpl();
        WebSocketConfigurator webSocketConfigurator = new WebSocketConfigurator(displayService);

        loadEnvironmentVariables(displayService);

        createDirectories(displayService);

        copyScripts(displayService);

        TemplateEngine templateEngine = getTemplateEngine();

        app = launchApp(templateEngine, webSocketConfigurator);
        LOGGER.info("App started successfully");

        try {
            MongoDatabase database = DatabaseManager.getDatabase();

            if (database != null) {
                LOGGER.info("Database connected successfully");
                createPages(database);
            }else {
                LOGGER.error("Error connecting to database");
                cleanUpResources(displayService);
            }
        } catch (Exception e) {
            LOGGER.error("Error starting application", e);
            cleanUpResources(displayService);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> cleanUpResources(displayService)));
    }

    private static void createPages(MongoDatabase database) throws IOException {
        PageRepository pageRepository = new MongoPageRepository(database);
        PageService pageService = new MongoPageService(pageRepository);
        PageCreator pageCreator = new PageCreator();
        pageCreator.createPagesFromDirectories(Paths.ROOT_FOLDER_PATH, app, pageService);
        LOGGER.info("Pages created successfully");
    }

    private static void copyScripts(DisplayService displayService) {
        try {
            FileUtils.copyIfNotExists("/homescreens.js", Paths.SCRIPT_FOLDER_PATH + "/homescreens.js");
        } catch (IOException e) {
            LOGGER.error("Error copying css file", e);
            cleanUpResources(displayService);
        }
        LOGGER.info("Script copied successfully to {}", Paths.SCRIPT_FOLDER_PATH);
    }

    private static void loadEnvironmentVariables(DisplayService displayService) {
        try {
            EnvironmentVars.load();
        } catch (IOException e) {
            LOGGER.error("Error loading environment variables", e);
            cleanUpResources(displayService);
        }
        LOGGER.info("Environment variables loaded successfully");
    }

    private static void createDirectories(DisplayService displayService) {
        try {
            FileUtils.createDirectoryIfNotExists(Paths.APP_FOLDER_PATH);
            FileUtils.createDirectoryIfNotExists(Paths.LOG_FOLDER_PATH);
            FileUtils.createDirectoryIfNotExists(Paths.SCRIPT_FOLDER_PATH);
        }catch (IOException e) {
            LOGGER.error("Error creating directories", e);
            cleanUpResources(displayService);
        }
        LOGGER.info("Folders created successfully");
    }

    private static void cleanUpResources(DisplayService displayService) {
        DatabaseManager.close();
        if(app != null) {
            displayService.closeAllSessions();
            app.close();
        }
    }

    @NotNull
    private static Javalin launchApp(TemplateEngine templateEngine,
                                     WebSocketConfigurator webSocketConfigurator) {
        Javalin app = Javalin.create(config -> {
            JavalinThymeleaf.init(templateEngine);
            config.staticFiles.add(Paths.ROOT_FOLDER_PATH, Location.EXTERNAL);
        });

        webSocketConfigurator.configure(app);

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
