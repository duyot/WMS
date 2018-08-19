package com.wms.services.interfaces;

import com.wms.dto.ActionMenuDTO;

import java.util.List;

/**
 * Created by duyot on 11/3/2016.
 */
public interface RoleActionService {
    List<ActionMenuDTO> getUserActionService(String roleCode, String cusId );
}
