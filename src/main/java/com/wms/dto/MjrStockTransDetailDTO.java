package com.wms.dto;

/**
 * Created by duyot on 12/29/2016.
 */
public class MjrStockTransDetailDTO {
    private String id;
    private String stockTransId;
    private String goodsId;
    private String goodsCode;
    private String goodsState;
    private String isSerial;
    private String amount;
    private String serial;
    private String inputPrice;
    private String outputPrice;
    private String cellCode;
    //unmap db field
    private String goodsName;
    private String columnId;
    //field to show on table
    private String amountValue;
    private String inputPriceValue;
    private String outputPriceValue;
    private String goodsStateValue;
    private String errorInfo;
    private String importDate;
    private String exportDate;
    private String statusValue;
    private String status;
    private String stockId;
    private String stockValue;
    private String stockTransCode;
    private String stockCode;
    private String stockName;
    private String stockTransType;
    private String unitName;
    private String stockTransCreatedDate;
    private String stockTransCreatedUser;
    private String partnerId;
    private String partnerCode;
    private String partnerName;
    private String totalMoney;
    private String ieMoney;
    //
    private String volume;
    private String weight;
    private String changeDate;
    private String custId;

    private String description;
    private String produceDate;
    private String expireDate;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(String changeDate) {
        this.changeDate = changeDate;
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

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getStockTransCode() {
        return stockTransCode;
    }

    public void setStockTransCode(String stockTransCode) {
        this.stockTransCode = stockTransCode;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getStockTransType() {
        return stockTransType;
    }

    public void setStockTransType(String stockTransType) {
        this.stockTransType = stockTransType;
    }



    public String getStockTransCreatedDate() {
        return stockTransCreatedDate;
    }

    public void setStockTransCreatedDate(String stockTransCreatedDate) {
        this.stockTransCreatedDate = stockTransCreatedDate;
    }

    public String getStockTransCreatedUser() {
        return stockTransCreatedUser;
    }

    public void setStockTransCreatedUser(String stockTransCreatedUser) {
        this.stockTransCreatedUser = stockTransCreatedUser;
    }

    public MjrStockTransDetailDTO(String id, String stockTransId, String goodsId, String goodsCode, String goodsState, String isSerial, String amount, String serial, String inputPrice, String outputPrice, String cellCode, String goodsName, String columnId, String amountValue, String inputPriceValue, String outputPriceValue, String goodsStateValue, String errorInfo, String importDate, String exportDate, String statusValue, String status, String stockId, String stockValue, String stockTransCode, String stockName, String stockTransType, String unitName, String stockTransCreatedDate, String stockTransCreatedUser, String partnerId, String partnerName, String totalMoney, String ieMoney, String volume, String weight, String description, String produceDate, String expireDate) {
        this.id = id;
        this.stockTransId = stockTransId;
        this.goodsId = goodsId;
        this.goodsCode = goodsCode;
        this.goodsState = goodsState;
        this.isSerial = isSerial;
        this.amount = amount;
        this.serial = serial;
        this.inputPrice = inputPrice;
        this.outputPrice = outputPrice;
        this.cellCode = cellCode;
        this.goodsName = goodsName;
        this.columnId = columnId;
        this.amountValue = amountValue;
        this.inputPriceValue = inputPriceValue;
        this.outputPriceValue = outputPriceValue;
        this.goodsStateValue = goodsStateValue;
        this.errorInfo = errorInfo;
        this.importDate = importDate;
        this.exportDate = exportDate;
        this.statusValue = statusValue;
        this.status = status;
        this.stockId = stockId;
        this.stockValue = stockValue;
        this.stockTransCode = stockTransCode;
        this.stockName = stockName;
        this.stockTransType = stockTransType;
        this.unitName = unitName;
        this.stockTransCreatedDate = stockTransCreatedDate;
        this.stockTransCreatedUser = stockTransCreatedUser;
        this.partnerId = partnerId;
        this.partnerName = partnerName;
        this.totalMoney = totalMoney;
        this.ieMoney = ieMoney;
        this.volume = volume;
        this.weight = weight;
        this.description = description;
        this.produceDate = produceDate;
        this.expireDate = expireDate;
    }

    public MjrStockTransDetailDTO(String id, String stockTransId, String goodsId, String goodsCode, String goodsState,
                                  String isSerial, String amount, String serial, String inputPrice, String outputPrice,
                                  String cellCode, String statusValue) {
        this.id = id;
        this.stockTransId = stockTransId;
        this.goodsId = goodsId;
        this.goodsCode = goodsCode;
        this.goodsState = goodsState;
        this.isSerial = isSerial;
        this.amount = amount;
        this.serial = serial;
        this.inputPrice = inputPrice;
        this.outputPrice = outputPrice;
        this.cellCode = cellCode;
        this.statusValue = statusValue;
    }

    public MjrStockTransDetailDTO(String custId, String stockId, String goodsId,String goodsState, String partnerId, String amount,String volume,String weight,String cellCode,String importDate, String changeDate, String inputPrice, String serial) {
        this.custId = custId;
        this.stockId = stockId;
        this.goodsId = goodsId;
        this.goodsState = goodsState;
        this.partnerId = partnerId;
        this.amount = amount;
        this.volume = volume;
        this.weight = weight;
        this.cellCode = cellCode;
        this.importDate = importDate;
        this.changeDate = changeDate;
        this.inputPrice = inputPrice;
        this.serial = serial;
    }

    public MjrStockTransDetailDTO(String goodsName, String goodsCode) {
        this.goodsName = goodsName;
        this.goodsCode = goodsCode;
    }

    public MjrStockTransDetailDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStockTransId() {
        return stockTransId;
    }

    public void setStockTransId(String stockTransId) {
        this.stockTransId = stockTransId;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public String getGoodsState() {
        return goodsState;
    }

    public void setGoodsState(String goodsState) {
        this.goodsState = goodsState;
    }

    public String getIsSerial() {
        return isSerial;
    }

    public void setIsSerial(String isSerial) {
        this.isSerial = isSerial;
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

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getInputPrice() {
        if (this.inputPrice == null){
            return "";
        }
        return inputPrice;
    }

    public void setInputPrice(String inputPrice) {
        this.inputPrice = inputPrice;
    }

    public String getOutputPrice() {
        if (this.outputPrice == null){
            return "";
        }
        return outputPrice;
    }

    public void setOutputPrice(String outputPrice) {
        this.outputPrice = outputPrice;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getCellCode() {
        return cellCode;
    }

    public void setCellCode(String cellCode) {
        this.cellCode = cellCode;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getAmountValue() {
        return amountValue;
    }

    public void setAmountValue(String amountValue) {
        this.amountValue = amountValue;
    }

    public String getInputPriceValue() {
        return inputPriceValue;
    }

    public void setInputPriceValue(String inputPriceValue) {
        this.inputPriceValue = inputPriceValue;
    }

    public String getOutputPriceValue() {
        return outputPriceValue;
    }

    public void setOutputPriceValue(String outputPriceValue) {
        this.outputPriceValue = outputPriceValue;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public String getGoodsStateValue() {
        return goodsStateValue;
    }

    public void setGoodsStateValue(String goodsStateValue) {
        this.goodsStateValue = goodsStateValue;
    }

    public String getImportDate() {
        return importDate;
    }

    public void setImportDate(String importDate) {
        this.importDate = importDate;
    }

    public String getExportDate() {
        return exportDate;
    }

    public void setExportDate(String exportDate) {
        this.exportDate = exportDate;
    }

    public String getStatusValue() {
        return statusValue;
    }

    public void setStatusValue(String statusValue) {
        this.statusValue = statusValue;
    }

    public String getStockValue() {
        return stockValue;
    }

    public void setStockValue(String stockValue) {
        this.stockValue = stockValue;
    }

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(String totalMoney) {
        this.totalMoney = totalMoney;
    }

    public String getIeMoney() {
        return ieMoney;
    }

    public void setIeMoney(String ieMoney) {
        this.ieMoney = ieMoney;
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
}
