package com.wms.services.impl;

import com.wms.dataprovider.CustomerDP;
import com.wms.dto.CustomerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by duyot on 12/6/2016.
 */
@Service("customerService")
public class CustomerServiceImpl extends BaseServiceImpl<CustomerDTO,CustomerDP> {
    @Autowired
    CustomerDP customerDP;

    @PostConstruct
    public void setupService(){
        this.tdp = customerDP;
    }
}
