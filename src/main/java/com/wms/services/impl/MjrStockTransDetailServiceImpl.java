package com.wms.services.impl;
import com.wms.dataprovider.MjrStockTransDetailDP;
import com.wms.dto.MjrStockTransDTO;
import com.wms.dto.MjrStockTransDetailDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;

@Service("mjrStockTransDetailService")
public class MjrStockTransDetailServiceImpl extends BaseServiceImpl<MjrStockTransDetailDTO,MjrStockTransDetailDP>{
    @Autowired
    MjrStockTransDetailDP mjrStockTransDetailDP;

    @PostConstruct
    public void setupService(){
        this.tdp = mjrStockTransDetailDP;
    }
}
