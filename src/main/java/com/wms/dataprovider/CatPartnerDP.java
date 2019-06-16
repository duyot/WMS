package com.wms.dataprovider;

import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.CatPartnerDTO;
import com.wms.dto.CatGoodsDTO;
import com.wms.dto.CatStockDTO;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import com.google.common.collect.Lists;
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
public class CatPartnerDP extends BaseDP<CatPartnerDTO> {
    private String GET_PARTNER_BY_USER =  "getPartnerByUser/";
    Logger log = LoggerFactory.getLogger(BaseDP.class);

    public CatPartnerDP() {
        super(CatPartnerDTO[].class, CatPartnerDTO.class, Constants.SERVICE_PREFIX.CAT_PARTNER_SERVICE);
    }

    public List<CatPartnerDTO> getPartnerByUser(Long userId){
        try {
            String findUrl =  getUrlLoadBalancing(userId, GET_PARTNER_BY_USER);
            ResponseEntity<CatPartnerDTO[]> responseEntity = restTemplate.exchange(findUrl,HttpMethod.GET,null, CatPartnerDTO[].class);
            return Arrays.asList(responseEntity.getBody());
        } catch (RestClientException e) {
            log.error(e.toString());
            return Lists.newArrayList();
        }
    }

    public List<CatPartnerDTO> getPartnerByUser(Long userId, Long partnerPermission){
        try {
            RestTemplate restTemplate = new RestTemplate();
            String query =  "userId="+ userId + "&partnerPermission=" + partnerPermission ;
            String url = getUrlLoadBalancingQuery(query, GET_PARTNER_BY_USER);
            ResponseEntity<CatPartnerDTO[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET,null,CatPartnerDTO[].class);
            return Arrays.asList(responseEntity.getBody());

        } catch (RestClientException e) {
            log.error(e.toString());
            return Lists.newArrayList();
        }
    }

}
