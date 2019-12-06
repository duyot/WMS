package com.wms.dto;

/**
 * Created by duyot on 4/19/2017.
 */
public class CatStockCellDTO {
    private String id;
    private String code;
    private String stockId;

    public CatStockCellDTO(String id, String code, String stockId) {
        this.id = id;
        this.code = code;
        this.stockId = stockId;
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

}
