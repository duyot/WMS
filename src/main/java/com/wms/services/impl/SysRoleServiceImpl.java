package com.wms.services.impl;

import com.wms.dataprovider.SysRoleDP;
import com.wms.dto.SysRoleDTO;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("roleServiceImpl")
public class SysRoleServiceImpl extends BaseServiceImpl<SysRoleDTO, SysRoleDP> {
    @Autowired
    SysRoleDP sysRoleDP;

    @PostConstruct
    public void setupService() {
        this.tdp = sysRoleDP;
    }
}
