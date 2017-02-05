package com.wms.controller.category;

import com.google.common.collect.Lists;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.utils.DataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CatGoodsGroupController {
    Logger log = LoggerFactory.getLogger(CatGoodsGroupController.class);

    @Autowired
    BaseService customerService;

    @Autowired
    BaseService catGoodsGroupService;

    private CatCustomerDTO selectedCustomer;

    private AuthTokenInfo tokenInfo;

    @ModelAttribute("tokenInfo")
    public void setTokenInfo(HttpServletRequest request){
        this.tokenInfo =  (AuthTokenInfo) request.getSession().getAttribute("tokenInfo");
    }

    @ModelAttribute("selectedCustomer")
    public void setSelectedCustomer(HttpServletRequest request){
        this.selectedCustomer =  (CatCustomerDTO) request.getSession().getAttribute("selectedCustomer");
    }

    @RequestMapping()
    public String home(Model model){
        model.addAttribute("menuName","menu.catgoodsgroup");
        return "category/cat_goods_group";
    }

    @RequestMapping(value = "/findByCondition",method = RequestMethod.GET)
    public  @ResponseBody List<CatGoodsGroupDTO> findByCondition(@RequestParam("customerId")String custId, @RequestParam("status")String status){
        List<Condition> lstCon = Lists.newArrayList();

        if(!DataUtil.isStringNullOrEmpty(custId) && !custId.equals(Constants.STATS_ALL)){
            lstCon.add(new Condition("custId", Constants.SQL_OPERATOR.EQUAL,custId));
        }

        if(!DataUtil.isStringNullOrEmpty(status) && !status.equals(Constants.STATS_ALL)){
            lstCon.add(new Condition("status", Constants.SQL_OPERATOR.EQUAL,status));
        }

        lstCon.add(new Condition("id",Constants.SQL_OPERATOR.ORDER,"desc"));

        List<CatGoodsGroupDTO> lstCatGoods = catGoodsGroupService.findByCondition(lstCon,tokenInfo);

        for(CatGoodsGroupDTO i: lstCatGoods){
            i.setCustName(selectedCustomer.getName());
        }

        return lstCatGoods;
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public String add(CatGoodsGroupDTO catGoodsGroup, RedirectAttributes redirectAttributes){
        catGoodsGroup.setStatus("1");
        ResponseObject response = catGoodsGroupService.add(catGoodsGroup,tokenInfo);
        if(Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusName())){
            redirectAttributes.addFlashAttribute("actionInfo","result.add.success");
            redirectAttributes.addFlashAttribute("successStyle",Constants.SUCCES_COLOR);
            log.info("Add: "+ catGoodsGroup.toString()+" SUCCESS");
        }else{
            log.info("Add: "+ catGoodsGroup.toString()+" ERROR");
            redirectAttributes.addFlashAttribute("actionInfo","result.add.fail");
        }

        return "redirect:/workspace/cat_goods_group_ctr";
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public String update(CatGoodsGroupDTO catGoodsGroup, RedirectAttributes redirectAttributes){
        log.info("Update cat_goods_group info: "+ catGoodsGroup.toString());
        if("on".equalsIgnoreCase(catGoodsGroup.getStatus())){
            catGoodsGroup.setStatus("1");
        }else{
            catGoodsGroup.setStatus("0");
        }
        ResponseObject response = catGoodsGroupService.update(catGoodsGroup,tokenInfo);
        if(Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusName())){
            log.info("SUCCESS");
            redirectAttributes.addFlashAttribute("actionInfo", "result.update.success");
            redirectAttributes.addFlashAttribute("successStyle",Constants.SUCCES_COLOR);
        }else if(Responses.ERROR_CONSTRAINT.getName().equalsIgnoreCase(response.getStatusName())){
            log.info("ERROR");
            redirectAttributes.addFlashAttribute("actionInfo","result.update.fail");
        }
        else{
            log.info("ERROR");
            redirectAttributes.addFlashAttribute("actionInfo","Lỗi hệ thống, liên hệ quản trị để được hỗ trợ!");
        }
        return  "redirect:/workspace/cat_goods_group_ctr";
    }

    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    public @ResponseBody String delete(@RequestParam("id")String id){
        try {
            Long idL = Long.parseLong(id);
            ResponseObject response = catGoodsGroupService.delete(idL,tokenInfo);
            if(Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusName())){
                return "1|Xoá thành công";
            }else{
                return "0|Xoá không thành công";
            }
        } catch (NumberFormatException e) {
            return "0|Xoá không thành công lỗi convert long";
        }
    }



}
