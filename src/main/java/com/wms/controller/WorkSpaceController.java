package com.wms.controller;

import com.google.common.collect.Lists;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
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

    @Autowired
    UserService userService;

    @Autowired
    BaseService roleService;

    Map<String,String> mapRoles;

    @ModelAttribute("mapRoles")
    public Map<String,String> mapRoles(){
        Map<String,String> mapRole = new HashMap<>();
        List<RoleDTO> lstRole = roleService.getAll();
        if(DataUtil.isListNullOrEmpty(lstRole)){
            return mapRole;
        }
        for(RoleDTO i: lstRole){
            mapRole.put(i.getId(),i.getRoleName());
        }
        this.mapRoles = mapRole;
        return mapRole;
    }

    @RequestMapping()
    public String home(Model model, HttpServletRequest request){
        Object isLogin = request.getSession().getAttribute("isLogin");
        List<ActionMenuDTO> lstActionMenu= (List<ActionMenuDTO>) request.getSession().getAttribute("lstUserAction");
        model.addAttribute("lstUserAction",lstActionMenu);
        model.addAttribute("action-info","");
        if(isLogin != null){
            return "workspace/wms_workspace";
        }else{
            return "index";
        }
    }

    @RequestMapping("/logout")
        public String logout(HttpServletRequest request){
            request.getSession().invalidate();
            //
            MDC.clear();
            return "redirect:/";
    }

    @RequestMapping(value = "/getuser",method = RequestMethod.GET,produces="application/json")
    public @ResponseBody List<User> getUser(@RequestParam("username")String username,@RequestParam("email")String email,
                                            @RequestParam("status")String status,@RequestParam("role")String role,
                                            @RequestParam("startDate")String startDate,@RequestParam("endDate")String endDate
    ){
        List<Condition> lstCondition = Lists.newArrayList();
            lstCondition.add(new Condition("username", Constants.SQL_OPERATOR.LIKE,username));
            lstCondition.add(new Condition("email", Constants.SQL_OPERATOR.LIKE,email));

        if(!DataUtil.isStringNullOrEmpty(status) && !status.equals(Constants.STATS_ALL)){
            lstCondition.add(new Condition("status", Constants.SQL_OPERATOR.EQUAL,status));
        }

        if(!DataUtil.isStringNullOrEmpty(role) && !role.equals(Constants.STATS_ALL)){
            lstCondition.add(new Condition("roleId", Constants.SQL_OPERATOR.EQUAL,role));
        }

        if(!DataUtil.isStringNullOrEmpty(startDate) && !DataUtil.isStringNullOrEmpty(endDate)){
            lstCondition.add(new Condition("createDate", Constants.SQL_OPERATOR.BETWEEN,startDate + "|"+ endDate));
        }

        return userService.findUserByCondition(lstCondition);
    }

    @RequestMapping(value = "/login",method = RequestMethod.GET,produces="text/plain")
    public @ResponseBody String login(){
       return "Success";
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public String add(User addUser,RedirectAttributes redirectAttributes){
        addUser.setPassword(DataUtil.MD5Encrypt("wms"));
        addUser.setRoleName(mapRoles.get(addUser.getRoleId()));
        log.info("Register user info: "+ addUser.toString());

        ResponseObject responseObject = userService.register(addUser);

        if(responseObject == null || responseObject.getStatusName().equalsIgnoreCase(Responses.ERROR.getName())){
            log.info("ERROR");
            redirectAttributes.addFlashAttribute("actionInfo","Lỗi: " +addUser.getUsername());
        }else if(responseObject.getStatusName().equalsIgnoreCase(Responses.ERROR_CONSTRAINT.getName())){
            log.info("ERROR CONSTRAINT");
            redirectAttributes.addFlashAttribute("actionInfo","Lỗi:" +addUser.getUsername()+ " đã có trên hệ thống!");
        }else{
            redirectAttributes.addFlashAttribute("actionInfo","Thành công: " +addUser.getUsername());
            redirectAttributes.addFlashAttribute("successStyle",Constants.SUCCES_COLOR);
            log.info("SUCCESS");
        }

        return "redirect:/workspace";
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public String update(User updateUser, RedirectAttributes redirectAttributes){
        updateUser.setRoleName(mapRoles.get(updateUser.getRoleId()));
        log.info("Update user info: "+ updateUser.toString());

        if(userService.update(updateUser)){
            log.info("SUCCESS");
            redirectAttributes.addFlashAttribute("actionInfo", "Cập nhật thành công: " +updateUser.getUsername());
            redirectAttributes.addFlashAttribute("successStyle",Constants.SUCCES_COLOR);
        }else{
            log.info("ERROR");
            redirectAttributes.addFlashAttribute("actionInfo","Cập nhật không thành công: " +updateUser.getUsername());
        }
        return  "redirect:/workspace";
    }

    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    public @ResponseBody String delete(@RequestParam("userId")String userId,Model model){
        try {
            Long id = Long.parseLong(userId);
            if(userService.delelte(id)){
                return "1|Xoá thành công";
            }else{
                return "0|Xoá không thành công";
            }
        } catch (NumberFormatException e) {
            return "0|Xoá không thành công lỗi convert long";
        }
    }

    @RequestMapping("/wms")
    public String wms(){
        return "index_boot";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public String uploadFile(@RequestParam("uploadfile") MultipartFile uploadfile,
                             @RequestParam("username") String username
                             ) {
        if(uploadfile == null || DataUtil.isStringNullOrEmpty(uploadfile.getOriginalFilename())){
            return "Không có file upload!";
        }

        String filename  = username+ "_" + DateTimeUtils.getSysDate_ddMMyyyy_HH_mm_ss()+ "_" + uploadfile.getOriginalFilename();

        if(!FunctionUtils.saveUploadedFile(uploadfile,filename)){
            return "Lỗi: đóng file trước khi upload!";
        }
        return "Upload thành công";
    }
}
