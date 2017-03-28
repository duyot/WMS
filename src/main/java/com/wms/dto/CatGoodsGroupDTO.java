package com.wms.dto;

/**
 * Created by duyot on 12/7/2016.
 */
public class CatGoodsGroupDTO {

    private String id;
    private String name;
    private String status;
    private String custId;
    private String custName;
    private String statusName;

    public CatGoodsGroupDTO(String id, String name, String status, String custId) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.custId = custId;
    }

    public CatGoodsGroupDTO() {
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

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    @Override
    public String toString() {
        return "CatGoodsGroupDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", custId='" + custId + '\'' +
                '}';
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
