package service;

import io.javalin.Javalin;
import utilities.Paths;

public class ApiConfiguratorImpl implements ApiConfigurator {

    private final DisplayService displayService;

    public ApiConfiguratorImpl(DisplayService displayService) {
        this.displayService = displayService;
    }

    public void configure(Javalin app) {
        app.get(Paths.API_PREFIX + "/displays",ctx -> {
            ctx.json(displayService.getDisplayList());
        });

        app.post(Paths.API_PREFIX + "/displays",ctx -> {
            String displayId = ctx.formParam("displayId");
            String name = ctx.formParam("name");
            displayService.registerDisplay(displayId, name);
        });
    }
}
