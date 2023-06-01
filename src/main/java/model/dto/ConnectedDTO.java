package model.dto;

public class ConnectedDTO extends WebSocketDTO {
    private final String displayId;

    private final boolean registered;

    public ConnectedDTO(String displayId, boolean registered) {
        super("connected");
        this.displayId = displayId;
        this.registered = registered;
    }

    public String getDisplayId() {
        return displayId;
    }

    public boolean getRegistered() {
        return registered;
    }
}
