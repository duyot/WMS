package com.wms.dto;

import com.wms.constants.Constants;
import com.wms.utils.DataUtil;

/**
 * Created by duyot on 12/9/2016.
 */
public class CatGoodsDTO extends BaseDTO{
    private String id;
    private String status;
    private String createdDate;
    private String custId;
    private String unitType;
    private String goodsGroupId;
    private String goodsGroupName;
    private String isSerial;
    private String description;
    private String inPrice;
    private String outPrice;
    private String brand;
    private String custName;
    private String unitTypeName;
    private String statusName;
    //
    private String columnId;
    private String isSerialName;
    private String inPriceValue;
    private String outPriceValue;
    private String errorInfo;
    private String amount;

    private String length;
    private String width;
    private String high;
    private String weight;
    private String volume;

    public CatGoodsDTO() {
    }

    public CatGoodsDTO(String id, String status, String createdDate, String custId, String unitType, String goodsGroupId, String goodsGroupName, String isSerial, String description, String inPrice, String outPrice, String brand, String custName, String unitTypeName, String statusName, String columnId, String isSerialName, String inPriceValue, String outPriceValue, String errorInfo, String amount, String length, String width, String high, String weight, String volume) {
        this.id = id;
        this.status = status;
        this.createdDate = createdDate;
        this.custId = custId;
        this.unitType = unitType;
        this.goodsGroupId = goodsGroupId;
        this.goodsGroupName = goodsGroupName;
        this.isSerial = isSerial;
        this.description = description;
        this.inPrice = inPrice;
        this.outPrice = outPrice;
        this.brand = brand;
        this.custName = custName;
        this.unitTypeName = unitTypeName;
        this.statusName = statusName;
        this.columnId = columnId;
        this.isSerialName = isSerialName;
        this.inPriceValue = inPriceValue;
        this.outPriceValue = outPriceValue;
        this.errorInfo = errorInfo;
        this.amount = amount;
        this.length = length;
        this.width = width;
        this.high = high;
        this.weight = weight;
        this.volume = volume;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getIsSerialName() {
        return isSerialName;
    }

    public void setIsSerialName(String isSerialName) {
        this.isSerialName = isSerialName;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public String getInPriceValue() {
        return inPriceValue;
    }

    public void setInPriceValue(String inPriceValue) {
        this.inPriceValue = inPriceValue;
    }

    public String getOutPriceValue() {
        return outPriceValue;
    }

    public void setOutPriceValue(String outPriceValue) {
        this.outPriceValue = outPriceValue;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getUnitTypeName() {
        return unitTypeName;
    }

    public void setUnitTypeName(String unitTypeName) {
        this.unitTypeName = unitTypeName;
    }

    public String getGoodsGroupName() {
        return goodsGroupName;
    }

    public void setGoodsGroupName(String goodsGroupName) {
        this.goodsGroupName = goodsGroupName;
    }





    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public String getGoodsGroupId() {
        return goodsGroupId;
    }

    public void setGoodsGroupId(String goodsGroupId) {
        this.goodsGroupId = goodsGroupId;
    }

    public String getIsSerial() {
        return isSerial;
    }

    public boolean isSerial(){
        return Constants.IS_SERIAL.equalsIgnoreCase(isSerial);
    }

    public void setIsSerial(String isSerial) {
        this.isSerial = isSerial;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInPrice() {
        return inPrice;
    }

    public void setInPrice(String inPrice) {
        this.inPrice = inPrice;
    }

    public String getOutPrice() {
        return outPrice;
    }

    public void setOutPrice(String outPrice) {
        this.outPrice = outPrice;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public void setVolumeFromSize() {
        Double length = DataUtil.isNullOrEmpty(getLength())? 0d:Double.valueOf(getLength());
        Double width  = DataUtil.isNullOrEmpty(getWidth())? 0d:Double.valueOf(getWidth());
        Double high   = DataUtil.isNullOrEmpty(getHigh())? 0d:Double.valueOf(getHigh());
        Double volume = Math.round(length * width * high/1000000 * 1000000d)/1000000d;
        this.volume =  volume.toString();
    }

    @Override
    public String toString() {
        return "CatGoodsDTO{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", custId='" + custId + '\'' +
                ", unitType='" + unitType + '\'' +
                ", goodsGroupId='" + goodsGroupId + '\'' +
                ", goodsGroupName='" + goodsGroupName + '\'' +
                ", isSerial='" + isSerial + '\'' +
                ", description='" + description + '\'' +
                ", inPrice='" + inPrice + '\'' +
                ", outPrice='" + outPrice + '\'' +
                ", brand='" + brand + '\'' +
                ", custName='" + custName + '\'' +
                ", unitTypeName='" + unitTypeName + '\'' +
                ", statusName='" + statusName + '\'' +
                ", columnId='" + columnId + '\'' +
                ", isSerialName='" + isSerialName + '\'' +
                ", inPriceValue='" + inPriceValue + '\'' +
                ", outPriceValue='" + outPriceValue + '\'' +
                ", errorInfo='" + errorInfo + '\'' +
                ", length='" + length + '\'' +
                ", width='" + width + '\'' +
                ", high='" + high + '\'' +
                ", weight='" + weight + '\'' +
                ", volume='" + volume + '\'' +
                '}';
    }


}
