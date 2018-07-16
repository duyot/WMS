package com.wms.controller;

import com.wms.constants.Responses;
import com.wms.dto.AuthTokenInfo;
import com.wms.dto.CatUserDTO;
import com.wms.dto.ResponseObject;
import com.wms.services.interfaces.CatUserService;
import com.wms.utils.DataUtil;
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

    @RequestMapping
    public String home(Model model){
        return "index";
    }

    private AuthTokenInfo tokenInfo;
    @ModelAttribute("tokenInfo")
    public void setTokenInfo(HttpServletRequest request){
        this.tokenInfo =  (AuthTokenInfo) request.getSession().getAttribute("tokenInfo");
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
        ResponseObject responseObject = catUserService.register(registerCatUserDTO,tokenInfo);
        if(responseObject == null || !Responses.SUCCESS.getName().equalsIgnoreCase(responseObject.getStatusName())){
            return "Đăng ký không thành công";
        }
        return "Đăng ký thành công";

    }

//    @RequestMapping(value = "/{sitemap:.+}",method = RequestMethod.GET)
//    public void sitemap(HttpServletResponse response){
//        response.setContentType(MediaType.APPLICATION_XML_VALUE);
//        response.setHeader("Content-type","application/xhtml+xml");
//        FunctionUtils.loadFileToClient(response, BundleUtils.getKey("template_url") + "sitemap.xml");
//    }
}
