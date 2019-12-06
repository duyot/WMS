package com.wms.services.impl;

import com.wms.dataprovider.MapUserPartnerDP;
import com.wms.dto.MapUserPartnerDTO;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("mapUserPartnerServiceImpl")
public class MapUserPartnerServiceImpl extends BaseServiceImpl<MapUserPartnerDTO, MapUserPartnerDP> {
    @Autowired
    MapUserPartnerDP mapUserPartnerDP;

    @PostConstruct
    public void setupService() {
        this.tdp = mapUserPartnerDP;
    }
}

