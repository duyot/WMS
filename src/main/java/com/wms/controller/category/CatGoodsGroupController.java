package com.wms.controller.category;

import com.google.common.collect.Lists;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.utils.DataUtil;
import org.apache.commons.collections.map.HashedMap;
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

    public Map<String, String> mapCustomer = new HashMap<>();



    @ModelAttribute("lstCustomers")
    public List<CustomerDTO> lstCustomer(HttpServletRequest request){
        List<CustomerDTO> lstCustomer = (List<CustomerDTO>) request.getSession().getAttribute("lstCustomers");
        mapCustomer = lstCustomer.stream().collect(Collectors.toMap(CustomerDTO::getId, CustomerDTO::getName));
        return (List<CustomerDTO>) request.getSession().getAttribute("lstCustomers");
    }

    @RequestMapping()
    public String home(Model model){
        model.addAttribute("menuName","Danh mục nhóm hàng hóa");
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

        List<CatGoodsGroupDTO> lstCatGoods = catGoodsGroupService.findByCondition(lstCon);

        for(CatGoodsGroupDTO i: lstCatGoods){
            i.setCustName(mapCustomer.get(i.getCustId()));
        }

        return lstCatGoods;
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public String add(CatGoodsGroupDTO catGoodsGroup, RedirectAttributes redirectAttributes){
        catGoodsGroup.setStatus("1");
        if(catGoodsGroupService.add(catGoodsGroup)){
            redirectAttributes.addFlashAttribute("actionInfo","Thành công");
            redirectAttributes.addFlashAttribute("successStyle",Constants.SUCCES_COLOR);
            log.info("Add: "+ catGoodsGroup.toString()+" SUCCESS");
        }else{
            log.info("Add: "+ catGoodsGroup.toString()+" ERROR");
            redirectAttributes.addFlashAttribute("actionInfo","Lỗi: nhóm hàng hóa đã có trên hệ thống! ");
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

        if(catGoodsGroupService.update(catGoodsGroup)){
            log.info("SUCCESS");
            redirectAttributes.addFlashAttribute("actionInfo", "Cập nhật thành công!");
            redirectAttributes.addFlashAttribute("successStyle",Constants.SUCCES_COLOR);
        }else{
            log.info("ERROR");
            redirectAttributes.addFlashAttribute("actionInfo","Cập nhật không thành công!");
        }
        return  "redirect:/workspace/cat_goods_group_ctr";
    }

    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    public @ResponseBody String delete(@RequestParam("id")String id,Model model){
        try {
            Long idL = Long.parseLong(id);
            if(catGoodsGroupService.delete(idL)){
                return "1|Xoá thành công";
            }else{
                return "0|Xoá không thành công";
            }
        } catch (NumberFormatException e) {
            return "0|Xoá không thành công lỗi convert long";
        }
    }



}
