package com.wms.services.impl;

import com.wms.dataprovider.RoleDP;
import com.wms.dto.RoleDTO;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by duyot on 11/9/2016.
 */
@Service("roleService")
public class RoleServiceImpl extends BaseServiceImpl<RoleDTO, RoleDP> {
    @Autowired
    RoleDP roleDP;

    @PostConstruct
    public void setupService() {
        this.tdp = roleDP;
    }

}
