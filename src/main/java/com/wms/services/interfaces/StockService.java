package com.wms.services.interfaces;

import com.wms.dto.CatPartnerDTO;
import com.wms.dto.CatStockDTO;

import java.util.List;

public interface StockService extends BaseService<CatStockDTO>{
    List<CatStockDTO> getStockByUser(Long userId );
    List<CatStockDTO> getStockByUser(Long userId, Long stockPermission );
}
