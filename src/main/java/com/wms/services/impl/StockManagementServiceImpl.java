package com.wms.services.impl;

import com.wms.dataprovider.StockManagementDP;
import com.wms.dto.*;
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

    public List<String> getListSerialInStock(String custId, String stockId, String goodsId, String goodsState,AuthTokenInfo tokenInfo){
        return stockManagementDP.getListSerialInStock(custId,stockId,goodsId,goodsState,tokenInfo);
    }

    public ResponseObject cancelTrans(String transId, AuthTokenInfo tokenInfo){
        return stockManagementDP.cancelTrans(transId,tokenInfo);
    }

    @Override
    public List<MjrStockTransDetailDTO> getTransGoodsDetail(String custId, String stockId, String transId, String transType, AuthTokenInfo tokenInfo) {
        return stockManagementDP.getTransGoodsDetail(custId,stockId,transId,transType,tokenInfo);
    }
    @Override
    public List<MjrStockTransDetailDTO> getListTransGoodsDetail(String lstStockTransId, AuthTokenInfo tokenInfo) {
        return stockManagementDP.getListTransGoodsDetail(lstStockTransId,tokenInfo);
    }
    public List<MjrStockTransDTO> getStockTransInfo(String lstStockTransId, AuthTokenInfo tokenInfo) {
        return stockManagementDP.getStockTransInfo(lstStockTransId,tokenInfo);
    }
}
