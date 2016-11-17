package com.wms.base;

import com.google.common.collect.Lists;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.Condition;
import com.wms.dto.ResponseObject;
import com.wms.utils.BundleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public  final String SERVICE_URL    = BundleUtils.getkey("rest_service_url");
    public  String SERVICE_PREFIX;

    public  String ADD_URL;
    public  String UPDATE_URL;
    public  String DELETE_URL;

    public  String FIND_CONDITION_URL;
    public  String GET_ALL_URL;

    public RestTemplate restTemplate;

    public Class<T[]> valueArrayClass;

    Logger log = LoggerFactory.getLogger(BaseDP.class);

    public BaseDP(Class<T[]> valueArrayClass,String service_prefix) {
        this.restTemplate = new RestTemplate();
        this.valueArrayClass = valueArrayClass;
        this.SERVICE_PREFIX = service_prefix;
        initMainURL();
    }

    private void initMainURL(){
        ADD_URL    = SERVICE_URL+SERVICE_PREFIX  + Constants.SERVICE_METHOD.ADD;
        UPDATE_URL = SERVICE_URL+SERVICE_PREFIX  + Constants.SERVICE_METHOD.UPDATE;
        DELETE_URL = SERVICE_URL+SERVICE_PREFIX  + Constants.SERVICE_METHOD.DELETE;

        FIND_CONDITION_URL = SERVICE_URL+SERVICE_PREFIX + Constants.SERVICE_METHOD.FIND_BY_CONDITION;
        GET_ALL_URL        = SERVICE_URL+SERVICE_PREFIX  + Constants.SERVICE_METHOD.GET_ALL;
    }

    public boolean add(T tObject){
        ResponseObject reponseRegister = null;
        try {
            reponseRegister = restTemplate.postForObject(ADD_URL, tObject,ResponseObject.class);
            if(reponseRegister.getStatusName().equalsIgnoreCase(Responses.SUCCESS.getName())){
                return true;
            }
        } catch (RestClientException e) {
            log.info(e.toString());
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(T tObject){
        ResponseObject responseRegister = null;
        try {
            responseRegister = restTemplate.postForObject(UPDATE_URL,tObject,ResponseObject.class);
            if(responseRegister.getStatusName().equalsIgnoreCase(Responses.SUCCESS.getName())){
                return true;
            }
        } catch (RestClientException e) {
            e.printStackTrace();
            log.info(e.toString());
        }
        return false;
    }

    public boolean delete(Long id){
        ResponseObject responseRegister = null;
        try {
            responseRegister = restTemplate.postForObject(DELETE_URL,id,ResponseObject.class);
            if(responseRegister.getStatusName().equalsIgnoreCase(Responses.SUCCESS.getName())){
                return true;
            }
        } catch (RestClientException e) {
            log.info(e.toString());
            e.printStackTrace();
        }
        return false;
    }

    public List<T> findByCondition(List<Condition> lstCondition){
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<T[]> responseEntity = restTemplate.postForEntity(FIND_CONDITION_URL, lstCondition,valueArrayClass);
            return Arrays.asList(responseEntity.getBody());
        } catch (RestClientException e) {
            log.info(e.toString());
            return new ArrayList<>();
        }
    }

    public List<T> getAll(){
        try {
            ResponseEntity<T[]> responseEntity = restTemplate.getForEntity(GET_ALL_URL,valueArrayClass);
            return Arrays.asList(responseEntity.getBody());
        } catch (RestClientException e) {
            log.info(e.toString());
            return Lists.newArrayList();
        }
    }
}
