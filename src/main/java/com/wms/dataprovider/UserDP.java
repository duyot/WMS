package com.wms.dataprovider;

import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.CatUserDTO;
import com.wms.dto.ResponseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;

/**
 * Created by duyot on 10/18/2016.
 */
@Repository
public class UserDP extends BaseDP<CatUserDTO>{
    private  final String LOGIN_URL = SERVICE_URL+SERVICE_PREFIX     + "login";

    Logger log = LoggerFactory.getLogger(UserDP.class);

    public UserDP() {
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


}
