package com.wms.dto;

import java.util.List;

/**
 * Created by duyot on 2/23/2017.
 */
public class ImportFileResultDTO {
    private boolean isValid;
    private List<MjrStockTransDetailDTO> lstGoodsImport;

    public ImportFileResultDTO(boolean isValid, List<MjrStockTransDetailDTO> lstGoodsImport) {
        this.isValid = isValid;
        this.lstGoodsImport = lstGoodsImport;
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
}
