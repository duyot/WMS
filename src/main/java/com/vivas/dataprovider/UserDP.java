package com.vivas.dataprovider;

import com.google.common.collect.Lists;
import com.vivas.constants.Constants;
import com.vivas.constants.Responses;
import com.vivas.dto.Condition;
import com.vivas.dto.ResponseObject;
import com.vivas.dto.User;
import com.vivas.utils.BundleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by duyot on 10/18/2016.
 */
@Repository
public class UserDP {

    private  final String SERVICE_URL    = BundleUtils.getkey("rest_service_url");
    private  final String SERVICE_PREFIX = "userservices/";

    private  final String ADD_URL    = SERVICE_URL+SERVICE_PREFIX  + Constants.SERVICE_METHOD.ADD;
    private  final String UPDATE_URL = SERVICE_URL+SERVICE_PREFIX  + Constants.SERVICE_METHOD.UPDATE;
    private  final String DELETE_URL = SERVICE_URL+SERVICE_PREFIX  + Constants.SERVICE_METHOD.DELETE;

    private  final String FIND_CONDITION_URL = SERVICE_URL+SERVICE_PREFIX + Constants.SERVICE_METHOD.FIND_BY_CONDITION;
    private  final String GET_ALL_URL        = SERVICE_URL+SERVICE_PREFIX  + Constants.SERVICE_METHOD.GET_ALL;

    private  final String LOGIN_URL = SERVICE_URL+SERVICE_PREFIX     + "login";

    RestTemplate restTemplate;
    Logger log = LoggerFactory.getLogger(UserDP.class);

    public UserDP() {
        restTemplate = new RestTemplate();
    }

    public boolean add(User user){
        ResponseObject reponseRegister = restTemplate.postForObject(ADD_URL, user,ResponseObject.class);
        if(reponseRegister.getStatusName().equalsIgnoreCase(Responses.SUCCESS.getName())){
            return true;
        }
        return false;
    }

    public boolean update(User user){
        ResponseObject responseRegister = restTemplate.postForObject(UPDATE_URL,user,ResponseObject.class);
        if(responseRegister.getStatusName().equalsIgnoreCase(Responses.SUCCESS.getName())){
            return true;
        }
        return false;
    }

    public boolean delete(Long id){
        String deleteURL = DELETE_URL + id;
        ResponseObject responseRegister = restTemplate.getForObject(deleteURL,ResponseObject.class);
        if(responseRegister.getStatusName().equalsIgnoreCase(Responses.SUCCESS.getName())){
            return true;
        }
        return false;
    }

    public List<User> getAll(){
        try {
            ResponseEntity<User[]> responseEntity = restTemplate.getForEntity(GET_ALL_URL, User[].class);
            return Arrays.asList(responseEntity.getBody());
        } catch (RestClientException e) {
            log.info(e.toString());
            return Lists.newArrayList();
        }
    }

    public List<User> findByCondition(List<Condition> lstCondition){
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<User[]> responseEntity = restTemplate.postForEntity(FIND_CONDITION_URL, lstCondition,User[].class);
            return Arrays.asList(responseEntity.getBody());
        } catch (RestClientException e) {
            return new ArrayList<>();
        }
    }


    public User login(User user){
        return restTemplate.postForObject(LOGIN_URL,user,User.class);
    }


}
