package com.wms.dataprovider;

import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.RoleDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * Created by duyot on 11/9/2016.
 */
@Repository
public class RoleDP extends BaseDP<RoleDTO>{
    Logger log = LoggerFactory.getLogger(RoleDP.class);

    public RoleDP() {
        super(RoleDTO[].class,RoleDTO.class, Constants.SERVICE_PREFIX.ROLE_SERVICE);
    }

}
