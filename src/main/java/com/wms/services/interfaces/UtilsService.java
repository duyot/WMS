package com.wms.services.interfaces;

import com.wms.dto.AuthTokenInfo;
import com.wms.dto.MjrStockGoodsTotalDTO;
import com.wms.dto.MjrStockTransDetailDTO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Created by duyot on 7/5/2017.
 */
public interface UtilsService {
    Long getCountGoodsDetail(String custId,String stockId, String goodsId,String isSerial,String goodsState,String partnerId,AuthTokenInfo tokenInfo);
    //co bo sung partnerId
    List<MjrStockTransDetailDTO> getGoodsDetail(String custId, String stockId, String goodsId, String isSerial, String goodsState,String partnerId,String limit,String offset,AuthTokenInfo tokenInfo);
    List<MjrStockGoodsTotalDTO> findMoreCondition(MjrStockGoodsTotalDTO searchGoodsTotalDTO, AuthTokenInfo tokenInfo);

}
