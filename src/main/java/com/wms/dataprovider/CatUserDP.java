package com.wms.dataprovider;

import com.google.common.collect.Lists;
import com.wms.base.BaseDP;
import com.wms.config.ProfileConfigInterface;
import com.wms.constants.Constants;
import com.wms.dto.CatCustomerDTO;
import com.wms.dto.CatUserDTO;
import com.wms.dto.ResponseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final String GET_CUSTOMER_URL =  "getCustomer/";
    private final String GET_USER_BY_CUST =  "getUserByCustomerId/";
    private final String UPDATE_USER_URL  = "updateUser/";
    private final String ADD  = "add/";
    private final String GUEST_ADD_USER_URL  = "guestAddUser/";
    private final String UPDATE_CUSTOMER_URL  = "updateCustomer/";

    @Autowired
    private ProfileConfigInterface profileConfig;

    Logger log = LoggerFactory.getLogger(CatUserDP.class);

    public CatUserDP() {
        super(CatUserDTO[].class,CatUserDTO.class,Constants.SERVICE_PREFIX.USER_SERVICE);
    }

    public ResponseObject register(CatUserDTO registerCatUserDTO){
        try {
            String updateUserUrl = getUrlLoadBalancing(0, UPDATE_USER_URL);
            return restTemplate.postForObject(updateUserUrl, registerCatUserDTO,ResponseObject.class);
        } catch (RestClientException e) {
            log.info(e.toString());
            return null;
        }
    }

	@Override
	public ResponseObject add(CatUserDTO registerCatUserDTO) {
		try {
			String url = getUrlWithoutTokenKey("register/addUser");
			return restTemplate.postForObject(url, registerCatUserDTO,ResponseObject.class);
		} catch (RestClientException e) {
			log.info(e.toString());
			return null;
		}
	}

	public ResponseObject updateUser(CatUserDTO updatedUser ){
        String updateUserUrl = getUrlLoadBalancing(0, UPDATE_USER_URL);
        try {
            return restTemplate.postForObject(updateUserUrl, updatedUser,ResponseObject.class);
        } catch (RestClientException e) {
            log.info(e.toString());
            return null;
        }
    }

    public ResponseObject updateCustomer(CatCustomerDTO updatedCustomer ){
        String url = getUrlLoadBalancing(0, UPDATE_CUSTOMER_URL);
        try {
            return restTemplate.postForObject(url, updatedCustomer,ResponseObject.class);
        } catch (RestClientException e) {
            log.info(e.toString());
            return null;
        }
    }

    public CatUserDTO login(CatUserDTO catUserDTO){
        return restTemplate.postForObject(profileConfig.getLoginURL(), catUserDTO,CatUserDTO.class);
    }

    public List<CatCustomerDTO> getCustomer(String userId) {
        try {
            long id= Long.parseLong(userId);
            String url = getUrlLoadBalancing(id, GET_CUSTOMER_URL);
            ResponseEntity<CatCustomerDTO[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET,null,CatCustomerDTO[].class);
            return Arrays.asList(responseEntity.getBody());
        } catch (RestClientException e) {
            log.error(e.toString());
            return Lists.newArrayList();
        } catch (NumberFormatException e){
            log.error(e.toString());
            return Lists.newArrayList();
        }
    }

    public List<CatUserDTO> getUserByCustomer(String custId ) {

        try {
            long id= Long.parseLong(custId);
            String url = getUrlLoadBalancing(id, GET_USER_BY_CUST);
            ResponseEntity<CatUserDTO[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET,null,CatUserDTO[].class);
            return Arrays.asList(responseEntity.getBody());
        } catch (RestClientException e) {
            log.error(e.toString());
            return Lists.newArrayList();
        } catch (NumberFormatException e){
            log.error(e.toString());
            return Lists.newArrayList();
        }
    }



}
