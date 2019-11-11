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
    private String invoiceNumber;
    private String type;
    private String status;
    private String createdDate;
    private String createdUser;
    private String transMoneyTotal;
    private String transMoneyDiscount;
    private String discountAmount;
    private String transMoneyRequire;
    private String description;
    //
    private String stockValue;
    private String typeValue;
    private String partnerName;
    private String partnerId;

    //DoanLV 26/05/2018 bo sung them thong tin de in phieu xuat
    private String customerName;
    private String stockName;
    private String stockCode;
    private String partnerCode;
    private String partnerTelNumber;
    private String partnerAddress;

    //Khach hang nhan trong cac giao dich xuat
    private String receiveName;
    private String receiveId;

    private String customerMoney;
    private String returnMoney;

    private String orderCode;
    private String orderId;
    private String exportMethod;

    private String cellCode;

    public String getCellCode() {
        return cellCode;
    }

    public void setCellCode(String cellCode) {
        this.cellCode = cellCode;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(String receiveId) {
        this.receiveId = receiveId;
    }

    public String getReceiveName() {
        return receiveName;
    }

    public void setReceiveName(String receiveName) {
        this.receiveName = receiveName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public String getPartnerCode() {
        return partnerCode;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public String getPartnerTelNumber() {
        return partnerTelNumber;
    }

    public void setPartnerTelNumber(String partnerTelNumber) {
        this.partnerTelNumber = partnerTelNumber;
    }

    public String getPartnerAddress() {
        return partnerAddress;
    }

    public void setPartnerAddress(String partnerAddress) {
        this.partnerAddress = partnerAddress;
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

    public MjrStockTransDTO() {
    }

    public MjrStockTransDTO(String id, String code, String custId, String stockId, String contractNumber, String invoiceNumber, String type, String status, String createdDate, String createdUser, String transMoneyTotal, String transMoneyDiscount, String discountAmount, String transMoneyRequire, String transMoneyReceive, String transMoneyResponse, String description,
                            String partnerId, String partnerName, String receiveId, String receiveName, String orderId, String orderCode) {
        this.id = id;
        this.code = code;
        this.custId = custId;
        this.stockId = stockId;
        this.contractNumber = contractNumber;
        this.invoiceNumber = invoiceNumber;
        this.type = type;
        this.status = status;
        this.createdDate = createdDate;
        this.createdUser = createdUser;
        this.transMoneyTotal = transMoneyTotal;
        this.transMoneyDiscount = transMoneyDiscount;
        this.discountAmount = discountAmount;
        this.transMoneyRequire = transMoneyRequire;
        this.description = description;
        this.partnerId = partnerId;
        this.partnerName = partnerName;
        this.receiveId = receiveId;
        this.receiveName = receiveName;
        this.orderId = orderId;
        this.orderCode = orderCode;
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

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
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


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getCustomerMoney() {
        return customerMoney;
    }

    public void setCustomerMoney(String customerMoney) {
        this.customerMoney = customerMoney;
    }

    public String getReturnMoney() {
        return returnMoney;
    }

    public void setReturnMoney(String returnMoney) {
        this.returnMoney = returnMoney;
    }

    public String getExportMethod() {
        return exportMethod;
    }

    public void setExportMethod(String exportMethod) {
        this.exportMethod = exportMethod;
    }
}
