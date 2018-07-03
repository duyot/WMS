package com.wms.services.impl;

import com.wms.dataprovider.MjrStockGoodsDP;
import com.wms.dataprovider.MjrStockGoodsTotalDP;
import com.wms.dto.AuthTokenInfo;
import com.wms.dto.MjrStockGoodsDTO;
import com.wms.dto.MjrStockGoodsTotalDTO;
import com.wms.dto.MjrStockTransDetailDTO;
import com.wms.services.interfaces.UtilsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by duyot on 3/24/2017.
 */
@Service("mjrStockGoodsTotalService")
public class MjrStockGoodsTotalServiceImpl extends BaseServiceImpl<MjrStockGoodsTotalDTO,MjrStockGoodsTotalDP> implements UtilsService{
    @Autowired
    MjrStockGoodsTotalDP mjrStockGoodsTotalDP;

    @PostConstruct
    public void setupService(){
        this.tdp = mjrStockGoodsTotalDP;
    }

    @Override
    public Long getCountGoodsDetail(String custId, String stockId, String goodsId, String isSerial, String goodsState,AuthTokenInfo tokenInfo) {
        return mjrStockGoodsTotalDP.getCountGoodsDetail(custId,stockId,goodsId,isSerial,goodsState,tokenInfo);
    }

    @Override
    public List<MjrStockTransDetailDTO> getGoodsDetail(String custId, String stockId, String goodsId,
                                                            String isSerial, String goodsState,String partnerId, String limit, String offset,AuthTokenInfo tokenInfo) {
        return mjrStockGoodsTotalDP.getGoodsDetail(custId,stockId,goodsId,isSerial,goodsState,partnerId,limit,offset,tokenInfo);
    }
    @Override
    public List<MjrStockGoodsTotalDTO> findMoreCondition(MjrStockGoodsTotalDTO searchGoodsTotalDTO,AuthTokenInfo tokenInfo) {
        return mjrStockGoodsTotalDP.findMoreCondition(searchGoodsTotalDTO,tokenInfo);
    }
}
