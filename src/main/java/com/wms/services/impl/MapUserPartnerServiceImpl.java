package com.wms.services.impl;

import com.wms.dataprovider.MapUserPartnerDP;
import com.wms.dataprovider.SysRoleMenuDP;
import com.wms.dto.MapUserPartnerDTO;
import com.wms.dto.SysRoleMenuDTO;
import com.wms.services.interfaces.SysRoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service("mapUserPartnerServiceImpl")
public class MapUserPartnerServiceImpl extends BaseServiceImpl<MapUserPartnerDTO,MapUserPartnerDP>  {
    @Autowired
    MapUserPartnerDP mapUserPartnerDP;

    @PostConstruct
    public void setupService(){
        this.tdp = mapUserPartnerDP;
        }
}

