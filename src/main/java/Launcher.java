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

public class Launcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);
    private static Javalin app = null;

    public static void main(String[] args) {
        String rootFolderPath = System.getProperty("user.home") + "/HomeScreens";
        String appFolderPath = rootFolderPath + "/apps";
        String scriptFolderPath = rootFolderPath + "/scripts";
        DisplayService displayService = new DisplayServiceImpl();
        WebSocketConfigurator webSocketConfigurator = new WebSocketConfigurator(displayService);
        try {
            FileUtils.createDirectoryIfNotExists(appFolderPath);
            FileUtils.createDirectoryIfNotExists(scriptFolderPath);
            LOGGER.info("App folder created successfully at {}", appFolderPath);

            FileUtils.copyIfNotExists("/homescreens.js", scriptFolderPath + "/homescreens.js");
            LOGGER.info("Script copied successfully to {}", scriptFolderPath);

            TemplateEngine templateEngine = getTemplateEngine(appFolderPath);

            app = launchApp(rootFolderPath, templateEngine, webSocketConfigurator);
            LOGGER.info("App started successfully");

            EnvironmentVars.load();
            MongoDatabase database = DatabaseManager.getDatabase();

            if (database != null) {
                LOGGER.info("Database connected successfully");
                PageRepository pageRepository = new MongoPageRepository(database);
                PageService pageService = new MongoPageService(pageRepository);
                PageCreator pageCreator = new PageCreator();
                pageCreator.createPagesFromDirectories(appFolderPath, app, pageService);
                LOGGER.info("Pages created successfully");
            }
        } catch (Exception e) {
            LOGGER.error("Error starting application", e);
            DatabaseManager.close();
            if(app != null) {
                app.close();
            }
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DatabaseManager.close();
            if(app != null) {
                app.close();
            }
        }));
    }

    @NotNull
    private static Javalin launchApp(String rootFolderPath, TemplateEngine templateEngine,
                                     WebSocketConfigurator webSocketConfigurator) {
        Javalin app = Javalin.create(config -> {
            JavalinThymeleaf.init(templateEngine);
            config.staticFiles.add(rootFolderPath, Location.EXTERNAL);
        });

        webSocketConfigurator.configure(app);

        app.start(8080);
        return app;
    }

    @NotNull
    private static TemplateEngine getTemplateEngine(String appFolderPath) {
        FileTemplateResolver templateResolver = new FileTemplateResolver();
        templateResolver.setPrefix(appFolderPath + "/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCacheable(false);
        templateResolver.setCheckExistence(true);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine;
    }
}
