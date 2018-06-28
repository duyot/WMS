package com.wms.controller.system_managerment;

import com.wms.base.BaseCommonController;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.StockService;
import com.wms.utils.DataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
                    return "1|update thành công";
                }else{
                    return "0|update không thành công";
                }

        } catch (NumberFormatException e) {
            return "0|update không thành công lỗi convert long";
        }
    }
    @RequestMapping(value = "/getListStock",method = RequestMethod.GET)
    public @ResponseBody UserStock getListStock(@RequestParam("custId")String custId,@RequestParam("userId")String userId){
        List<Condition> lstCon = new ArrayList<>();
        lstCon.add(new Condition("custId" , Constants.SQL_PRO_TYPE.LONG,Constants.SQL_OPERATOR.EQUAL,custId));
        lstCon.add(new Condition("status" ,Constants.SQL_OPERATOR.EQUAL,"1"));
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
                return "1|Xoá thành công";
            }else{
                return "0|Xoá không thành công";
            }
        } catch (NumberFormatException e) {
            return "0|Xoá không thành công lỗi convert long";
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
            return "1|Thêm mới thành công";
          }  catch (NumberFormatException e) {
            return "0|Thêm mới không thành công";
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

}
