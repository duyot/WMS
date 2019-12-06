package com.wms.dataprovider;

import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.SysRoleMenuDTO;
import org.springframework.stereotype.Repository;

@Repository
public class SysRoleMenuDP extends BaseDP<SysRoleMenuDTO> {
    public SysRoleMenuDP() {
        super(SysRoleMenuDTO[].class, SysRoleMenuDTO.class, Constants.SERVICE_PREFIX.SYS_ROLE_MENU_SERVICE);
    }
}
