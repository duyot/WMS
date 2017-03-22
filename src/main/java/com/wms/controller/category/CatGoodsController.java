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
 * Created by duyot on 12/9/2016.
 */
@Controller
@RequestMapping("/workspace/cat_goods_ctr")
public class CatGoodsController {
    Logger log = LoggerFactory.getLogger(CatGoodsController.class);
    private CatCustomerDTO selectedCustomer;

    public Map<String, String> mapGoodsGroup = new HashMap<>();

    @Autowired
    BaseService catGoodsGroupService;

    @Autowired
    BaseService catGoodsService;

    private AuthTokenInfo tokenInfo;

    @ModelAttribute("tokenInfo")
    public void setTokenInfo(HttpServletRequest request){
        tokenInfo =  (AuthTokenInfo) request.getSession().getAttribute("tokenInfo");
    }

    @ModelAttribute("selectedCustomer")
    public void setSelectedCustomer(HttpServletRequest request){
        this.selectedCustomer =  (CatCustomerDTO) request.getSession().getAttribute("selectedCustomer");
    }

    @ModelAttribute("mapGoodsGroup")
    public Map<String, String> setLstGoodsGroup(HttpServletRequest request){
        if(tokenInfo == null){
            this.tokenInfo =  (AuthTokenInfo) request.getSession().getAttribute("tokenInfo");
        }
        CatCustomerDTO curCust = (CatCustomerDTO) request.getSession().getAttribute("selectedCustomer");
        List<Condition> lstCon = Lists.newArrayList();
        lstCon.add(new Condition("status", Constants.SQL_OPERATOR.EQUAL,Constants.STATUS.ACTIVE));
        lstCon.add(new Condition("custId", Constants.SQL_OPERATOR.EQUAL,curCust.getId()));
        lstCon.add(new Condition("name",Constants.SQL_OPERATOR.ORDER,"desc"));
        List<CatGoodsGroupDTO> lstCatGoodsGroup = catGoodsGroupService.findByCondition(lstCon,tokenInfo);

        for(CatGoodsGroupDTO i: lstCatGoodsGroup){
            mapGoodsGroup.put(i.getId(), i.getName());
        }
        return mapGoodsGroup;
    }

    @RequestMapping()
    public String home(Model model){
        model.addAttribute("menuName","menu.catgoods");
        return "category/cat_goods";
    }
    @RequestMapping(value = "/findByCondition",method = RequestMethod.GET)
    public  @ResponseBody List<CatGoodsDTO> findByCondition(@RequestParam("customerId")String custId, @RequestParam("status")String status){
        List<Condition> lstCon = Lists.newArrayList();

        if(!DataUtil.isStringNullOrEmpty(custId) && !custId.equals(Constants.STATS_ALL)){
            lstCon.add(new Condition("custId",Constants.SQL_PRO_TYPE.LONG,Constants.SQL_OPERATOR.EQUAL,custId));
        }
        if(!DataUtil.isStringNullOrEmpty(status) && !status.equals(Constants.STATS_ALL)){
            lstCon.add(new Condition("status", Constants.SQL_OPERATOR.EQUAL,status));
        }
        lstCon.add(new Condition("id",Constants.SQL_PRO_TYPE.LONG,Constants.SQL_OPERATOR.ORDER,"desc"));

        List<CatGoodsDTO> lstCatGoods = catGoodsService.findByCondition(lstCon,tokenInfo);

        for(CatGoodsDTO i: lstCatGoods){
            i.setCustName(selectedCustomer.getName());
            i.setGoodsGroupName(mapGoodsGroup.get(i.getGoodsGroupId()));

        }

        return lstCatGoods;
    }
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public String add(CatGoodsDTO catGoods, RedirectAttributes redirectAttributes){
        catGoods.setStatus("1");
        catGoods.setCustId("1000");
        ResponseObject response = catGoodsService.add(catGoods,tokenInfo);
        if(Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusName())){
            redirectAttributes.addFlashAttribute("actionInfo","result.add.success");
            redirectAttributes.addFlashAttribute("successStyle",Constants.SUCCES_COLOR);
            log.info("Add: "+ catGoods.toString()+" SUCCESS");
        }else{
            log.info("Add: "+ catGoods.toString()+" ERROR");
            redirectAttributes.addFlashAttribute("actionInfo","result.add.fail");
        }

        return "redirect:/workspace/cat_goods_ctr";
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public String update(CatGoodsDTO catGoods, RedirectAttributes redirectAttributes){
        catGoods.setCustId("1000");
        log.info("Update cat_goods info: "+ catGoods.toString());
        if("on".equalsIgnoreCase(catGoods.getStatus())){
            catGoods.setStatus("1");
        }else{
            catGoods.setStatus("0");
        }
        ResponseObject response = catGoodsService.update(catGoods,tokenInfo);
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
        return  "redirect:/workspace/cat_goods_ctr";
    }

    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    public @ResponseBody String delete(@RequestParam("id")String id){
        try {
            Long idL = Long.parseLong(id);
            ResponseObject response = catGoodsService.delete(idL,tokenInfo);
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
