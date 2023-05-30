package controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import service.PageService;

public class PageController {

    private final PageService pageService;

    private final String path;

    public PageController(String path, PageService pageService) {
        this.path = path;
        this.pageService = pageService;
    }

    public void registerRoutes(Javalin app) {
        app.get(path, this::handleGet);
        app.post(path, this::handlePost);
    }

    private void handleGet(Context ctx) {
        String pageName = ctx.path().substring(1);
        String finalPath = pageName + "/index.html";
        ctx.render(finalPath);
    }

    private void handlePost(Context ctx) {
        //get content

    }
}
