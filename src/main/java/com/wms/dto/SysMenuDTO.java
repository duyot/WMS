package com.wms.dto;

/**
 * Created by duyot on 11/2/2016.
 */
public class SysMenuDTO {
    private String id;
    private String name;
    private String code;
    private String parentId;
    private String url;
    private String status;
    private String levels;
    private String orders;
    private String imgClass;

    public SysMenuDTO(String id, String name, String code, String parentId, String url, String status, String levels, String orders, String imgClass) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.parentId = parentId;
        this.url = url;
        this.status = status;
        this.levels = levels;
        this.orders = orders;
        this.imgClass = imgClass;
    }

    public SysMenuDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLevels() {
        return levels;
    }

    public void setLevels(String levels) {
        this.levels = levels;
    }

    public String getOrders() {
        return orders;
    }

    public void setOrders(String orders) {
        this.orders = orders;
    }


    public String getImgClass() {
        return imgClass;
    }

    public void setImgClass(String imgClass) {
        this.imgClass = imgClass;
    }

}
