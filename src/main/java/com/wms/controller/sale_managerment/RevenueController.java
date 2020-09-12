package com.wms.controller.sale_managerment;

import com.google.common.collect.Lists;
import com.wms.base.BaseController;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.CatUserService;
import com.wms.services.interfaces.RevenueService;
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
    RevenueService revenueService;

    @Autowired
    BaseService mjrStockTransService;


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
                                     @RequestParam("type") String type, @RequestParam("partnerId") String partnerId,
                                     @RequestParam("paymentStatus") String paymentStatus
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

        if (!DataUtil.isStringNullOrEmpty(paymentStatus) && !paymentStatus.equals(Constants.STATS_ALL)) {
            lstCon.add(new Condition("paymentStatus", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, paymentStatus));
        }

        lstCon.add(new Condition("id", Constants.SQL_OPERATOR.ORDER, "desc"));
        //
        lstRevenue = revenueService.findByCondition(lstCon);
        if (DataUtil.isListNullOrEmpty(lstRevenue)) {
            return Lists.newArrayList();
        }
        lstRevenue = setRevenueInfoValue(lstRevenue);
        return lstRevenue;
    }

    //==================================================================================================================
    @RequestMapping(value = "/getListRevenueFile")
    public void getListRevenueFile(HttpServletResponse response) {
        if (DataUtil.isListNullOrEmpty(lstRevenue)) {
            lstRevenue.add(new RevenueDTO("", "", "","", "", "", "",
                    "", "", "", "", "","", "", "",""));
            startDate = "";
            endDate = "";
        }
        String prefixFileName = "Thongtin_ds_doanhthu_";
        String fileResource = exportListRevenue(lstRevenue, prefixFileName);
        FunctionUtils.loadFileToClient(response, fileResource);
    }

    //==================================================================================================================
    @RequestMapping(value = "/getListSumRevenueFile")
    public void getListSumRevenueFile(HttpServletResponse response, @RequestParam("partnerId") String partnerId, @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate) {
        List<RevenueDTO> lstSumRevenue = revenueService.getSumRevenue(selectedCustomer.getId(),partnerId, startDate, endDate);
        String prefixFileName = "Thongtin_tonghop_doanhthu_";
        String fileResource = exportListSumRevenue(lstSumRevenue, prefixFileName);
        FunctionUtils.loadFileToClient(response, fileResource);
    }

    //==================================================================================================================
    private String exportListRevenue(List<RevenueDTO> lstRevenue, String prefixFileName) {
        //Hien thi thong tin khach hang nhan
        for (RevenueDTO i : lstRevenue) {
            if(i.getPartnerId() != null && mapPartnerIdPartner.containsKey(i.getPartnerId())){
                CatPartnerDTO catPartnerDTO = mapPartnerIdPartner.get(i.getPartnerId());
                i.setPartnerName(mapPartnerIdPartner.get(catPartnerDTO.getId()).getName());
            }
        }
        String templatePath = profileConfig.getTemplateURL() + Constants.FILE_RESOURCE.LIST_REVENUE_TEMPLATE;
        //
        File file = new File(templatePath);
        String templateAbsolutePath = file.getAbsolutePath();

        Map<String, Object> beans = new HashMap<>();
        beans.put("items", lstRevenue);
        beans.put("startDate", startDate);
        beans.put("endDate", endDate);


        String fullFileName = prefixFileName + "_" + DateTimeUtils.getSysDateTimeForFileName() + ".xlsx";
        String reportFullPath = profileConfig.getTempURL() + fullFileName;
        //
        FunctionUtils.exportExcel(templateAbsolutePath, beans, reportFullPath);
        return reportFullPath;
    }

    //==================================================================================================================
    private String exportListSumRevenue(List<RevenueDTO> lstRevenue, String prefixFileName) {
        //Hien thi thong tin khach hang nhan
        Double amount = 0.0;
        Double paymentAmount = 0.0;
        for (RevenueDTO i : lstRevenue) {
            if(i.getPartnerId() != null && mapPartnerIdPartner.containsKey(i.getPartnerId())){
                CatPartnerDTO catPartnerDTO = mapPartnerIdPartner.get(i.getPartnerId());
                i.setPartnerName(mapPartnerIdPartner.get(catPartnerDTO.getId()).getName());
            }
            amount = 0.0;
            paymentAmount = 0.0;
            if(!DataUtil.isNullOrEmpty(i.getAmount())){
                amount = Double.valueOf(i.getAmount());
                i.setAmountValue(amount);
            }

            if(!DataUtil.isNullOrEmpty(i.getPaymentAmount())){
                paymentAmount = Double.valueOf(i.getPaymentAmount());
                i.setPaymentAmountValue(paymentAmount);
                i.setPaymentRemainValue(amount-paymentAmount);
            }else{
                i.setPaymentRemainValue(i.getAmountValue());
            }
        }
        String templatePath = profileConfig.getTemplateURL() + Constants.FILE_RESOURCE.LIST_SUM_REVENUE_TEMPLATE;
        //
        File file = new File(templatePath);
        String templateAbsolutePath = file.getAbsolutePath();

        Map<String, Object> beans = new HashMap<>();
        beans.put("items", lstRevenue);
        beans.put("startDate", startDate);
        beans.put("endDate", endDate);


        String fullFileName = prefixFileName + "_" + DateTimeUtils.getSysDateTimeForFileName() + ".xlsx";
        String reportFullPath = profileConfig.getTempURL() + fullFileName;
        //
        FunctionUtils.exportExcel(templateAbsolutePath, beans, reportFullPath);
        return reportFullPath;
    }

    private List<RevenueDTO> setRevenueInfoValue(List<RevenueDTO> lstRevenue) {
        List<MjrStockTransDTO> finalResult = new ArrayList<MjrStockTransDTO>();
        String partnerPermission = currentUser.getPartnerPermission();
        boolean fladAdd = true;
        Double totalAmount = 0.0;
        Double charge = 0.0;
        Double vat = 0.0;

        for (RevenueDTO i : lstRevenue) {
            totalAmount = 0.0;
            charge = 0.0;
            vat = 0.0;
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
            if(!DataUtil.isStringNullOrEmpty(i.getCharge())){
                charge = Double.valueOf(i.getCharge());
                i.setCharge(FunctionUtils.formatNumber(FunctionUtils.removeScientificNotation(i.getCharge())));
            }else{
                charge = 0.0;
            }
            i.setChargeValue(charge);
            if(!DataUtil.isStringNullOrEmpty(i.getVat())){
                vat = Double.valueOf(i.getVat());
            }else{
                vat = 0.0;
            }
            totalAmount = 0.0;
            if(!DataUtil.isStringNullOrEmpty(i.getAmount())){
                totalAmount = Double.valueOf(i.getAmount()) + Math.round(Double.valueOf(i.getAmount())*vat/100) + charge;
                i.setTotalAmount(FunctionUtils.formatNumber(FunctionUtils.removeScientificNotation(String.valueOf(totalAmount))));
                i.setTotalAmountValue(totalAmount);
                i.setAmountValue(Double.valueOf(i.getAmount()));
                i.setAmount(FunctionUtils.formatNumber(FunctionUtils.removeScientificNotation(i.getAmount())));
            }else{
                i.setAmountValue(0.0);
            }
            if("3".equals(i.getPaymentStatus())){
                i.setPaymentStatusValue(Constants.PAYMENT_COMPLETE);
            }else if ("2".equals(i.getPaymentStatus())){
                i.setPaymentStatusValue(Constants.PAYMENT_PROCESSING);
            }else{
                i.setPaymentStatusValue(Constants.PAYMENT_NOT_COMPLETE);
            }
            if(!DataUtil.isStringNullOrEmpty(i.getPaymentAmount())){
                i.setPaymentRemain(FunctionUtils.formatNumber(FunctionUtils.removeScientificNotation(String.valueOf(totalAmount - Double.valueOf(i.getPaymentAmount())))));
                i.setPaymentRemainValue(totalAmount-Double.valueOf(i.getPaymentAmount()));
                i.setPaymentAmountValue(Double.valueOf(i.getPaymentAmount()));
                i.setPaymentAmount(FunctionUtils.formatNumber(FunctionUtils.removeScientificNotation(i.getPaymentAmount())));
            }else{
                i.setPaymentRemain(i.getTotalAmount());
                i.setPaymentRemainValue(i.getTotalAmountValue());
                i.setPaymentAmountValue(0.0);
            }
        }
        return lstRevenue;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody
    String update( RevenueDTO revenueDTO, HttpServletRequest request) {
        log.info("Update Revenue_History info: " + revenueDTO.toString());
        revenueDTO.setCustId(this.selectedCustomer.getId());
        RevenueDTO updateDTO = (RevenueDTO) revenueService.findById(Long.valueOf(revenueDTO.getId()));

        if(DataUtil.isStringNullOrEmpty(revenueDTO.getPaymentAction())){
            //update
            if(!DataUtil.isStringNullOrEmpty(revenueDTO.getAmount())){
                updateDTO.setAmount(revenueDTO.getAmount().replaceAll(",",""));
            }
            if(!DataUtil.isStringNullOrEmpty(revenueDTO.getCharge())){
                updateDTO.setCharge(revenueDTO.getCharge().replaceAll(",",""));
            }
            updateDTO.setVat(revenueDTO.getVat());
            updateDTO.setPartnerId(revenueDTO.getPartnerId());
            if(!"1".equals(updateDTO.getType())){
                updateDTO.setCreatedDate(revenueDTO.getCreatedDate());
            }
            updateDTO.setDescription(revenueDTO.getDescription());
            updateDTO.setCreatedUser(this.currentUser.getCode());
        }else{
            //payment
            updateDTO.setPaymentAmount(revenueDTO.getPaymentAmount().replaceAll(",",""));
            updateDTO.setPaymentDate(revenueDTO.getPaymentDate());
            updateDTO.setPaymentStatus(revenueDTO.getPaymentStatus());
            updateDTO.setPaymentDescription(revenueDTO.getPaymentDescription());
        }

        ResponseObject response = revenueService.update(updateDTO);
        if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode()) ) {
            if(!DataUtil.isStringNullOrEmpty(updateDTO.getStockTransId()) && DataUtil.isStringNullOrEmpty(revenueDTO.getPaymentAction())){
                MjrStockTransDTO mjrStockTransDTO = (MjrStockTransDTO) mjrStockTransService.findById(Long.valueOf(updateDTO.getStockTransId()));
                if (mjrStockTransDTO != null && revenueDTO.getPartnerId() != null) {
                    mjrStockTransDTO.setReceiveId(revenueDTO.getPartnerId());
                    mjrStockTransDTO.setReceiveName(mapPartnerIdPartner.get(revenueDTO.getPartnerId()).getName());
                    mjrStockTransService.update(mjrStockTransDTO);
                }
            }
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

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public @ResponseBody
    String add(RevenueDTO revenueDTO, HttpServletRequest request) {
        revenueDTO.setCustId(this.selectedCustomer.getId());
        if(!DataUtil.isStringNullOrEmpty(revenueDTO.getAmount())){
            revenueDTO.setAmount(revenueDTO.getAmount().replaceAll(",",""));
        }
        if(!DataUtil.isStringNullOrEmpty(revenueDTO.getCharge())){
            revenueDTO.setCharge(revenueDTO.getCharge().replaceAll(",",""));
        }
        revenueDTO.setCreatedUser(this.currentUser.getCode());
        revenueDTO.setType("2");
        revenueDTO.setPaymentStatus("1");

        ResponseObject response = revenueService.add(revenueDTO);
        if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
            log.info("Add: " + revenueDTO.toString() + " SUCCESS");
            return "1|Thêm mới thành công";
        } else if (Responses.ERROR_CONSTRAINT.getName().equalsIgnoreCase(response.getStatusName())) {
            log.info("Add: " + revenueDTO.toString() + " ERROR");
            return "0|Thông tin đã có trên hệ thống";
        } else {
            log.info("Add: " + revenueDTO.toString() + " ERROR");
            return "0|Thêm mới không thành công";
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public @ResponseBody
    String delete(@RequestParam("id") String id,  HttpServletRequest request) {
        try {

            Long idL = Long.parseLong(id);
            ResponseObject response = revenueService.delete(idL);
            if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
                return "1|Xoá thành công";
            } else {
                return "0|Xoá không thành công";
            }
        } catch (NumberFormatException e) {
            return "0|Xoá không thành công lỗi convert long";
        }
    }
}
