package com.wms.controller;

import com.wms.dto.CatCustomerDTO;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.MDC;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by duyot on 10/12/2016.
 */
@Controller
@RequestMapping("/workspace")
@Scope("session")
public class WorkSpaceController {
    List<CatCustomerDTO> lstCustomer;

    @RequestMapping()
    public String home(HttpServletRequest request) {
        Object isLogin = request.getSession().getAttribute("isLogin");
        if (isLogin == null) {
            return "index";
        }
        return "workspace/dashboard";
    }

    //select customer
    @RequestMapping(value = "/select_customer/{custId}", method = RequestMethod.GET)
    public @ResponseBody
    String selectCustomer(@PathVariable("custId") String custId, HttpServletRequest request) {
        CatCustomerDTO selectedCustomer = null;
        for (CatCustomerDTO customer : lstCustomer) {
            if (customer.getId().equalsIgnoreCase(custId)) {
                selectedCustomer = customer;
                selectedCustomer.setName(StringEscapeUtils.escapeHtml(selectedCustomer.getName()));
                break;
            }
        }
        request.getSession().setAttribute("selectedCustomer", selectedCustomer);
        return selectedCustomer.getName();
    }

    //---------------------------------------------------------------------------------------------------------
    @RequestMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        //
        MDC.clear();
        return "redirect:/";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = "text/plain")
    public @ResponseBody
    String login() {
        return "Success";
    }

    @RequestMapping("/cat_goods_group")
    public String redirectCatGoodsGroup() {
        return "redirect:/workspace/cat_goods_group_ctr";
    }

    @RequestMapping("/cat_goods")
    public String redirectCatGoods() {
        return "redirect:/workspace/cat_goods_ctr";
    }


    @RequestMapping("/cat_stock")
    public String redirectCatStock() {
        return "redirect:/workspace/cat_stock_ctr";
    }


    @RequestMapping("/cat_partner")
    public String redirectCatPartner() {
        return "redirect:/workspace/cat_partner_ctr";
    }

    @RequestMapping("/cat_reason")
    public String redirectCatReason() {
        return "redirect:/workspace/cat_reason_ctr";
    }

    @RequestMapping("/cat_department")
    public String redirectCatDepartment() {
        return "redirect:/workspace/cat_department_ctr";
    }

    @RequestMapping("/import_stock")
    public String redirectImportStock() {
        return "redirect:/workspace/stock_management/import";
    }


    @RequestMapping("/export_stock")
    public String redirectExportStock() {
        return "redirect:/workspace/stock_management/export";
    }

    @RequestMapping("/stock_info")
    public String redirectStockInfo() {
        return "redirect:/workspace/utils/stockInfo";
    }

    @RequestMapping("/update_stock_goods_info")
    public String redirectUpdateStockGoodsInfor() {
        return "redirect:/workspace/utils/update_stock_goods_info";
    }

    @RequestMapping("/search_serial")
    public String redirectSearchSerial() {
        return "redirect:/workspace/utils/searchSerial";
    }


    @RequestMapping("/trans_info")
    public String redirectTransInfo() {
        return "redirect:/workspace/utils/trans_info";
    }

    @RequestMapping("/dashboard")
    public String redirectDashboard() {
        return "redirect:/workspace/dashboard_ctr";
    }

    @RequestMapping("/cat_menu")
    public String redirectCatMenu() {
        return "redirect:/workspace/cat_menu_ctr";
    }

    @RequestMapping("/cat_customer")
    public String redirectCatCustomer() {
        return "redirect:/workspace/cat_customer_ctr";
    }

    @RequestMapping("/cat_role")
    public String redirectCatRole() {
        return "redirect:/workspace/cat_role_ctr";
    }

    @RequestMapping("/export_stock_order")
    public String redirectExportStockOrder() {
        return "redirect:/workspace/export_stock_order_ctr";
    }

    @RequestMapping("/import_stock_order")
    public String redirectImportStockOrder() {
        return "redirect:/workspace/import_stock_order_ctr";
    }

    @RequestMapping("/cat_user")
    public String redirectCatUser() {
        return "redirect:/workspace/cat_user_ctr";
    }

    @RequestMapping("/sale")
    public String redirectsale() {
        return "redirect:/workspace/sale_ctr";
    }

    @RequestMapping("/delivery")
    public String redirectDelivery() {
        return "redirect:/workspace/delivery_ctr";
    }
}
