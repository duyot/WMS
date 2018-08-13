package com.wms.services.interfaces;

import com.wms.dto.*;

import java.util.List;

/**
 * Created by duyot on 2/16/2017.
 */
public interface StockManagementService {
    ResponseObject importStock(StockTransDTO stockTrans);
    ResponseObject exportStock(StockTransDTO stockTrans );
    List<String> getListSerialInStock(String custId, String stockId, String goodsId, String goodsState );
    ResponseObject cancelTrans(String transId );
    List<MjrStockTransDetailDTO> getTransGoodsDetail(String custId, String stockId, String transId, String transType );
    List<MjrStockTransDetailDTO> getListTransGoodsDetail(String lstStockTransId );
    List<MjrStockTransDTO> getStockTransInfo(String lstStockTransId );
}
