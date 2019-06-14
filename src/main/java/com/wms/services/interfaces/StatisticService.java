package com.wms.services.interfaces;

import com.wms.dto.CatGoodsDTO;
import com.wms.dto.CatStockDTO;
import com.wms.dto.ChartDTO;

import java.util.List;
import java.util.Map;

/**
 * Created by duyot on 5/18/2017.
 */
public interface StatisticService {
    List<ChartDTO> getRevenue(String custId, String type);
    List<ChartDTO> getTopGoods(String custId, String type , Map<String,CatGoodsDTO> mapGoods);
    List<ChartDTO> getKPIStorage(String custId, String type, Map<String,CatStockDTO> mapStock);
    List<ChartDTO> getTransaction(String custId, String type , String userId);
}
