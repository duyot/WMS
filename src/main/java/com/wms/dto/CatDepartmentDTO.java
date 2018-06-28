package com.wms.dto;


/**
 * Created by doanlv4 on 2/17/2017.
 */
public class CatDepartmentDTO {
    private String id;
    private String name;
    private String code;
    private String status;
    private String custId;
    private String path;
    private String parentId;

    public CatDepartmentDTO(String id, String code, String name, String status, String custId, String path, String parentId ) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
        this.custId = custId;
        this.path = path;
        this.parentId = parentId;
    }

    public CatDepartmentDTO() {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return "CatPartnerDTO{" +
                "id='" + id + '\'' +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", custId='" + custId + '\'' +
                ", path='" + path + '\'' +
                ", parentId='" + parentId + '\'' +
                '}';
    }
}