package com.wms.services.impl;

import com.wms.dataprovider.CatUserDP;
import com.wms.dto.*;
import com.wms.services.interfaces.CatUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by duyot on 10/17/2016.
 */
@Service("catUserServices")
public class CatUserServiceImpl extends BaseServiceImpl<CatUserDTO,CatUserDP> implements CatUserService {

    @Autowired
    CatUserDP catUserDP;

    @PostConstruct
    public void setupService(){
        this.tdp = catUserDP;
    }

    @Override
    public ResponseObject register(CatUserDTO catUserDTO) {
        return catUserDP.register(catUserDTO);
    }

    @Override
    public ResponseObject updateUser(CatUserDTO catUserDTO ) {
        return catUserDP.updateUser(catUserDTO);
    }

    @Override
    public ResponseObject updateCustomer(CatCustomerDTO catCustomerDTO ) {
        return catUserDP.updateCustomer(catCustomerDTO);
    }

    @Override
    public CatUserDTO login(CatUserDTO catUserDTO) {
        return catUserDP.login(catUserDTO);
    }

    @Override
    public List<CatCustomerDTO> getCustomer(String userId ) {
        return catUserDP.getCustomer(userId);
    }

    @Override
    public List<CatUserDTO> getUserByCustomer(String custId) {
        return catUserDP.getUserByCustomer(custId);
    }
}
