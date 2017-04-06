package com.wms.services.impl;

import com.wms.dataprovider.MjrStockTransDP;
import com.wms.dto.MjrStockTransDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;

/**
 * Created by duyot on 4/4/2017.
 */
@Service("mjrStockTransService")
public class MjrStockTransServiceImpl extends BaseServiceImpl<MjrStockTransDTO,MjrStockTransDP>{
    @Autowired
    MjrStockTransDP mjrStockTransDP;

    @PostConstruct
    public void setupService(){
        this.tdp = mjrStockTransDP;
    }
}
