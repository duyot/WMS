package com.wms.dataprovider;

import com.google.common.collect.Lists;
import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.RoleDTO;
import com.wms.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;

import java.util.Arrays;
import java.util.List;

/**
 * Created by duyot on 11/9/2016.
 */
@Repository
public class RoleDP extends BaseDP<RoleDTO>{
    Logger log = LoggerFactory.getLogger(RoleDP.class);

    public RoleDP() {
        super(RoleDTO[].class, Constants.SERVICE_PREFIX.ROLE_SERVICE);
    }

}
