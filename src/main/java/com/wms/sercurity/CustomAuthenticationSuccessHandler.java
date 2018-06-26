package com.wms.sercurity;

import com.wms.dto.ActionMenuDTO;
import com.wms.dto.AuthTokenInfo;
import com.wms.dto.CatCustomerDTO;
import com.wms.dto.SysRoleDTO;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.CatUserService;
import com.wms.services.interfaces.RoleActionService;
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
    RoleActionService roleActionService;

    @Autowired
    CatUserService catUserServices;

    @Autowired
    BaseService roleServiceImpl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        //set timeout
        HttpSession session = httpServletRequest.getSession();
        session.setMaxInactiveInterval(60*60*3);
		/*Set some session variables*/
        WMSUserDetails authUser = (WMSUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SysRoleDTO sysRoleDTO = (SysRoleDTO)roleServiceImpl.findById(Long.parseLong(authUser.getCatUserDTO().getRoleId()),authUser.getTokenInfo());
        authUser.getCatUserDTO().setSysRoleDTO(sysRoleDTO);
        session.setAttribute("user", authUser.getCatUserDTO());
        //
        CatCustomerDTO customer = getCustomer(authUser.getCatUserDTO().getCustId(),authUser.getTokenInfo());
        customer.setName(StringEscapeUtils.escapeHtml(customer.getName()));
        session.setAttribute("selectedCustomer",customer);
        //
        List<ActionMenuDTO> lstMenu = roleActionService.getUserActionService(authUser.getCatUserDTO().getRoleId(),authUser.getCatUserDTO().getCustId(),authUser.getTokenInfo());
        //
        session.setAttribute("authorities", authentication.getAuthorities());
        session.setAttribute("lstUserAction", lstMenu);
        session.setAttribute("isLogin", true);
        session.setAttribute("tokenInfo", authUser.getTokenInfo());
        //redirect
        String targetUrl = determineTargetUrl(authentication);
        redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, targetUrl);
    }

    private CatCustomerDTO getCustomer(String custId, AuthTokenInfo tokenInfo){
        return (CatCustomerDTO) customerService.findById(Long.parseLong(custId),tokenInfo);
    }

    protected String determineTargetUrl(Authentication authentication) {
//            Set<String> authorities = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
//            if (authorities.contains("ROLE_SYS_ADMIN")) {
//                return "/workspace";
//            } else if (authorities.contains("ROLE_CUS_ADMIN")) {
//                return "/workspace";
//            } else if (authorities.contains("ROLE_ADMIN")) {
//                return "/workspace";
//            }else{
//                return "/workspace";
//            }
        return "/workspace";
    }

    public RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

}
