package com.vivas.dataprovider;

import com.google.common.collect.Lists;
import com.vivas.constants.Responses;
import com.vivas.dto.ResponseObject;
import com.vivas.dto.User;
import com.vivas.utils.BundleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * Created by duyot on 10/18/2016.
 */
@Repository
public class UserDP {

    private  String SERVICE_URL = BundleUtils.getkey("rest_service_url");
    private  String SERVICE_PREFIX = "userservices/";
    private  String GET_ALL_URL  = SERVICE_URL+SERVICE_PREFIX  + "getAll";
    private  String REGISTER_URL = SERVICE_URL+SERVICE_PREFIX  + "saveUser";
    private  String LOGIN_URL = SERVICE_URL+SERVICE_PREFIX     + "login";


    RestTemplate restTemplate;
    Logger log = LoggerFactory.getLogger(UserDP.class);

    public UserDP() {
        restTemplate = new RestTemplate();
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

    public boolean register(User registerUser){
        ResponseObject reponseRegister = restTemplate.postForObject(REGISTER_URL,registerUser,ResponseObject.class);
        if(reponseRegister.getStatusName().equalsIgnoreCase(Responses.SUCCESS.getName())){
            return true;
        }
        return false;
    }
    public User login(User user){
        return restTemplate.postForObject(LOGIN_URL,user,User.class);

    }

}
