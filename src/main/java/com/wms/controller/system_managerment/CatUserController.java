package com.wms.controller.system_managerment;

import com.wms.base.BaseCommonController;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.StockService;
import com.wms.utils.DataUtil;
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
@RequestMapping("/workspace/cat_user_ctr")
@Scope("session")
public class CatUserController extends BaseCommonController {

    private Logger log = LoggerFactory.getLogger(CatUserController.class);
    @Autowired
    BaseService catDeptServicesImpl;
    @Autowired
    BaseService catUserServices;

    @Autowired
    BaseService roleServiceImpl;
    @Autowired
    StockService catStockService;
    @Autowired
    BaseService mapUserStockServiceImpl;
    List<TreeModel> lstTreeModal = new ArrayList<>();
    Map<String,CatDepartmentDTO> mapIdDept = new HashMap<>();
    List<String> lstParentDeptId = new ArrayList<>();
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
        lstCon.add(new Condition("status", Constants.SQL_PRO_TYPE.BYTE, Constants.SQL_OPERATOR.EQUAL,status));
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
            mapIdDept.clear();
            List<Condition> lstConDept = new ArrayList<>();
            lstConDept.add(new Condition("status", Constants.SQL_OPERATOR.EQUAL, Constants.STATUS.ACTIVE));
            List<CatDepartmentDTO> lstDepts = new ArrayList<>();
            if (!isRoot){
                lstConDept.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG ,Constants.SQL_OPERATOR.EQUAL,selectedCustomer.getId()));
            }
            lstDepts = catDeptServicesImpl.findByCondition(lstConDept,tokenInfo);
            for (CatDepartmentDTO item : lstDepts){
                mapIdDept.put(item.getId(),item);
            }

        for (CatUserDTO catUserDTO :lstUsers){
            catUserDTO.setStatusName(mapAppStatus.get(catUserDTO.getStatus()));
            if (!DataUtil.isNullOrEmpty(catUserDTO.getDeptId())){
                catUserDTO.setDeptName(mapIdDept.get(catUserDTO.getDeptId()).getName());
            }

        }
        return lstUsers;
    }


    @RequestMapping(value = "/getAllDepartment",method = RequestMethod.GET)
    public  @ResponseBody List<CatDepartmentDTO> getAllDepartment(@RequestParam("status")String status ){

        List<Condition> lstCon = new ArrayList<>();
        List<CatDepartmentDTO> lstDepts = new ArrayList<>();
        lstCon.add(new Condition("status", Constants.SQL_OPERATOR.EQUAL, Constants.STATUS.ACTIVE));
        if (!isRoot){
            lstCon.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG ,Constants.SQL_OPERATOR.EQUAL,selectedCustomer.getId()));

            lstDepts = catDeptServicesImpl.findByCondition(lstCon,tokenInfo);
        }

        return lstDepts;
    }
    @ModelAttribute("lstTreeModel")
    public  @ResponseBody List<TreeModel> getTreeDepartment(HttpServletRequest request){
        if (isRoot){
            return new ArrayList<>();
        }
        lstTreeModal = new ArrayList<>();

        if(selectedCustomer == null){
            this.selectedCustomer =  (CatCustomerDTO) request.getSession().getAttribute("selectedCustomer");
        }
        List<Condition> lstCon = new ArrayList<>();
        lstCon.add(new Condition("status", Constants.SQL_OPERATOR.EQUAL, Constants.STATUS.ACTIVE));
        lstCon.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG ,Constants.SQL_OPERATOR.EQUAL,selectedCustomer.getId()));
        if(tokenInfo == null){
            this.tokenInfo =  (AuthTokenInfo) request.getSession().getAttribute("tokenInfo");
        }
        List<CatDepartmentDTO> lstDepts = new ArrayList<>();
        lstDepts = catDeptServicesImpl.findByCondition(lstCon,tokenInfo);

        mapIdDept.clear();
        for (CatDepartmentDTO item : lstDepts){
            mapIdDept.put(item.getId(),item);
        }
        lstParentDeptId.clear();
        for (CatDepartmentDTO item : lstDepts){
            String path = buildPath(item,mapIdDept);
            if (!DataUtil.isNullOrEmpty(path)){
                TreeModel treeModel;
                treeModel = new TreeModel(item.getId(),path.replaceFirst("/",""),item.getName());
                lstTreeModal.add(treeModel);
            }
        }
        List<TreeModel> lstRemoveTree = new ArrayList<>();
        for (TreeModel item:lstTreeModal){
            if (lstParentDeptId.contains(item.getId())){
                lstRemoveTree.add(item);
            }
        }
        lstTreeModal.removeAll(lstRemoveTree);
        return lstTreeModal;
    }
    @RequestMapping(value = "/getRoles",method = RequestMethod.GET)
    public  @ResponseBody List<SysRoleDTO> getRoles(@RequestParam("custId")String custId ){

        List<Condition> lstCon = new ArrayList<>();
        lstCon.add(new Condition("status", Constants.SQL_PRO_TYPE.BYTE, Constants.SQL_OPERATOR.EQUAL, Constants.STATUS.ACTIVE));
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
    public @ResponseBody String updateUserRole(@RequestParam("userId")String userId,@RequestParam("roleId")String roleId , @RequestParam("roleName")String roleName ,@RequestParam("block")String block){
        try {
            Long idL = Long.parseLong(userId);
            CatUserDTO catUserDTO = (CatUserDTO) catUserServices.findById(idL,tokenInfo);
            catUserDTO.setRoleId(roleId);
            catUserDTO.setRoleName(roleName);
            if (block.equalsIgnoreCase("0")){
                catUserDTO.setBlock("1");
            }else {
                catUserDTO.setBlock("0");
            }

            ResponseObject response = catUserServices.update(catUserDTO, tokenInfo);
            if(Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())){
                return ResourceBundleUtils.getkey(Constants.RESPONSE.UPDATE_SUSSESS);
            }else{
                return ResourceBundleUtils.getkey(Constants.RESPONSE.UPDATE_ERROR);
            }
        } catch (NumberFormatException e) {
            return ResourceBundleUtils.getkey(Constants.RESPONSE.UPDATE_ERROR);
        }
    }
    @RequestMapping(value = "/updateDepartment",method = RequestMethod.GET)
    public @ResponseBody String updateDepartment(@RequestParam("userId")String userId,@RequestParam("deptId")String deptId){
        try {
            Long idL = Long.parseLong(userId);
            CatUserDTO catUserDTO = (CatUserDTO) catUserServices.findById(idL,tokenInfo);
            catUserDTO.setDeptId(deptId);
            catUserDTO.setDeptName(mapIdDept.get(deptId).getName());
            ResponseObject response = catUserServices.update(catUserDTO, tokenInfo);
            if(Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())){

                return ResourceBundleUtils.getkey(Constants.RESPONSE.UPDATE_SUSSESS);
            }else{
                return ResourceBundleUtils.getkey(Constants.RESPONSE.UPDATE_ERROR);
            }
        } catch (NumberFormatException e) {
            return ResourceBundleUtils.getkey(Constants.RESPONSE.UPDATE_ERROR);
        }
    }
    @RequestMapping(value = "/updateUserStock",method = RequestMethod.GET)
    public @ResponseBody String updateUserStock(@RequestParam("userId")String userId,@RequestParam("stockId")String stockId ){
        try {
            stockId = stockId.replaceFirst(",","");
            List<Condition> lstCon = new ArrayList<>();
            boolean isError = false;
    //        delete all stock of this user
            lstCon.add(new Condition("userId" , Constants.SQL_PRO_TYPE.LONG,Constants.SQL_OPERATOR.EQUAL,userId));
            String result = mapUserStockServiceImpl.deleteByCondition(lstCon,tokenInfo);
            if (Responses.SUCCESS.getName().equalsIgnoreCase(result) ){
                if ( !DataUtil.isNullOrEmpty(stockId)){
                    List<MapUserStockDTO> lstMapUserStock = new ArrayList<>();
                    String[] stockids = stockId.split(",");
                    for (int i = 0 ; i <stockids.length ; i ++){
                        lstMapUserStock.add(new MapUserStockDTO(null,userId,stockids[i]));
                    }
                    ResponseObject response =  mapUserStockServiceImpl.addList(lstMapUserStock,tokenInfo);
                    if(!Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())){
                        isError = true;
                    }
                }

            }else{
                isError = true;
            }
                if(!isError){
                    return ResourceBundleUtils.getkey(Constants.RESPONSE.UPDATE_SUSSESS);
                }else{
                    return ResourceBundleUtils.getkey(Constants.RESPONSE.UPDATE_ERROR);
                }

        } catch (NumberFormatException e) {
            return ResourceBundleUtils.getkey(Constants.RESPONSE.UPDATE_ERROR);
        }
    }
    @RequestMapping(value = "/getListStock",method = RequestMethod.GET)
    public @ResponseBody UserStock getListStock(@RequestParam("custId")String custId,@RequestParam("userId")String userId){
        List<Condition> lstCon = new ArrayList<>();
        lstCon.add(new Condition("custId" , Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL,custId));
        lstCon.add(new Condition("status" , Constants.SQL_PRO_TYPE.BYTE, Constants.SQL_OPERATOR.EQUAL,Constants.STATUS.ACTIVE));
        List<CatStockDTO> lstStock = catStockService.findByCondition(lstCon,tokenInfo);
//        get current stocks of this user
        List<Condition> lstConUserStock = new ArrayList<>();
        lstConUserStock.add(new Condition("userId" , Constants.SQL_PRO_TYPE.LONG,Constants.SQL_OPERATOR.EQUAL,userId));
        List<MapUserStockDTO> lstMapUserStock = mapUserStockServiceImpl.findByCondition(lstConUserStock,tokenInfo);
        String[] stocksId = new String[lstMapUserStock.size()];
        for(int i =0;i<lstMapUserStock.size();i++){
            stocksId[i]=lstMapUserStock.get(i).getStockId();
        }
        return new UserStock(lstStock,stocksId);
    }

    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    public @ResponseBody String delete(@RequestParam("id")String id, HttpServletRequest request){
        try {
            Long idL = Long.parseLong(id);
            CatUserDTO catUserDTO = (CatUserDTO) catUserServices.findById(idL,tokenInfo);
            catUserDTO.setStatus(Constants.STATUS.IN_ACTIVE);
            ResponseObject response = catUserServices.update(catUserDTO, tokenInfo);
            if(Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())){
                return ResourceBundleUtils.getkey(Constants.RESPONSE.DELETE_SUSSESS);
            }else{
                return ResourceBundleUtils.getkey(Constants.RESPONSE.DELETE_ERROR);
            }
        } catch (NumberFormatException e) {
            return ResourceBundleUtils.getkey(Constants.RESPONSE.DELETE_ERROR);
        }
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public @ResponseBody  String add(CatUserDTO catUserDTO, HttpServletRequest request){
        catUserDTO.setStatus("1");
        catUserDTO.setBlock("0");
        if (!isRoot){
            catUserDTO.setCustId(selectedCustomer.getId());
        }

        ResponseObject response = catUserServices.add(catUserDTO,tokenInfo);
        try {
            Long idL = Long.parseLong(response.getStatusCode());
            return ResourceBundleUtils.getkey(Constants.RESPONSE.INSERT_SUSSESS);
          }  catch (NumberFormatException e) {
            return ResourceBundleUtils.getkey(Constants.RESPONSE.INSERT_ERROR);
         }
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public @ResponseBody String update(CatUserDTO catUserDTO, HttpServletRequest request){
        log.info("Update menu info: "+ catUserDTO.toString());

        if("on".equalsIgnoreCase(catUserDTO.getStatus())){
            catUserDTO.setStatus("1");
        }else{
            catUserDTO.setStatus("0");
        }

        ResponseObject response = catUserServices.update(catUserDTO,tokenInfo);
        if(Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())){
            log.info("SUCCESS");
            return ResourceBundleUtils.getkey(Constants.RESPONSE.UPDATE_SUSSESS);
        }else if(Responses.ERROR_CONSTRAINT.getName().equalsIgnoreCase(response.getStatusName())){
            log.info("ERROR");
            return ResourceBundleUtils.getkey(Constants.RESPONSE.UPDATE_ERROR);
        }
        else{
            log.info("ERROR");
            return ResourceBundleUtils.getkey(Constants.RESPONSE.UPDATE_ERROR);
        }

    }


    @RequestMapping(value = "/resetPassWord",method = RequestMethod.POST)
    public @ResponseBody String register(CatUserDTO registerCatUserDTO){
        log.info("Register user info: "+ registerCatUserDTO.toString());
       String userId =  registerCatUserDTO.getId();
       Long id =Long.parseLong(userId);
        CatUserDTO catUserDTO =  (CatUserDTO)catUserServices.findById(id,tokenInfo);
        if (catUserDTO == null){
            return ResourceBundleUtils.getkey(Constants.RESPONSE.UPDATE_ERROR);
        }
        catUserDTO.setPassword(DataUtil.BCryptPasswordEncoder(registerCatUserDTO.getPassword()));
        ResponseObject responseObject = catUserServices.update(catUserDTO,tokenInfo);
        if(responseObject == null || !Responses.SUCCESS.getName().equalsIgnoreCase(responseObject.getStatusCode())){
            return ResourceBundleUtils.getkey(Constants.RESPONSE.UPDATE_ERROR);
        }
        return ResourceBundleUtils.getkey(Constants.RESPONSE.UPDATE_SUSSESS);

    }

    public String buildPath(CatDepartmentDTO item,Map<String,CatDepartmentDTO>  mapIdDept){
        String path = "";
        CatDepartmentDTO parrentItem =mapIdDept.get(item.getParentId());
        if (parrentItem!=null){
            path = buildPath(parrentItem,mapIdDept) +"/"+ parrentItem.getName();
            lstParentDeptId.add(parrentItem.getId());
        }
        return path;
    }
}
