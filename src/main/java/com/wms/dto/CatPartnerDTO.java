package com.wms.dto;

/**
 * Created by duyot on 2/17/2017.
 */
public class CatPartnerDTO {
    private String id;
    private String custId;
    private String code;
    private String name;
    private String address;
    private String status;
    private String custName;
    private String telNumber;
    private String statusName;
    private String parentId;
    private String parentName;
    private String userManagerId;
    private String userManagerName;
    private String errorInfo;

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
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

    public CatPartnerDTO(String id, String custId, String code, String name, String address, String status, String telNumber) {
        this.id = id;
        this.custId = custId;
        this.code = code;
        this.name = name;
        this.address = address;
        this.status = status;
        this.telNumber = telNumber;
    }

    public CatPartnerDTO() {
    }

    public String getTelNumber() {
        return telNumber;
    }

    public void setTelNumber(String telNumber) {
        this.telNumber = telNumber;
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

    public String getUserManagerId() {
        return userManagerId;
    }

    public void setUserManagerId(String userManagerId) {
        this.userManagerId = userManagerId;
    }

    public String getUserManagerName() {
        return userManagerName;
    }

    public void setUserManagerName(String userManagerName) {
        this.userManagerName = userManagerName;
    }

    @Override
    public String toString() {
        return "CatPartnerDTO{" +
                "id='" + id + '\'' +
                ", custId='" + custId + '\'' +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", status='" + status + '\'' +
                ", custName='" + custName + '\'' +
                ", telNumber='" + telNumber + '\'' +
                ", statusName='" + statusName + '\'' +
                ", parentId='" + parentId + '\'' +
                ", parentName='" + parentName + '\'' +
                ", userManagerId='" + userManagerId + '\'' +
                ", userManagerName'" + userManagerName + '\'' +
                '}';
    }
}
