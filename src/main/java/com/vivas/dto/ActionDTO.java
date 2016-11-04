package com.vivas.dto;

/**
 * Created by duyot on 11/2/2016.
 */
public class ActionDTO   {
    private String id;
    private String actionName;
    private String actionCode;
    private String parentActionId;
    private String url;
    private String status;
    private String createDate;
    private String createUser;
    private String levels;
    private String orders;
    private String imgClass;

    public ActionDTO(String id, String actionName, String actionCode,
                     String parentActionId, String url, String status,
                     String createDate, String createUser, String levels,
                     String orders, String imgClass) {
        this.id = id;
        this.actionName = actionName;
        this.actionCode = actionCode;
        this.parentActionId = parentActionId;
        this.url = url;
        this.status = status;
        this.createDate = createDate;
        this.createUser = createUser;
        this.levels = levels;
        this.orders = orders;
        this.imgClass = imgClass;
    }

    public ActionDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public String getParentActionId() {
        return parentActionId;
    }

    public void setParentActionId(String parentActionId) {
        this.parentActionId = parentActionId;
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

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
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
