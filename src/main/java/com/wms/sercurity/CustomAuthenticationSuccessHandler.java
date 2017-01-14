package com.wms.sercurity;

import com.google.common.collect.Lists;
import com.wms.dto.CustomerDTO;
import com.wms.services.interfaces.BaseService;
import com.wms.utils.DataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Created by duyot on 11/18/2016.
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    Logger log = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);
    @Autowired
    BaseService customerService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        HttpSession session = httpServletRequest.getSession();

		/*Set some session variables*/
        WMSUserDetails authUser = (WMSUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        session.setAttribute("user", authUser.getCatUserDTO());
        session.setAttribute("lstCustomers", getCustomer(authUser.getCatUserDTO().getCustId()));
        session.setAttribute("authorities", authentication.getAuthorities());
        session.setAttribute("lstUserAction", authUser.getLstMenu());
        session.setAttribute("isLogin", true);
        //
        String targetUrl = determineTargetUrl(authentication);
        redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, targetUrl);
    }

    private List<CustomerDTO> getCustomer(String custId){
        List<CustomerDTO> lstCustomer = Lists.newArrayList();
        if(DataUtil.isStringNullOrEmpty(custId)){
            return null;
        }
        String[] arrCustId = custId.split(",");
        for(int i = 0;i<arrCustId.length;i++){
            try {
                Long id = Long.parseLong(arrCustId[i]);
                CustomerDTO customer =  (CustomerDTO) customerService.findById(id);
                lstCustomer.add(customer);
            } catch (NumberFormatException e) {
                log.error(e.toString());
            }
        }
        //
        return lstCustomer;
    }

    protected String determineTargetUrl(Authentication authentication) {
                Set<String> authorities = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
                if (authorities.contains("ROLE_SYS_ADMIN")) {
                    return "/workspace";
                } else if (authorities.contains("ROLE_CUS_ADMIN")) {
                    return "/workspace";
                } else if (authorities.contains("ROLE_ADMIN")) {
                    return "/workspace";
                }else{
                    return "/workspace";
        }
    }

    public RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

}
