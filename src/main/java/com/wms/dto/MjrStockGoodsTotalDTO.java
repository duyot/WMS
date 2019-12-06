package com.wms.dto;

/**
 * Created by duyot on 12/19/2016.
 */
public class MjrStockGoodsTotalDTO {
    private String id;
    private String custId;
    private String goodsId;
    private String goodsCode;
    private String goodsName;
    private String goodsState;
    private String stockId;
    private String amount;
    private String changeDate;
    //name
    private String goodsStateName;
    private String stockName;
    private String amountValue;
    //Chi de phuc vu add tham so vao tim kiem chu ko luu vao database
    private String partnerId;
    private String userId;
    private String goodsUnitName;

    public String getGoodsUnitName() {
        return goodsUnitName;
    }

    public void setGoodsUnitName(String goodsUnitName) {
        this.goodsUnitName = goodsUnitName;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public MjrStockGoodsTotalDTO(String id, String custId, String goodsId, String goodsCode, String goodsName, String goodsState, String stockId, String amount, String changeDate) {
        this.id = id;
        this.custId = custId;
        this.goodsId = goodsId;
        this.goodsCode = goodsCode;
        this.goodsName = goodsName;
        this.goodsState = goodsState;
        this.stockId = stockId;
        this.amount = amount;
        this.changeDate = changeDate;
    }

    public MjrStockGoodsTotalDTO() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsState() {
        return goodsState;
    }

    public void setGoodsState(String goodsState) {
        this.goodsState = goodsState;
    }

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(String changeDate) {
        this.changeDate = changeDate;
    }

    public String getGoodsStateName() {
        return goodsStateName;
    }

    public void setGoodsStateName(String goodsStateName) {
        this.goodsStateName = goodsStateName;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getAmountValue() {
        return amountValue;
    }

    public void setAmountValue(String amountValue) {
        this.amountValue = amountValue;
    }

    @Override
    public String toString() {
        return "MjrStockGoodsTotalDTO{" +
                "id='" + id + '\'' +
                ", custId='" + custId + '\'' +
                ", goodsId='" + goodsId + '\'' +
                ", goodsCode='" + goodsCode + '\'' +
                ", goodsName='" + goodsName + '\'' +
                ", goodsState='" + goodsState + '\'' +
                ", stockId='" + stockId + '\'' +
                ", amount='" + amount + '\'' +
                ", changeDate='" + changeDate + '\'' +
                '}';
    }
}
