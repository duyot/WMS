package com.wms.services.impl;

import com.wms.dataprovider.CatDeptDP;
import com.wms.dataprovider.CatGoodsGroupDP;
import com.wms.dto.CatDepartmentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service("catDeptServicesImpl")
public class CatDeptServicesImpl extends BaseServiceImpl<CatDepartmentDTO,CatDeptDP> {
    @Autowired
    CatDeptDP catDeptDP;

    @PostConstruct
    public void setupService(){
        this.tdp = catDeptDP;
    }
}
