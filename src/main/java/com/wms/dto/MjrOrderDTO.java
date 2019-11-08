package com.wms.dto;


/**
 * Created by truongbx
 */


public class MjrOrderDTO {

    private String id;
    private String code;
    private String custId;
    private String stockId;
    private String stockValue;
    private String type;
    private String typeValue;
    private String exportMethod;
    private String status;
    private String createdDate;
    private String createdUser;
    private String description;
    private String partnerId;
    private String partnerName;

    private String receiveName;
    private String receiveId;


    public MjrOrderDTO() {
    }

    public MjrOrderDTO(String id, String code, String custId, String stockId, String type, String exportMethod, String status, String createdDate, String createdUser, String description, String partnerId, String partnerName, String receiveName, String receiveId) {
        this.id = id;
        this.code = code;
        this.custId = custId;
        this.stockId = stockId;
        this.type = type;
        this.exportMethod = exportMethod;
        this.status = status;
        this.createdDate = createdDate;
        this.createdUser = createdUser;
        this.description = description;
        this.partnerId = partnerId;
        this.partnerName = partnerName;
        this.receiveName = receiveName;
        this.receiveId = receiveId;
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

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExportMethod() {
        return exportMethod;
    }

    public void setExportMethod(String exportMethod) {
        this.exportMethod = exportMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(String createdUser) {
        this.createdUser = createdUser;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getReceiveName() {
        return receiveName;
    }

    public void setReceiveName(String receiveName) {
        this.receiveName = receiveName;
    }

    public String getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(String receiveId) {
        this.receiveId = receiveId;
    }

    public String getStockValue() {
        return stockValue;
    }

    public void setStockValue(String stockValue) {
        this.stockValue = stockValue;
    }

    public String getTypeValue() {
        return typeValue;
    }

    public void setTypeValue(String typeValue) {
        this.typeValue = typeValue;
    }
}
