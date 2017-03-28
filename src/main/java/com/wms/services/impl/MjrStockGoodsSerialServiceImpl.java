package com.wms.services.impl;

import com.wms.dataprovider.MjrStockGoodsSerialDP;
import com.wms.dto.MjrStockGoodsSerialDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;

/**
 * Created by duyot on 3/24/2017.
 */
@Service("mjrStockGoodsSerialService")
public class MjrStockGoodsSerialServiceImpl extends BaseServiceImpl<MjrStockGoodsSerialDTO,MjrStockGoodsSerialDP>{
    @Autowired
    MjrStockGoodsSerialDP mjrStockGoodsSerialDP;

    @PostConstruct
    public void setupService(){
        this.tdp = mjrStockGoodsSerialDP;
    }
}
