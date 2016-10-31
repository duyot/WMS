package com.vivas.services.impl;

import com.google.common.collect.Lists;
import com.vivas.dataprovider.UserDP;
import com.vivas.dto.Condition;
import com.vivas.dto.User;
import com.vivas.services.interfaces.UserService;
import org.apache.catalina.mbeans.MemoryUserDatabaseMBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by duyot on 10/17/2016.
 */
@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserDP userDP;

    @Override
    public List<User> getAlls() {
       return userDP.getAll();
    }

    @Override
    public boolean register(User user) {
        return userDP.add(user);
    }

    @Override
    public User login(User user) {
        return userDP.login(user);
    }

    @Override
    public List<User> findUserByCondition(List<Condition> lstCondition) {
        return userDP.findByCondition(lstCondition);
    }


}
