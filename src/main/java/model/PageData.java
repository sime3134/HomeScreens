package model;

import java.util.HashMap;
import java.util.Map;

public class PageData {
    private String path;
    private Map<String, Object> data;
    public PageData(String path) {
        this.path = path;
        this.data = new HashMap<>();
    }

    public PageData(String path, Map<String, Object> data) {
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
