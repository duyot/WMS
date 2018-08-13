package com.wms.services.impl;

import com.wms.dataprovider.CatStockDP;
import com.wms.dto.CatStockDTO;
import com.wms.services.interfaces.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by duyot on 2/17/2017.
 */
@Service("catStockService")
public class CatStockServiceImpl extends BaseServiceImpl<CatStockDTO,CatStockDP> implements StockService {
    @Autowired
    CatStockDP catStockDP;

    @PostConstruct
    public void setupService(){
        this.tdp = catStockDP;
    }

    @Override
    public List<CatStockDTO> getStockByUser(Long userId ) {
        return catStockDP.getStockByUser(userId);
    }
}
