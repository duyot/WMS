package com.vivas.services.impl;

import com.vivas.dataprovider.ActionMenuDP;
import com.vivas.dto.ActionMenuDTO;
import com.vivas.services.interfaces.RoleActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by duyot on 11/3/2016.
 */
@Service
public class RoleActionServiceImpl implements RoleActionService{

    @Autowired
    ActionMenuDP actionMenuDP;

    @Override
    public List<ActionMenuDTO> getUserActionService(String roleId) {
        return actionMenuDP.getActionMenu(roleId);
    }
}
