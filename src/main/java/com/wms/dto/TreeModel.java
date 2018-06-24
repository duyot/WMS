package com.wms.dto;

public class TreeModel {
    String id;
    String path;
    String name;


    public TreeModel(String id, String path, String name) {
        this.id = id;
        this.path = path;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
