package com.wms.utils;

import com.wms.dto.MjrStockTransDetailDTO;

import java.util.Comparator;

public class StockGoodsComparator implements Comparator<MjrStockTransDetailDTO> {
    public int compare(MjrStockTransDetailDTO a, MjrStockTransDetailDTO b) {
        int stockId1 = Integer.parseInt(a.getStockId());
        int stockId2 = Integer.parseInt(b.getStockId());
        if (stockId1 != stockId2) {
            return stockId1 > stockId2 ? 1:-1;
        }

        int goodsId1 = Integer.parseInt(a.getGoodsId());
        int goodsId2 = Integer.parseInt(b.getGoodsId());
        return goodsId1 >= goodsId2 ? 1:-1;
    }
}
