package com.wms.dto;

import java.util.List;

/**
 * Created by duyot on 6/8/2017.
 */
public class ServerPagingDTO {
    private Long total;
    private List<MjrStockTransDetailDTO> rows;

    public ServerPagingDTO(Long total, List<MjrStockTransDetailDTO> rows) {
        this.total = total;
        this.rows = rows;
    }

    public ServerPagingDTO() {
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<MjrStockTransDetailDTO> getRows() {
        return rows;
    }

    public void setRows(List<MjrStockTransDetailDTO> rows) {
        this.rows = rows;
    }
}
