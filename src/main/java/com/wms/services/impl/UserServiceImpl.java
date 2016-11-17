package com.wms.services.impl;

import com.wms.dataprovider.UserDP;
import com.wms.dto.Condition;
import com.wms.dto.ResponseObject;
import com.wms.dto.User;
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
    public List<User> getAlls() {
       return userDP.getAll();
    }

    @Override
    public ResponseObject register(User user) {
        return userDP.register(user);
    }

    @Override
    public User login(User user) {
        return userDP.login(user);
    }

    @Override
    public List<User> findUserByCondition(List<Condition> lstCondition) {
        return userDP.findByCondition(lstCondition);
    }

    @Override
    public boolean update(User user) {
        return userDP.update(user);
    }

    @Override
    public boolean delelte(Long id) {
        return userDP.delete(id);
    }


}
