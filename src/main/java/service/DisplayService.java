package service;

import io.javalin.websocket.WsConnectContext;
import model.Display;
import model.dto.DisplayDTO;

import java.util.List;

public interface DisplayService {
    void addDisplaySession(String displayId, WsConnectContext ctx);

    void removeDisplaySession(String displayId);

    WsConnectContext getDisplaySession(String displayId);

    void closeAllSessions();

    Display getDisplay(String displayId);

    void registerDisplay(String displayId, String name);

    boolean isDisplayRegistered(String displayId);

    List<DisplayDTO> getDisplayList();

}
