package com.wms.controller.category;

import com.google.common.collect.Lists;
import com.wms.base.BaseCommonController;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.utils.DataUtil;
import com.wms.utils.ResourceBundleUtils;
import org.apache.commons.lang.StringEscapeUtils;
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
@RequestMapping("/workspace/cat_partner_ctr")
public class CatPartnerController extends BaseCommonController{
    Logger log = LoggerFactory.getLogger(CatPartnerController.class);

    @Autowired
    BaseService customerService;

    @Autowired
    BaseService catPartnerService;

    @RequestMapping()
    public String home(Model model){
        model.addAttribute("menuName","menu.catPartner");
        return "category/cat_partner";
    }

    @RequestMapping(value = "/findByCondition",method = RequestMethod.GET)
    public  @ResponseBody List<CatPartnerDTO> findByCondition(@RequestParam("customerId")String custId, @RequestParam("status")String status){
        List<Condition> lstCon = Lists.newArrayList();

        if(!DataUtil.isStringNullOrEmpty(custId) && !custId.equals(Constants.STATS_ALL)){
            lstCon.add(new Condition("custId", Constants.SQL_OPERATOR.EQUAL,custId));
        }

        if(!DataUtil.isStringNullOrEmpty(status) && !status.equals(Constants.STATS_ALL)){
            lstCon.add(new Condition("status", Constants.SQL_OPERATOR.EQUAL,status));
        }

        lstCon.add(new Condition("id",Constants.SQL_OPERATOR.ORDER,"desc"));

        List<CatPartnerDTO> lstCatPartner = catPartnerService.findByCondition(lstCon,tokenInfo);

        for(CatPartnerDTO i: lstCatPartner){
            i.setName(StringEscapeUtils.escapeHtml(i.getName()));
            i.setCustName(selectedCustomer.getName());
            i.setStatusName(mapAppStatus.get(i.getStatus()));
        }

        return lstCatPartner;
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public String add(CatPartnerDTO catPartnerDTO, RedirectAttributes redirectAttributes){
        catPartnerDTO.setStatus("1");
        catPartnerDTO.setCustId(this.selectedCustomer.getId());
        ResponseObject response = catPartnerService.add(catPartnerDTO,tokenInfo);
        if(Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusName())){
            redirectAttributes.addFlashAttribute("actionInfo","result.add.success");
            redirectAttributes.addFlashAttribute("successStyle",Constants.SUCCES_COLOR);
            log.info("Add: "+ catPartnerDTO.toString()+" SUCCESS");
        }else if(Responses.ERROR_CONSTRAINT.getName().equalsIgnoreCase(response.getStatusName()))
        {
            log.info("Add: "+ catPartnerDTO.toString()+" ERROR");
            redirectAttributes.addFlashAttribute("actionInfo","result.fail.constraint");
        }else{
            log.info("Add: "+ catPartnerDTO.toString()+" ERROR");
            redirectAttributes.addFlashAttribute("actionInfo","result.fail.contact");
        }

        return "redirect:/workspace/cat_partner_ctr";
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public String update(CatPartnerDTO catPartnerDTO, RedirectAttributes redirectAttributes){
        log.info("Update cat_partner info: "+ catPartnerDTO.toString());
        catPartnerDTO.setCustId(this.selectedCustomer.getId());
        if("on".equalsIgnoreCase(catPartnerDTO.getStatus())){
            catPartnerDTO.setStatus("1");
        }else{
            catPartnerDTO.setStatus("0");
        }
        ResponseObject response = catPartnerService.update(catPartnerDTO,tokenInfo);
        if(Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())){
            log.info("SUCCESS");
            redirectAttributes.addFlashAttribute("actionInfo", "result.update.success");
            redirectAttributes.addFlashAttribute("successStyle",Constants.SUCCES_COLOR);
        }else if(Responses.ERROR_CONSTRAINT.getName().equalsIgnoreCase(response.getStatusCode())){
            log.info("ERROR");
            redirectAttributes.addFlashAttribute("actionInfo","result.fail.constraint");
        }
        else{
            log.info("ERROR");
            redirectAttributes.addFlashAttribute("actionInfo","result.fail.contact");
        }
        return  "redirect:/workspace/cat_partner_ctr";
    }

    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    public @ResponseBody String delete(@RequestParam("id")String id){
        try {
            Long idL = Long.parseLong(id);
            ResponseObject response = catPartnerService.delete(idL,tokenInfo);
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
