package service;

import io.javalin.Javalin;
import model.dto.ConnectedDTO;
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
        app.ws("/connect/{displayId}", ws -> {
            ws.onConnect(ctx -> {
                ctx.session.setIdleTimeout(Duration.ofDays(999));
                String displayId = ctx.pathParam("displayId");
                System.out.println("Display id: " + displayId);
                if(displayId.equals("new")) {
                    displayId = UUID.randomUUID().toString();
                }
                ctx.attribute("displayId", displayId);
                displayService.addDisplaySession(displayId, ctx);
                LOGGER.info("Display session connected: {}", displayId);
                ConnectedDTO response = new ConnectedDTO(displayId, displayService.isDisplayRegistered(displayId));
                ctx.send(response);
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
