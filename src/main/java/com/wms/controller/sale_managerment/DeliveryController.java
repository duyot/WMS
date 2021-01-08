package com.wms.controller.utils;

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

/**
 * Created by duyot on 3/31/2017.
 */
@Controller
@RequestMapping("/workspace/delivery_ctr")
@Scope("session")
public class DeliveryController extends BaseController {
    @Autowired
    public CatUserService catUserService;

    @Autowired
    BaseService mjrStockTransService;


    Logger log = LoggerFactory.getLogger(DeliveryController.class);
    //
    private List<MjrStockTransDTO> lstTrans;
    private List<CatUserDTO> lstUsers;
    private List<AppParamsDTO> lstAppTransType;
    private Map<String, String> mapAppTransType;
    private List<AppParamsDTO> lstAppDeliveryStatus;
    private Map<String, String> mapAppDeliveryStatus;
    //
    private String startDate;
    private String endDate;

    @PostConstruct
    public void init() {
        this.lstAppTransType = FunctionUtils.getAppParamByType(Constants.APP_PARAMS.TRANS_TYPE, lstAppParams);
        this.mapAppTransType = FunctionUtils.buildMapAppParams(lstAppTransType);
        this.lstAppDeliveryStatus = FunctionUtils.getAppParamByType(Constants.APP_PARAMS.DELIVERY_STATUS, lstAppParams);
        this.mapAppDeliveryStatus = FunctionUtils.buildMapAppParams(lstAppDeliveryStatus);
        lstUsers = FunctionUtils.getCustomerUsers(catUserService, selectedCustomer);
    }

    //==================================================================================================================
    @RequestMapping()
    public String home(Model model) {
        lstTrans = Lists.newArrayList();
        model.addAttribute("menuName", "menu.sale.delivery");
        model.addAttribute("lstUsers", lstUsers);
        model.addAttribute("lstStock", lstStock);
        model.addAttribute("lstPartner", lstPartner);
        model.addAttribute("lstReason", lstReasonExport);
        return "sale_managerment/delivery";
    }

    @RequestMapping(value = "/findTrans", method = RequestMethod.GET)
    public @ResponseBody
    List<MjrStockTransDTO> findTrans(@RequestParam("stockId") String stockId, @RequestParam("createdUser") String createdUser,
                                     @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate,
                                     @RequestParam("transType") String transType, @RequestParam("partnerId") String partnerId,
                                     @RequestParam("reasonId") String reasonId, @RequestParam("deliveryStatus") String deliveryStatus
    ) {
        List<Condition> lstCon = Lists.newArrayList();
        lstCon.add(new Condition("status", Constants.SQL_PRO_TYPE.BYTE, Constants.SQL_OPERATOR.EQUAL, Constants.STATUS.ACTIVE));

        lstCon.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, selectedCustomer.getId()));
        if (!DataUtil.isStringNullOrEmpty(stockId) && !stockId.equals(Constants.STATS_ALL)) {
            lstCon.add(new Condition("stockId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, stockId));
        }
        if (!DataUtil.isStringNullOrEmpty(partnerId) && !partnerId.equals(Constants.STATS_ALL)) {
            lstCon.add(new Condition("receiveId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, partnerId));
        }
        if (!DataUtil.isStringNullOrEmpty(reasonId) && !reasonId.equals(Constants.STATS_ALL)) {
            lstCon.add(new Condition("reasonId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, reasonId));
        }
        if (!DataUtil.isStringNullOrEmpty(startDate) && !DataUtil.isStringNullOrEmpty(endDate)) {
            this.startDate = startDate;
            this.endDate = endDate;
            lstCon.add(new Condition("createdDate", Constants.SQL_OPERATOR.BETWEEN, startDate + "|" + endDate));
        }

        if (!DataUtil.isStringNullOrEmpty(createdUser) && !createdUser.equals(Constants.STATS_ALL)) {
            lstCon.add(new Condition("createdUser", Constants.SQL_OPERATOR.EQUAL, createdUser));
        }

        if (!DataUtil.isStringNullOrEmpty(transType) && !transType.equals(Constants.STATS_ALL)) {
            lstCon.add(new Condition("type", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, transType));
        }

        if (!DataUtil.isStringNullOrEmpty(deliveryStatus) && !deliveryStatus.equals(Constants.STATS_ALL)) {
            lstCon.add(new Condition("deliveryStatus", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, deliveryStatus));
        }


        lstCon.add(new Condition("createdDate", Constants.SQL_OPERATOR.ORDER, "desc"));
        //
        lstTrans = mjrStockTransService.findByCondition(lstCon);
        if (DataUtil.isListNullOrEmpty(lstTrans)) {
            return Lists.newArrayList();
        }
        lstTrans = setTransInfoValue(lstTrans);
        return lstTrans;
    }

    @RequestMapping(value = "/updateDelivery", method = RequestMethod.POST)
    public @ResponseBody
    String updateDelivery(@RequestBody MjrStockTransDTO mjrStockTransDTO) {
        log.info("Update delivery info: " + mjrStockTransDTO.toString());
        MjrStockTransDTO updateDTO = (MjrStockTransDTO) mjrStockTransService.findById(Long.valueOf(mjrStockTransDTO.getId()));
        updateDTO.setDeliverySenderInfo(mjrStockTransDTO.getDeliverySenderInfo());
        updateDTO.setDeliveryReceiverInfo(mjrStockTransDTO.getDeliveryReceiverInfo());
        updateDTO.setDeliveryDescription(mjrStockTransDTO.getDeliveryDescription());
        updateDTO.setDeliveryStatus(mjrStockTransDTO.getDeliveryStatus());

        ResponseObject response = mjrStockTransService.update(updateDTO);
        if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
            log.info("SUCCESS");
            return "SUCCESS";
        } else {
            log.info("ERROR");
            return "ERROR";
        }

    }
    //==================================================================================================================
    private List<MjrStockTransDTO> setTransInfoValue(List<MjrStockTransDTO> lstTransDetail) {
        List<MjrStockTransDTO> finalResult = new ArrayList<MjrStockTransDTO>();
        String partnerPermission = currentUser.getPartnerPermission();
        boolean fladAdd = true;
        for (MjrStockTransDTO i : lstTransDetail) {
            fladAdd = true;
            if (!"".equals(FunctionUtils.getMapValue(mapStockIdStock, i.getStockId()))) {
                //Neu user co phan quyen theo doi tac => chi tim giao dich cua cac doi tac duoc phan quyen
                if ("1".equals(partnerPermission) && !mapPartnerIdPartner.containsKey(i.getPartnerId())) {
                    fladAdd = false;
                }
                if (fladAdd) {
                    CatStockDTO catStockDTO = mapStockIdStock.get(i.getStockId());
                    i.setStockValue(catStockDTO.getName());
                    i.setStockCode(catStockDTO.getCode());
                    i.setTypeValue(mapAppTransType.get(i.getType()));
                    i.setDeliveryStatusValue(mapAppDeliveryStatus.get(i.getDeliveryStatus()));
                    i.setTransMoneyTotal(FunctionUtils.formatNumber(FunctionUtils.removeScientificNotation(i.getTransMoneyTotal())));
                    finalResult.add(i);
                }
            }
        }
        return finalResult;
    }

    //==================================================================================================================
    @RequestMapping(value = "/getListDeliveryFile")
    public void getListDeliveryFile(HttpServletResponse response) {
        if (DataUtil.isListNullOrEmpty(lstTrans)) {
            lstTrans.add(new MjrStockTransDTO("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "","","",""));
            startDate = "";
            endDate = "";
        }
        String prefixFileName = "DanhSach_vanchuyen_";
        String fileResource = exportListStockTrans(lstTrans, prefixFileName);
        FunctionUtils.loadFileToClient(response, fileResource);
    }

    //==================================================================================================================
    private String exportListStockTrans(List<MjrStockTransDTO> lstTrans, String prefixFileName) {
        //Hien thi thong tin don vi cha cua cac khach hang nhan (trong giao dich xuat kho)
        for (MjrStockTransDTO i : lstTrans) {
            if(i.getReceiveId() != null && mapPartnerIdPartner.containsKey(i.getReceiveId())){
                CatPartnerDTO catPartnerDTO = mapPartnerIdPartner.get(i.getReceiveId());
                if (catPartnerDTO.getParentId() != null && mapPartnerIdPartner.containsKey(catPartnerDTO.getParentId())){
                    i.setParentReceiveName(mapPartnerIdPartner.get(catPartnerDTO.getParentId()).getName());
                }
            }
        }
        String templatePath = profileConfig.getTemplateURL() + Constants.FILE_RESOURCE.LIST_DELIVERY_TEMPLATE;
        //
        File file = new File(templatePath);
        String templateAbsolutePath = file.getAbsolutePath();

        Map<String, Object> beans = new HashMap<>();
        beans.put("items", lstTrans);
        beans.put("startDate", startDate);
        beans.put("endDate", endDate);


        String fullFileName = prefixFileName + "_" + DateTimeUtils.getSysDateTimeForFileName() + ".xlsx";
        String reportFullPath = profileConfig.getTempURL() + fullFileName;
        //
        FunctionUtils.exportExcel(templateAbsolutePath, beans, reportFullPath);
        return reportFullPath;
    }
}
