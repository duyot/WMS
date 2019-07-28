package com.wms.services.interfaces;

import com.wms.dto.MjrStockGoodsTotalDTO;
import com.wms.dto.MjrStockTransDetailDTO;

import java.util.List;

/**
 * Created by duyot on 7/5/2017.
 */
public interface UtilsService {
    Long getCountGoodsDetail(String custId,String stockId, String goodsId,String isSerial,String goodsState,String partnerId );
    //co bo sung partnerId
    List<MjrStockTransDetailDTO> getGoodsDetail(String custId, String stockId, String goodsId, String isSerial, String goodsState,String partnerId,String limit,String offset );
    List<MjrStockGoodsTotalDTO> findMoreCondition(MjrStockGoodsTotalDTO searchGoodsTotalDTO );
    List<MjrStockTransDetailDTO> getAllStockGoodsDetail(String userId, String custId, String stockId, String partnerId, String goodsId, String status);
}
