package com.wms.services.impl;

import com.wms.dataprovider.StockManagementDP;
import com.wms.dto.AuthTokenInfo;
import com.wms.dto.ResponseObject;
import com.wms.dto.StockTransDTO;
import com.wms.services.interfaces.StockManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by duyot on 2/16/2017.
 */
@Service("stockManagementService")
public class StockManagementServiceImpl implements StockManagementService{
    @Autowired
    StockManagementDP stockManagementDP;


    @Override
    public ResponseObject importStock(StockTransDTO stockTrans, AuthTokenInfo tokenInfo) {
        return stockManagementDP.importStock(stockTrans,tokenInfo);
    }

    @Override
    public ResponseObject exportStock(StockTransDTO stockTrans,AuthTokenInfo tokenInfo) {
        return stockManagementDP.exportStock(stockTrans,tokenInfo);
    }
}
