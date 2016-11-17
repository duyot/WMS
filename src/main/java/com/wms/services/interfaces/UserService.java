package com.wms.services.interfaces;


import com.wms.dto.Condition;
import com.wms.dto.ResponseObject;
import com.wms.dto.User;

import java.util.List;

/**
 * Created by duyot on 10/17/2016.
 */
public interface UserService {
    List<User> getAlls();

    public ResponseObject register(User user);

    public User login(User user);

    public List<User> findUserByCondition(List<Condition> lstCondition);

    public boolean update(User user);

    public boolean delelte(Long id);
}
