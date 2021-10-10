package com.wms.dto;

import java.util.List;

/**
 * Created by duyot on 4/27/2017.
 */
public class ListStockCellDTO {
    private List<CatStockCellDTO> lstStockCell;

    public ListStockCellDTO(List<CatStockCellDTO> lstStockCell) {
        this.lstStockCell = lstStockCell;
    }

    public ListStockCellDTO() {
    }

    public List<CatStockCellDTO> getLstStockCell() {
        return lstStockCell;
    }

    public void setLstStockCell(List<CatStockCellDTO> lstStockCell) {
        this.lstStockCell = lstStockCell;
    }
}
