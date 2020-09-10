package com.wms.dataprovider;

import com.google.common.collect.Lists;
import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.RevenueDTO;

import java.util.ArrayList;
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
    private final String GET_SUM_REVENUE = "getSumRevenue";

    Logger log = LoggerFactory.getLogger(BaseDP.class);

    public RevenueDP() {
        super(RevenueDTO[].class, RevenueDTO.class, Constants.SERVICE_PREFIX.REVENUE_SERVICE);
    }

    public List<RevenueDTO> getSumRevenue(String custId, String partnerId, String startDate, String endDate) {

        String query = "custId=" + custId + "&partnerId=" + partnerId + "&startDate=" + startDate + "&endDate=" + endDate;
        String url = getUrlLoadBalancingQuery(query, GET_SUM_REVENUE);
        try {
            ResponseEntity<RevenueDTO[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, RevenueDTO[].class);
            return Arrays.asList(responseEntity.getBody());
        } catch (RestClientException e) {
            log.error(e.toString());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
