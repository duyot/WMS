package com.wms.dataprovider;

import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.ActionMenuDTO;
import com.wms.dto.ChartDTO;
import com.wms.dto.SysMenuDTO;
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
public class ActionMenuDP  extends BaseDP<ActionMenuDTO> {
    Logger log = LoggerFactory.getLogger(ActionMenuDP.class);

    private  String GET_ACTION_MENU_URL    =   "getUserAction";
    public ActionMenuDP() {
        super(ActionMenuDTO[].class, ActionMenuDTO.class,  Constants.SERVICE_PREFIX.SYS_ROLE_MENU_SERVICE);
    }

    public List<ActionMenuDTO> getActionMenu(String roleId,String cusId ){
        RestTemplate restTemplate = new RestTemplate();
        String query =  "cusId="+cusId+"&roleId="+roleId ;
        String url = getUrlLoadBalancingQuery(query, GET_ACTION_MENU_URL);
        try {
            ResponseEntity<ActionMenuDTO[]> responseEntity = restTemplate.getForEntity(url,ActionMenuDTO[].class);
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
