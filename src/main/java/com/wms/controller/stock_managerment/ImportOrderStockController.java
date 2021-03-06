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
import java.io.File;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wms.utils.SessionUtils;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/workspace/import_stock_order_ctr")
@Scope("session")
public class ImportOrderStockController extends BaseController {
    @Autowired
    OrderExportService mjrOrderService;
    private Logger log = LoggerFactory.getLogger(ImportOrderStockController.class);
    @Autowired
    public CatUserService catUserService;
    @Autowired
    BaseService catStockCellService;
    private List<CatUserDTO> lstUsers;
    private List<MjrOrderDTO> lstOrder;
    List<ComboSourceDTO> cells = new ArrayList<>();
    public LinkedHashMap<String, String> mapUnitType;

    //------------------------------------------------------------------------------------------------------------------
    @PostConstruct
    public void init() {
        if (!isDataLoaded) {
            initBaseBean();
        }
        this.lstUsers = FunctionUtils.getCustomerUsers(catUserService, selectedCustomer);
    }

    @RequestMapping()
    public String home(Model model) {
        model.addAttribute("menuName", "menu.importStockOrder");
        model.addAttribute("controller", "/workspace/import_stock_order_ctr/");
        model.addAttribute("lstStock", lstStock);
        model.addAttribute("lstPartner", lstPartner);
        model.addAttribute("lstUsers", lstUsers);
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
        initMapUnitType();
        return "stock_management/import_stock_order";
    }

    private void initMapUnitType() {
        //
        if (lstAppParams == null) {
            lstAppParams = FunctionUtils.getAppParams(appParamsService);
        }
        mapUnitType = FunctionUtils.buildMapAppParams(FunctionUtils.getAppParamByType(Constants.APP_PARAMS.UNIT_TYPE, lstAppParams));
    }

    //------------------------------------------------------------------------------------------------------------------
    @ModelAttribute("data-reload")
    public void checkReloadData(HttpServletRequest request) {
        if (SessionUtils.isPropertiesModified(request, Constants.DATA_MODIFIED.IMPORT_GOODS_MODIFIED)) {
            initGoods();
            SessionUtils.setReloadedModified(request, Constants.DATA_MODIFIED.IMPORT_GOODS_MODIFIED);
        }
        if (SessionUtils.isPropertiesModified(request, Constants.DATA_MODIFIED.IMPORT_CELL_MODIFIED)) {
            initCells();
            SessionUtils.setReloadedModified(request, Constants.DATA_MODIFIED.IMPORT_CELL_MODIFIED);
        }
        if (SessionUtils.isPropertiesModified(request, Constants.DATA_MODIFIED.IMPORT_STOCK_MODIFIED)) {
            initStocks();
            SessionUtils.setReloadedModified(request, Constants.DATA_MODIFIED.IMPORT_STOCK_MODIFIED);
        }
        if (SessionUtils.isPropertiesModified(request, Constants.DATA_MODIFIED.PARTNER_MODIFIED)) {
            initPartner();
            SessionUtils.setReloadedModified(request, Constants.DATA_MODIFIED.PARTNER_MODIFIED);
        }
    }

    @RequestMapping(value = "/findDataByCondition", method = RequestMethod.GET)
    public @ResponseBody
    List<MjrOrderDTO> findOrder(@RequestParam("stockId") String stockId, @RequestParam("createdUser") String createdUser,
                                @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate, @RequestParam("status") String status) {
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
        lstCon.add(new Condition("type", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, "1"));

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
            e.setStockValue(mapStockIdStock.get(e.getStockId()) != null ? mapStockIdStock.get(e.getStockId()).getName() : "");
            String value = "";
            if (e.getStatus().equalsIgnoreCase("1")) {
                value = "Chưa thực nhập";
            } else if (e.getStatus().equalsIgnoreCase("2")) {
                value = "Đã thực nhập";
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

    @RequestMapping(value = "/orderImport", method = RequestMethod.POST)
    @ResponseBody
    public ResponseObject orderImport(@RequestBody OrderExportDTO orderExportDTO) {
        initImportOrder(orderExportDTO.getMjrOrderDTO());
        orderExportDTO.getLstMjrOrderDetailDTOS().forEach(e -> {
            CatGoodsDTO goodsItem = mapGoodsCodeGoods.get(e.getGoodsCode());
            if (goodsItem != null) {
                e.setGoodsId(goodsItem.getId());
                e.setIsSerial(goodsItem.getIsSerial());
                e.setUnitName(mapUnitType.get(goodsItem.getUnitType()));
            }
            if (!DataUtil.isNullOrEmpty(e.getSerial())) {
                e.setIsSerial("1");
            } else {
                e.setIsSerial("0");
            }
            if(!DataUtil.isNullOrEmpty(e.getProduceDate()) && "dd/mm/yyyy".equalsIgnoreCase(e.getProduceDate())){
                e.setProduceDate("");
            }
            if(!DataUtil.isNullOrEmpty(e.getExpireDate()) && "dd/mm/yyyy".equalsIgnoreCase(e.getExpireDate())){
                e.setExpireDate("");
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
            responseObject.setKey("IMPORTED");
            return responseObject;
        }
        return mjrOrderService.deleteOrder(orderid);
    }
    //==================================================================================================================
    @RequestMapping(value = "/orderImportFile", method = RequestMethod.GET)
    public void orderExportFile(@RequestParam("orderId") String orderId, HttpServletResponse response) {


        String prefixFileName = "Thong_tin_chitiet_yeucau_xuatkho";
        String templatePath = profileConfig.getTemplateURL() +selectedCustomer.getCode()+File.separator+ File.separator + Constants.FILE_RESOURCE.EXPORT_ORDER_BILL;
        File file = new  File(templatePath);
        log.info("url " + templatePath);
        if (!file.exists()){
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


    public List<ComboSourceDTO> getCells() {
        return cells;
    }

    public void setCells(List<ComboSourceDTO> cells) {
        this.cells = cells;
    }

    private void initImportOrder(MjrOrderDTO mjrOrderDTO) {
        mjrOrderDTO.setCustId(selectedCustomer.getId());
        mjrOrderDTO.setType(Constants.IMPORT_TYPE.IMPORT);
        mjrOrderDTO.setStatus("1");
        mjrOrderDTO.setCreatedUser(currentUser.getCode());
        //Nhap hang cua doi tac
        if (mjrOrderDTO.getPartnerName() != null && !mjrOrderDTO.getPartnerName().trim().equals("") && mjrOrderDTO.getPartnerName().contains("|")) {
            String[] splitPartner = mjrOrderDTO.getPartnerName().split("\\|");
            if (splitPartner.length > 0) {
                String partnerCode = splitPartner[0];
                CatPartnerDTO catPartnerDTO = mapPartnerCodePartner.get(partnerCode);
                if (catPartnerDTO != null) {
                    mjrOrderDTO.setPartnerId(catPartnerDTO.getId());
                    String partnerName = "";
                    if (!DataUtil.isStringNullOrEmpty(catPartnerDTO.getName())) {
                        partnerName = partnerName + catPartnerDTO.getName();
                    }
                    if (!DataUtil.isStringNullOrEmpty(catPartnerDTO.getTelNumber())) {
                        partnerName = partnerName + "|" + catPartnerDTO.getTelNumber();
                    }
                    mjrOrderDTO.setPartnerName(partnerName);
                }
            }
        }
    }

    public OrderExportService getMjrOrderService() {
        return mjrOrderService;
    }

    public void setMjrOrderService(OrderExportService mjrOrderService) {
        this.mjrOrderService = mjrOrderService;
    }

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    public CatUserService getCatUserService() {
        return catUserService;
    }

    public void setCatUserService(CatUserService catUserService) {
        this.catUserService = catUserService;
    }

    public BaseService getCatStockCellService() {
        return catStockCellService;
    }

    public void setCatStockCellService(BaseService catStockCellService) {
        this.catStockCellService = catStockCellService;
    }

    public List<CatUserDTO> getLstUsers() {
        return lstUsers;
    }

    public void setLstUsers(List<CatUserDTO> lstUsers) {
        this.lstUsers = lstUsers;
    }

    public List<MjrOrderDTO> getLstOrder() {
        return lstOrder;
    }

    public void setLstOrder(List<MjrOrderDTO> lstOrder) {
        this.lstOrder = lstOrder;
    }

    public LinkedHashMap<String, String> getMapUnitType() {
        return mapUnitType;
    }

    public void setMapUnitType(LinkedHashMap<String, String> mapUnitType) {
        this.mapUnitType = mapUnitType;
    }
}
