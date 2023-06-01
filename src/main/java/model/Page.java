package model;

import java.util.HashMap;
import java.util.Map;

public class Page {
    private final String path;
    private final Map<String, Object> data;
    public Page(String path) {
        this.path = path;
        this.data = new HashMap<>();
    }

    public Page(String path, Map<String, Object> data) {
        this.path = path;
        this.data = data;
    }

    public String getPath() {
        return path;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void addData(String key, Object value) {
        data.put(key, value);
    }
}
