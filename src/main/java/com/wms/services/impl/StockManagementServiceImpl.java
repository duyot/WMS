package com.wms.services.impl;

import com.wms.dataprovider.StockManagementDP;
import com.wms.dto.AuthTokenInfo;
import com.wms.dto.ResponseObject;
import com.wms.dto.StockTransDTO;
import com.wms.services.interfaces.StockManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by duyot on 2/16/2017.
 */
@Service("stockManagementService")
public class StockManagementServiceImpl implements StockManagementService{
    @Autowired
    StockManagementDP stockManagementDP;


    public ResponseObject importStock(StockTransDTO stockTrans, AuthTokenInfo tokenInfo) {
        return stockManagementDP.importStock(stockTrans,tokenInfo);
    }

    public ResponseObject exportStock(StockTransDTO stockTrans,AuthTokenInfo tokenInfo) {
        return stockManagementDP.exportStock(stockTrans,tokenInfo);

    }

    @Override
    public List<String> getListSerialInStock(String custId, String stockId, String goodsId, String goodsState,AuthTokenInfo tokenInfo){
        return stockManagementDP.getListSerialInStock(custId,stockId,goodsId,goodsState,tokenInfo);
    }
}
