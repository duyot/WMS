package com.wms.dto;

public class InventoryInfoDTO {
    private String numOfGoodsUnderLimit;
    private String numOfGoodsLackWithOrder;

    public String getNumOfGoodsUnderLimit() {
        return numOfGoodsUnderLimit;
    }

    public void setNumOfGoodsUnderLimit(String numOfGoodsUnderLimit) {
        this.numOfGoodsUnderLimit = numOfGoodsUnderLimit;
    }

    public String getNumOfGoodsLackWithOrder() {
        return numOfGoodsLackWithOrder;
    }

    public void setNumOfGoodsLackWithOrder(String numOfGoodsLackWithOrder) {
        this.numOfGoodsLackWithOrder = numOfGoodsLackWithOrder;
    }
}
