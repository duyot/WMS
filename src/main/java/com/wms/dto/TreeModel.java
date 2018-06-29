package com.wms.dto;

public class TreeModel {
    String id;
    String path;
    String name;
    boolean isSellected;


    public TreeModel(String id, String path, String name) {
        this.id = id;
        this.path = path;
        this.name = name;
    }

    public TreeModel(String id, String path, String name, boolean isSellected) {
        this.id = id;
        this.path = path;
        this.name = name;
        this.isSellected = isSellected;
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

    public boolean isSellected() {
        return isSellected;
    }

    public void setSellected(boolean sellected) {
        isSellected = sellected;
    }
}
