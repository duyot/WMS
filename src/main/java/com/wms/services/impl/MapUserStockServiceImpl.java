package com.wms.services.impl;

import com.wms.dataprovider.MapUserStockDP;
import com.wms.dataprovider.SysRoleMenuDP;
import com.wms.dto.MapUserStockDTO;
import com.wms.dto.SysRoleMenuDTO;
import com.wms.services.interfaces.SysRoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service("mapUserStockServiceImpl")
public class MapUserStockServiceImpl extends BaseServiceImpl<MapUserStockDTO,MapUserStockDP>  {
    @Autowired
    MapUserStockDP mapUserStockDP;

    @PostConstruct
    public void setupService(){
        this.tdp = mapUserStockDP;
        }
}