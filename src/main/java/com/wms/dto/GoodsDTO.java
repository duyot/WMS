package com.wms.dto;

import com.wms.base.BaseDTO;
import com.wms.base.BaseModel;
import com.wms.persistents.model.Goods;
import com.wms.utils.DateTimeUtils;
import com.wms.utils.StringUtils;

/**
 * Created by duyot on 12/9/2016.
 */
public class GoodsDTO extends BaseDTO{
    private String id;
    private String code;
    private String name;
    private String status;
    private String createDate;
    private String custId;
    private String unitType;
    private String goodsGroupId;
    private String serialType;
    private String description;
    private String inPrice;
    private String outPrice;
    private String brand;

    public GoodsDTO() {
    }

    public GoodsDTO(String id, String code, String name, String status, String createDate, String custId, String unitType, String goodsGroupId, String serialType, String description, String inPrice, String outPrice, String brand) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
        this.createDate = createDate;
        this.custId = custId;
        this.unitType = unitType;
        this.goodsGroupId = goodsGroupId;
        this.serialType = serialType;
        this.description = description;
        this.inPrice = inPrice;
        this.outPrice = outPrice;
        this.brand = brand;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
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

    public String getSerialType() {
        return serialType;
    }

    public void setSerialType(String serialType) {
        this.serialType = serialType;
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

    @Override
    public BaseModel toModel() {
        return new Goods(!StringUtils.validString(id) ? null:Long.valueOf(id),code,name,status,
                !StringUtils.validString(createDate) ? null: DateTimeUtils.convertStringToDate(createDate),
                !StringUtils.validString(custId) ? null:Long.valueOf(custId),unitType,!StringUtils.validString(goodsGroupId) ? null:Long.valueOf(goodsGroupId),
                serialType,description,!StringUtils.validString(inPrice) ? null:Double.valueOf(inPrice),!StringUtils.validString(inPrice) ? null:Double.valueOf(inPrice),brand
                );
    }
}
