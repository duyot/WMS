package com.wms.base;

import com.google.common.collect.Lists;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.AuthTokenInfo;
import com.wms.dto.Condition;
import com.wms.dto.ResponseObject;
import com.wms.utils.BundleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by duyot on 11/9/2016.
 */
public class BaseDP<T> {
    public  final String SERVICE_URL    = BundleUtils.getKey("rest_service_url");
    public  String SERVICE_PREFIX;

    public  String GET_SYSDATE_URL;
    public  String GET_SYSDATE_WITH_PATTERN_URL;

    public  String ADD_URL;
    public  String UPDATE_URL;
    public  String DELETE_URL;

    public  String FIND_BY_ID_URL;
    public  String FIND_CONDITION_URL;
    public  String GET_ALL_URL;

    public RestTemplate restTemplate;

    public Class<T[]> valueArrayClass;
    public Class objectClass;

    Logger log = LoggerFactory.getLogger(BaseDP.class);

    public BaseDP(Class<T[]> valueArrayClass,Class objectClass,String service_prefix) {
        this.restTemplate = new RestTemplate();
        this.valueArrayClass = valueArrayClass;
        this.objectClass = objectClass;
        this.SERVICE_PREFIX = service_prefix;
        initMainURL();
    }

    private void initMainURL(){
        GET_SYSDATE_URL               = SERVICE_URL+SERVICE_PREFIX  + Constants.SERVICE_METHOD.GET_SYS_DATE;
        GET_SYSDATE_WITH_PATTERN_URL  = SERVICE_URL+SERVICE_PREFIX  + Constants.SERVICE_METHOD.GET_SYS_DATE_PATTERN;

        ADD_URL    = SERVICE_URL+SERVICE_PREFIX  + Constants.SERVICE_METHOD.ADD;
        UPDATE_URL = SERVICE_URL+SERVICE_PREFIX  + Constants.SERVICE_METHOD.UPDATE;
        DELETE_URL = SERVICE_URL+SERVICE_PREFIX  + Constants.SERVICE_METHOD.DELETE;

        FIND_BY_ID_URL     = SERVICE_URL+SERVICE_PREFIX + Constants.SERVICE_METHOD.FIND_BY_ID;
        FIND_CONDITION_URL = SERVICE_URL+SERVICE_PREFIX + Constants.SERVICE_METHOD.FIND_BY_CONDITION;
        GET_ALL_URL        = SERVICE_URL+SERVICE_PREFIX  + Constants.SERVICE_METHOD.GET_ALL;
    }

    public String getSysDate(AuthTokenInfo tokenInfo){
        String findUrl = GET_SYSDATE_URL + tokenInfo.getAccess_token();
        try {
            return  restTemplate.getForObject(findUrl, String.class);
        } catch (RestClientException e) {
            log.error(e.toString());
            return "";
        }
    }

    public String getSysDateWithPattern(String pattern,AuthTokenInfo tokenInfo){
        try {
            return restTemplate.postForObject(GET_SYSDATE_WITH_PATTERN_URL + tokenInfo.getAccess_token(), pattern,String.class);
        } catch (RestClientException e) {
            log.info(e.toString());
            e.printStackTrace();
            return "";
        }
    }

    public ResponseObject add(T tObject, AuthTokenInfo tokenInfo){
        try {
            return restTemplate.postForObject(ADD_URL + tokenInfo.getAccess_token(), tObject,ResponseObject.class);
        } catch (RestClientException e) {
            log.info(e.toString());
            e.printStackTrace();
            return  new ResponseObject(Responses.ERROR.getName(),Responses.ERROR.getName(),"");
        }
    }

    public ResponseObject update(T tObject, AuthTokenInfo tokenInfo){
        try {
            return restTemplate.postForObject(UPDATE_URL + tokenInfo.getAccess_token(),tObject,ResponseObject.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            log.info(e.toString());
            return  new ResponseObject(Responses.ERROR.getName(),Responses.ERROR.getName(),"");
        }
    }

    public ResponseObject delete(Long id, AuthTokenInfo tokenInfo){
        String deleteURL = DELETE_URL + id + "?access_token="+ tokenInfo.getAccess_token();
        try {
            ResponseEntity<ResponseObject> response = restTemplate.exchange(deleteURL, HttpMethod.DELETE, null, ResponseObject.class);
            return response.getBody();
        }catch (Exception e){
            e.printStackTrace();
            log.info(e.toString());
            return  new ResponseObject(Responses.ERROR.getName(),Responses.ERROR.getName(),"");
        }
    }

    public T findById(Long id, AuthTokenInfo tokenInfo){
        String findUrl = FIND_BY_ID_URL + id + "?access_token="+ tokenInfo.getAccess_token();
        try {
            return (T) restTemplate.getForObject(findUrl, objectClass);
        } catch (RestClientException e) {
            log.error(e.toString());
            return null;
        }
    }

    public List<T> findByCondition(List<Condition> lstCondition, AuthTokenInfo tokenInfo){
        try {
            ResponseEntity<T[]> responseEntity = restTemplate.postForEntity(FIND_CONDITION_URL+ tokenInfo.getAccess_token(), lstCondition,valueArrayClass);
            return Arrays.asList(responseEntity.getBody());
        } catch (RestClientException e) {
            log.error(e.toString());
            return new ArrayList<>();
        }
    }

    public List<T> getAll(AuthTokenInfo tokenInfo){
        try {
            ResponseEntity<T[]> responseEntity = restTemplate.exchange(GET_ALL_URL+ tokenInfo.getAccess_token(),HttpMethod.GET,null,valueArrayClass);
            return Arrays.asList(responseEntity.getBody());
        } catch (RestClientException e) {
            log.error(e.toString());
            return Lists.newArrayList();
        }
    }
}
