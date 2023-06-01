package service;

import io.javalin.websocket.WsConnectContext;
import model.Display;
import model.dto.DisplayDTO;
import repository.DisplayRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DisplayServiceImpl implements DisplayService {
    private final ConcurrentHashMap<String, WsConnectContext> displaySessions;
    private final DisplayRepository displayRepository;
    public DisplayServiceImpl(DisplayRepository displayRepository) {
        displaySessions = new ConcurrentHashMap<>();
        this.displayRepository = displayRepository;
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

    @Override
    public Display getDisplay(String displayId) {
        return displayRepository.findById(displayId);
    }

    @Override
    public void registerDisplay(String displayId, String name) {
        Display display = new Display(displayId, name);
        displayRepository.insert(display);
    }

    @Override
    public boolean isDisplayRegistered(String displayId) {
        return displayRepository.findById(displayId) != null;
    }

    @Override
    public List<DisplayDTO> getDisplayList() {
        List<DisplayDTO> displayList = new ArrayList<>();
        Map<String, Display> allDisplaysMap = new HashMap<>();

        List<Display> allDisplaysInDatabase = displayRepository.findAll();
        for(Display display : allDisplaysInDatabase) {
            allDisplaysMap.put(display.displayId(), display);
        }

        displaySessions.forEach((displayId, ctx) -> {
            Display display = allDisplaysMap.get(displayId);
            if(display != null) {
                displayList.add(new DisplayDTO(display.displayId(), display.name(), true));
            } else {
                displayList.add(new DisplayDTO(displayId, "Guest", false));
            }
        });

        return displayList;
    }


}
