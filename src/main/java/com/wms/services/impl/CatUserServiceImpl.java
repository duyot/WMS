package com.wms.services.impl;

import com.wms.dataprovider.CatUserDP;
import com.wms.dto.*;
import com.wms.services.interfaces.CatUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by duyot on 10/17/2016.
 */
@Service("catUserServices")
public class CatUserServiceImpl extends BaseServiceImpl<CatUserDTO,CatUserDP> implements CatUserService {

    @Autowired
    CatUserDP catUserDP;


    @Override
    public ResponseObject register(CatUserDTO catUserDTO,AuthTokenInfo tokenInfo) {
        return catUserDP.register(catUserDTO);
    }


    @Override
    public CatUserDTO login(CatUserDTO catUserDTO) {
        return catUserDP.login(catUserDTO);
    }

    @Override
    public List<CatCustomerDTO> getCustomer(String userId, AuthTokenInfo tokenInfo) {
        return catUserDP.getCustomer(userId,tokenInfo);
    }
}
