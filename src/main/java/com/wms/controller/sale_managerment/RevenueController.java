package com.wms.controller.sale_managerment;

import com.google.common.collect.Lists;
import com.wms.base.BaseController;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.CatUserService;
import com.wms.services.interfaces.StockManagementService;
import com.wms.utils.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.export.SimpleDocxReportConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
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
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;


/**
 * Created by duyot on 3/31/2017.
 */
@Controller
@RequestMapping("/workspace/revenue_ctr")
@Scope("session")
public class RevenueController extends BaseController {

    @Autowired
    public CatUserService catUserService;

    @Autowired
    BaseService revenueService;


    Logger log = LoggerFactory.getLogger(RevenueController.class);
    //
    private List<RevenueDTO> lstRevenue;
    private List<CatUserDTO> lstUsers;
    private String startDate;
    private String endDate;

    @PostConstruct
    public void init() {
        lstUsers = FunctionUtils.getCustomerUsers(catUserService, selectedCustomer);
    }

    //==================================================================================================================
    @RequestMapping()
    public String home(Model model) {
        lstRevenue = Lists.newArrayList();
        model.addAttribute("menuName", "menu.sale.revenue");
        model.addAttribute("lstUsers", lstUsers);
        model.addAttribute("lstStock", lstStock);
        model.addAttribute("lstPartner", lstPartner);
        return "sale_managerment/revenue";
    }

    @RequestMapping(value = "/findByCondition", method = RequestMethod.GET)
    public @ResponseBody
    List<RevenueDTO> findByCondition(@RequestParam("createdUser") String createdUser,
                                     @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate,
                                     @RequestParam("type") String type, @RequestParam("partnerId") String partnerId
    ) {
        List<Condition> lstCon = Lists.newArrayList();
        lstCon.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, selectedCustomer.getId()));

        if (!DataUtil.isStringNullOrEmpty(partnerId) && !partnerId.equals(Constants.STATS_ALL)) {
            lstCon.add(new Condition("partnerId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, partnerId));
        }

        if (!DataUtil.isStringNullOrEmpty(startDate) && !DataUtil.isStringNullOrEmpty(endDate)) {
            this.startDate = startDate;
            this.endDate = endDate;
            lstCon.add(new Condition("createdDate", Constants.SQL_OPERATOR.BETWEEN, startDate + "|" + endDate));
        }

        if (!DataUtil.isStringNullOrEmpty(createdUser) && !createdUser.equals(Constants.STATS_ALL)) {
            lstCon.add(new Condition("createdUser", Constants.SQL_OPERATOR.EQUAL, createdUser));
        }

        if (!DataUtil.isStringNullOrEmpty(type) && !type.equals(Constants.STATS_ALL)) {
            lstCon.add(new Condition("type", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, type));
        }

        lstCon.add(new Condition("createdDate", Constants.SQL_OPERATOR.ORDER, "desc"));
        //
        lstRevenue = revenueService.findByCondition(lstCon);
        if (DataUtil.isListNullOrEmpty(lstRevenue)) {
            return Lists.newArrayList();
        }
        lstRevenue = setRevenueInfoValue(lstRevenue);
        return lstRevenue;
    }

    private List<RevenueDTO> setRevenueInfoValue(List<RevenueDTO> lstRevenue) {
        List<MjrStockTransDTO> finalResult = new ArrayList<MjrStockTransDTO>();
        String partnerPermission = currentUser.getPartnerPermission();
        boolean fladAdd = true;
        Double totalAmount = 0.0;
        Double charge = 0.0;
        Double vat = 0.0;

        for (RevenueDTO i : lstRevenue) {
            if(i.getPartnerId() != null && mapPartnerIdPartner.get(i.getPartnerId()) != null) {
                i.setPartnerName(mapPartnerIdPartner.get(i.getPartnerId()).getName());
            }
            if("1".equals(i.getType())){
                i.setTypeValue(Constants.TYPE_EXPORTED);
            }else{
                i.setTypeValue(Constants.TYPE_MANUAL);
            }
            if("-1.0".equals(i.getVat())){
                i.setVatValue(Constants.REVENUE_NO_VAT);
            }else{
                i.setVatValue(i.getVat());
            }
            if(i.getCharge() != null){
                charge = Double.valueOf(i.getCharge());
            }else{
                charge = 0.0;
            }
            if(i.getVat() != null){
                vat = Double.valueOf(i.getVat());
            }else{
                vat = 0.0;
            }
            if(i.getAmount() != null){
                i.setTotalAmount(FunctionUtils.formatNumber(FunctionUtils.removeScientificNotation(String.valueOf(Double.valueOf(i.getAmount()) + Math.round(Double.valueOf(i.getAmount())*vat/100) + charge))));
                i.setAmount(FunctionUtils.formatNumber(FunctionUtils.removeScientificNotation(i.getAmount())));
            }

        }
        return lstRevenue;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody
    String update(RevenueDTO revenueDTO, HttpServletRequest request) {
        log.info("Update Revenue_History info: " + revenueDTO.toString());
        revenueDTO.setCustId(this.selectedCustomer.getId());
        ResponseObject response = revenueService.update(revenueDTO);
        if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
            log.info("SUCCESS");
            return "1|Cập nhật thành công";
        } else if (Responses.ERROR_CONSTRAINT.getName().equalsIgnoreCase(response.getStatusName())) {
            log.info("ERROR");
            return "0|Thông tin đã có trên hệ thống";
        } else {
            log.info("ERROR");
            return "0|Cập nhật không thành công";
        }

    }
}
