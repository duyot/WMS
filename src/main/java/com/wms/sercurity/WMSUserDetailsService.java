package com.wms.sercurity;

import com.google.common.collect.Lists;
import com.wms.constants.Constants;
import com.wms.dto.Condition;
import com.wms.dto.User;
import com.wms.services.interfaces.RoleActionService;
import com.wms.services.interfaces.UserService;
import com.wms.utils.DataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;

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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<Condition> lstCon = Lists.newArrayList();
        lstCon.add(new Condition("username", Constants.SQL_OPERATOR.EQUAL,username));
        //
        try {
            List<User> lstUser = userService.findUserByCondition(lstCon);
            if(DataUtil.isListNullOrEmpty(lstUser)){
                log.info("User not available");
                return null;
            }
            User loggedUser = lstUser.get(0);
            return new WMSUserDetails(loggedUser,roleActionService.getUserActionService(loggedUser.getRoleId()));
        } catch (Exception e) {
            throw new UsernameNotFoundException("User not found");
        }
    }
}
