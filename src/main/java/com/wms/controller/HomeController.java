package com.wms.controller;

import com.wms.constants.Responses;
import com.wms.dto.ActionMenuDTO;
import com.wms.dto.ResponseObject;
import com.wms.dto.User;
import com.wms.services.interfaces.RoleActionService;
import com.wms.services.interfaces.UserService;
import com.wms.utils.DataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by duyot on 10/18/2016.
 */
@Controller
@RequestMapping("/")
public class HomeController {
    Logger log = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    UserService userService;

    @Autowired
    RoleActionService roleActionService;

    @RequestMapping
    public String home(Model model){
        return "index";
    }

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String doLogin(User user,Model model, HttpServletRequest request){
        String plainPassword = user.getPassword();
        user.setPassword(DataUtil.MD5Encrypt(plainPassword));

        String accountName = user.getUsername();
        if(DataUtil.isEmail(accountName)){
            user.setEmail(accountName);
            user.setUsername(null);
        }

        User loggedUser = userService.login(user);

        if(DataUtil.isStringNullOrEmpty(loggedUser.getUserId())){
            log.info("Login fail for user: "+ user.getUsername());
            model.addAttribute("errorMessage","Thông tin tài khoản không đúng");
            return "/index";
        }

        if(DataUtil.isStringNullOrEmpty(loggedUser.getImgUrl())){
            loggedUser.setImgUrl("default.jpg");
        }
        //set some session attribute
        log.info("Login susscessfully for user: "+ loggedUser.getUsername());
        request.getSession().setAttribute("isLogin",true);
        request.getSession().setAttribute("user", loggedUser);
        MDC.put("userid",loggedUser.getUsername());
        //
        List<ActionMenuDTO> lstActionMenu = roleActionService.getUserActionService(loggedUser.getRoleId());
        request.getSession().setAttribute("lstUserAction", lstActionMenu);
        return "redirect:workspace";

    }

    @RequestMapping(value = "/register",method = RequestMethod.POST,produces="text/plain")
    public @ResponseBody String register(User registerUser){
        registerUser.setPassword(DataUtil.MD5Encrypt(registerUser.getPassword()));
        log.info("Register user info: "+ registerUser.toString());
        ResponseObject responseObject = userService.register(registerUser);
        if(responseObject == null || !Responses.SUCCESS.getName().equalsIgnoreCase(responseObject.getStatusName())){
            return "Đăng ký không thành công";
        }
        return "Đăng ký thành công";

    }
}
