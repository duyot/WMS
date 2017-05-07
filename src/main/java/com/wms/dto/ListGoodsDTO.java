package com.wms.dto;

import java.util.List;

/**
 * Created by duyot on 4/27/2017.
 */
public class ListGoodsDTO {
    private List<CatGoodsDTO> lstGoods;

    public ListGoodsDTO(List<CatGoodsDTO> lstGoods) {
        this.lstGoods = lstGoods;
    }

    public ListGoodsDTO() {
    }

    public List<CatGoodsDTO> getLstGoods() {
        return lstGoods;
    }

    public void setLstGoods(List<CatGoodsDTO> lstGoods) {
        this.lstGoods = lstGoods;
    }
}
