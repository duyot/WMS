package com.wms.services.interfaces;

import com.wms.dto.AuthTokenInfo;
import com.wms.dto.ResponseObject;
import com.wms.dto.StockTransDTO;

/**
 * Created by duyot on 2/16/2017.
 */
public interface StockManagementService {
    ResponseObject importStock(StockTransDTO stockTrans,AuthTokenInfo tokenInfo);
    ResponseObject exportStock(StockTransDTO stockTrans,AuthTokenInfo tokenInfo);
}
