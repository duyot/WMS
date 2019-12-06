package com.wms.dto;


public class MapUserStockDTO {

    String id;
    String userId;
    String stockId;

    public MapUserStockDTO(String id, String userId, String stockId) {
        this.id = id;
        this.userId = userId;
        this.stockId = stockId;
    }

    public MapUserStockDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

}
