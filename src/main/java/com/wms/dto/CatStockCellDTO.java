package com.wms.dto;

/**
 * Created by duyot on 4/19/2017.
 */
public class CatStockCellDTO {
    private String id;
    private String code;
    private String stockId;
    private String maxWeight;
    private String maxVolume;
    private String manyCodes;
    private String maxWeightValue;
    private String maxVolumeValue;
    private String manyCodesValue;

    public CatStockCellDTO(String id, String code, String stockId,String maxWeight, String maxVolume, String manyCodes) {
        this.id = id;
        this.code = code;
        this.stockId = stockId;
        this.maxWeight = maxWeight;
        this.maxVolume = maxVolume;
        this.manyCodes = manyCodes;
    }

    public CatStockCellDTO() {
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

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public String getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(String maxWeight) {
        this.maxWeight = maxWeight;
    }

    public String getMaxVolume() {
        return maxVolume;
    }

    public void setMaxVolume(String maxVolume) {
        this.maxVolume = maxVolume;
    }

    public String getManyCodes() {
        return manyCodes;
    }

    public void setManyCodes(String manyCodes) {
        this.manyCodes = manyCodes;
    }

    public String getMaxWeightValue() {
        return maxWeightValue;
    }

    public void setMaxWeightValue(String maxWeightValue) {
        this.maxWeightValue = maxWeightValue;
    }

    public String getMaxVolumeValue() {
        return maxVolumeValue;
    }

    public void setMaxVolumeValue(String maxVolumeValue) {
        this.maxVolumeValue = maxVolumeValue;
    }

    public String getManyCodesValue() {
        return manyCodesValue;
    }

    public void setManyCodesValue(String manyCodesValue) {
        this.manyCodesValue = manyCodesValue;
    }
}
