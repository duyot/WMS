package com.wms.dto;

/**
 * Created by duyot on 2/17/2017.
 */
public class RevenueDTO {
    private String id;
    private String custId;
    private String partnerId;
    private String partnerName;
    private String amount;
    private String vat;
    private String charge;
    private String stockTransId;
    private String stockTransCode;
    private String description;
    private String type;
    private String typeValue;
    private String createdUser;
    private String createdDate;
    private String vatValue;
    private String totalAmount;



    public RevenueDTO(String id, String custId, String partnerId, String amount, String vat, String charge, String stockTransId,
                      String stockTransCode, String description, String type, String createdUser, String createdDate) {
        this.id = id;
        this.custId = custId;
        this.partnerId = partnerId;
        this.amount = amount;
        this.vat = vat;
        this.charge = charge;
        this.stockTransId = stockTransId;
        this.stockTransCode = stockTransCode;
        this.description = description;
        this.type = type;
        this.createdUser = createdUser;
        this.createdDate = createdDate;
    }

    public RevenueDTO() {
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getVatValue() {
        return vatValue;
    }

    public void setVatValue(String vatValue) {
        this.vatValue = vatValue;
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

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getVat() {
        return vat;
    }

    public void setVat(String vat) {
        this.vat = vat;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getStockTransId() {
        return stockTransId;
    }

    public void setStockTransId(String stockTransId) {
        this.stockTransId = stockTransId;
    }

    public String getStockTransCode() {
        return stockTransCode;
    }

    public void setStockTransCode(String stockTransCode) {
        this.stockTransCode = stockTransCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeValue() {
        return typeValue;
    }

    public void setTypeValue(String typeValue) {
        this.typeValue = typeValue;
    }

    public String getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(String createdUser) {
        this.createdUser = createdUser;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "RevenueDTO{" +
                "id='" + id + '\'' +
                ", custId='" + custId + '\'' +
                ", partnerId='" + partnerId + '\'' +
                ", amount='" + amount + '\'' +
                ", vat='" + vat + '\'' +
                ", charge='" + charge + '\'' +
                ", stockTransId='" + stockTransId + '\'' +
                ", stockTransCode='" + stockTransCode + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", createdUser='" + createdUser + '\'' +
                ", createdDate='" + createdDate + '\'' +
                '}';
    }
}
