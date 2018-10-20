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


    public ResponseObject importStock(StockTransDTO stockTrans ) {
        return stockManagementDP.importStock(stockTrans);
    }

    public ResponseObject exportStock(StockTransDTO stockTrans ) {
        return stockManagementDP.exportStock(stockTrans);

    }

    public List<String> getListSerialInStock(String custId, String stockId, String goodsId, String goodsState ){
        return stockManagementDP.getListSerialInStock(custId,stockId,goodsId,goodsState);
    }

    public ResponseObject cancelTrans(String transId ){
        return stockManagementDP.cancelTrans(transId);
    }

    @Override
    public List<MjrStockTransDetailDTO> getTransGoodsDetail(String custId, String stockId, String transId, String transType ) {
        return stockManagementDP.getTransGoodsDetail(custId,stockId,transId,transType);
    }
    @Override
    public List<MjrStockTransDetailDTO> getListTransGoodsDetail(String transId ) {
        return stockManagementDP.getListTransGoodsDetail(transId);
    }
    public List<MjrStockTransDTO> getStockTransInfo(String lstStockTransId ) {
        return stockManagementDP.getStockTransInfo(lstStockTransId);
    }
}
