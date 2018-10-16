package com.wms.controller;

import com.wms.config.WMSConfigManagerment;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.CatCustomerDTO;
import com.wms.dto.CatUserDTO;
import com.wms.dto.ResponseObject;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.CatUserService;
import com.wms.utils.BundleUtils;
import com.wms.utils.DataUtil;
import com.wms.utils.DateTimeUtils;
import com.wms.utils.ResourceBundleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * Created by duyot on 10/18/2016.
 */
    @Controller
    @RequestMapping("/")
    @Scope("session")
    public class HomeController {
    Logger log = LoggerFactory.getLogger(HomeController.class);
    Logger logError = LoggerFactory.getLogger("ERROR");

    @Autowired
    CatUserService catUserService;
    @Autowired
    BaseService customerService;

    @RequestMapping
    public String home(Model model){
        return "index";
    }


    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public String login() {
        return "security_login";
    }
    @RequestMapping(value = "/failureLogin",method = RequestMethod.GET)
    public String failureLogin(@RequestParam(value = "message") String message, final Model model) {
        model.addAttribute("message", message);
        return "security_login";
    }


    @RequestMapping(value = "/register",method = RequestMethod.GET)
    public String register(){
        return "register";
    }

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        return "redirect:/login?logout";
    }

    @RequestMapping(value="/language/en", method = RequestMethod.GET)
    public String changeLanguage2English(HttpServletRequest request, HttpServletResponse response) {
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        localeResolver.setLocale(request,response,Locale.ENGLISH);
        return "redirect:/";
    }

    @RequestMapping(value="/language/vi", method = RequestMethod.GET)
    public String changeLanguage2Vietnamese(HttpServletRequest request, HttpServletResponse response) {
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        localeResolver.setLocale(request,response,new Locale("vi", "VN"));
        return "redirect:/";
    }



    @RequestMapping(value = "/register",method = RequestMethod.POST,produces="text/plain")
    public @ResponseBody String register(CatUserDTO registerCatUserDTO){
        registerCatUserDTO.setPassword(DataUtil.BCryptPasswordEncoder(registerCatUserDTO.getPassword()));
        log.info("Register user info: "+ registerCatUserDTO.toString());
        CatCustomerDTO catCustomerDTO = new CatCustomerDTO();
        catCustomerDTO.setAddress(registerCatUserDTO.getAddress());
        catCustomerDTO.setName(registerCatUserDTO.getCustName());
        catCustomerDTO.setTelNumber(registerCatUserDTO.getTelNumber());
        catCustomerDTO.setEmail(registerCatUserDTO.getEmail());
        catCustomerDTO.setTrial("1");
        catCustomerDTO.setStatus("1");
        catCustomerDTO.setCode( DateTimeUtils.getTimeStamp());
        ResponseObject result = customerService.add(catCustomerDTO);
        if (Responses.SUCCESS.getName().equalsIgnoreCase(result.getStatusCode())){
            registerCatUserDTO.setRoleId(WMSConfigManagerment.DEFAUL_ROLE_GUESTID);
            registerCatUserDTO.setRoleName(WMSConfigManagerment.DEFAUL_ROLE_GUESTNAME);
            registerCatUserDTO.setCustId(result.getKey());
            registerCatUserDTO.setStatus("1");
            registerCatUserDTO.setBlock("0");
            ResponseObject responseObject = catUserService.guestAddUser(registerCatUserDTO);
            try {
                Long idL = Long.parseLong(responseObject.getKey());
                return ResourceBundleUtils.getkey(Constants.RESPONSE.REGISTER_SUSSESS);
            }  catch (NumberFormatException e) {
                return ResourceBundleUtils.getkey(DataUtil.isNullOrEmpty(responseObject.getKey())?Constants.RESPONSE.INSERT_ERROR:responseObject.getKey());
            }
        }
        return ResourceBundleUtils.getkey(Constants.RESPONSE.REGISTER_ERROR);
    }

//    @RequestMapping(value = "/{sitemap:.+}",method = RequestMethod.GET)
//    public void sitemap(HttpServletResponse response){
//        response.setContentType(MediaType.APPLICATION_XML_VALUE);
//        response.setHeader("Content-type","application/xhtml+xml");
//        FunctionUtils.loadFileToClient(response, BundleUtils.getKey("template_url") + "sitemap.xml");
//    }
}
