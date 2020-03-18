package com.wms.dto;

import java.util.List;

/**
 * Created by duyot on 2/23/2017.
 */
public class ImportFileResultDTO {
    private boolean isValid;
    private List<MjrStockTransDetailDTO> lstGoodsImport;
    private List<CatGoodsDTO> lstGoods;
    private String serial;
    private List<String> lstSerial;

    public ImportFileResultDTO(boolean isValid, List<MjrStockTransDetailDTO> lstGoodsImport) {
        this.isValid = isValid;
        this.lstGoodsImport = lstGoodsImport;
    }

    public List<CatGoodsDTO> getLstGoods() {
        return lstGoods;
    }

    public void setLstGoods(List<CatGoodsDTO> lstGoods) {
        this.lstGoods = lstGoods;
    }

    public ImportFileResultDTO() {
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public List<MjrStockTransDetailDTO> getLstGoodsImport() {
        return lstGoodsImport;
    }

    public void setLstGoodsImport(List<MjrStockTransDetailDTO> lstGoodsImport) {
        this.lstGoodsImport = lstGoodsImport;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public List<String> getLstSerial() {
        return lstSerial;
    }

    public void setLstSerial(List<String> lstSerial) {
        this.lstSerial = lstSerial;
    }
}
