package com.wms.services.impl;

import com.wms.dataprovider.UserDP;
import com.wms.dto.CatUserDTO;
import com.wms.dto.Condition;
import com.wms.dto.ResponseObject;
import com.wms.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by duyot on 10/17/2016.
 */
@Service
public class UserServiceImpl  implements UserService {

    @Autowired
    UserDP userDP;

    @Override
    public List<CatUserDTO> getAlls() {
       return userDP.getAll();
    }

    @Override
    public ResponseObject register(CatUserDTO catUserDTO) {
        return userDP.register(catUserDTO);
    }

    @Override
    public CatUserDTO login(CatUserDTO catUserDTO) {
        return userDP.login(catUserDTO);
    }

    @Override
    public List<CatUserDTO> findUserByCondition(List<Condition> lstCondition) {
        return userDP.findByCondition(lstCondition);
    }

    @Override
    public boolean update(CatUserDTO catUserDTO) {
        return userDP.update(catUserDTO);
    }

    @Override
    public boolean delelte(Long id) {
        return userDP.delete(id);
    }


}
