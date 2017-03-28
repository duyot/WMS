package com.wms.services.impl;

import com.wms.dataprovider.MjrStockGoodsDP;
import com.wms.dataprovider.MjrStockGoodsTotalDP;
import com.wms.dto.MjrStockGoodsDTO;
import com.wms.dto.MjrStockGoodsTotalDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by duyot on 3/24/2017.
 */
@Service("mjrStockGoodsTotalService")
public class MjrStockGoodsTotalServiceImpl extends BaseServiceImpl<MjrStockGoodsTotalDTO,MjrStockGoodsTotalDP>{
    @Autowired
    MjrStockGoodsTotalDP mjrStockGoodsTotalDP;

    @PostConstruct
    public void setupService(){
        this.tdp = mjrStockGoodsTotalDP;
    }
}
