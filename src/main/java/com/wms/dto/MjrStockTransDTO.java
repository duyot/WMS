package com.wms.dto;

/**
 * Created by duyot on 12/28/2016.
 */


public class MjrStockTransDTO {

    private String id;
    private String code;
    private String custId;
    private String stockId;
    private String contractNumber;
    private String invoicetNumber;
    private String type;
    private String status;
    private String createdDate;
    private String createdUser;
    private String transMoneyTotal;
    private String transMoneyDiscount;
    private String discountAmount;
    private String transMoneyRequire;
    private String transMoneyReceive;
    private String transMoneyResponse;

    public MjrStockTransDTO() {
    }

    public MjrStockTransDTO(String id, String code, String custId, String stockId, String contractNumber, String invoicetNumber, String type, String status, String createdDate, String createdUser, String transMoneyTotal, String transMoneyDiscount, String discountAmount, String transMoneyRequire, String transMoneyReceive, String transMoneyResponse) {
        this.id = id;
        this.code = code;
        this.custId = custId;
        this.stockId = stockId;
        this.contractNumber = contractNumber;
        this.invoicetNumber = invoicetNumber;
        this.type = type;
        this.status = status;
        this.createdDate = createdDate;
        this.createdUser = createdUser;
        this.transMoneyTotal = transMoneyTotal;
        this.transMoneyDiscount = transMoneyDiscount;
        this.discountAmount = discountAmount;
        this.transMoneyRequire = transMoneyRequire;
        this.transMoneyReceive = transMoneyReceive;
        this.transMoneyResponse = transMoneyResponse;
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

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getInvoicetNumber() {
        return invoicetNumber;
    }

    public void setInvoicetNumber(String invoicetNumber) {
        this.invoicetNumber = invoicetNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getTransMoneyTotal() {
        return transMoneyTotal;
    }

    public void setTransMoneyTotal(String transMoneyTotal) {
        this.transMoneyTotal = transMoneyTotal;
    }

    public String getTransMoneyDiscount() {
        return transMoneyDiscount;
    }

    public void setTransMoneyDiscount(String transMoneyDiscount) {
        this.transMoneyDiscount = transMoneyDiscount;
    }

    public String getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(String discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getTransMoneyRequire() {
        return transMoneyRequire;
    }

    public void setTransMoneyRequire(String transMoneyRequire) {
        this.transMoneyRequire = transMoneyRequire;
    }

    public String getTransMoneyReceive() {
        return transMoneyReceive;
    }

    public void setTransMoneyReceive(String transMoneyReceive) {
        this.transMoneyReceive = transMoneyReceive;
    }

    public String getTransMoneyResponse() {
        return transMoneyResponse;
    }

    public void setTransMoneyResponse(String transMoneyResponse) {
        this.transMoneyResponse = transMoneyResponse;
    }

}