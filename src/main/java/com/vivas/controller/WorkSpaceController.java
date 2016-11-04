package com.vivas.controller;

import com.google.common.collect.Lists;
import com.vivas.constants.Constants;
import com.vivas.dto.ActionMenuDTO;
import com.vivas.dto.Condition;
import com.vivas.dto.User;
import com.vivas.services.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.PathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
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
    public @ResponseBody List<User> getUser(@RequestParam("username")String username,@RequestParam("email")String email,
                                            @RequestParam("status")String status,@RequestParam("role")String role
    ){
        log.info("Search info: "+ username + "--"+ email+"--"+status+ "--"+role);

        List<Condition> lstCondition = Lists.newArrayList();
            lstCondition.add(new Condition("username", Constants.SQL_OPERATOR.LIKE,username));
            lstCondition.add(new Condition("email", Constants.SQL_OPERATOR.LIKE,email));
            if(!status.equals(Constants.STATS_ALL)){
                lstCondition.add(new Condition("status", Constants.SQL_OPERATOR.EQUAL,status));
            }

        return userService.findUserByCondition(lstCondition);
    }

    @RequestMapping(value = "/login",method = RequestMethod.GET,produces="text/plain")
    public @ResponseBody String login(){
       return "Success";
    }



    @RequestMapping()
        public String home(Model model, HttpServletRequest request){
        Object isLogin = request.getSession().getAttribute("isLogin");
        List<ActionMenuDTO> lstActionMenu= (List<ActionMenuDTO>) request.getSession().getAttribute("lstUserAction");
        model.addAttribute("lstUserAction",lstActionMenu);
        if(isLogin != null){
            return "workspace/wms_workspace";
        }else{
            return "index";
        }
    }

    @RequestMapping("/wms")
    public String wms(){
        return "index_boot";
    }

}
