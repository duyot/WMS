package com.wms.services.impl;

import com.wms.dataprovider.ActionMenuDP;
import com.wms.dto.ActionMenuDTO;
import com.wms.services.interfaces.RoleActionService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by duyot on 11/3/2016.
 */
@Service
public class RoleActionServiceImpl implements RoleActionService {

    @Autowired
    ActionMenuDP actionMenuDP;

    @Override
    public List<ActionMenuDTO> getUserActionService(String roleId, String cusId) {
        return actionMenuDP.getActionMenu(roleId, cusId);
    }
}
