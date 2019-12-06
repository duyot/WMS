package com.wms.services.impl;

import com.wms.dataprovider.CatCustDP;
import com.wms.dto.CatCustomerDTO;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("catCustServicesImpl")
public class CatCustServicesImpl extends BaseServiceImpl<CatCustomerDTO, CatCustDP> {
    @Autowired
    CatCustDP catCustDP;

    @PostConstruct
    public void setupService() {
        this.tdp = catCustDP;
    }
}
