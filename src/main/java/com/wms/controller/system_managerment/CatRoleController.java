package com.wms.controller.system_managerment;

import com.google.common.collect.Lists;
import com.wms.base.BaseCommonController;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.SysRoleMenuService;
import com.wms.utils.DataUtil;
import com.wms.utils.FunctionUtils;
import com.wms.utils.ResourceBundleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/workspace/cat_role_ctr")
@Scope("session")
public class CatRoleController extends BaseCommonController {
    private Logger log = LoggerFactory.getLogger(CatRoleController.class);
    @Autowired
    SysRoleMenuService sysRoleMenuServiceImpl;

    @Autowired
    BaseService roleServiceImpl;
    @Autowired
    BaseService menuService;
    Map<String,SysMenuDTO> mapIdSysMenu = new HashMap<>();
    Map<String,String> mapRoleMenu = new HashMap<>();
    Map<String,String> mapMenuRoleId = new HashMap<>();
    @RequestMapping()
    public String home(Model model){
        model.addAttribute("menuName","menu.catrole");
        return "system_managerment/cat_role";
    }


    @RequestMapping(value = "/findByCondition",method = RequestMethod.GET)
    public  @ResponseBody
    List<SysRoleDTO> findByCondition(@RequestParam("status")String status , @RequestParam("keyword")String keyword){
        List<Condition> lstCon = Lists.newArrayList();
        if (currentUser.getRoleCode().equalsIgnoreCase("ROOT")){
//            user has root role can only seach admin roles
            lstCon.add(new Condition("code", Constants.SQL_OPERATOR.EQUAL,"ADMIN"));
        }else{
//            admin and other roles only seach roles belong its customer except admin role.
            lstCon.add(new Condition("code", Constants.SQL_OPERATOR.NOT_EQUAL,"ADMIN"));
            lstCon.add(new Condition("custId",Constants.SQL_PRO_TYPE.LONG,Constants.SQL_OPERATOR.EQUAL,selectedCustomer.getId()));
        }
        if(!DataUtil.isStringNullOrEmpty(status) && !status.equals(Constants.STATS_ALL)){
            lstCon.add(new Condition("status", Constants.SQL_OPERATOR.EQUAL,status));
        }
        if(!DataUtil.isStringNullOrEmpty(keyword) && !status.equals(Constants.STATS_ALL)){
            lstCon.add(new Condition("name", Constants.SQL_OPERATOR.LIKE,keyword));
            lstCon.add(new Condition("code", Constants.SQL_OPERATOR.LIKE,keyword));
        }

        lstCon.add(new Condition("id",Constants.SQL_OPERATOR.ORDER,"desc"));
        List<SysRoleDTO> lstRole = roleServiceImpl.findByCondition(lstCon,tokenInfo);
        StringBuilder roles = new StringBuilder();
        for (SysRoleDTO sysRoleDTO :lstRole){
            sysRoleDTO.setStatusName(mapAppStatus.get(sysRoleDTO.getStatus()));
            roles.append(",").append(sysRoleDTO.getCode());
        }
        List<Condition> lstRoleCondition = new ArrayList<>();
        lstRoleCondition.add(new Condition("roleCode",Constants.SQL_OPERATOR.IN,roles.toString().replaceFirst(",","")));
        lstRoleCondition.add(new Condition("custId",Constants.SQL_PRO_TYPE.LONG,Constants.SQL_OPERATOR.EQUAL ,selectedCustomer.getId()));
        lstRoleCondition.add(new Condition("roleCode",Constants.SQL_OPERATOR.ORDER ,"desc"));
        List<SysRoleMenuDTO> lstRoleMenu = sysRoleMenuServiceImpl.findByCondition(lstRoleCondition,tokenInfo);
//        map role list menu
        String previousRole = "";
        String menuList = "";
        for (SysRoleMenuDTO roleMenu : lstRoleMenu){
            if (!previousRole.equalsIgnoreCase(roleMenu.getRoleCode())&&!DataUtil.isNullOrEmpty(previousRole)){

               mapRoleMenu.put(previousRole,menuList.replaceFirst(",",""));
                menuList = "";
            }
               previousRole = roleMenu.getRoleCode();
               menuList = menuList + "," + roleMenu.getMenuId();


        }
         mapRoleMenu.put(previousRole,menuList.replaceFirst(",",""));
        for (SysRoleMenuDTO sysRoleMenuDTO : lstRoleMenu){
             sysRoleMenuDTO.setMenuId(mapRoleMenu.get(sysRoleMenuDTO.getRoleCode()));
        }
        return lstRole;
    }

//    @RequestMapping(value = "/getMapRoleMenu",method = RequestMethod.GET)
//    public  @ResponseBody
//    List<SysRoleDTO> getMapRoleMenu(@RequestParam("code")String roleCode){
//        List<Condition> lstCon = Lists.newArrayList();
//        if(!DataUtil.isStringNullOrEmpty(status) && !status.equals(Constants.STATS_ALL)){
//            lstCon.add(new Condition("roleCode", Constants.SQL_OPERATOR.EQUAL,roleCode));
//        }
//
//        List<SysRoleDTO> lstRole = roleServiceImpl.findByCondition(lstCon,tokenInfo);
//        for (SysRoleDTO sysRoleDTO :lstRole){
//            sysRoleDTO.setStatusName(mapAppStatus.get(sysRoleDTO.getStatus()));
//        }
//        return lstRole;
//    }
    @ModelAttribute("lstTreeModel")
    public  @ResponseBody
    List<TreeModel> getMenuData(HttpServletRequest request){

        List<SysMenuDTO> lstMenu ;
        List<TreeModel> lstTreeModel = new ArrayList<>();
        if (currentUser.getRoleCode().equalsIgnoreCase("ROOT")){
//            get full menu
            List<Condition> lstCon = Lists.newArrayList();
            lstCon.add(new Condition("status", Constants.SQL_OPERATOR.EQUAL,Constants.STATUS.ACTIVE));
            lstCon.add(new Condition("levels",Constants.SQL_OPERATOR.ORDER,"desc"));
            if(tokenInfo == null){
                this.tokenInfo =  (AuthTokenInfo) request.getSession().getAttribute("tokenInfo");
            }
            lstMenu = menuService.findByCondition(lstCon,tokenInfo);
        }else{
//            only get roles whichs be assigned to customer'admin
        }

//        for (SysMenuDTO menuitem : lstMenu){
//            menuitem.setLocalizationName(ResourceBundleUtils.getkey(menuitem.getName()) == null ?menuitem.getName(): ResourceBundleUtils.getkey(menuitem.getName()));
//            mapIdSysMenu.put(menuitem.getId(),menuitem);
//        }
//        for (SysMenuDTO menuitem : lstMenu){
//            String path = buildPath(menuitem);
//            if (!DataUtil.isNullOrEmpty(path)){
//                TreeModel treeModel = new TreeModel(menuitem.getId(),path.replaceFirst("/",""),menuitem.getLocalizationName());
//                lstTreeModel.add(treeModel);
//            }
//
//
//        }
        return lstTreeModel;
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public @ResponseBody  String add(SysRoleDTO sysRoleDTO, HttpServletRequest request){
        sysRoleDTO.setStatus("1");
        sysRoleDTO.setCustId(selectedCustomer.getId());
        ResponseObject response = roleServiceImpl.add(sysRoleDTO,tokenInfo);
        if(Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())){

            return "1|Thêm mới thành công";
        }else if(Responses.ERROR_CONSTRAINT.getName().equalsIgnoreCase(response.getStatusName()))
        {
            return "0|Thông tin đã có trên hệ thống";
        }else{
            return "0|Thêm mới không thành công";
        }
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public @ResponseBody String update(SysRoleDTO sysRoleDTO, HttpServletRequest request){
        log.info("Update menu info: "+ sysRoleDTO.toString());

        if("on".equalsIgnoreCase(sysRoleDTO.getStatus())){
            sysRoleDTO.setStatus("1");
        }else{
            sysRoleDTO.setStatus("0");
        }
        ResponseObject response = roleServiceImpl.update(sysRoleDTO,tokenInfo);
        if(Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())){
            log.info("SUCCESS");
            return "1|Cập nhật thành công";
        }else if(Responses.ERROR_CONSTRAINT.getName().equalsIgnoreCase(response.getStatusName())){
            log.info("ERROR");
            return "0|Thông tin đã có trên hệ thống";
        }
        else{
            log.info("ERROR");
            return "0|Cập nhật không thành công";
        }

    }

    @RequestMapping(value = "/assignRoleMenu",method = RequestMethod.POST)
    public @ResponseBody String assignRoleMenu(SysRoleDTO sysRoleDTO, HttpServletRequest request){
        log.info("assignRoleMenu: "+ sysRoleDTO.toString());

        ResponseObject response = roleServiceImpl.update(sysRoleDTO,tokenInfo);
        if(Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())){
            log.info("SUCCESS");
            return "1|Gán quyền thành công";
        }else if(Responses.ERROR_CONSTRAINT.getName().equalsIgnoreCase(response.getStatusName())){
            log.info("ERROR");
            return "0|Gán quyền không thành công";
        }
        else{
            log.info("ERROR");
            return "0|Gán quyền không thành công";
        }

    }
    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    public @ResponseBody String delete(@RequestParam("id")String id, HttpServletRequest request){
        try {
            Long idL = Long.parseLong(id);
            SysRoleDTO deleteObject = (SysRoleDTO) roleServiceImpl.findById(idL,tokenInfo);
            deleteObject.setStatus(Constants.STATUS.IN_ACTIVE);
            ResponseObject response = roleServiceImpl.update(deleteObject, tokenInfo);
            if(Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())){
                request.getSession().setAttribute("isCatPartnerModified",true);
                return "1|Xoá thành công";
            }else{
                return "0|Xoá không thành công";
            }
        } catch (NumberFormatException e) {
            return "0|Xoá không thành công lỗi convert long";
        }
    }
    public String buildPath(SysMenuDTO item){
        String path = "";
        SysMenuDTO parrentItem =mapIdSysMenu.get(item.getParentId());
        if (parrentItem!=null){
            path = buildPath(parrentItem) +"/"+ parrentItem.getLocalizationName();
        }
        return path;
    }
}
