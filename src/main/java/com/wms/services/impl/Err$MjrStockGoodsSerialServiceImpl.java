package com.wms.services.impl;

import com.wms.dataprovider.CatGoodsGroupDP;
import com.wms.dataprovider.Err$MjrStockGoodsSerialDP;
import com.wms.dto.CatGoodsGroupDTO;
import com.wms.dto.Err$MjrStockGoodsSerialDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by duyot on 3/7/2017.
 */
@Service("err$MjrStockGoodsSerialService")
public class Err$MjrStockGoodsSerialServiceImpl extends BaseServiceImpl<Err$MjrStockGoodsSerialDTO,Err$MjrStockGoodsSerialDP>{
    @Autowired
    Err$MjrStockGoodsSerialDP err$MjrStockGoodsSerialDP;

    @PostConstruct
    public void setupService(){
        this.tdp = err$MjrStockGoodsSerialDP;
    }
}
