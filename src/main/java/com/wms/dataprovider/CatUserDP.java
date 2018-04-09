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
    private final String LOGIN_URL = BundleUtils.getKey("login_url");
    private final String GET_CUSTOMER_URL = BundleUtils.getKey("rest_service_url") + Constants.SERVICE_PREFIX.USER_SERVICE + "getCustomer/";
    private final String GET_USER_BY_CUST = BundleUtils.getKey("rest_service_url") + Constants.SERVICE_PREFIX.USER_SERVICE + "getUserByCustomerId/";
    private final String UPDATE_USER_URL  = BundleUtils.getKey("rest_service_url") + Constants.SERVICE_PREFIX.USER_SERVICE + "updateUser/";
    private final String UPDATE_CUSTOMER_URL  = BundleUtils.getKey("rest_service_url") + Constants.SERVICE_PREFIX.CUSTOMER_SERVICE + "updateCustomer/";

    Logger log = LoggerFactory.getLogger(CatUserDP.class);

    public CatUserDP() {
        super(CatUserDTO[].class,CatUserDTO.class,Constants.SERVICE_PREFIX.USER_SERVICE);
    }

    public ResponseObject register(CatUserDTO registerCatUserDTO){
        try {
            return restTemplate.postForObject(LOGIN_URL, registerCatUserDTO,ResponseObject.class);
        } catch (RestClientException e) {
            log.info(e.toString());
            return null;
        }
    }

    public ResponseObject updateUser(CatUserDTO updatedUser, AuthTokenInfo tokenInfo){
        String updateUserUrl = UPDATE_USER_URL + "?access_token="+ tokenInfo.getAccess_token();
        try {
            return restTemplate.postForObject(updateUserUrl, updatedUser,ResponseObject.class);
        } catch (RestClientException e) {
            log.info(e.toString());
            return null;
        }
    }

    public ResponseObject updateCustomer(CatCustomerDTO updatedCustomer, AuthTokenInfo tokenInfo){
        String updateCustomerUrl = UPDATE_CUSTOMER_URL + "?access_token="+ tokenInfo.getAccess_token();
        try {
            return restTemplate.postForObject(updateCustomerUrl, updatedCustomer,ResponseObject.class);
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

    public List<CatUserDTO> getUserByCustomer(String custId, AuthTokenInfo tokenInfo) {
        String getCustomerURL = GET_USER_BY_CUST + custId + "?access_token="+ tokenInfo.getAccess_token();
        try {
            ResponseEntity<CatUserDTO[]> responseEntity = restTemplate.exchange(getCustomerURL, HttpMethod.GET,null,CatUserDTO[].class);
            return Arrays.asList(responseEntity.getBody());
        } catch (RestClientException e) {
            log.error(e.toString());
            return Lists.newArrayList();
        }
    }



}
