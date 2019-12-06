package com.wms.dataprovider;

import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.SysRoleDTO;
import org.springframework.stereotype.Repository;

@Repository
public class SysRoleDP extends BaseDP<SysRoleDTO> {

    public SysRoleDP() {
        super(SysRoleDTO[].class, SysRoleDTO.class, Constants.SERVICE_PREFIX.ROLE_SERVICE);
    }
}
