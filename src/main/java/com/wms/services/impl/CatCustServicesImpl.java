package com.wms.services.impl;

import com.wms.dataprovider.CatCustDP;
import com.wms.dataprovider.CatDeptDP;
import com.wms.dto.CatCustomerDTO;
import com.wms.dto.CatDepartmentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service("catCustServicesImpl")
public class CatCustServicesImpl extends BaseServiceImpl<CatCustomerDTO,CatCustDP> {
    @Autowired
    CatCustDP catCustDP;

    @PostConstruct
    public void setupService(){
        this.tdp = catCustDP;
    }
}
