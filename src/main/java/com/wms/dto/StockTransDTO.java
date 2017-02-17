package com.wms.dto;

import java.util.List;

/**
 * Created by duyot on 12/29/2016.
 */
public class StockTransDTO {
    MjrStockTransDTO mjrStockTransDTO;
    List<MjrStockTransDetailDTO> lstMjrStockTransDetail;

    public StockTransDTO(MjrStockTransDTO mjrStockTransDTO, List<MjrStockTransDetailDTO> lstMjrStockTransDetail) {
        this.mjrStockTransDTO = mjrStockTransDTO;
        this.lstMjrStockTransDetail = lstMjrStockTransDetail;
    }

    public StockTransDTO() {
    }

    public MjrStockTransDTO getMjrStockTransDTO() {
        return mjrStockTransDTO;
    }

    public void setMjrStockTransDTO(MjrStockTransDTO mjrStockTransDTO) {
        this.mjrStockTransDTO = mjrStockTransDTO;
    }

    public List<MjrStockTransDetailDTO> getLstMjrStockTransDetail() {
        return lstMjrStockTransDetail;
    }

    public void setLstMjrStockTransDetail(List<MjrStockTransDetailDTO> lstMjrStockTransDetail) {
        this.lstMjrStockTransDetail = lstMjrStockTransDetail;
    }
}
