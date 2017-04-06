package com.wms.dto;

/**
 * Created by doanlv4 on 28/03/2017.
 */
public class AppParamsDTO {
    private String id;
    private String code;
    private String name;
    private String status;
    private String statusName;
    private String parOrder;

    public AppParamsDTO(String id, String code, String name, String status, String parOrder) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
        this.parOrder = parOrder;
    }

    public AppParamsDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getParOrder() {
        return parOrder;
    }

    public void setParOrder(String parOrder) {
        this.parOrder = parOrder;
    }
}
