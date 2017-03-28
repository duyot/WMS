package com.wms.services.impl;

import com.wms.dataprovider.MjrStockGoodsDP;
import com.wms.dto.MjrStockGoodsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;

/**
 * Created by duyot on 3/24/2017.
 */
@Service("mjrStockGoodsService")
public class MjrStockGoodsServiceImpl extends BaseServiceImpl<MjrStockGoodsDTO,MjrStockGoodsDP>{
    @Autowired
    MjrStockGoodsDP mjrStockGoodsDP;

    @PostConstruct
    public void setupService(){
        this.tdp = mjrStockGoodsDP;
    }
}
