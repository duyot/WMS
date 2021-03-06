package com.wms.dto;

/**
 * Created by duyot on 3/6/2017.
 */
public class Err$MjrStockGoodsSerialDTO {
    private String id;
    private String custId;
    private String stockId;
    private String goodsId;
    private String goodsState;
    private String cellCode;
    private String amount;
    private String serial;
    private String importDate;
    private String changeDate;
    private String status;
    private String partnerId;
    private String importStockTransId;
    private String inputPrice;
    private String outputPrice;
    //
    private String oraErrorMessage;

    public Err$MjrStockGoodsSerialDTO(String id, String custId, String stockId, String goodsId, String goodsState,
                                      String cellCode, String amount, String serial, String importDate, String changeDate,
                                      String status, String partnerId, String importStockTransId, String inputPrice,
                                      String outputPrice, String oraErrorMessage) {
        this.id = id;
        this.custId = custId;
        this.stockId = stockId;
        this.goodsId = goodsId;
        this.goodsState = goodsState;
        this.cellCode = cellCode;
        this.amount = amount;
        this.serial = serial;
        this.importDate = importDate;
        this.changeDate = changeDate;
        this.status = status;
        this.partnerId = partnerId;
        this.importStockTransId = importStockTransId;
        this.inputPrice = inputPrice;
        this.outputPrice = outputPrice;
        this.oraErrorMessage = oraErrorMessage;
    }

    public Err$MjrStockGoodsSerialDTO() {
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

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsState() {
        return goodsState;
    }

    public void setGoodsState(String goodsState) {
        this.goodsState = goodsState;
    }

    public String getCellCode() {
        return cellCode;
    }

    public void setCellCode(String cellCode) {
        this.cellCode = cellCode;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getImportDate() {
        return importDate;
    }

    public void setImportDate(String importDate) {
        this.importDate = importDate;
    }

    public String getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(String changeDate) {
        this.changeDate = changeDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getImportStockTransId() {
        return importStockTransId;
    }

    public void setImportStockTransId(String importStockTransId) {
        this.importStockTransId = importStockTransId;
    }

    public String getInputPrice() {
        return inputPrice;
    }

    public void setInputPrice(String inputPrice) {
        this.inputPrice = inputPrice;
    }

    public String getOutputPrice() {
        return outputPrice;
    }

    public void setOutputPrice(String outputPrice) {
        this.outputPrice = outputPrice;
    }

    public String getOraErrorMessage() {
        return oraErrorMessage;
    }

    public void setOraErrorMessage(String oraErrorMessage) {
        this.oraErrorMessage = oraErrorMessage;
    }

}
