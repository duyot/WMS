package com.wms.sercurity;

import com.google.common.collect.Lists;
import com.wms.constants.Constants;
import com.wms.dto.CatUserDTO;
import com.wms.dto.Condition;
import com.wms.services.interfaces.RoleActionService;
import com.wms.services.interfaces.UserService;
import com.wms.utils.DataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by duyot on 11/18/2016.
 */
@Component("wmsUserDetailsService")
public class WMSUserDetailsService implements UserDetailsService {
    @Autowired
    public UserService userService;
    @Autowired
    RoleActionService roleActionService;

    Logger log = LoggerFactory.getLogger(WMSUserDetailsService.class);
    @Override
    public UserDetails loadUserByUsername(String code) throws UsernameNotFoundException {
        List<Condition> lstCon = Lists.newArrayList();
        lstCon.add(new Condition("code", Constants.SQL_OPERATOR.EQUAL,code));
        //
        try {
            List<CatUserDTO> lstCatUserDTO = userService.findUserByCondition(lstCon);
            if(DataUtil.isListNullOrEmpty(lstCatUserDTO)){
                log.info("CatUserDTO not available");
                return null;
            }
            CatUserDTO loggedCatUserDTO = lstCatUserDTO.get(0);
            return new WMSUserDetails(loggedCatUserDTO,roleActionService.getUserActionService(loggedCatUserDTO.getRoleCode()));
        } catch (Exception e) {
            throw new UsernameNotFoundException("CatUserDTO not found");
        }
    }
}
