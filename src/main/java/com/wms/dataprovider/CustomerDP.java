package com.wms.dataprovider;

import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.CustomerDTO;
import org.springframework.stereotype.Repository;

/**
 * Created by duyot on 12/6/2016.
 */
@Repository
public class CustomerDP extends BaseDP<CustomerDTO> {
    public CustomerDP() {
        super(CustomerDTO[].class,CustomerDTO.class, Constants.SERVICE_PREFIX.CUSTOMER_SERVICE);
    }
}
