package model.dto;

public abstract class WebSocketDTO {
    private final String type;

    protected WebSocketDTO(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
