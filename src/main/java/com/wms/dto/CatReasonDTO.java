package com.wms.dto;

/**
 * Created by duyot on 2/17/2017.
 */
public class CatReasonDTO {
    private String id;
    private String custId;    
    private String name;
    private String status;
    private String custName;
    private String statusName;
    private String type;
    private String typeName;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public CatReasonDTO(String id, String custId, String name, String status, String type) {
        this.id = id;
        this.custId = custId;
        this.name = name;
        this.status = status;
        this.type = type;
    }

    public CatReasonDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
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

    @Override
    public String toString() {
        return "CatReasonDTO{" +
                "id='" + id + '\'' +
                ", custId='" + custId + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", custName='" + custName + '\'' +
                ", statusName='" + statusName + '\'' +
                ", type='" + type + '\'' +
                ", typeName='" + typeName + '\'' +
                '}';
    }
}
