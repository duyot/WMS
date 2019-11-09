package com.wms.dto;

/**
 * Created by truongbx
 */
public class RealExportExcelDTO {
    private String goodsCode;
    private String goodsName;
    private String goodsState;
    private String amount;
    private String unitName;
    private String weight;
    private String volume;
    private String cellCode;
    private String description;

    public RealExportExcelDTO() {
    }

    public RealExportExcelDTO(String goodsCode, String goodsName, String goodsState, String amount, String unitName, String weight, String volume, String cellCode, String description) {
        this.goodsCode = goodsCode;
        this.goodsName = goodsName;
        this.goodsState = goodsState;
        this.amount = amount;
        this.unitName = unitName;
        this.weight = weight;
        this.volume = volume;
        this.cellCode = cellCode;
        this.description = description;
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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getCellCode() {
        return cellCode;
    }

    public void setCellCode(String cellCode) {
        this.cellCode = cellCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
