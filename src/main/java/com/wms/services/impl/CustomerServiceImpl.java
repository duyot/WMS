package com.wms.services.impl;

import com.wms.dataprovider.CustomerDP;
import com.wms.dto.CatCustomerDTO;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by duyot on 12/6/2016.
 */
@Service("customerService")
public class CustomerServiceImpl extends BaseServiceImpl<CatCustomerDTO, CustomerDP> {
    @Autowired
    CustomerDP customerDP;

    @PostConstruct
    public void setupService() {
        this.tdp = customerDP;
    }
}
