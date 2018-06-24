package com.wms.services.interfaces;

import com.wms.dto.AuthTokenInfo;
import com.wms.dto.CatStockDTO;

import java.util.List;

public interface StockService {
    List<CatStockDTO> getStockByUser(Long userId, AuthTokenInfo tokenInfo);
}