package com.wms.controller.category;

import com.google.common.collect.Lists;
import com.wms.base.BaseCommonController;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.utils.DataUtil;
import com.wms.utils.FunctionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by duyot on 12/6/2016.
 */
@Controller
@RequestMapping("/workspace/cat_goods_group_ctr")
@Scope("session")
public class CatGoodsGroupController extends BaseCommonController{
    Logger log = LoggerFactory.getLogger(CatGoodsGroupController.class);
    @Autowired
    BaseService catGoodsGroupService;

    @RequestMapping()
    public String home(Model model){
        model.addAttribute("menuName","menu.catgoodsgroup");
        return "category/cat_goods_group";
    }

    @RequestMapping(value = "/findByCondition",method = RequestMethod.GET)
    public  @ResponseBody List<CatGoodsGroupDTO> findByCondition(@RequestParam("status")String status){
        List<Condition> lstCon = Lists.newArrayList();
        lstCon.add(new Condition("custId",Constants.SQL_PRO_TYPE.LONG,Constants.SQL_OPERATOR.EQUAL,selectedCustomer.getId()));
        lstCon.add(new Condition("status",Constants.SQL_OPERATOR.NOT_EQUAL,Constants.STATUS.DELETED));

        if(!DataUtil.isStringNullOrEmpty(status) && !status.equals(Constants.STATS_ALL)){
            lstCon.add(new Condition("status", Constants.SQL_OPERATOR.EQUAL,status));
        }
        lstCon.add(new Condition("id",Constants.SQL_OPERATOR.ORDER,"desc"));

        List<CatGoodsGroupDTO> lstCatGoods = catGoodsGroupService.findByCondition(lstCon,tokenInfo);

        for(CatGoodsGroupDTO i: lstCatGoods){
            i.setName(StringEscapeUtils.escapeHtml(i.getName()));
            i.setCustName(selectedCustomer.getName());
            i.setStatusName(mapAppStatus.get(i.getStatus()));
        }

        return lstCatGoods;
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public @ResponseBody String add(CatGoodsGroupDTO catGoodsGroup, HttpServletRequest request){
        catGoodsGroup.setStatus("1");
        catGoodsGroup.setCustId(this.selectedCustomer.getId());
        ResponseObject response = catGoodsGroupService.add(catGoodsGroup,tokenInfo);
        if(Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())){
            log.info("Add: "+ catGoodsGroup.toString()+" SUCCESS");
            request.getSession().setAttribute("isCatGoodsGroupModified",true);
            return "1|Thêm mới thành công";
        }else if(Responses.ERROR_CONSTRAINT.getName().equalsIgnoreCase(response.getStatusName()))
        {
            log.info("Add: "+ catGoodsGroup.toString()+" ERROR");
            return "0|Thông tin đã có trên hệ thống";
        }else{
            log.info("Add: "+ catGoodsGroup.toString()+" ERROR");
            return "0|Thêm mới không thành công";
        }
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public @ResponseBody String update(CatGoodsGroupDTO catGoodsGroup, HttpServletRequest request){
        log.info("Update cat_goods_group info: "+ catGoodsGroup.toString());
        catGoodsGroup.setCustId(this.selectedCustomer.getId());
        if("on".equalsIgnoreCase(catGoodsGroup.getStatus())){
            catGoodsGroup.setStatus("1");
        }else{
            catGoodsGroup.setStatus("0");
        }
        ResponseObject response = catGoodsGroupService.update(catGoodsGroup,tokenInfo);
        if(Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())){
            log.info("SUCCESS");
            request.getSession().setAttribute("isCatGoodsGroupModified",true);
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
            CatGoodsGroupDTO deleteObject = (CatGoodsGroupDTO) catGoodsGroupService.findById(idL,tokenInfo);
            deleteObject.setStatus(Constants.STATUS.DELETED);
            ResponseObject response = catGoodsGroupService.update(deleteObject,tokenInfo);
            if(Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())){
                request.getSession().setAttribute("isCatGoodsGroupModified",true);
                return "1|Xoá thành công";
            }else{
                return "0|Xoá không thành công";
            }
        } catch (NumberFormatException e) {
            return "0|Xoá không thành công lỗi convert long";
        }
    }
}
