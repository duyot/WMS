package com.wms.dataprovider;

import com.wms.dto.ActionMenuDTO;
import com.wms.utils.BundleUtils;
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
 * Created by duyot on 11/3/2016.
 */
@Repository
public class ActionMenuDP {
    Logger log = LoggerFactory.getLogger(ActionMenuDP.class);
    private  final String SERVICE_URL    = BundleUtils.getkey("rest_service_url");
    private  final String SERVICE_PREFIX = "sysRoleMenuServices/";

    private  final String GET_ACTION_MENU_URL    = SERVICE_URL+SERVICE_PREFIX  + "getUserAction/";

    public List<ActionMenuDTO> getActionMenu(String roleCode){
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<ActionMenuDTO[]> responseEntity = restTemplate.getForEntity(GET_ACTION_MENU_URL+roleCode,ActionMenuDTO[].class);
            if(responseEntity.getBody() != null){
                return Arrays.asList(responseEntity.getBody());
            }else{
                log.info("Cannot get info");
                return new ArrayList<>();
            }
        } catch (RestClientException e) {
            return new ArrayList<>();
        }
    }
}
