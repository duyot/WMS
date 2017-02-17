package com.wms.dto;

import java.util.List;

/**
 * Created by duyot on 2/16/2017.
 */
public class StockManagementDTO {
    private String stockId;
    private List<MjrStockTransDetailDTO> lstGoods;

    public StockManagementDTO(List<MjrStockTransDetailDTO> lstGoods, String stockId) {
        this.stockId = stockId;
        this.lstGoods = lstGoods;
    }

    public StockManagementDTO() {
    }

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public List<MjrStockTransDetailDTO> getLstGoods() {
        return lstGoods;
    }

    public void setLstGoods(List<MjrStockTransDetailDTO> lstGoods) {
        this.lstGoods = lstGoods;
    }
}
