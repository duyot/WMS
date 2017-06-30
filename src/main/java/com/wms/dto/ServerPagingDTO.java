package com.wms.dto;

import java.util.List;

/**
 * Created by duyot on 6/8/2017.
 */
public class ServerPagingDTO {
        private int total;
        private List<MjrStockGoodsTotalDTO> rows;

    public ServerPagingDTO(int total, List<MjrStockGoodsTotalDTO> rows) {
        this.total = total;
        this.rows = rows;
    }

    public ServerPagingDTO() {
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<MjrStockGoodsTotalDTO> getRows() {
        return rows;
    }

    public void setRows(List<MjrStockGoodsTotalDTO> rows) {
        this.rows = rows;
    }
}
