package com.wms.controller;

import com.google.common.collect.Lists;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.RoleActionService;
import com.wms.services.interfaces.UserService;
import com.wms.utils.BundleUtils;
import com.wms.utils.DataUtil;
import com.wms.utils.DateTimeUtils;
import com.wms.utils.FunctionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.thymeleaf.util.DateUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by duyot on 10/12/2016.
 */
@Controller
@RequestMapping("/workspace")
public class WorkSpaceController {
    Logger log = LoggerFactory.getLogger(WorkSpaceController.class);

    @RequestMapping()
    public String home(HttpServletRequest request){
        Object isLogin = request.getSession().getAttribute("isLogin");
        if(isLogin == null){
            return "index";
        }
        return "workspace/wms";
    }

    //redirect with role------------------------------------------------------------------------------------------------
    @RequestMapping("/sysadmin")
    public String sysadmin(){
        return "workspace/wms";
    }
    @RequestMapping("/cusadmin")
    public String cusadmin(){
        return "workspace/wms";
    }
    @RequestMapping("/admin")
    public String admin(){
        return "workspace/wms";
    }
    @RequestMapping("/user")
    public String user(){
        return "workspace/wms";
    }

    //------------------------------------------------------------------------------------------------------------------
    @RequestMapping("/logout")
        public String logout(HttpServletRequest request){
            request.getSession().invalidate();
            //
            MDC.clear();
            return "redirect:/";
    }

    @RequestMapping(value = "/login",method = RequestMethod.GET,produces="text/plain")
    public @ResponseBody String login(){
       return "Success";
    }

    @PreAuthorize("hasAnyRole('SYS_ADMIN','CUS_ADMIN','ADMIN')")
    @RequestMapping("/cat_goods_group")
    public String redirectCatGoodsGroup(){
        return "redirect:/workspace/cat_goods_group_ctr";
    }

    @PreAuthorize("hasAnyRole('SYS_ADMIN','CUS_ADMIN','ADMIN')")
    @RequestMapping("/cat_goods")
    public String redirectCatGoods(){
        return "redirect:/workspace/goods_ctr";
    }
}
