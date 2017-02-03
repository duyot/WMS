package com.wms.dataprovider;

import com.google.common.collect.Lists;
import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.AuthTokenInfo;
import com.wms.dto.CatCustomerDTO;
import com.wms.dto.CatUserDTO;
import com.wms.dto.ResponseObject;
import com.wms.utils.BundleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;

import java.util.Arrays;
import java.util.List;

/**
 * Created by duyot on 10/18/2016.
 */
@Repository
public class CatUserDP extends BaseDP<CatUserDTO>{
    private final String LOGIN_URL = BundleUtils.getkey("login_url");
    private final String GET_CUSTOMER_URL = BundleUtils.getkey("rest_service_url") + Constants.SERVICE_PREFIX.USER_SERVICE + "getCustomer/";

    Logger log = LoggerFactory.getLogger(CatUserDP.class);

    public CatUserDP() {
        super(CatUserDTO[].class,CatUserDTO.class,Constants.SERVICE_PREFIX.USER_SERVICE);
    }

    public ResponseObject register(CatUserDTO registerCatUserDTO){
        try {
            return restTemplate.postForObject(ADD_URL, registerCatUserDTO,ResponseObject.class);
        } catch (RestClientException e) {
            log.info(e.toString());
            return null;
        }
    }

    public CatUserDTO login(CatUserDTO catUserDTO){
        return restTemplate.postForObject(LOGIN_URL, catUserDTO,CatUserDTO.class);
    }

    public List<CatCustomerDTO> getCustomer(String userId, AuthTokenInfo tokenInfo) {
        String getCustomerURL = GET_CUSTOMER_URL + userId + "?access_token="+ tokenInfo.getAccess_token();
        try {
            ResponseEntity<CatCustomerDTO[]> responseEntity = restTemplate.exchange(getCustomerURL, HttpMethod.GET,null,CatCustomerDTO[].class);
            return Arrays.asList(responseEntity.getBody());
        } catch (RestClientException e) {
            log.error(e.toString());
            return Lists.newArrayList();
        }
    }


}
