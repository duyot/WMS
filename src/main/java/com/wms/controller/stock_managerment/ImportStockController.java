package com.wms.controller.stock_managerment;

import com.google.common.collect.Lists;
import com.wms.base.BaseController;
import com.wms.config.ProfileConfigInterface;
import com.wms.constants.Constants;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.StockManagementService;
import com.wms.utils.DataUtil;
import com.wms.utils.FunctionUtils;
import com.wms.utils.JSONUtils;
import com.wms.utils.SessionUtils;
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
    public List<ComboSourceDTO> cells = Lists.newArrayList();
    @Autowired
    StockManagementService stockManagementService;
    @Autowired
    BaseService err$MjrStockGoodsSerialService;
    @Autowired
    BaseService catStockCellService;
    private HashSet<String> setGoodsCode;

    private Logger log = LoggerFactory.getLogger(ImportStockController.class);
    //------------------------------------------------------------------------------------------------------------------
    @PostConstruct
    public void init() {
        if (!isDataLoaded) {
            initBaseBean();
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    @ModelAttribute("data-reload")
    public void checkReloadData(HttpServletRequest request) {
        if (SessionUtils.isPropertiesModified(request, Constants.DATA_MODIFIED.IMPORT_GOODS_MODIFIED)) {
            initGoods();
            initSetGoodsCode();
            SessionUtils.setReloadedModified(request, Constants.DATA_MODIFIED.IMPORT_GOODS_MODIFIED);
        }
    }

    @ModelAttribute("cells")
    public List<ComboSourceDTO> getCells(HttpServletRequest request) {
        if (SessionUtils.isPropertiesModified(request, Constants.DATA_MODIFIED.IMPORT_CELL_MODIFIED)) {
            initCells();
            SessionUtils.setReloadedModified(request, Constants.DATA_MODIFIED.IMPORT_CELL_MODIFIED);
        }
        return cells;
    }

    @ModelAttribute("lstStock")
    public List<CatStockDTO> getStocks(HttpServletRequest request) {
        if (SessionUtils.isPropertiesModified(request, Constants.DATA_MODIFIED.IMPORT_STOCK_MODIFIED)) {
            initStocks();
            SessionUtils.setReloadedModified(request, Constants.DATA_MODIFIED.IMPORT_STOCK_MODIFIED);
        }
        return lstStock;
    }
    //------------------------------------------------------------------------------------------------------------------
    @RequestMapping()
    public String home(Model model) {
        model.addAttribute("menuName", "menu.importstock");
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

    @RequestMapping(value = "/getGoodsCode")
    public @ResponseBody
    List<String> getGoodsCodes() {
        List<String> lstGoodsCode = Lists.newArrayList();
        for (CatGoodsDTO i : lstGoods) {
            lstGoodsCode.add(i.getCode() + "|" + i.getName());
        }
        return lstGoodsCode;
    }

    @RequestMapping(value = "/getPartnerName")
    public @ResponseBody
    List<String> getPartnerName() {
        List<String> lstPartneName = Lists.newArrayList();
        StringBuilder namePlus = new StringBuilder();
        for (CatPartnerDTO i : lstPartner) {
            namePlus.append(i.getCode());
            if (!DataUtil.isStringNullOrEmpty(i.getName())) {
                namePlus.append("|").append(i.getName());
            }
            if (!DataUtil.isStringNullOrEmpty(i.getTelNumber())) {
                namePlus.append("|").append(i.getTelNumber());
            }
            lstPartneName.add(namePlus.toString());
            namePlus.setLength(0);
        }
        return lstPartneName;
    }

    //@Support function--------------------------------------------------------------------------------------------------
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

    private void initSetGoodsCode(){
        setGoodsCode = new HashSet<>();
        if (!DataUtil.isListNullOrEmpty(lstGoods)) {
            for (CatGoodsDTO i : lstGoods) {
                setGoodsCode.add(i.getCode());
            }
        }
    }
}
