package com.wms.controller.stock_managerment;

import com.google.common.collect.Lists;
import com.wms.base.BaseController;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.OrderExportService;
import com.wms.services.interfaces.StockManagementService;
import com.wms.utils.DataUtil;
import com.wms.utils.FunctionUtils;
import com.wms.utils.JSONUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * Created by duyot on 2/15/2017.
 */
@Controller
@RequestMapping("/workspace/stock_management/export")
@Scope("session")
public class ExportStockController extends BaseController {
    @Autowired
    public BaseService catPartnerService;
    //
    public List<CatPartnerDTO> lstPartner;
    Logger log = LoggerFactory.getLogger(ExportStockController.class);
    //
    @Autowired
    StockManagementService stockManagementService;
    @Autowired
    BaseService mjrStockGoodsTotalService;
    @Autowired
    BaseService err$MjrStockGoodsSerialService;
    //
    @Autowired
    BaseService catStockCellService;

    @Autowired
    OrderExportService mjrOrderService;

    //
    List<ComboSourceDTO> cells;
    Map<String, String> mapCellIdCellCode = new HashMap<>();
    //
    private HashSet<String> setGoodsCode = new HashSet<>();
    //
    private int previousStockId = -1;
    public LinkedHashMap<String, String> mapUnitType;

    //
    @PostConstruct
    public void initBaseBean() {
        initMapUnitType();
    }

    private void initMapUnitType() {
        //
        if (lstAppParams == null) {
            lstAppParams = FunctionUtils.getAppParams(appParamsService);
        }
        mapUnitType = FunctionUtils.buildMapAppParams(FunctionUtils.getAppParamByType(Constants.APP_PARAMS.UNIT_TYPE, lstAppParams));
    }

    //
    @ModelAttribute("setGoodsCode")
    public void setGoodsCode(HttpServletRequest request) {
        //
        if (selectedCustomer == null) {
            this.selectedCustomer = (CatCustomerDTO) request.getSession().getAttribute("selectedCustomer");
        }
        if (lstGoods == null || isGoodsModified(request)) {
            lstGoods = FunctionUtils.getListGoods(catGoodsService, selectedCustomer);
            buildMapGoods();
            //
            for (CatGoodsDTO i : lstGoods) {
                setGoodsCode.add(i.getCode());
            }
            request.getSession().setAttribute("isGoodsModifiedExportStock", false);
        }
    }

    @ModelAttribute("lstPartner")
    public List<CatPartnerDTO> setPartner(HttpServletRequest request) {
        return lstPartner;
    }

    @ModelAttribute("cells")
    public List<ComboSourceDTO> getCells(HttpServletRequest request) {
        if (selectedCustomer == null) {
            this.selectedCustomer = (CatCustomerDTO) request.getSession().getAttribute("selectedCustomer");
        }
        //
        if (currentUser == null) {
            this.currentUser = (CatUserDTO) request.getSession().getAttribute("user");
        }
        //
        if (lstStock == null || isStockModified(request)) {
            lstStock = FunctionUtils.getListStock(stockService, currentUser);
            buildMapStock();
            request.getSession().setAttribute("isStockModifiedImportStock", false);
        }
        //
        if (!DataUtil.isListNullOrEmpty(lstStock)) {
            int currentStockId = Integer.parseInt(lstStock.get(0).getId());
            if (cells == null || cells.size() == 0 || previousStockId != currentStockId) {
                /*cells = Lists.newArrayList();
                previousStockId = currentStockId;
                List<Condition> conditions = Lists.newArrayList();
                conditions.add(new Condition("stockId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, currentStockId + ""));
                List<CatStockCellDTO> cellsDTO = catStockCellService.findByCondition(conditions);
                if (!DataUtil.isStringNullOrEmpty(cellsDTO)) {
                    for (CatStockCellDTO i : cellsDTO) {
                        cells.add(new ComboSourceDTO(Integer.parseInt(i.getId()), i.getCode(), i.getId(), i.getCode()));
                        mapCellIdCellCode.put(i.getId(), i.getCode());
                    }
                }*/
            }
        }
        return cells;
    }

    private boolean isGoodsModified(HttpServletRequest request) {
        if (request.getSession().getAttribute("isGoodsModifiedExportStock") == null) {
            return true;
        }
        return (boolean) request.getSession().getAttribute("isGoodsModifiedExportStock");
    }

    private boolean isStockModified(HttpServletRequest request) {
        if (request.getSession().getAttribute("isStockModifiedExportStock") == null) {
            return true;
        }
        return (boolean) request.getSession().getAttribute("isStockModifiedExportStock");
    }

    //
    @ModelAttribute("getStock")
    public void getStock(HttpServletRequest request) {
        if (selectedCustomer == null) {
            this.selectedCustomer = (CatCustomerDTO) request.getSession().getAttribute("selectedCustomer");
        }
        //
        if (currentUser == null) {
            this.currentUser = (CatUserDTO) request.getSession().getAttribute("user");
        }
        //
        if (lstStock == null || isStockModified(request)) {
            lstStock = FunctionUtils.getListStock(stockService, currentUser);
            buildMapStock();
            request.getSession().setAttribute("isStockModifiedExportStock", false);
        }
    }

    //
    @RequestMapping()
    public String home(Model model) {
        model.addAttribute("menuName", "menu.exportstock");
        return "stock_management/export_stock";
    }

    @RequestMapping(value = "/getTemplateFile")
    public void getTemplateFile(HttpServletResponse response) {
        String fileResource = profileConfig.getTemplateURL() + Constants.FILE_RESOURCE.EXPORT_TEMPLATE;
        FunctionUtils.loadFileToClient(response, fileResource);
    }

    @RequestMapping(value = "/getErrorImportFile")
    public void getErrorImportFile(HttpServletRequest request, HttpServletResponse response) {
        String fileName = profileConfig.getTempURL() + request.getSession().getAttribute("file_import_error");
        FunctionUtils.loadFileToClient(response, fileName);
    }

    @RequestMapping(value = "/getErrorImportStockFile/{id}")
    public void getErrorImportStockFile(@PathVariable("id") String stockTransId, HttpServletRequest request, HttpServletResponse response) {
        List<Err$MjrStockGoodsSerialDTO> lstGoodsError = getListImportError(stockTransId);
        //
        if (!DataUtil.isListNullOrEmpty(lstGoodsError)) {
            Err$MjrStockGoodsSerialDTO errorItem = lstGoodsError.get(0);
            String prefixFileName = "Error_" + errorItem.getCustId() + "_" + errorItem.getStockId() + "_" + errorItem.getImportStockTransId();
            String fileName = FunctionUtils.exportExcelError(FunctionUtils.convertListErrorToTransDetail(lstGoodsError, mapGoodsIdGoods), prefixFileName, false, profileConfig);
            FunctionUtils.loadFileToClient(response, profileConfig.getTempURL() + fileName);
        }
    }

    @RequestMapping(value = "/isSerial")
    public @ResponseBody
    CatGoodsDTO isSerial(@RequestParam("code") String code, @RequestParam("stockId") String stockId) {
        CatGoodsDTO catGoodsDTO = mapGoodsCodeGoods.get(code);
        catGoodsDTO.setInPrice(FunctionUtils.removeScientificNotation(catGoodsDTO.getInPrice()));
        catGoodsDTO.setOutPrice(FunctionUtils.removeScientificNotation(catGoodsDTO.getOutPrice()));
        //get current amount in stock
        List<Condition> lstCon = Lists.newArrayList();
        lstCon.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, selectedCustomer.getId()));
        lstCon.add(new Condition("stockId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, stockId));
        lstCon.add(new Condition("goodsId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, catGoodsDTO.getId()));
        List<MjrStockGoodsTotalDTO> lstTotal = mjrStockGoodsTotalService.findByCondition(lstCon);
        if (DataUtil.isListNullOrEmpty(lstTotal)) {
            catGoodsDTO.setAmount("0");
            return catGoodsDTO;
        }
        MjrStockGoodsTotalDTO item = lstTotal.get(0);
        catGoodsDTO.setAmount(item.getAmount());
        //
        return catGoodsDTO;
    }

    @RequestMapping(value = "/getListSerialInStock")
    public @ResponseBody
    List<String> getListSerialInStock(@RequestParam("stockId") String stockId,
                                      @RequestParam("goodsCode") String goodsCode, @RequestParam("goodsState") String goodsState
    ) {
        CatGoodsDTO goods = mapGoodsCodeGoods.get(goodsCode);
        if (goods == null) {
            return Lists.newArrayList();
        }
        List<String> lst = stockManagementService.getListSerialInStock(selectedCustomer.getId(), stockId, goods.getId(), goodsState);
        return lst;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public List<MjrStockTransDetailDTO> uploadFile(MultipartHttpServletRequest request) {
        //1. get the files from the request object
        Iterator<String> itr = request.getFileNames();
        MultipartFile mpf = request.getFile(itr.next());
        //
        ImportFileResultDTO importFileResult = FunctionUtils.getListStockImportFromFile(mpf, setGoodsCode, mapGoodsCodeGoods, mapAppGoodsState, false);
        if (!importFileResult.isValid()) {
            //save error file
            String prefixFileName = selectedCustomer.getId() + "_" + currentUser.getCode();
            String fileName = FunctionUtils.exportExcelError(importFileResult.getLstGoodsImport(), prefixFileName, false, profileConfig);
            //save in session
            request.getSession().setAttribute("file_import_error", fileName);
            return null;
        }
        return importFileResult.getLstGoodsImport();
    }

    @RequestMapping(value = "/exportStock", method = RequestMethod.POST)
    @ResponseBody
    public ResponseObject exportStock(@RequestBody StockManagementDTO stockManagementDTO) {
        long startTime = System.currentTimeMillis();
        log.info("------------------------------------------------------------");
        log.info(currentUser.getCode() + " export: " + stockManagementDTO.getLstGoods().size() + " items.");
        String sysdate = catStockService.getSysDate();
        StockTransDTO stockTrans = initStockTrans(stockManagementDTO, sysdate);
        log.info("Export request: " + JSONUtils.object2JSONString(stockTrans));
        ResponseObject response = stockManagementService.exportStock(stockTrans);
        log.info("Result " + response.getStatusCode() + " - " + response.getStatusName() + " in " + (System.currentTimeMillis() - startTime) + "ms");
        return response;
    }

    @RequestMapping(value = "/exportOrder", method = RequestMethod.POST)
    @ResponseBody
    public ResponseObject exportOrder(@RequestBody StockManagementDTO stockManagementDTO) {
        long startTime = System.currentTimeMillis();
        String sysdate = catStockService.getSysDate();
        MjrOrderDTO mjrOrderDTO = setInfoStockTrans(stockManagementDTO);
        StockTransDTO stockTrans = initStockTrans(stockManagementDTO, sysdate);
        log.info("Export request: " + JSONUtils.object2JSONString(stockTrans));
        ResponseObject response = stockManagementService.exportStock(stockTrans);
        log.info("Result " + response.getStatusCode() + " - " + response.getStatusName() + " in " + (System.currentTimeMillis() - startTime) + "ms");
        if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
            mjrOrderDTO.setStatus("2");//Cap nhat da thuc xuat cho yeu cau
            mjrOrderService.update(mjrOrderDTO);
        }
        return response;
    }

    private MjrOrderDTO setInfoStockTrans(StockManagementDTO stockManagementDTO) {
        String orderId = stockManagementDTO.getMjrStockTransDTO().getOrderId();

        List<Condition> lstCon = Lists.newArrayList();
        lstCon.add(new Condition("status", Constants.SQL_PRO_TYPE.BYTE, Constants.SQL_OPERATOR.EQUAL, '1'));
        lstCon.add(new Condition("id", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, orderId));
        List<MjrOrderDTO> lstOrder = mjrOrderService.findByCondition(lstCon);
        MjrOrderDTO mjrOrderDTO;
        if (DataUtil.isListNullOrEmpty(lstOrder)) {
            return new MjrOrderDTO();
        } else {
            mjrOrderDTO = lstOrder.get(0);
            MjrStockTransDTO mjrStockTransDTO = stockManagementDTO.getMjrStockTransDTO();
            mjrStockTransDTO.setCustId(mjrOrderDTO.getCustId());
            mjrStockTransDTO.setStockId(mjrOrderDTO.getStockId());
            mjrStockTransDTO.setDescription(mjrOrderDTO.getDescription());
            mjrStockTransDTO.setReceiveId(mjrOrderDTO.getReceiveId());
            mjrStockTransDTO.setReceiveName(mjrOrderDTO.getReceiveName());
            mjrStockTransDTO.setPartnerId(mjrOrderDTO.getPartnerId());
            mjrStockTransDTO.setPartnerName(mjrOrderDTO.getPartnerName());
            mjrStockTransDTO.setExportMethod(mjrOrderDTO.getExportMethod());
            mjrStockTransDTO.setOrderId(mjrOrderDTO.getId());
            mjrStockTransDTO.setOrderCode(mjrOrderDTO.getCode());
            stockManagementDTO.setMjrStockTransDTO(mjrStockTransDTO);
        }
        List<MjrStockTransDetailDTO> lstGoods = new ArrayList<MjrStockTransDetailDTO>();
        List<MjrOrderDetailDTO> lstMjrOrderDTOS = mjrOrderService.getListOrderDetail(orderId);
        lstMjrOrderDTOS.forEach(e -> {
            e.setGoodsName(mapGoodsIdGoods.get(e.getGoodsId()).getName());
            e.setOutputPrice(mapGoodsIdGoods.get(e.getGoodsId()).getOutPrice());

            MjrStockTransDetailDTO mjrStockTransDetailDTO = new MjrStockTransDetailDTO();
            mjrStockTransDetailDTO.setGoodsId(e.getGoodsId());
            mjrStockTransDetailDTO.setGoodsCode(e.getGoodsCode());
            mjrStockTransDetailDTO.setGoodsName(e.getGoodsName());
            mjrStockTransDetailDTO.setGoodsState(e.getGoodsState());
            mjrStockTransDetailDTO.setPartnerId(e.getPartnerId());
            mjrStockTransDetailDTO.setAmount(e.getAmount());
            mjrStockTransDetailDTO.setUnitName(e.getUnitName());
            mjrStockTransDetailDTO.setIsSerial(e.getIsSerial());
            mjrStockTransDetailDTO.setDescription(e.getDescription());
            mjrStockTransDetailDTO.setVolume(e.getVolume());
            mjrStockTransDetailDTO.setWeight(e.getWeight());
            mjrStockTransDetailDTO.setTotalMoney(e.getTotalMoney());
            mjrStockTransDetailDTO.setGoodsId(e.getGoodsId());
            mjrStockTransDetailDTO.setOutputPrice(e.getOutputPrice());
            lstGoods.add(mjrStockTransDetailDTO);
        });
        stockManagementDTO.setLstGoods(lstGoods);

        return mjrOrderDTO;
    }

    //@Support function--------------------------------------------------------------------------------------------------
    private List<Err$MjrStockGoodsSerialDTO> getListImportError(String stockTransId) {
        List<Condition> lstCon = Lists.newArrayList();
        lstCon.add(new Condition("importStockTransId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, stockTransId));
        return err$MjrStockGoodsSerialService.findByCondition(lstCon);
    }

    private StockTransDTO initStockTrans(StockManagementDTO stockManagementDTO, String sysdate) {
        StockTransDTO stockTrans = new StockTransDTO();
        //
        MjrStockTransDTO mjrStockTransDTO = initMjrStockTrans(stockManagementDTO.getMjrStockTransDTO(), sysdate);
        //
        mjrStockTransDTO.setTransMoneyTotal(calTotalMoneyTrans(stockManagementDTO.getLstGoods()));
        stockTrans.setMjrStockTransDTO(mjrStockTransDTO);
        //
        List<MjrStockTransDetailDTO> lstStockTransDetails = initListTransDetail(stockManagementDTO.getLstGoods());
        stockTrans.setLstMjrStockTransDetail(lstStockTransDetails);
        //
        return stockTrans;
    }

    private String calTotalMoneyTrans(List<MjrStockTransDetailDTO> lstGoods) {
        float total = 0f;
        for (MjrStockTransDetailDTO i : lstGoods) {
            total += Float.parseFloat(i.getTotalMoney());
        }
        return String.valueOf(total);
    }

    private List<MjrStockTransDetailDTO> initListTransDetail(List<MjrStockTransDetailDTO> lstGoods) {
        CatGoodsDTO goodsItem;
        for (MjrStockTransDetailDTO i : lstGoods) {
            goodsItem = mapGoodsCodeGoods.get(i.getGoodsCode());
            if (goodsItem != null) {
                i.setGoodsId(goodsItem.getId());
                i.setIsSerial(goodsItem.getIsSerial());
                i.setUnitName(mapUnitType.get(goodsItem.getUnitType()));
            }
            //
            i.setOutputPrice(FunctionUtils.unformatFloat(i.getOutputPrice()));
            i.setAmount(FunctionUtils.unformatFloat(i.getAmount()));
            i.setCellCode(mapCellIdCellCode.get(i.getCellCode()));
            //
            i.setAmountValue(null);
            i.setInputPriceValue(null);
            i.setOutputPriceValue(null);
            i.setGoodsStateValue(null);
            i.setErrorInfo(null);
        }
        return lstGoods;
    }

    private MjrStockTransDTO initMjrStockTrans(MjrStockTransDTO mjrStockTransDTO, String sysdate) {
        mjrStockTransDTO.setCustId(selectedCustomer.getId());
        mjrStockTransDTO.setType(Constants.IMPORT_TYPE.EXPORT);
        mjrStockTransDTO.setStatus(Constants.STATUS.ACTIVE);
        mjrStockTransDTO.setCreatedDate(sysdate);
        mjrStockTransDTO.setCreatedUser(currentUser.getCode());
        //Nguoi nhan khi xuat
        if (mjrStockTransDTO.getReceiveName() != null && !mjrStockTransDTO.getReceiveName().trim().equals("") && mjrStockTransDTO.getReceiveName().contains("|")) {
            String[] splitPartner = mjrStockTransDTO.getReceiveName().split("\\|");
            if (splitPartner.length > 0) {
                String partnerCode = splitPartner[0];
                CatPartnerDTO catPartnerDTO = FunctionUtils.getPartner(catPartnerService, selectedCustomer.getId(), partnerCode, null);
                if (catPartnerDTO != null) {
                    String receiverName = "";
                    if (!DataUtil.isStringNullOrEmpty(catPartnerDTO.getName())) {
                        receiverName = receiverName + catPartnerDTO.getName();
                    }
                    if (!DataUtil.isStringNullOrEmpty(catPartnerDTO.getTelNumber())) {
                        receiverName = receiverName + "|" + catPartnerDTO.getTelNumber();
                    }
                    mjrStockTransDTO.setReceiveId(catPartnerDTO.getId());
                    mjrStockTransDTO.setReceiveName(receiverName);
                }
            }
        }
        //Xuat hang cua doi tac
        if (mjrStockTransDTO.getPartnerId() != null) {
            CatPartnerDTO catPartnerDTO = FunctionUtils.getPartner(catPartnerService, selectedCustomer.getId(), null, mjrStockTransDTO.getPartnerId());
            if (catPartnerDTO != null) {
                String receiverName = "";
                if (!DataUtil.isStringNullOrEmpty(catPartnerDTO.getName())) {
                    receiverName = receiverName + catPartnerDTO.getName();
                }
                if (!DataUtil.isStringNullOrEmpty(catPartnerDTO.getTelNumber())) {
                    receiverName = receiverName + "|" + catPartnerDTO.getTelNumber();
                }
                mjrStockTransDTO.setPartnerId(catPartnerDTO.getId());
                mjrStockTransDTO.setPartnerName(receiverName);
            }
        }
        //
        return mjrStockTransDTO;
    }

    @RequestMapping(value = "/getPartnerName")
    public @ResponseBody
    List<String> getPartnerName() {
        List<String> lstPartneName = Lists.newArrayList();
        StringBuilder namePlus = new StringBuilder();
        for (CatPartnerDTO i : lstPartner) {
            namePlus.append(i.getCode()).append("|").append(i.getName()).append("|").append(i.getTelNumber());
            lstPartneName.add(namePlus.toString());
            namePlus.setLength(0);
        }
        return lstPartneName;
    }

    //
    @ModelAttribute("setPartnerName")
    public void setPartnerName(HttpServletRequest request) {
        //
        if (selectedCustomer == null) {
            this.selectedCustomer = (CatCustomerDTO) request.getSession().getAttribute("selectedCustomer");
        }
        if (lstPartner == null || isGoodsModified(request)) {
            lstPartner = FunctionUtils.getListPartner(catPartnerService, selectedCustomer);
        }
    }

}
