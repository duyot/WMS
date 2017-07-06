package com.wms.dto;

/**
 * Created by duyot on 2/17/2017.
 */
public class CatStockDTO extends BaseDTO{
    private String id;
    private String custId;
    private String address;
    private String status;
    private String managerInfo;
    private String custName;
    private String statusName;

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

    public CatStockDTO(String id, String custId, String code, String name, String address, String status, String managerInfo) {
        this.id = id;
        this.custId = custId;
        this.code = code;
        this.name = name;
        this.address = address;
        this.status = status;
        this.managerInfo = managerInfo;
    }

    public CatStockDTO() {
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getManagerInfo() {
        return managerInfo;
    }

    public void setManagerInfo(String managerInfo) {
        this.managerInfo = managerInfo;
    }

    @Override
    public String toString() {
        return "CatStockDTO{" +
                "code='" + code + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", custId='" + custId + '\'' +
                ", address='" + address + '\'' +
                ", status='" + status + '\'' +
                ", managerInfo='" + managerInfo + '\'' +
                ", custName='" + custName + '\'' +
                ", statusName='" + statusName + '\'' +
                '}';
    }
}
