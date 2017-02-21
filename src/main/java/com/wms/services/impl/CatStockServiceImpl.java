package com.wms.services.impl;

import com.wms.dataprovider.CatStockDP;
import com.wms.dto.CatStockDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by duyot on 2/17/2017.
 */
@Service("catStockService")
public class CatStockServiceImpl extends BaseServiceImpl<CatStockDTO,CatStockDP>{
    @Autowired
    CatStockDP catStockDP;

    @PostConstruct
    public void setupService(){
        this.tdp = catStockDP;
    }
}