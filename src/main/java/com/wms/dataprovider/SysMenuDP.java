package com.wms.dataprovider;

import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.SysMenuDTO;
import org.springframework.stereotype.Repository;

@Repository
public class SysMenuDP extends BaseDP<SysMenuDTO> {

    public SysMenuDP() {
        super(SysMenuDTO[].class, SysMenuDTO.class, Constants.SERVICE_PREFIX.SYS_MENU_SERVICE);
    }
}

