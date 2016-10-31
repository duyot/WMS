package com.vivas.controller;

import com.vivas.dto.User;
import com.vivas.services.interfaces.UserService;
import com.vivas.utils.DataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.xml.crypto.Data;
import java.util.Date;

/**
 * Created by duyot on 10/18/2016.
 */
@Controller
@RequestMapping("/")
public class HomeController {
    Logger log = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    UserService userService;

    @RequestMapping
    public String home(Model model){
        return "index";
    }

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String doLogin(User user, HttpServletRequest request){
        String plainPassword = user.getPassword();
        user.setPassword(DataUtil.MD5Encrypt(plainPassword));

        String accountName = user.getUsername();
        if(DataUtil.isEmail(accountName)){
            user.setEmail(accountName);
            user.setUsername(null);
        }

        User loggedUser = userService.login(user);

        if(!DataUtil.isStringNullOrEmpty(loggedUser.getUserId())){
            if(DataUtil.isStringNullOrEmpty(loggedUser.getImgUrl())){
                loggedUser.setImgUrl("default.jpg");
            }
            //set some session attribute
            log.info("Login susscessfully for user: "+ loggedUser.getUsername());
            request.getSession().setAttribute("isLogin",true);
            request.getSession().setAttribute("user", loggedUser);
            return "redirect:workspace";
        }else{
            log.info("Login fail for user: "+ user.getUsername());
            return "common/error";
        }

    }

    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public String register(User registerUser){
        log.info("Register user info: "+ registerUser.toString());
        registerUser.setPassword(DataUtil.MD5Encrypt(registerUser.getPassword()));
        if(userService.register(registerUser)){
            log.info("SUCCESS");
        }else{
            log.info("FAIL");
        }

        return "common/success";
    }
}
