package com.vivas.controller;

import com.google.common.collect.Lists;
import com.vivas.dto.User;
import com.vivas.services.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by duyot on 10/12/2016.
 */
@Controller
@RequestMapping("/workspace")
public class WorkSpaceController {

    Logger log = LoggerFactory.getLogger(WorkSpaceController.class);

    @Autowired
    UserService userService;

    @RequestMapping("/logout")
        public String logout(HttpServletRequest request){
            request.getSession().invalidate();
            return "redirect:/";
    }

    @RequestMapping(value = "/getuser",method = RequestMethod.GET,produces="application/json")
    public @ResponseBody List<User> getUser(){
       return userService.getAlls();
    }



    @RequestMapping()
        public String home(HttpServletRequest request){
        Object isLogin = request.getSession().getAttribute("isLogin");
        if(isLogin != null){
            return "workspace/wms_workspace";
        }else{
            return "index";
        }
    }

    @RequestMapping("/wms")
    public String wms(){
        return "workspace/wms";
    }

}
