package com.wms.controller.common_managerment;

import com.google.common.collect.Lists;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.CatUserService;
import com.wms.utils.DataUtil;
import com.wms.utils.DateTimeUtils;
import com.wms.utils.FunctionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by duyot on 12/5/2016.
 */
@Controller
@RequestMapping("/list_employee")
public class ListEmployeeController {
    Logger log = LoggerFactory.getLogger(ListEmployeeController.class);

    @Autowired
    CatUserService catUserService;

    @Autowired
    BaseService roleService;

    Map<String,String> mapRoles;

    private AuthTokenInfo tokenInfo;

    @ModelAttribute("tokenInfo")
    public void setTokenInfo(HttpServletRequest request){
        this.tokenInfo =  (AuthTokenInfo) request.getSession().getAttribute("tokenInfo");
    }

    @ModelAttribute("mapRoles")
    public Map<String,String> mapRoles(){
        Map<String,String> mapRole = new HashMap<>();
        List<RoleDTO> lstRole = roleService.getAll(tokenInfo);
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
    public String home(HttpServletRequest request){
        return "common_managerment/list_employee";
    }




    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public String add(CatUserDTO addCatUserDTO, RedirectAttributes redirectAttributes){
        addCatUserDTO.setPassword(DataUtil.BCryptPasswordEncoder("wms"));
        addCatUserDTO.setRoleName(mapRoles.get(addCatUserDTO.getRoleName()));
        log.info("Register user info: "+ addCatUserDTO.toString());

        ResponseObject responseObject = catUserService.register(addCatUserDTO,tokenInfo);

        if(responseObject == null || responseObject.getStatusName().equalsIgnoreCase(Responses.ERROR.getName())){
            log.info("ERROR");
            redirectAttributes.addFlashAttribute("actionInfo","Lỗi: " + addCatUserDTO.getCode());
        }else if(responseObject.getStatusName().equalsIgnoreCase(Responses.ERROR_CONSTRAINT.getName())){
            log.info("ERROR CONSTRAINT");
            redirectAttributes.addFlashAttribute("actionInfo","Lỗi:" + addCatUserDTO.getCode()+ " đã có trên hệ thống!");
        }else{
            redirectAttributes.addFlashAttribute("actionInfo","Thành công: " + addCatUserDTO.getCode());
            redirectAttributes.addFlashAttribute("successStyle",Constants.SUCCES_COLOR);
            log.info("SUCCESS");
        }

        return "redirect:/workspace";
    }

    @RequestMapping(value = "/getuser",method = RequestMethod.GET,produces="application/json")
    public @ResponseBody
    List<CatUserDTO> getUser(@RequestParam("username")String username, @RequestParam("email")String email,
                             @RequestParam("status")String status, @RequestParam("role")String role,
                             @RequestParam("startDate")String startDate, @RequestParam("endDate")String endDate
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

//        return catUserService.f(lstCondition,tokenInfo);
        return null;
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public String update(CatUserDTO updateCatUserDTO, RedirectAttributes redirectAttributes){
        updateCatUserDTO.setRoleName(mapRoles.get(updateCatUserDTO.getRoleName()));
        log.info("Update user info: "+ updateCatUserDTO.toString());

//        ResponseObject response = catUserService.update(updateCatUserDTO,tokenInfo);
        ResponseObject response = null;
        if(Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusName())){
            log.info("SUCCESS");
            redirectAttributes.addFlashAttribute("actionInfo", "Cập nhật thành công: " + updateCatUserDTO.getCode());
            redirectAttributes.addFlashAttribute("successStyle",Constants.SUCCES_COLOR);
        }else{
            log.info("ERROR");
            redirectAttributes.addFlashAttribute("actionInfo","Cập nhật không thành công: " + updateCatUserDTO.getCode());
        }
        return  "redirect:/workspace";
    }

    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    public @ResponseBody String delete(@RequestParam("userId")String userId,Model model){
        try {
            Long id = Long.parseLong(userId);
//            ResponseObject response = catUserService.delete(id,tokenInfo);
            ResponseObject response = null;
            if(Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusName())){
                return "1|Xoá thành công";
            }else{
                return "0|Xoá không thành công";
            }
        } catch (NumberFormatException e) {
            return "0|Xoá không thành công lỗi convert long";
        }
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
