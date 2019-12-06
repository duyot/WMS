package com.wms.services.impl;

import com.wms.dataprovider.MjrStockTransDetailDP;
import com.wms.dto.MjrStockTransDetailDTO;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("mjrStockTransDetailService")
public class MjrStockTransDetailServiceImpl extends BaseServiceImpl<MjrStockTransDetailDTO, MjrStockTransDetailDP> {
    @Autowired
    MjrStockTransDetailDP mjrStockTransDetailDP;

    @PostConstruct
    public void setupService() {
        this.tdp = mjrStockTransDetailDP;
    }
}
