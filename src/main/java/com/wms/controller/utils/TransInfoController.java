package com.wms.controller.utils;

import com.google.common.collect.Lists;
import com.wms.base.BaseController;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.CatUserService;
import com.wms.services.interfaces.StockManagementService;
import com.wms.utils.ConvertCurrenciesToText;
import com.wms.utils.DataUtil;
import com.wms.utils.DateTimeUtils;
import com.wms.utils.FunctionUtils;
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

/**
 * Created by duyot on 3/31/2017.
 */
@Controller
@RequestMapping("/workspace/utils/trans_info")
@Scope("session")
public class TransInfoController extends BaseController {
    @Autowired
    public CatUserService catUserService;
    @Autowired
    public BaseService catPartnerService;
    @Autowired
    public BaseService catCustServicesImpl;
    @Autowired
    BaseService mjrStockTransService;
    @Autowired
    BaseService mjrStockTransDetailService;
    @Autowired
    StockManagementService stockManagementService;
    Logger log = LoggerFactory.getLogger(TransInfoController.class);
    //
    private List<MjrStockTransDTO> lstTrans;
    private List<CatUserDTO> lstUsers;
    private List<AppParamsDTO> lstAppTransType;
    private Map<String, String> mapAppTransType;
    //
    private String startDate;
    private String endDate;

    @PostConstruct
    public void init() {
        this.lstAppTransType = FunctionUtils.getAppParamByType(Constants.APP_PARAMS.TRANS_TYPE, lstAppParams);
        this.mapAppTransType = FunctionUtils.buildMapAppParams(lstAppTransType);
        lstUsers = FunctionUtils.getCustomerUsers(catUserService, selectedCustomer);
    }

    //==================================================================================================================
    @RequestMapping()
    public String home(Model model) {
        lstTrans = Lists.newArrayList();
        model.addAttribute("menuName", "menu.utils.transinfo");
        model.addAttribute("lstUsers", lstUsers);
        return "utils/trans_info";
    }

    @RequestMapping(value = "/findTrans", method = RequestMethod.GET)
    public @ResponseBody
    List<MjrStockTransDTO> findTrans(@RequestParam("stockId") String stockId, @RequestParam("createdUser") String createdUser,
                                     @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate,
                                     @RequestParam("transType") String transType, @RequestParam("partnerId") String partnerId
    ) {
        List<Condition> lstCon = Lists.newArrayList();
        lstCon.add(new Condition("status", Constants.SQL_PRO_TYPE.BYTE, Constants.SQL_OPERATOR.EQUAL, Constants.STATUS.ACTIVE));

        lstCon.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, selectedCustomer.getId()));
        if (!DataUtil.isStringNullOrEmpty(stockId) && !stockId.equals(Constants.STATS_ALL)) {
            lstCon.add(new Condition("stockId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, stockId));
        }
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

        if (!DataUtil.isStringNullOrEmpty(transType) && !transType.equals(Constants.STATS_ALL)) {
            lstCon.add(new Condition("type", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, transType));
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

    //==================================================================================================================
    @RequestMapping(value = "/getListTransFile")
    public void getListTransFile(HttpServletResponse response) {
        if (DataUtil.isListNullOrEmpty(lstTrans)) {
            lstTrans.add(new MjrStockTransDTO("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""));
            startDate = "";
            endDate = "";
        }
        String prefixFileName = "Thongtin_ds_giaodich_";
        String fileResource = exportListStockTrans(lstTrans, prefixFileName);
        FunctionUtils.loadFileToClient(response, fileResource);
    }

    //==================================================================================================================
    @RequestMapping(value = "/getListAllTransDetailsFile")
    public void getListTransDetailFile(HttpServletResponse response) {
        int size = 0;
        StringBuilder lstStockTransId = new StringBuilder();
        if (!DataUtil.isListNullOrEmpty(lstTrans)) {
            size = lstTrans.size();
        }
        for (int i = 0; i < size; i++) {
            lstStockTransId.append(lstTrans.get(i).getId());
            if (i != size - 1) {
                lstStockTransId.append(",");
            }
        }
        List<MjrStockTransDetailDTO> lstTransDetail = stockManagementService.getListTransGoodsDetail(lstStockTransId.toString());
        if (DataUtil.isListNullOrEmpty(lstTransDetail)) {
            lstTransDetail.add(new MjrStockTransDetailDTO());
            startDate = "";
            endDate = "";
        }
        //
        String ieMoney;
        for (MjrStockTransDetailDTO i : lstTransDetail) {
            i.setGoodsName(FunctionUtils.getMapValue(mapGoodsIdGoods, i.getGoodsId()));
            //
            ieMoney = !DataUtil.isStringNullOrEmpty(i.getInputPrice()) ? i.getInputPrice() : i.getOutputPrice();
            i.setIeMoney(ieMoney);
        }
        String prefixFileName = "Thongtin_chitiet_cac_giaodich_";
        String fileResource = exportListStockTransDetail(lstTransDetail, prefixFileName);
        FunctionUtils.loadFileToClient(response, fileResource);
    }

    @RequestMapping(value = "/getListTransDetailFile")
    public void getListTransDetailFile(@RequestParam("transId") String transId, HttpServletResponse response) {
        List<MjrStockTransDetailDTO> lstTransDetail = stockManagementService.getListTransGoodsDetail(transId);
        if (DataUtil.isListNullOrEmpty(lstTransDetail)) {
            lstTransDetail.add(new MjrStockTransDetailDTO());
            startDate = "";
            endDate = "";
        }
        //
        String ieMoney;
        for (MjrStockTransDetailDTO i : lstTransDetail) {
            i.setGoodsName(FunctionUtils.getMapValue(mapGoodsIdGoods, i.getGoodsId()));
            //
            ieMoney = !DataUtil.isStringNullOrEmpty(i.getInputPrice()) ? i.getInputPrice() : i.getOutputPrice();
            i.setIeMoney(ieMoney);
        }
        String prefixFileName = "Thongtin_chitiet_giaodich_" + transId + "_";
        String fileResource = exportListStockTransDetail(lstTransDetail, prefixFileName);
        FunctionUtils.loadFileToClient(response, fileResource);
    }


    //==================================================================================================================
    @RequestMapping(value = "/cancelTrans")
    public @ResponseBody
    String cancelTrans(@RequestParam("transId") String transId) {
        ResponseObject response = stockManagementService.cancelTrans(transId);
        if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
            return "1|Hủy phiếu thành công";
        } else {
            return "0|Hủy phiếu không thành công";
        }
    }

    //==================================================================================================================
    @RequestMapping(value = "/exportTransInfo")
    public void exportTransInfo(HttpServletResponse response, @RequestParam("transId") String transId) {
        log.info("exportTransInfo: " + transId);
        //Lay thong tin chung giao dich xuat kho
        List<MjrStockTransDTO> lstStockTrans = stockManagementService.getStockTransInfo(transId);
        MjrStockTransDTO mjrStockTransDTO = null;
        if (lstStockTrans != null && lstStockTrans.size() > 0) {
            mjrStockTransDTO = lstStockTrans.get(0);
        }

        CatCustomerDTO catCustomerDTO = (CatCustomerDTO) catCustServicesImpl.findById(Long.parseLong(mjrStockTransDTO.getCustId()));
        List<MjrStockTransDetailDTO> lstStockTransDetail = stockManagementService.getListTransGoodsDetail(transId);
        String prefixFileName = "Thongtin_chitiet_giaodich_";
        String templatePath;
        int total = 0;
        Map<String, Object> parameters = new HashMap<String, Object>();
        if (mjrStockTransDTO.getType().equalsIgnoreCase("1")) {
            templatePath = profileConfig.getTemplateURL() + Constants.FILE_RESOURCE.IMPORT_BILL;
            prefixFileName = prefixFileName + "nhapkho_";
        } else {
            templatePath = profileConfig.getTemplateURL() + Constants.FILE_RESOURCE.EXPORT_BILL;
            parameters.put("receiverName", mjrStockTransDTO.getReceiveName());
            prefixFileName = prefixFileName + "xuatkho_";
        }
        log.info("exportTransInfo: template path " + templatePath);
        prefixFileName = prefixFileName + DateTimeUtils.getTimeStamp();

        String outPutFile = profileConfig.getTempURL() + prefixFileName + ".docx";

        try {

            JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(lstStockTransDetail);

            String sum = getTotal(mjrStockTransDTO.getType(), lstStockTransDetail);
            parameters.put("itemList", itemsJRBean);
            parameters.put("custName", catCustomerDTO.getName());
            parameters.put("stockCode", mapStockIdStock.get(mjrStockTransDTO.getStockId()).getCode());
            parameters.put("numberTrans", mjrStockTransDTO.getCode());
            parameters.put("partner", mjrStockTransDTO.getPartnerName());
            parameters.put("stockName", mapStockIdStock.get(mjrStockTransDTO.getStockId()).getName());
            parameters.put("importMan", mjrStockTransDTO.getCreatedUser());
            parameters.put("note", mjrStockTransDTO.getDescription());
            parameters.put("sum", sum);
            parameters.put("totalWeight", getTotalWeight(lstStockTransDetail));
            parameters.put("receivedCustomer", mjrStockTransDTO.getReceiveName() == null ? "" : mjrStockTransDTO.getReceiveName());
            parameters.put("orderCode", mjrStockTransDTO.getOrderCode() == null ? "" : mjrStockTransDTO.getOrderCode());
            parameters.put("currencyText", ConvertCurrenciesToText.convertToText(sum));


            JasperPrint jasperPrint = JasperFillManager.fillReport(templatePath, parameters, new JREmptyDataSource());
            log.info("exportTransInfo: fillReport ");
            JRDocxExporter export = new JRDocxExporter();
            export.setExporterInput(new SimpleExporterInput(jasperPrint));
            export.setExporterOutput(new SimpleOutputStreamExporterOutput(new File(outPutFile)));

            SimpleDocxReportConfiguration config = new SimpleDocxReportConfiguration();

            export.setConfiguration(config);
            export.exportReport();
            FunctionUtils.loadFileToClient(response, outPutFile);

            log.info("Done");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //==================================================================================================================
    @RequestMapping(value = "/viewTransInfo")
    public @ResponseBody
    List<MjrStockTransDetailDTO> getTransGoodsDetail(@RequestParam("transId") String transId) {
        List<MjrStockTransDetailDTO> lstTransGoodsDetail = stockManagementService.getListTransGoodsDetail(transId);
        if (DataUtil.isListNullOrEmpty(lstTransGoodsDetail)) {
            return Lists.newArrayList();
        }
        for (MjrStockTransDetailDTO i : lstTransGoodsDetail) {
            try {
                i.setGoodsName(mapGoodsIdGoods.get(i.getGoodsId()).getName());
            } catch (Exception e) {
                i.setGoodsName("");
            }
        }
        return lstTransGoodsDetail;
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
                    i.setStockValue(FunctionUtils.getMapValue(mapStockIdStock, i.getStockId()));
                    i.setTypeValue(mapAppTransType.get(i.getType()));
                    i.setTransMoneyTotal(FunctionUtils.formatNumber(FunctionUtils.removeScientificNotation(i.getTransMoneyTotal())));
                    finalResult.add(i);
                }
            }
        }
        return finalResult;
    }

    //==================================================================================================================
    private String exportListStockTrans(List<MjrStockTransDTO> lstTrans, String prefixFileName) {
        String templatePath = profileConfig.getTemplateURL() + Constants.FILE_RESOURCE.LIST_TRANS_TEMPLATE;
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

    //==================================================================================================================
    private String exportListStockTransDetail(List<MjrStockTransDetailDTO> lstTransDetail, String prefixFileName) {
        String templatePath = profileConfig.getTemplateURL() + Constants.FILE_RESOURCE.LIST_TRANS_DETAIL_TEMPLATE;
        //
        File file = new File(templatePath);
        String templateAbsolutePath = file.getAbsolutePath();

        Map<String, Object> beans = new HashMap<>();
        beans.put("items", lstTransDetail);
        beans.put("startDate", startDate);
        beans.put("endDate", endDate);
        try {
            beans.put("exportedTime", DateTimeUtils.getSysDateTime());
        } catch (Exception ex) {
            System.out.println(ex.fillInStackTrace());
        }

        String fullFileName = prefixFileName + "_" + DateTimeUtils.getSysDateTimeForFileName() + ".xlsx";
        String reportFullPath = profileConfig.getTempURL() + fullFileName;
        //
        FunctionUtils.exportExcel(templateAbsolutePath, beans, reportFullPath);
        return reportFullPath;
    }

    public String getTotal(String type, List<MjrStockTransDetailDTO> lstStockTransDetail) {
        double total = 0;
        for (MjrStockTransDetailDTO item : lstStockTransDetail) {
            item.setAmount(item.getAmount().replaceAll(",", ""));
            int amount = item.getAmount().equalsIgnoreCase("") ? 1 : Integer.parseInt(item.getAmount());
            if (type.equalsIgnoreCase("1")) {
                double inputPrice = item.getInputPrice() == null || "".equalsIgnoreCase(item.getInputPrice()) ? 0 : Double.parseDouble(item.getInputPrice().replace(",", ""));
                total = total + inputPrice * amount;
            } else {
                double outputPrice = item.getOutputPrice() == null || "".equalsIgnoreCase(item.getOutputPrice()) ? 0 : Double.parseDouble(item.getOutputPrice().replace(",", ""));
                total = total + outputPrice * amount;
            }
        }

        return ConvertCurrenciesToText.currencyFormat(String.format("%.2f", total));
    }

    public String getTotalWeight(List<MjrStockTransDetailDTO> lstStockTransDetail) {
        double total = 0;
        for (MjrStockTransDetailDTO item : lstStockTransDetail) {
            item.setAmount(item.getAmount().replaceAll(",", ""));
            int amount = item.getAmount().equalsIgnoreCase("") ? 1 : Integer.parseInt(item.getAmount());
            double weight = item.getWeight() == null || "".equalsIgnoreCase(item.getWeight()) ? 0 : Double.parseDouble(item.getWeight().replace(",", ""));
            total = total + weight * amount;
        }
        total = total / 1000;
        return ConvertCurrenciesToText.weightFormat(String.format("%.3f", total));
    }
}
