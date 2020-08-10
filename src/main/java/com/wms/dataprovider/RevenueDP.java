package com.wms.dataprovider;

import com.google.common.collect.Lists;
import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.RevenueDTO;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Created by duyot on 2/17/2017.
 */
@Repository
public class RevenueDP extends BaseDP<RevenueDTO> {
    Logger log = LoggerFactory.getLogger(BaseDP.class);

    public RevenueDP() {
        super(RevenueDTO[].class, RevenueDTO.class, Constants.SERVICE_PREFIX.REVENUE_SERVICE);
    }

}
