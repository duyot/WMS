package com.wms.controller;

import com.google.common.collect.Lists;
import com.wms.dto.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Scope;
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
@Scope("session")
public class WorkSpaceController {
    List<CatCustomerDTO> lstCustomer;

    @RequestMapping()
    public String home(HttpServletRequest request){
        Object isLogin = request.getSession().getAttribute("isLogin");
        if(isLogin == null){
            return "index";
        }
        return "workspace/dashboard";
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
    //---------------------------------------------------------------------------------------------------------
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
    @RequestMapping("/cat_department")
    public String redirectCatDepartment(){
        return "redirect:/workspace/cat_department_ctr";
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping("/cat_user")
    public String redirectCatUser(){
        return "redirect:/workspace/cat_user_ctr";
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping("/import_stock")
    public String redirectImportStock(){
        return "redirect:/workspace/stock_management/import";
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping("/export_stock")
    public String redirectExportStock(){
        return "redirect:/workspace/stock_management/export";
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping("/stock_info")
    public String redirectStockInfo(){
        return "redirect:/workspace/utils/stockInfo";
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping("/search_serial")
    public String redirectSearchSerial(){
        return "redirect:/workspace/utils/searchSerial";
    }


    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping("/trans_info")
    public String redirectTransInfo(){
        return "redirect:/workspace/utils/trans_info";
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping("/dashboard")
    public String redirectDashboard(){
        return "redirect:/workspace/dashboard_ctr";
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping("/cat_menu")
    public String redirectCatMenu(){ return "redirect:/workspace/cat_menu_ctr";
    }
}
