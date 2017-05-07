package com.wms.services.impl;

import com.wms.dataprovider.CatStockCellDP;
import com.wms.dto.CatStockCellDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by duyot on 4/19/2017.
 */
@Service("catStockCellService")
public class CatStockCellServiceImpl extends BaseServiceImpl<CatStockCellDTO,CatStockCellDP>{
    @Autowired
    CatStockCellDP CatStockCellDP;

    @PostConstruct
    public void setupService(){
        this.tdp = CatStockCellDP;
    }
}
