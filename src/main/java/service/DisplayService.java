package service;

import io.javalin.websocket.WsConnectContext;

public interface DisplayService {
    void addDisplaySession(String displayId, WsConnectContext ctx);

    void removeDisplaySession(String displayId);

    WsConnectContext getDisplaySession(String displayId);

    void closeAllSessions();

}
