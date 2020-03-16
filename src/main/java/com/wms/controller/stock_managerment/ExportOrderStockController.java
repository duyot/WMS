package com.wms.controller.stock_managerment;

import com.google.common.collect.Lists;
import com.wms.base.BaseController;
import com.wms.constants.Constants;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.CatUserService;
import com.wms.services.interfaces.OrderExportService;
import com.wms.utils.DataUtil;
import com.wms.utils.DateTimeUtils;
import com.wms.utils.FunctionUtils;
import com.wms.utils.SessionUtils;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
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
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/workspace/export_stock_order_ctr")
@Scope("session")
public class ExportOrderStockController extends BaseController {
    @Autowired
    public CatUserService catUserService;
    @Autowired
    OrderExportService mjrOrderService;
    @Autowired
    BaseService catStockCellService;

    List<ComboSourceDTO> cells = Lists.newArrayList();
    private List<CatUserDTO> lstUsers;
    private List<MjrOrderDTO> lstOrder;

    private Logger log = LoggerFactory.getLogger(ExportOrderStockController.class);

    //------------------------------------------------------------------------------------------------------------------
    @PostConstruct
    public void init() {
        if (!isDataLoaded) {
            initBaseBean();
        }
        this.lstUsers = FunctionUtils.getCustomerUsers(catUserService, selectedCustomer);
    }

    //------------------------------------------------------------------------------------------------------------------
    @ModelAttribute("data-reload")
    public void checkReloadData(HttpServletRequest request) {
        if (SessionUtils.isPropertiesModified(request, Constants.DATA_MODIFIED.EXPORT_GOODS_MODIFIED)) {
            initGoods();
            SessionUtils.setReloadedModified(request, Constants.DATA_MODIFIED.EXPORT_GOODS_MODIFIED);
        }
        if (SessionUtils.isPropertiesModified(request, Constants.DATA_MODIFIED.EXPORT_STOCK_MODIFIED)) {
            initStocks();
            SessionUtils.setReloadedModified(request, Constants.DATA_MODIFIED.EXPORT_STOCK_MODIFIED);
        }
        if (SessionUtils.isPropertiesModified(request, Constants.DATA_MODIFIED.EXPORT_CELL_MODIFIED)) {
            initCells();
            SessionUtils.setReloadedModified(request, Constants.DATA_MODIFIED.EXPORT_CELL_MODIFIED);
        }
        if (SessionUtils.isPropertiesModified(request, Constants.DATA_MODIFIED.PARTNER_MODIFIED)) {
            initPartner();
            SessionUtils.setReloadedModified(request, Constants.DATA_MODIFIED.PARTNER_MODIFIED);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    @RequestMapping()
    public String home(Model model) {
        model.addAttribute("menuName", "menu.exportStockOrder");
        model.addAttribute("controller", "/workspace/export_stock_order_ctr/");
        model.addAttribute("lstUsers", lstUsers);
        model.addAttribute("lstStock", lstStock);
        model.addAttribute("lstPartner", lstPartner);
        model.addAttribute("lstReasonExport", lstReasonExport);

        //
        cells.clear();
        if (!DataUtil.isListNullOrEmpty(lstStock)) {
            int currentStockId = Integer.parseInt(lstStock.get(0).getId());
            List<CatStockCellDTO> cellInSelectedStock = mapStockIdCells.get(currentStockId);
            if (!DataUtil.isStringNullOrEmpty(cellInSelectedStock)) {
                for (CatStockCellDTO i : cellInSelectedStock) {
                    cells.add(new ComboSourceDTO(Integer.parseInt(i.getId()), i.getCode(), i.getId(), i.getCode()));
                }
            }
        }
        model.addAttribute("cells", cells);
        //
        return "stock_management/export_stock_order";
    }

    @RequestMapping(value = "/findDataByCondition", method = RequestMethod.GET)
    public @ResponseBody
    List<MjrOrderDTO> findOrder(@RequestParam("stockId") String stockId, @RequestParam("createdUser") String createdUser,
                                @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate,
                                @RequestParam("status") String status, @RequestParam("reasonId") String reasonId) {
        List<Condition> lstCon = Lists.newArrayList();

        lstCon.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, selectedCustomer.getId()));
        if (!DataUtil.isStringNullOrEmpty(stockId) && !stockId.equals(Constants.STATS_ALL)) {
            lstCon.add(new Condition("stockId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, stockId));
        }
        if (!DataUtil.isStringNullOrEmpty(startDate) && !DataUtil.isStringNullOrEmpty(endDate)) {
            lstCon.add(new Condition("createdDate", Constants.SQL_OPERATOR.BETWEEN, startDate + "|" + endDate));
        }
        if (!DataUtil.isStringNullOrEmpty(createdUser) && !createdUser.equals(Constants.STATS_ALL)) {
            lstCon.add(new Condition("createdUser", Constants.SQL_OPERATOR.EQUAL, createdUser));
        }
        if (!DataUtil.isStringNullOrEmpty(reasonId) && !createdUser.equals(Constants.STATS_ALL)) {
            lstCon.add(new Condition("reasonId", Constants.SQL_OPERATOR.EQUAL, reasonId));
        }
        lstCon.add(new Condition("type", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, "2"));

        if (!DataUtil.isStringNullOrEmpty(status) && !status.equals(Constants.STATS_ALL)) {
            lstCon.add(new Condition("status", Constants.SQL_PRO_TYPE.BYTE, Constants.SQL_OPERATOR.EQUAL, status));
        }
        lstCon.add(new Condition("createdDate", Constants.SQL_OPERATOR.ORDER, "desc"));
//		TODO: status here
        lstOrder = mjrOrderService.findByCondition(lstCon);
        if (DataUtil.isListNullOrEmpty(lstOrder)) {
            return Lists.newArrayList();
        }
        lstOrder.forEach(e -> {
            e.setStockValue(mapStockIdStock.get(e.getStockId()).getName());
            String value = "";
            if (e.getStatus().equalsIgnoreCase("1")) {
                value = "Chưa thực xuất";
            } else if (e.getStatus().equalsIgnoreCase("2")) {
                value = "Đã thực xuất";
            }
            e.setTypeValue(value);
        });
        return lstOrder;
    }

    @RequestMapping(value = "/getOrderDetail", method = RequestMethod.GET)
    public @ResponseBody
    List<MjrOrderDetailDTO> getOrderDetail(@RequestParam("orderid") String orderid) {
        List<MjrOrderDetailDTO> lstMjrOrderDTOS = mjrOrderService.getListOrderDetail(orderid);
        lstMjrOrderDTOS.forEach(e -> {
            e.setGoodsName(mapGoodsIdGoods.get(e.getGoodsId()).getName());
        });
        return lstMjrOrderDTOS;
    }

    @RequestMapping(value = "/orderExport", method = RequestMethod.POST)
    @ResponseBody
    public ResponseObject orderExport(@RequestBody OrderExportDTO orderExportDTO) {
        initExportOrder(orderExportDTO.getMjrOrderDTO());
        orderExportDTO.getLstMjrOrderDetailDTOS().forEach(e -> {
            CatGoodsDTO goodsItem = mapGoodsCodeGoods.get(e.getGoodsCode());
            if (goodsItem != null) {
                e.setGoodsId(goodsItem.getId());
                e.setIsSerial(goodsItem.getIsSerial());
                e.setUnitName(mapAppParamsUnitName.get(goodsItem.getUnitType()));
            }
            if (!DataUtil.isNullOrEmpty(e.getSerial())) {
                e.setIsSerial("1");
            } else {
                e.setIsSerial("0");
            }

            e.setGoodsId(mapGoodsCodeGoods.get(e.getGoodsCode()).getId());
            e.setGoodsOrder((orderExportDTO.getLstMjrOrderDetailDTOS().indexOf(e) + 1) +"");
        });
        return mjrOrderService.orderExport(orderExportDTO);
    }


    @RequestMapping(value = "/deleteOrder", method = RequestMethod.GET)
    @ResponseBody
    public ResponseObject deleteOrder(@RequestParam("orderid") String orderid) {
        MjrOrderDTO mjrOrderDTO = mjrOrderService.findById(Long.parseLong(orderid));
        if (mjrOrderDTO.getStatus().equalsIgnoreCase("2")) {
            ResponseObject responseObject = new ResponseObject();
            responseObject.setStatusName("FAIL");
            responseObject.setKey("EXPORTED");
            return responseObject;
        }
        return mjrOrderService.deleteOrder(orderid);
    }

    @RequestMapping(value = "/checkExists", method = RequestMethod.POST)
    @ResponseBody
    public MjrOrderDTO checkExists(@RequestBody OrderExportDTO orderExportDTO) {
        MjrOrderDTO mjrOrderDTO = orderExportDTO.getMjrOrderDTO();
        initExportOrder(mjrOrderDTO);
        List<Condition> lstCon = Lists.newArrayList();

        lstCon.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, selectedCustomer.getId()));
        lstCon.add(new Condition("stockId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, mjrOrderDTO.getStockId()));
        lstCon.add(new Condition("receiveId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, mjrOrderDTO.getReceiveId()));
        lstCon.add(new Condition("description", Constants.SQL_PRO_TYPE.STRING, Constants.SQL_OPERATOR.LIKE, mjrOrderDTO.getDescription()));

        List<MjrOrderDTO> lstOrderExists = mjrOrderService.findByCondition(lstCon);
        List<MjrOrderDTO> LstResult = Lists.newArrayList();
        for (MjrOrderDTO obj : lstOrderExists) {
            if (DataUtil.isNullOrEmpty(mjrOrderDTO.getId()) || !mjrOrderDTO.getId().equals(obj.getId())) {
                LstResult.add(obj);
            }
        }
        if (DataUtil.isListNullOrEmpty(LstResult)) {
            return new MjrOrderDTO();
        }
        return LstResult.get(0);
    }

    @RequestMapping(value = "/orderExportFile", method = RequestMethod.GET)
    public void orderExportFile(@RequestParam("orderId") String orderId, HttpServletResponse response) {


        String prefixFileName = "Thong_tin_chitiet_yeucau_xuatkho";
        String templatePath = profileConfig.getTemplateURL() + selectedCustomer.getCode() + File.separator + File.separator + Constants.FILE_RESOURCE.EXPORT_ORDER_BILL;
        File file = new File(templatePath);
        log.info("url " + templatePath);
        if (!file.exists()) {
            log.info("Url is not exist  " + templatePath);
            templatePath = profileConfig.getTemplateURL() + Constants.FILE_RESOURCE.EXPORT_ORDER_BILL;
        }

        String outPutFile = profileConfig.getTempURL() + prefixFileName + "_" + DateTimeUtils.getTimeStamp() + ".docx";
        List<RealExportExcelDTO> realExportExcelDTOS = mjrOrderService.orderExportExcel(orderId);
        realExportExcelDTOS.forEach(e -> {
            CatGoodsDTO goodsItem = mapGoodsCodeGoods.get(e.getGoodsCode());
            e.setGoodsName(goodsItem.getName());
            if (e.getGoodsState().equalsIgnoreCase("1")) {
                e.setGoodsState("Bình thường");
            } else {
                e.setGoodsState("Hỏng");
            }
        });
        MjrOrderDTO mjrOrderDTO = null;
        for (MjrOrderDTO item : lstOrder) {
            if (item.getId().equalsIgnoreCase(orderId)) {
                mjrOrderDTO = item;
            }
        }
        try {

            JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(realExportExcelDTOS);
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("itemList", itemsJRBean);
            parameters.put("orderCode", mjrOrderDTO.getCode());
            parameters.put("partner", mjrOrderDTO.getPartnerName());
            parameters.put("custName", mjrOrderDTO.getReceiveName());
            parameters.put("stockName", mjrOrderDTO.getStockValue());
            parameters.put("description", mjrOrderDTO.getDescription());
            parameters.put("reasonName", mjrOrderDTO.getReasonName());
            parameters.put("createdUser", currentUser.getCode());
            JasperPrint jasperPrint = JasperFillManager.fillReport(templatePath, parameters, new JREmptyDataSource());
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

    //------------------------------------------------------------------------------------------------------------------
    private void initExportOrder(MjrOrderDTO mjrOrderDTO) {
        mjrOrderDTO.setCustId(selectedCustomer.getId());
        mjrOrderDTO.setType(Constants.IMPORT_TYPE.EXPORT);
        mjrOrderDTO.setStatus("1");
        mjrOrderDTO.setCreatedUser(currentUser.getCode());
        if (!DataUtil.isStringNullOrEmpty(mjrOrderDTO.getReasonId()) && !mjrOrderDTO.getReasonId().equals(Constants.STATS_ALL)) {
            mjrOrderDTO.setReasonId(mjrOrderDTO.getReasonId());
            mjrOrderDTO.setReasonName(mapReasonIdReason.get(mjrOrderDTO.getReasonId()).getName());
        }
        //Nguoi nhan khi xuat
        if (mjrOrderDTO.getReceiveName() != null && !mjrOrderDTO.getReceiveName().trim().equals("") && mjrOrderDTO.getReceiveName().contains("|")) {
            String[] splitPartner = mjrOrderDTO.getReceiveName().split("\\|");
            if (splitPartner.length > 0) {
                String partnerCode = splitPartner[0];
                CatPartnerDTO catPartnerDTO = mapPartnerCodePartner.get(partnerCode);
                if (catPartnerDTO != null) {
                    mjrOrderDTO.setReceiveId(catPartnerDTO.getId());
                }
            }
        }
        //Xuat hang cua doi tac
        if (!DataUtil.isStringNullOrEmpty(mjrOrderDTO.getPartnerId())) {
            CatPartnerDTO catPartnerDTO = mapPartnerIdPartner.get(mjrOrderDTO.getPartnerId());
            if (catPartnerDTO != null) {
                String receiverName = "";
                if (!DataUtil.isStringNullOrEmpty(catPartnerDTO.getName())) {
                    receiverName = receiverName + catPartnerDTO.getName();
                }
                if (!DataUtil.isStringNullOrEmpty(catPartnerDTO.getTelNumber())) {
                    receiverName = receiverName + "|" + catPartnerDTO.getTelNumber();
                }
                mjrOrderDTO.setPartnerId(catPartnerDTO.getId());
                mjrOrderDTO.setPartnerName(receiverName);
            }
        }
        //
    }

}
