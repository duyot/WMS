package com.wms.services.interfaces;

import com.wms.dto.AuthTokenInfo;
import com.wms.dto.CatGoodsDTO;
import com.wms.dto.ChartDTO;

import java.util.List;
import java.util.Map;

/**
 * Created by duyot on 5/18/2017.
 */
public interface StatisticService {
    List<ChartDTO> getRevenue(String custId, String type, AuthTokenInfo tokenInfo);
    List<ChartDTO> getTopGoods(String custId, String type, AuthTokenInfo tokenInfo, Map<String,CatGoodsDTO> mapGoods);
}