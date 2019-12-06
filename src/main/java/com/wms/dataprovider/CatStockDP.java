package com.wms.dataprovider;

import com.google.common.collect.Lists;
import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.CatStockDTO;
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
public class CatStockDP extends BaseDP<CatStockDTO> {
    private String GET_STOCK_BY_USER = "getStockByUser/";
    Logger log = LoggerFactory.getLogger(BaseDP.class);

    public CatStockDP() {
        super(CatStockDTO[].class, CatStockDTO.class, Constants.SERVICE_PREFIX.CAT_STOCK_SERVICE);
    }

    public List<CatStockDTO> getStockByUser(Long userId) {
        try {
            String findUrl = getUrlLoadBalancing(userId, GET_STOCK_BY_USER);
            ResponseEntity<CatStockDTO[]> responseEntity = restTemplate.exchange(findUrl, HttpMethod.GET, null, CatStockDTO[].class);
            return Arrays.asList(responseEntity.getBody());
        } catch (RestClientException e) {
            log.error(e.toString());
            return Lists.newArrayList();
        }
    }

    public List<CatStockDTO> getStockByUser(Long userId, Long stockPermission) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String query = "userId=" + userId + "&stockPermission=" + stockPermission;
            String url = getUrlLoadBalancingQuery(query, GET_STOCK_BY_USER);
            ResponseEntity<CatStockDTO[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, CatStockDTO[].class);
            return Arrays.asList(responseEntity.getBody());

        } catch (RestClientException e) {
            log.error(e.toString());
            return Lists.newArrayList();
        }
    }
}
