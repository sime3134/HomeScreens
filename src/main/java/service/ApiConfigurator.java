package service;

import io.javalin.Javalin;

public interface ApiConfigurator {
    void configure(Javalin app);
}
