package com.wms.services.interfaces;

import com.wms.dto.ResponseObject;
import com.wms.dto.StockTransDTO;

/**
 * Created by duyot on 2/16/2017.
 */
public interface StockManagementService {
    ResponseObject importStock(StockTransDTO stockTrans);
    ResponseObject exportStock(StockTransDTO stockTrans);
}
