package com.wms.services.impl;

import com.wms.dataprovider.CatDeptDP;
import com.wms.dto.CatDepartmentDTO;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("catDeptServicesImpl")
public class CatDeptServicesImpl extends BaseServiceImpl<CatDepartmentDTO, CatDeptDP> {
    @Autowired
    CatDeptDP catDeptDP;

    @PostConstruct
    public void setupService() {
        this.tdp = catDeptDP;
    }
}
