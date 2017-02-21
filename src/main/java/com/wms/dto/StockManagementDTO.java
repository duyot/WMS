package com.wms.dto;

import java.util.List;

/**
 * Created by duyot on 2/16/2017.
 */
public class StockManagementDTO {
    private MjrStockTransDTO mjrStockTransDTO;
    private List<MjrStockTransDetailDTO> lstGoods;

    public StockManagementDTO(List<MjrStockTransDetailDTO> lstGoods,  MjrStockTransDTO mjrStockTransDTO) {
        this.mjrStockTransDTO = mjrStockTransDTO;
        this.lstGoods = lstGoods;
    }

    public StockManagementDTO() {
    }

    public MjrStockTransDTO getMjrStockTransDTO() {
        return mjrStockTransDTO;
    }

    public void setMjrStockTransDTO(MjrStockTransDTO mjrStockTransDTO) {
        this.mjrStockTransDTO = mjrStockTransDTO;
    }

    public List<MjrStockTransDetailDTO> getLstGoods() {
        return lstGoods;
    }

    public void setLstGoods(List<MjrStockTransDetailDTO> lstGoods) {
        this.lstGoods = lstGoods;
    }
}
