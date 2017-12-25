package com.wms.services.interfaces;

import com.wms.dto.*;

import java.util.List;

/**
 * Created by duyot on 2/16/2017.
 */
public interface StockManagementService {
    ResponseObject importStock(StockTransDTO stockTrans,AuthTokenInfo tokenInfo);
    ResponseObject exportStock(StockTransDTO stockTrans,AuthTokenInfo tokenInfo);
    List<String> getListSerialInStock(String custId, String stockId, String goodsId, String goodsState, AuthTokenInfo tokenInfo);
    ResponseObject cancelTrans(String transId, AuthTokenInfo tokenInfo);
    List<MjrStockTransDetailDTO> getTransGoodsDetail(String custId, String stockId, String transId, String transType, AuthTokenInfo tokenInfo);
    List<MjrStockTransDetailDTO> getListTransGoodsDetail(String lstStockTransId, AuthTokenInfo tokenInfo);
}
