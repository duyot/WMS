package com.wms.sercurity;

import com.wms.dto.ActionMenuDTO;
import com.wms.dto.AuthTokenInfo;
import com.wms.dto.CatUserDTO;
import com.wms.services.interfaces.CatUserService;
import com.wms.services.interfaces.RoleActionService;
import com.wms.utils.DataUtil;
import com.wms.utils.FunctionUtils;
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
    public CatUserService catUserService;
    @Autowired
    RoleActionService roleActionService;

    Logger log = LoggerFactory.getLogger(WMSUserDetailsService.class);
    @Override
    public UserDetails loadUserByUsername(String code) throws UsernameNotFoundException {
        CatUserDTO loggingUser = new CatUserDTO();
        loggingUser.setCode(code);
        try {
            CatUserDTO loggedUser = catUserService.login(loggingUser);
            if(loggedUser.getCode() == null){
                log.info("CatUserDTO not available");
                return null;
            }
            //
            if (DataUtil.isStringNullOrEmpty(loggedUser.getImgUrl())) {
                loggedUser.setImgUrl("default.jpg");
            }
            //
            AuthTokenInfo tokenInfo = getAuthenTokenInfo(loggedUser);
            List<ActionMenuDTO> lstMenu = roleActionService.getUserActionService(loggedUser.getRoleCode(),tokenInfo);

            return new WMSUserDetails(loggedUser,lstMenu,tokenInfo);
        } catch (Exception e) {
            throw new UsernameNotFoundException("CatUserDTO not found");
        }
    }

    private AuthTokenInfo getAuthenTokenInfo(CatUserDTO catUserDTO){
        return FunctionUtils.sendTokenRequest(catUserDTO.getCode(),catUserDTO.getPassword());
    }

}
