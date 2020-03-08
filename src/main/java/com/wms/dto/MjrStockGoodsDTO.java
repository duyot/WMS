package com.wms.dto;

/**
 * Created by duyot on 1/3/2017.
 */
public class MjrStockGoodsDTO {
    private String id;
    private String custId;
    private String stockId;
    private String goodsId;
    private String goodsState;
    private String cellCode;
    private String amount;
    private String importDate;
    private String changeDate;
    private String status;
    private String partnerId;
    private String importStockTransId;
    private String inputPrice;
    private String outputPrice;
    private String exportDate;
    private String exportStockTransId;
    private String volume;
    private String weight;
    private String produceDate;
    private String expireDate;
    private String description;
    private String content;

    public MjrStockGoodsDTO(String id, String custId, String stockId, String goodsId, String goodsState, String cellCode, String amount, String importDate, String changeDate, String status, String partnerId, String importStockTransId, String inputPrice, String outputPrice, String exportDate, String exportStockTransId, String volume, String weight, String produceDate, String expireDate, String description, String content) {
        this.id = id;
        this.custId = custId;
        this.stockId = stockId;
        this.goodsId = goodsId;
        this.goodsState = goodsState;
        this.cellCode = cellCode;
        this.amount = amount;
        this.importDate = importDate;
        this.changeDate = changeDate;
        this.status = status;
        this.partnerId = partnerId;
        this.importStockTransId = importStockTransId;
        this.inputPrice = inputPrice;
        this.outputPrice = outputPrice;
        this.exportDate = exportDate;
        this.exportStockTransId = exportStockTransId;
        this.volume = volume;
        this.weight = weight;
        this.produceDate = produceDate;
        this.expireDate = expireDate;
        this.description = description;
        this.content = content;
    }

    public MjrStockGoodsDTO() {
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

    public String getExportDate() {
        return exportDate;
    }

    public void setExportDate(String exportDate) {
        this.exportDate = exportDate;
    }

    public String getExportStockTransId() {
        return exportStockTransId;
    }

    public void setExportStockTransId(String exportStockTransId) {
        this.exportStockTransId = exportStockTransId;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getProduceDate() {
        return produceDate;
    }

    public void setProduceDate(String produceDate) {
        this.produceDate = produceDate;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
