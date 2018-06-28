package com.wms.controller.system_managerment;

import com.wms.base.BaseCommonController;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.utils.DataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/workspace/cat_user_ctr")
@Scope("session")
public class CatUserController extends BaseCommonController {


    @Autowired
    BaseService catDeptServicesImpl;
    @Autowired
    BaseService catUserServices;

    @Autowired
    BaseService roleServiceImpl;

    boolean isRoot = false;
    @RequestMapping()
    public String home(Model model){
        model.addAttribute("menuName","menu.catuser");
        model.addAttribute("controller","/workspace/cat_user_ctr/");
        isRoot = currentUser.getSysRoleDTO().getType().equalsIgnoreCase("1")?true:false ;
        model.addAttribute("isRoot",isRoot);
        return "system_managerment/cat_user";
    }

    @RequestMapping(value = "/findByCondition",method = RequestMethod.GET)
    public  @ResponseBody List<CatUserDTO> findByCondition(@RequestParam("status")String status , @RequestParam("keyword")String keyword, @RequestParam("deptId")String deptId){
        List<Condition> lstCon = new ArrayList<>();
        lstCon.add(new Condition("status", Constants.SQL_OPERATOR.EQUAL,status));
        if(!DataUtil.isStringNullOrEmpty(keyword)){
            lstCon.add(new Condition("name", Constants.SQL_OPERATOR.LIKE,keyword));
            lstCon.add(new Condition("code", Constants.SQL_OPERATOR.LIKE,keyword));
        }
        if (!DataUtil.isStringNullOrEmpty(deptId)&& !deptId.equalsIgnoreCase("0")){{
            lstCon.add(new Condition("deptId", Constants.SQL_PRO_TYPE.LONG ,Constants.SQL_OPERATOR.EQUAL,deptId));
        }}
        if (!isRoot){
            lstCon.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG ,Constants.SQL_OPERATOR.EQUAL,selectedCustomer.getId()));

        }

        List<CatUserDTO> lstUsers = catUserServices.findByCondition(lstCon,tokenInfo);
        for (CatUserDTO catUserDTO :lstUsers){
            catUserDTO.setStatusName(mapAppStatus.get(catUserDTO.getStatus()));
        }
        return lstUsers;
    }


    @RequestMapping(value = "/getAllDepartment",method = RequestMethod.GET)
    public  @ResponseBody List<CatDepartmentDTO> getAllDepartment(@RequestParam("status")String status ){

        List<Condition> lstCon = new ArrayList<>();
        lstCon.add(new Condition("status", Constants.SQL_OPERATOR.EQUAL, Constants.STATUS.ACTIVE));
        if (!isRoot){
            lstCon.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG ,Constants.SQL_OPERATOR.EQUAL,selectedCustomer.getId()));

        }
        List<CatDepartmentDTO> lstDepts = new ArrayList<>();
        lstDepts = catDeptServicesImpl.findByCondition(lstCon,tokenInfo);
        return lstDepts;
    }
    @RequestMapping(value = "/getRoles",method = RequestMethod.GET)
    public  @ResponseBody List<SysRoleDTO> getRoles(@RequestParam("custId")String custId ){

        List<Condition> lstCon = new ArrayList<>();
        lstCon.add(new Condition("status", Constants.SQL_OPERATOR.EQUAL, Constants.STATUS.ACTIVE));
        if (isRoot){
            lstCon.add(new Condition("type",Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, "2"));
        }else {
            lstCon.add(new Condition("custId",Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, custId));
        }

        List<SysRoleDTO> lstSysRoles = new ArrayList<>();
        lstSysRoles = roleServiceImpl.findByCondition(lstCon,tokenInfo);
        return lstSysRoles;
    }


    @RequestMapping(value = "/updateUserRole",method = RequestMethod.GET)
    public @ResponseBody String updateUserRole(@RequestParam("userId")String userId,@RequestParam("roleId")String roleId , @RequestParam("block")String block){
        try {
            Long idL = Long.parseLong(userId);
            CatUserDTO catUserDTO = (CatUserDTO) catUserServices.findById(idL,tokenInfo);
            catUserDTO.setRoleId(roleId);
            if (block.equalsIgnoreCase("0")){
                catUserDTO.setBlock("1");
            }else {
                catUserDTO.setBlock("0");
            }

            ResponseObject response = catUserServices.update(catUserDTO, tokenInfo);
            if(Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())){
                return "1|update thành công";
            }else{
                return "0|update không thành công";
            }
        } catch (NumberFormatException e) {
            return "0|update không thành công lỗi convert long";
        }
    }

    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    public @ResponseBody String delete(@RequestParam("id")String id, HttpServletRequest request){
        try {
            Long idL = Long.parseLong(id);
            CatDepartmentDTO catDepartmentDTO = (CatDepartmentDTO) catDeptServicesImpl.findById(idL,tokenInfo);
            catDepartmentDTO.setStatus(Constants.STATUS.IN_ACTIVE);
            ResponseObject response = catDeptServicesImpl.update(catDepartmentDTO, tokenInfo);
            if(Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())){
                return "1|Xoá thành công";
            }else{
                return "0|Xoá không thành công";
            }
        } catch (NumberFormatException e) {
            return "0|Xoá không thành công lỗi convert long";
        }
    }

}
