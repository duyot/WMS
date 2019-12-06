package com.wms.services.impl;

import com.wms.dataprovider.SysRoleMenuDP;
import com.wms.dto.SysRoleMenuDTO;
import com.wms.services.interfaces.SysRoleMenuService;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("sysRoleMenuServiceImpl")
public class SysRoleMenuServiceImpl extends BaseServiceImpl<SysRoleMenuDTO, SysRoleMenuDP> implements SysRoleMenuService {
    @Autowired
    SysRoleMenuDP sysRoleMenuDP;

    @PostConstruct
    public void setupService() {
        this.tdp = sysRoleMenuDP;
    }
}