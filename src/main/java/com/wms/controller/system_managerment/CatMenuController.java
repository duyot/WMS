package com.wms.controller.system_managerment;

import com.google.common.collect.Lists;
import com.wms.base.BaseCommonController;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.controller.category.CatPartnerController;
import com.wms.dto.CatPartnerDTO;
import com.wms.dto.Condition;
import com.wms.dto.ResponseObject;
import com.wms.dto.SysMenuDTO;
import com.wms.services.interfaces.BaseService;
import com.wms.utils.BundleUtils;
import com.wms.utils.DataUtil;
import com.wms.utils.ResourceBundleUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/workspace/cat_menu_ctr")
@Scope("session")
public class CatMenuController extends BaseCommonController {

    private Logger log = LoggerFactory.getLogger(CatMenuController.class);

    @Autowired
    BaseService menuService;
    Map<String,SysMenuDTO> mapIdSysMenu = new HashMap<>();
    @RequestMapping()
    public String home(Model model){
        model.addAttribute("menuName","menu.catmenu");
        return "system_managerment/cat_menu";
    }


    @RequestMapping(value = "/findByCondition",method = RequestMethod.GET)
    public  @ResponseBody
    List<SysMenuDTO> findByCondition(@RequestParam("status")String status , @RequestParam("keyword")String keyword){
        List<Condition> lstCon = Lists.newArrayList();
        if(!DataUtil.isStringNullOrEmpty(status) && !status.equals(Constants.STATS_ALL)){
            lstCon.add(new Condition("status", Constants.SQL_OPERATOR.EQUAL,status));
        }
        if(!DataUtil.isStringNullOrEmpty(keyword) && !status.equals(Constants.STATS_ALL)){
            lstCon.add(new Condition("name", Constants.SQL_OPERATOR.LIKE,keyword));
            lstCon.add(new Condition("code", Constants.SQL_OPERATOR.LIKE,keyword));
        }
//
        lstCon.add(new Condition("id",Constants.SQL_OPERATOR.ORDER,"desc"));
        List<SysMenuDTO> lstMenu = menuService.findByCondition(lstCon,tokenInfo);
        for (SysMenuDTO menuitem : lstMenu){
            menuitem.setStatusName(mapAppStatus.get(menuitem.getStatus()));
            menuitem.setLocalizationName(ResourceBundleUtils.getkey(menuitem.getName()) == null ?menuitem.getName(): ResourceBundleUtils.getkey(menuitem.getName()));
            mapIdSysMenu.put(menuitem.getId(),menuitem);
        }
        for (SysMenuDTO menuitem : lstMenu){
            SysMenuDTO parrentItem =mapIdSysMenu.get(menuitem.getParentId());
            if (parrentItem!=null){
                menuitem.setParentMenu(parrentItem.getLocalizationName());
            }

        }
       return lstMenu;
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public @ResponseBody  String add(SysMenuDTO sysMenuDTO, HttpServletRequest request){
        sysMenuDTO.setStatus("1");
        if (sysMenuDTO.getParentId().equalsIgnoreCase("0")){
            sysMenuDTO.setLevels("1");
        }else{
            SysMenuDTO parent = mapIdSysMenu.get(sysMenuDTO.getParentId());
            int lever = Integer.parseInt(parent.getLevels()) + 1;
            sysMenuDTO.setLevels(String.valueOf(lever));
        }
        ResponseObject response = menuService.add(sysMenuDTO,tokenInfo);
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
    public @ResponseBody String update(SysMenuDTO sysMenuDTO, HttpServletRequest request){
        log.info("Update menu info: "+ sysMenuDTO.toString());

        if("on".equalsIgnoreCase(sysMenuDTO.getStatus())){
            sysMenuDTO.setStatus("1");
        }else{
            sysMenuDTO.setStatus("0");
        }
        ResponseObject response = menuService.update(sysMenuDTO,tokenInfo);
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

    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    public @ResponseBody String delete(@RequestParam("id")String id, HttpServletRequest request){
        try {
            Long idL = Long.parseLong(id);
            SysMenuDTO deleteObject = (SysMenuDTO) menuService.findById(idL,tokenInfo);
            deleteObject.setStatus(Constants.STATUS.IN_ACTIVE);
            ResponseObject response = menuService.update(deleteObject, tokenInfo);
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
}
