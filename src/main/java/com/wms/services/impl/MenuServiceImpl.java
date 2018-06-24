package com.wms.services.impl;

import com.wms.dataprovider.SysMenuDP;
import com.wms.dto.SysMenuDTO;
import com.wms.services.interfaces.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service("menuService")
public class MenuServiceImpl extends BaseServiceImpl<SysMenuDTO,SysMenuDP> implements MenuService{

    @Autowired
    SysMenuDP sysMenuDP;

    @PostConstruct
    public void setupService(){
        this.tdp = sysMenuDP;
    }
}
