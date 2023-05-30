package service;

import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.UUID;

public class WebSocketConfigurator {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketConfigurator.class);

    private final DisplayService displayService;

    public WebSocketConfigurator(DisplayService displayService) {
        this.displayService = displayService;
    }

    public void configure(Javalin app) {
        app.ws("/connect", ws -> {
            ws.onConnect(ctx -> {
                ctx.session.setIdleTimeout(Duration.ofDays(1));
                String displayId = ctx.queryParam("displayId");
                if(displayId == null) {
                    displayId = UUID.randomUUID().toString();
                }
                ctx.attribute("displayId", displayId);
                displayService.addDisplaySession(displayId, ctx);
                LOGGER.info("Display session connected: {}", displayId);
                ctx.send(displayId);
            });
            ws.onMessage(ctx -> {
                //TODO: Sending messages to other screens? Home screen summary etc.
            });
            ws.onClose(ctx -> {
                String displayId = ctx.attribute("displayId");
                if(displayId != null) {
                    displayService.removeDisplaySession(displayId);
                    LOGGER.info("Display session closed: {}", displayId);
                } else {
                    LOGGER.warn("Display session closed without displayId");
                }
            });
            ws.onError(ctx -> {
                LOGGER.error("Error in websocket", ctx.error());
            });
        });
    }
}
