package com.vivas.services.interfaces;

import com.vivas.dto.ActionMenuDTO;

import java.util.List;

/**
 * Created by duyot on 11/3/2016.
 */
public interface RoleActionService {
    public List<ActionMenuDTO> getUserActionService(String roleId);
}
