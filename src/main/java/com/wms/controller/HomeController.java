package com.wms.controller;

import com.wms.constants.Responses;
import com.wms.dto.AuthTokenInfo;
import com.wms.dto.ResponseObject;
import com.wms.dto.CatUserDTO;
import com.wms.services.interfaces.RoleActionService;
import com.wms.services.interfaces.CatUserService;
import com.wms.utils.DataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by duyot on 10/18/2016.
 */
@Controller
@RequestMapping("/")
public class HomeController {
    Logger log = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    CatUserService catUserService;

    @Autowired
    RoleActionService roleActionService;

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
    public String login(){
        return "sercurity_login";
    }

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout";
    }


//    @RequestMapping(value = "/login",method = RequestMethod.POST)
//    public String doLogin(CatUserDTO user,Model model, HttpServletRequest request){
//        //
////        WMSUserDetails loggedUser = (WMSUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        //
//        String plainPassword = user.getPassword();
//        user.setPassword(DataUtil.MD5Encrypt(plainPassword));
//
//        String accountName = user.getUsername();
//        if(DataUtil.isEmail(accountName)){
//            user.setEmail(accountName);
//            user.setUsername(null);
//        }
//
//        CatUserDTO loggedUser = catUserService.login(user);
//
//        if(DataUtil.isStringNullOrEmpty(loggedUser.getUserId())){
//            log.info("Login fail for user: "+ user.getUsername());
//            model.addAttribute("errorMessage","Thông tin tài khoản không đúng");
//            return "/index";
//        }
//
//        if(DataUtil.isStringNullOrEmpty(loggedUser.getImgUrl())){
//            loggedUser.setImgUrl("default.jpg");
//        }
//        //set some session attribute
//        log.info("Login susscessfully for user: "+ loggedUser.getUsername());
//        request.getSession().setAttribute("isLogin",true);
//        request.getSession().setAttribute("user", loggedUser);
//        MDC.put("userid",loggedUser.getUsername());
//        //
//        List<ActionMenuDTO> lstActionMenu = roleActionService.getUserActionService(loggedUser.getRoleId());
//        request.getSession().setAttribute("lstUserAction", lstActionMenu);
//        return "redirect:workspace";
//    }

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
}
