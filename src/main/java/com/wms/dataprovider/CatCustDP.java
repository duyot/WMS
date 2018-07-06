package com.wms.dataprovider;

import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.CatCustomerDTO;
import com.wms.dto.CatDepartmentDTO;
import org.springframework.stereotype.Repository;

@Repository
public class CatCustDP extends BaseDP<CatCustomerDTO> {
    public CatCustDP() {
        super(CatCustomerDTO[].class, CatCustomerDTO.class,  Constants.SERVICE_PREFIX.CUSTOMER_SERVICE);
    }
}
