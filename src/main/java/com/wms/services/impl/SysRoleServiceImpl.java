package com.wms.services.impl;

import com.wms.dataprovider.SysMenuDP;
import com.wms.dataprovider.SysRoleDP;
import com.wms.dto.SysRoleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service("roleServiceImpl")
public class SysRoleServiceImpl  extends BaseServiceImpl<SysRoleDTO,SysRoleDP> {
    @Autowired
    SysRoleDP sysRoleDP;

    @PostConstruct
    public void setupService(){
        this.tdp = sysRoleDP;
    }
}
