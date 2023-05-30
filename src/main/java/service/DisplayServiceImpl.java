package service;

import io.javalin.websocket.WsConnectContext;

import java.util.concurrent.ConcurrentHashMap;

public class DisplayServiceImpl implements DisplayService {
    private final ConcurrentHashMap<String, WsConnectContext> displaySessions;
    public DisplayServiceImpl() {
        displaySessions = new ConcurrentHashMap<>();
    }

    @Override
    public void addDisplaySession(String displayId, WsConnectContext ctx) {
        displaySessions.put(displayId, ctx);
    }

    @Override
    public void removeDisplaySession(String displayId) {
        displaySessions.remove(displayId);
    }

    @Override
    public WsConnectContext getDisplaySession(String displayId) {
        return displaySessions.get(displayId);
    }

    @Override
    public void closeAllSessions() {
            displaySessions.forEach((displayId, ctx) -> ctx.session.close());
    }


}
