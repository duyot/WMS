package com.wms.controller;

import com.google.common.collect.Lists;
import com.wms.dto.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by duyot on 10/12/2016.
 */
@Controller
@RequestMapping("/workspace")
public class WorkSpaceController {
    Logger log = LoggerFactory.getLogger(WorkSpaceController.class);

    List<CatCustomerDTO> lstCustomer;
    @RequestMapping()
    public String home(HttpServletRequest request){
        Object isLogin = request.getSession().getAttribute("isLogin");
        if(isLogin == null){
            return "index";
        }
        return "workspace/wms";
    }

    @ModelAttribute("lstCustomers")
    public List<CatCustomerDTO> lstCustomers(HttpServletRequest request){
        this.lstCustomer = (List<CatCustomerDTO>) request.getSession().getAttribute("lstCustomers");
        if(lstCustomer == null){
            return Lists.newArrayList();
        }
        return  lstCustomer;
    }

    @ModelAttribute("mapCustomer")
    public Map<String, String> mapCustomer(HttpServletRequest request){
        List<CatCustomerDTO> lstCustomers = (List<CatCustomerDTO>) request.getSession().getAttribute("lstCustomers");
        if(lstCustomers == null){//has just only one customer
            return new HashMap<>();
        }
        return lstCustomers.stream().collect(Collectors.toMap(CatCustomerDTO::getId, CatCustomerDTO::getName));
    }

    //select customer
    @RequestMapping(value = "/select_customer/{custId}",method = RequestMethod.GET)
    public @ResponseBody String selectCustomer(@PathVariable("custId") String custId,HttpServletRequest request){
        CatCustomerDTO selectedCustomer = null;
        for(CatCustomerDTO customer:lstCustomer){
            if(customer.getId().equalsIgnoreCase(custId)){
                selectedCustomer = customer;
                selectedCustomer.setName(StringEscapeUtils.escapeHtml(selectedCustomer.getName()));
                break;
            }
        }
        request.getSession().setAttribute("selectedCustomer",selectedCustomer);
        return selectedCustomer.getName();
}
    //redirect with role------------------------------------------------------------------------------------------------
    @RequestMapping("/sysadmin")
    public String sysadmin(){
        return "workspace/wms";
    }
    @RequestMapping("/cusadmin")
    public String cusadmin(){
        return "workspace/wms";
    }
    @RequestMapping("/admin")
    public String admin(){
        return "workspace/wms";
    }
    @RequestMapping("/user")
    public String user(){
        return "workspace/wms";
    }

    //---------------------------------------------------------------------------------------------------------
    // ---------
    @RequestMapping("/logout")
        public String logout(HttpServletRequest request){
            request.getSession().invalidate();
            //
            MDC.clear();
            return "redirect:/";
    }

    @RequestMapping(value = "/login",method = RequestMethod.GET,produces="text/plain")
    public @ResponseBody String login(){
        return "Success";
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping("/cat_goods_group")
    public String redirectCatGoodsGroup(){
        return "redirect:/workspace/cat_goods_group_ctr";
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping("/cat_goods")
    public String redirectCatGoods(){
        return "redirect:/workspace/cat_goods_ctr";
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping("/cat_stock")
    public String redirectCatStock(){
        return "redirect:/workspace/cat_stock_ctr";
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping("/cat_partner")
    public String redirectCatPartner(){
        return "redirect:/workspace/cat_partner_ctr";
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping("/import_stock")
    public String redirectImportStock(){
        return "redirect:/workspace/stock_management";
    }
}
