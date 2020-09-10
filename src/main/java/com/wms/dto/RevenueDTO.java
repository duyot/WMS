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
    private String paymentStatus;
    private String paymentStatusValue;
    private String paymentAmount;
    private String paymentDescription;
    private String paymentDate;
    private String paymentAction;
    private String paymentRemain;
    private Double amountValue;
    private Double paymentAmountValue;
    private Double paymentRemainValue;
    private Double chargeValue;
    private Double totalAmountValue;

    public RevenueDTO(String id, String custId, String partnerId, String amount, String vat, String charge, String stockTransId,
                      String stockTransCode, String description, String type, String createdUser, String createdDate,
                      String paymentStatus, String paymentAmount, String paymentDescription, String paymentDate) {
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
        this.paymentStatus = paymentStatus;
        this.paymentAmount = paymentAmount;
        this.paymentDescription = paymentDescription;
        this.paymentDate = paymentDate;
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

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(String paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getPaymentDescription() {
        return paymentDescription;
    }

    public void setPaymentDescription(String paymentDescription) {
        this.paymentDescription = paymentDescription;
    }

    public String getPaymentStatusValue() {
        return paymentStatusValue;
    }

    public void setPaymentStatusValue(String paymentStatusValue) {
        this.paymentStatusValue = paymentStatusValue;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentAction() {
        return paymentAction;
    }

    public void setPaymentAction(String paymentAction) {
        this.paymentAction = paymentAction;
    }

    public String getPaymentRemain() {
        return paymentRemain;
    }

    public void setPaymentRemain(String paymentRemain) {
        this.paymentRemain = paymentRemain;
    }

    public Double getAmountValue() {
        return amountValue;
    }

    public void setAmountValue(Double amountValue) {
        this.amountValue = amountValue;
    }

    public Double getPaymentAmountValue() {
        return paymentAmountValue;
    }

    public void setPaymentAmountValue(Double paymentAmountValue) {
        this.paymentAmountValue = paymentAmountValue;
    }

    public Double getPaymentRemainValue() {
        return paymentRemainValue;
    }

    public void setPaymentRemainValue(Double paymentRemainValue) {
        this.paymentRemainValue = paymentRemainValue;
    }

    public Double getChargeValue() {
        return chargeValue;
    }

    public void setChargeValue(Double chargeValue) {
        this.chargeValue = chargeValue;
    }

    public Double getTotalAmountValue() {
        return totalAmountValue;
    }

    public void setTotalAmountValue(Double totalAmountValue) {
        this.totalAmountValue = totalAmountValue;
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
                ", paymentStatus='" + paymentStatus + '\'' +
                ", paymentAmount='" + paymentAmount + '\'' +
                ", paymentDescription='" + paymentDescription + '\'' +
                ", paymentDate='" + paymentDate + '\'' +
                '}';
    }
}
