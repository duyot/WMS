package com.wms.sercurity;

import com.wms.dto.AuthTokenInfo;
import com.wms.dto.CatCustomerDTO;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.CatUserService;
import com.wms.utils.DataUtil;
import org.apache.commons.lang.StringEscapeUtils;
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
    @Autowired
    BaseService customerService;

    @Autowired
    CatUserService catUserServices;



    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        HttpSession session = httpServletRequest.getSession();

		/*Set some session variables*/
        WMSUserDetails authUser = (WMSUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        session.setAttribute("user", authUser.getCatUserDTO());
        List<CatCustomerDTO> lstCustomers = getCustomer(authUser.getCatUserDTO().getId(),authUser.getTokenInfo());
        if(!DataUtil.isListNullOrEmpty(lstCustomers)){
            if(lstCustomers.size() > 1){
                session.setAttribute("lstCustomers",lstCustomers);
            }else{
                CatCustomerDTO customer = lstCustomers.get(0);
                customer.setName(StringEscapeUtils.escapeHtml(customer.getName()));
                session.setAttribute("selectedCustomer",customer);
            }
        }
        session.setAttribute("authorities", authentication.getAuthorities());
        session.setAttribute("lstUserAction", authUser.getLstMenu());
        session.setAttribute("isLogin", true);
        session.setAttribute("tokenInfo", authUser.getTokenInfo());
        //
        String targetUrl = determineTargetUrl(authentication);
        redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, targetUrl);
    }

    private List<CatCustomerDTO> getCustomer(String userId, AuthTokenInfo tokenInfo){
        return catUserServices.getCustomer(userId,tokenInfo);
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
