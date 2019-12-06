package com.wms.services.impl;

import com.wms.dataprovider.MapUserStockDP;
import com.wms.dto.MapUserStockDTO;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("mapUserStockServiceImpl")
public class MapUserStockServiceImpl extends BaseServiceImpl<MapUserStockDTO, MapUserStockDP> {
    @Autowired
    MapUserStockDP mapUserStockDP;

    @PostConstruct
    public void setupService() {
        this.tdp = mapUserStockDP;
    }
}