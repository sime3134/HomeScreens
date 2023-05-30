package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

public class WebSocketConfigurator {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketConfigurator.class);

    private final DisplayService displayService;
    private final ObjectMapper objectMapper;

    public WebSocketConfigurator(DisplayService displayService) {
        this.displayService = displayService;
        this.objectMapper = new ObjectMapper();
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
                Map<String, String> response = Map.of("displayId", displayId, "type", "displayId");
                ctx.send(objectMapper.writeValueAsString(response));
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
