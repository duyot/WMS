package com.wms.dto;

/**
 * Created by duyot on 2/17/2017.
 */
public class CatStockDTO {
    private String id;
    private String custId;
    private String code;
    private String name;
    private String address;
    private String status;
    private String managerInfo;
    private String custName;

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

}