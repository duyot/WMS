package com.wms.controller.stock_managerment;

import com.google.common.collect.Lists;
import com.wms.base.BaseController;
import com.wms.config.ProfileConfigInterface;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.OrderExportService;
import com.wms.services.interfaces.StockManagementService;
import com.wms.utils.DataUtil;
import com.wms.utils.FunctionUtils;
import com.wms.utils.JSONUtils;
import com.wms.utils.SessionUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * Created by duyot on 2/15/2017.
 */
@Controller
@RequestMapping("/workspace/stock_management/import")
@Scope("session")
public class ImportStockController extends BaseController {
    @Autowired
    public BaseService catPartnerService;
    @Autowired
    public ProfileConfigInterface profileConfig;
    @Autowired
    public StockManagementService stockManagementService;
    @Autowired
    public BaseService err$MjrStockGoodsSerialService;
    @Autowired
    public BaseService catStockCellService;
    @Autowired
    OrderExportService mjrOrderService;

    private HashSet<String> setGoodsCode;
    private List<String> lstGoodsCode;
    private List<String> lstPartnerName;
    public List<ComboSourceDTO> cells = Lists.newArrayList();

    private Logger log = LoggerFactory.getLogger(ImportStockController.class);

    //------------------------------------------------------------------------------------------------------------------
    @PostConstruct
    public void init() {
        if (!isDataLoaded) {
            initBaseBean();
        }
        initSetGoodsCode();
    }

    //------------------------------------------------------------------------------------------------------------------
    @ModelAttribute("data-reload")
    public void checkReloadData(HttpServletRequest request) {
        if (SessionUtils.isPropertiesModified(request, Constants.DATA_MODIFIED.IMPORT_GOODS_MODIFIED)) {
            initGoods();
            initSetGoodsCode();
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

    //------------------------------------------------------------------------------------------------------------------
    @RequestMapping()
    public String home(Model model) {
        model.addAttribute("menuName", "menu.importstock");
        model.addAttribute("cells", cells);
        model.addAttribute("lstStock", lstStock);
        model.addAttribute("lstReasonImport", lstReasonImport);

        return "stock_management/import_stock";
    }

    @RequestMapping(value = "/getTemplateFile")
    public void getTemplateFile(HttpServletResponse response) {
        FunctionUtils.loadFileToClient(response, profileConfig.getTemplateURL() + Constants.FILE_RESOURCE.IMPORT_TEMPLATE);
    }

    @RequestMapping(value = "/getErrorImportFile")
    public void getErrorImportFile(HttpServletRequest request, HttpServletResponse response) {
        String fileLocation = profileConfig.getTempURL() + request.getSession().getAttribute("file_import_error");
        request.getSession().removeAttribute("file_import_error");
        FunctionUtils.loadFileToClient(response, fileLocation);
    }

    @RequestMapping(value = "/getErrorImportStockFile/{id}")
    public void getErrorImportStockFile(@PathVariable("id") String stockTransId, HttpServletResponse response) {
        List<Err$MjrStockGoodsSerialDTO> lstGoodsError = getListImportError(stockTransId);
        //
        if (!DataUtil.isListNullOrEmpty(lstGoodsError)) {
            Err$MjrStockGoodsSerialDTO errorItem = lstGoodsError.get(0);
            String prefixFileName = "Error_" + errorItem.getCustId() + "_" + errorItem.getStockId() + "_" + errorItem.getImportStockTransId();
            String fileName = FunctionUtils.exportExcelError(FunctionUtils.convertListErrorToTransDetail(lstGoodsError, mapGoodsIdGoods), prefixFileName, true, profileConfig);
            FunctionUtils.loadFileToClient(response, profileConfig.getTempURL() + fileName);
        }
    }

    @RequestMapping(value = "/isSerial", method = RequestMethod.GET)
    public @ResponseBody
    CatGoodsDTO isSerial(@RequestParam("code") String code) {
        CatGoodsDTO catGoodsDTO = mapGoodsCodeGoods.get(code);
        catGoodsDTO.setInPrice(FunctionUtils.removeScientificNotation(catGoodsDTO.getInPrice()));
        catGoodsDTO.setOutPrice(FunctionUtils.removeScientificNotation(catGoodsDTO.getOutPrice()));
        return catGoodsDTO;
    }

    @RequestMapping(value = "/getCells")
    public @ResponseBody
    List<ComboSourceDTO> getCellByStock(@RequestParam("stockId") String stockId) {
        cells.clear();
        List<CatStockCellDTO> cellInSelectedStock = mapStockIdCells.get(stockId);
        if (!DataUtil.isStringNullOrEmpty(cellInSelectedStock)) {
            for (CatStockCellDTO i : cellInSelectedStock) {
                cells.add(new ComboSourceDTO(Integer.parseInt(i.getId()), i.getCode(), i.getId(), i.getCode()));
            }
        }
        return cells;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public List<MjrStockTransDetailDTO> uploadFile(MultipartHttpServletRequest request) {
        //
        Iterator<String> itr = request.getFileNames();
        MultipartFile mpf = request.getFile(itr.next());
        //
        ImportFileResultDTO importFileResult = FunctionUtils.getListStockImportFromFile(mpf, setGoodsCode, mapGoodsCodeGoods, mapAppGoodsState, true);
        if (!importFileResult.isValid()) {
            //save error file
            String prefixFileName = selectedCustomer.getId() + "_" + currentUser.getCode();
            String fileName = FunctionUtils.exportExcelError(importFileResult.getLstGoodsImport(), prefixFileName, true, profileConfig);
            //save in session
            request.getSession().setAttribute("file_import_error", fileName);
            return null;
        }
        return importFileResult.getLstGoodsImport();
    }

    @RequestMapping(value = "/import", method = RequestMethod.POST)
    @ResponseBody
    public ResponseObject importStock(@RequestBody StockManagementDTO stockManagementDTO) {
        long startTime = System.currentTimeMillis();
        log.info("------------------------------------------------------------");
        log.info(currentUser.getCode() + " import: " + stockManagementDTO.getLstGoods().size() + " items.");
        String sysdate = catStockService.getSysDate();
        StockTransDTO stockTrans = initStockTrans(stockManagementDTO, sysdate);
        ResponseObject response = stockManagementService.importStock(stockTrans);
        log.info("Import data: " + JSONUtils.object2JSONString(stockTrans));
        log.info("Result " + response.toString() + " in " + (System.currentTimeMillis() - startTime) + "ms");
        return response;
    }

    @RequestMapping(value = "/importOrder", method = RequestMethod.POST)
    @ResponseBody
    public ResponseObject importOrder(@RequestBody StockManagementDTO stockManagementDTO) {
        long startTime = System.currentTimeMillis();
        String sysdate = catStockService.getSysDate();
        MjrOrderDTO mjrOrderDTO = setInfoStockTrans(stockManagementDTO);
        StockTransDTO stockTrans = initStockTrans(stockManagementDTO, sysdate);
        log.info("Import request: " + JSONUtils.object2JSONString(stockTrans));
        ResponseObject response = stockManagementService.importStock(stockTrans);
        log.info("Result " + response.getStatusCode() + " - " + response.getStatusName() + " in " + (System.currentTimeMillis() - startTime) + "ms");
        if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
            mjrOrderDTO.setStatus("2");//Cap nhat da thuc nhap cho yeu cau
            mjrOrderService.update(mjrOrderDTO);
        }
        return response;
    }

    //------------------------------------------------------------------------------------------------------------------
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
            mjrStockTransDTO.setPartnerId(mjrOrderDTO.getPartnerId());
            mjrStockTransDTO.setPartnerName(mjrOrderDTO.getPartnerName());
            mjrStockTransDTO.setOrderId(mjrOrderDTO.getId());
            mjrStockTransDTO.setOrderCode(mjrOrderDTO.getCode());
            stockManagementDTO.setMjrStockTransDTO(mjrStockTransDTO);
        }
        List<MjrStockTransDetailDTO> lstGoods = new ArrayList<>();
        List<MjrOrderDetailDTO> lstMjrOrderDTOS = mjrOrderService.getListOrderDetail(orderId);
        lstMjrOrderDTOS.forEach(e -> {
            e.setGoodsName(mapGoodsIdGoods.get(e.getGoodsId()).getName());
            //e.setOutputPrice(mapGoodsIdGoods.get(e.getGoodsId()).getOutPrice());

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
            mjrStockTransDetailDTO.setInputPrice(e.getInputPrice());
            mjrStockTransDetailDTO.setProduceDate(e.getProduceDate());
            mjrStockTransDetailDTO.setExpireDate(e.getExpireDate());
            mjrStockTransDetailDTO.setSerial(e.getSerial());
            lstGoods.add(mjrStockTransDetailDTO);
        });
        stockManagementDTO.setLstGoods(lstGoods);

        return mjrOrderDTO;
    }

    @RequestMapping(value = "/getGoodsCode")
    public @ResponseBody
    List<String> getGoodsCodes() {
        return lstGoodsCode;
    }

    @RequestMapping(value = "/getPartnerName")
    public @ResponseBody
    List<String> getPartnerName() {
        if (DataUtil.isListNullOrEmpty(lstPartnerName)) {
            initListPartnerName();
        }
        return lstPartnerName;
    }

    //------------------------------------------------------------------------------------------------------------------
    private List<Err$MjrStockGoodsSerialDTO> getListImportError(String stockTransId) {
        List<Condition> lstCon = Lists.newArrayList();
        lstCon.add(new Condition("importStockTransId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, stockTransId));
        return err$MjrStockGoodsSerialService.findByCondition(lstCon);
    }

    private StockTransDTO initStockTrans(StockManagementDTO stockManagementDTO, String sysdate) {
        StockTransDTO stockTrans = new StockTransDTO();
        MjrStockTransDTO mjrStockTransDTO = initMjrStockTrans(stockManagementDTO.getMjrStockTransDTO(), sysdate);
        //set total money
        mjrStockTransDTO.setTransMoneyTotal(calTotalMoneyTrans(stockManagementDTO.getLstGoods()));
        stockTrans.setMjrStockTransDTO(mjrStockTransDTO);
        //
        List<MjrStockTransDetailDTO> lstStockTransDetails = initListTransDetail(stockManagementDTO.getLstGoods(), mjrStockTransDTO);
        stockTrans.setLstMjrStockTransDetail(lstStockTransDetails);
        //
        return stockTrans;
    }

    private String calTotalMoneyTrans(List<MjrStockTransDetailDTO> lstGoods) {
        float total = 0f;
        for (MjrStockTransDetailDTO i : lstGoods) {
            if (i.getTotalMoney() != null && !"".equals(i.getTotalMoney())) {
                total += Float.parseFloat(i.getTotalMoney().replaceAll(",", ""));
            }
            if (i.getProduceDate() != null && "dd/mm/yyyy".equals(i.getProduceDate().trim())) {
                i.setProduceDate("");
            }
            if (i.getExpireDate() != null && "dd/mm/yyyy".equals(i.getExpireDate().trim())) {
                i.setExpireDate("");
            }
        }
        return String.valueOf(total);
    }

    private List<MjrStockTransDetailDTO> initListTransDetail(List<MjrStockTransDetailDTO> lstGoods, MjrStockTransDTO stockTrans) {
        CatGoodsDTO goodsItem;
        for (MjrStockTransDetailDTO i : lstGoods) {
            goodsItem = mapGoodsCodeGoods.get(i.getGoodsCode());
            if (goodsItem != null) {
                i.setGoodsId(goodsItem.getId());
                i.setIsSerial(goodsItem.getIsSerial());
                i.setUnitName(mapAppParamsUnitName.get(goodsItem.getUnitType()));
            }
            //
            i.setInputPrice(FunctionUtils.unformatFloat(i.getInputPrice()));
            i.setAmount(FunctionUtils.unformatFloat(i.getAmount()));
            i.setCellCode(i.getCellCode());
            i.setGoodsState(i.getGoodsState());
            //
            i.setAmountValue(null);
            i.setInputPriceValue(null);
            i.setOutputPriceValue(null);
            i.setGoodsStateValue(null);
            i.setErrorInfo(null);
            i.setTotalMoney(FunctionUtils.unformatFloat(i.getTotalMoney()));
            i.setVolume(FunctionUtils.unformatFloat(i.getVolume()));
            i.setWeight(FunctionUtils.unformatFloat(i.getWeight()));
            i.setDescription(stockTrans.getDescription());
            i.setCellCode(stockTrans.getCellCode());
        }
        return lstGoods;
    }

    private MjrStockTransDTO initMjrStockTrans(MjrStockTransDTO mjrStockTransDTO, String sysdate) {
        mjrStockTransDTO.setCustId(selectedCustomer.getId());
        mjrStockTransDTO.setType(Constants.IMPORT_TYPE.IMPORT);
        mjrStockTransDTO.setStatus(Constants.STATUS.ACTIVE);
        mjrStockTransDTO.setCreatedDate(sysdate);
        mjrStockTransDTO.setCreatedUser(currentUser.getCode());
        if (!DataUtil.isStringNullOrEmpty(mjrStockTransDTO.getReasonId()) && !mjrStockTransDTO.getReasonId().equals(Constants.STATS_ALL)) {
            mjrStockTransDTO.setReasonId(mjrStockTransDTO.getReasonId());
            mjrStockTransDTO.setReasonName(mapReasonIdReason.get(mjrStockTransDTO.getReasonId()).getName());
        }
        if (!DataUtil.isStringNullOrEmpty(mjrStockTransDTO.getPartnerName())) {
            String[] splitPartner = mjrStockTransDTO.getPartnerName().split("\\|");
            if (splitPartner.length > 0) {
                String partnerCode = splitPartner[0];
                CatPartnerDTO catPartnerDTO = mapPartnerCodePartner.get(partnerCode);
                if (catPartnerDTO != null) {
                    String partnerName = "";
                    if (!DataUtil.isStringNullOrEmpty(catPartnerDTO.getName())) {
                        partnerName = partnerName + catPartnerDTO.getName();
                    }
                    if (!DataUtil.isStringNullOrEmpty(catPartnerDTO.getTelNumber())) {
                        partnerName = partnerName + "|" + catPartnerDTO.getTelNumber();
                    }
                    mjrStockTransDTO.setPartnerId(catPartnerDTO.getId());
                    mjrStockTransDTO.setPartnerName(partnerName);
                }
            }
        }
        //
        return mjrStockTransDTO;
    }

    private void initSetGoodsCode() {
        setGoodsCode = new HashSet<>();
        lstGoodsCode = new ArrayList<>();
        if (!DataUtil.isListNullOrEmpty(lstGoods)) {
            for (CatGoodsDTO i : lstGoods) {
                setGoodsCode.add(i.getCode());
                lstGoodsCode.add(i.getCode() + "|" + i.getName());
            }
        }
    }

    private void initListPartnerName() {
        lstPartnerName = Lists.newArrayList();
        StringBuilder namePlus = new StringBuilder();
        for (CatPartnerDTO i : super.lstPartner) {
            namePlus.append(i.getCode());
            if (!DataUtil.isStringNullOrEmpty(i.getName())) {
                namePlus.append("|").append(i.getName());
            }
            if (!DataUtil.isStringNullOrEmpty(i.getTelNumber())) {
                namePlus.append("|").append(i.getTelNumber());
            }
            lstPartnerName.add(namePlus.toString());
            namePlus.setLength(0);
        }
    }
}
