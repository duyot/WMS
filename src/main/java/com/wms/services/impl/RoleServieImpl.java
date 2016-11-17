package com.wms.services.impl;

import com.wms.dataprovider.RoleDP;
import com.wms.dto.RoleDTO;
import com.wms.services.interfaces.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by duyot on 11/9/2016.
 */
@Service("roleService")
public class RoleServieImpl extends BaseServiceImpl<RoleDTO,RoleDP> {
    @Autowired
    RoleDP roleDP;

    @PostConstruct
    public void setupService(){
        this.tdp = roleDP;
    }

}
