package com.vivas.services.interfaces;


import com.vivas.dto.Condition;
import com.vivas.dto.User;

import java.util.List;

/**
 * Created by duyot on 10/17/2016.
 */
public interface UserService {
    List<User> getAlls();

    public boolean register(User user);

    public User login(User user);

    public List<User> findUserByCondition(List<Condition> lstCondition);
}
