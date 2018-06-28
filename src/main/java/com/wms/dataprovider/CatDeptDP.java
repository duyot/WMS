package com.wms.dataprovider;

import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.CatDepartmentDTO;
import com.wms.dto.SysRoleDTO;
import org.springframework.stereotype.Repository;

@Repository
public class CatDeptDP extends BaseDP<CatDepartmentDTO> {
    public CatDeptDP() {
        super(CatDepartmentDTO[].class, CatDepartmentDTO.class,  Constants.SERVICE_PREFIX.CAT_DEPARTMENT_SERVICE);
    }
}
