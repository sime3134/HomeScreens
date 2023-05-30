package service;

import controller.PageController;
import io.javalin.Javalin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class PageCreator {
    public void createPagesFromDirectories(String path, Javalin app, PageService pageService) throws IOException {

        Path start = Paths.get(path);
        if (start.toFile().exists()) {
            try (Stream<Path> paths = Files.walk(start)) {
                paths.filter(Files::isDirectory)
                        .forEach(directory -> {
                            String pageName = directory.getFileName().toString();
                            String pagePath = "/" + pageName;
                            createPage(pagePath, app, pageService);
                        });
            }
        } else {
            throw new IOException("Directory does not exist: " + path);
        }
    }

    private void createPage(String path, Javalin app, PageService pageService) {
        PageController projectsPage = new PageController(path, pageService);
        projectsPage.registerRoutes(app);
    }
}
