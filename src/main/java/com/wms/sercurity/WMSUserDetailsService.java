package com.wms.sercurity;

import com.wms.config.ProfileConfigInterface;
import com.wms.config.CustomerProdProfileConfig;
import com.wms.dto.CatUserDTO;
import com.wms.ribbon.CurrentUserLogIn;
import com.wms.services.interfaces.CatUserService;
import com.wms.services.interfaces.RoleActionService;
import com.wms.utils.DataUtil;
import com.wms.utils.EncyptionSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

/**
 * Created by duyot on 11/18/2016.
 */
@Component("wmsUserDetailsService")
public class WMSUserDetailsService implements UserDetailsService {
    @Autowired
    public CatUserService catUserService;
    @Autowired
    RoleActionService roleActionService;
    @Autowired
    CurrentUserLogIn currentUserLogIn;

    @Autowired
    private ProfileConfigInterface profileConfig;

    Logger log = LoggerFactory.getLogger(WMSUserDetailsService.class);

    @Override
    public UserDetails loadUserByUsername(String code) throws UsernameNotFoundException {
        if (profileConfig instanceof CustomerProdProfileConfig){
            try {
				EncyptionSecurity encyptionSecurity = new EncyptionSecurity();
              InetAddress inetAddress = InetAddress.getLocalHost();
              if (!encyptionSecurity.encrypt(inetAddress.getHostAddress()).equals(profileConfig.getSecurityToken())){
                  throw new InvalidServerException("invalid server");
              }
            }catch (Exception e){
                throw new UsernameNotFoundException("server error");
            }
        }
        CatUserDTO loggingUser = new CatUserDTO();
        loggingUser.setCode(code);
        try {
            CatUserDTO loggedUser = catUserService.login(loggingUser);
            if (loggedUser.getCode() == null) {
                log.info("CatUserDTO not available");
                return null;
            }
            //
            if (DataUtil.isStringNullOrEmpty(loggedUser.getImgUrl())) {
                loggedUser.setImgUrl("default.jpg");
            }
            currentUserLogIn.setCurrentUser(loggedUser);
            //

            return new WMSUserDetails(loggedUser, null);
        } catch (Exception e) {
            throw new UsernameNotFoundException("CatUserDTO not found");
        }
    }

}
