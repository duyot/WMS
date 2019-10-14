package com.wms.controller.stock_managerment;

import com.google.common.collect.Lists;
import com.wms.base.BaseController;
import com.wms.config.ProfileConfigInterface;
import com.wms.constants.Constants;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.StockManagementService;
import com.wms.utils.BundleUtils;
import com.wms.utils.DataUtil;
import com.wms.utils.FunctionUtils;
import com.wms.utils.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by duyot on 2/15/2017.
 */
@Controller
@RequestMapping("/workspace/stock_management/import")
@Scope("session")
public class ImportStockController extends BaseController {
    @Autowired
    public BaseService catPartnerService;
    //
    public Map<String, CatGoodsDTO> mapGoodsCodeNameGoods;
    //
    @Autowired
    StockManagementService stockManagementService;
    @Autowired
    BaseService err$MjrStockGoodsSerialService;
    @Autowired
    BaseService catStockCellService;
    @Autowired
    public ProfileConfigInterface profileConfig;
    //
    Map<String, String> mapCellIdCellCode = new HashMap<>();
    List<ComboSourceDTO> cells;
    public LinkedHashMap<String, String> mapUnitType;
    //
    private Logger log = LoggerFactory.getLogger(ImportStockController.class);
    //
    private HashSet<String> setGoodsCode = new HashSet<>();
    //
    private int previousStockId = -1;
    //
    @PostConstruct
    public void initBean(){
        initMapUnitType();
    }

    private void initMapUnitType(){
        //
        if(lstAppParams == null){
            lstAppParams = FunctionUtils.getAppParams(appParamsService);
        }
        mapUnitType = FunctionUtils.buildMapAppParams(FunctionUtils.getAppParamByType(Constants.APP_PARAMS.UNIT_TYPE,lstAppParams));
    }

    @ModelAttribute("setGoodsCode")
    public void setGoodsCode(HttpServletRequest request) {
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
            request.getSession().setAttribute("isGoodsModifiedImportStock", false);
        }
        //
        mapGoodsCodeNameGoods = FunctionUtils.clone(mapGoodsCodeGoods);
        for (CatGoodsDTO i : lstGoods) {
            mapGoodsCodeNameGoods.put(i.getName(), i);
        }
    }

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
            request.getSession().setAttribute("isStockModifiedImportStock", false);
        }
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
                cells = Lists.newArrayList();
                previousStockId = currentStockId;
                List<Condition> conditions = Lists.newArrayList();
                conditions.add(new Condition("stockId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, currentStockId + ""));
                List<CatStockCellDTO> cellsDTO = catStockCellService.findByCondition(conditions);
                if (!DataUtil.isStringNullOrEmpty(cellsDTO)) {
                    for (CatStockCellDTO i : cellsDTO) {
                        cells.add(new ComboSourceDTO(Integer.parseInt(i.getId()), i.getCode(), i.getId(), i.getCode()));
                        mapCellIdCellCode.put(i.getId(), i.getCode());
                    }
                }
            }
        }
        return cells;
    }

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
        CatGoodsDTO catGoodsDTO = mapGoodsCodeNameGoods.get(code);
        catGoodsDTO.setInPrice(FunctionUtils.removeScientificNotation(catGoodsDTO.getInPrice()));
        catGoodsDTO.setOutPrice(FunctionUtils.removeScientificNotation(catGoodsDTO.getOutPrice()));
        return catGoodsDTO;
    }

    @RequestMapping(value = "/getCells")
    public @ResponseBody
    List<ComboSourceDTO> getCellByStock(@RequestParam("stockId") String stockId) {
        if (cells == null || previousStockId != Integer.parseInt(stockId)) {
            cells = Lists.newArrayList();
            previousStockId = Integer.parseInt(stockId);
            //
            List<Condition> conditions = Lists.newArrayList();
            conditions.add(new Condition("stockId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, stockId));
            List<CatStockCellDTO> cellsDTO = catStockCellService.findByCondition(conditions);
            if (!DataUtil.isStringNullOrEmpty(cellsDTO)) {
                for (CatStockCellDTO i : cellsDTO) {
                    cells.add(new ComboSourceDTO(Integer.parseInt(i.getId()), i.getCode(), i.getId(), i.getCode()));
                    mapCellIdCellCode.put(i.getId(), i.getCode());
                }
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
            String fileName = FunctionUtils.exportExcelError(importFileResult.getLstGoodsImport(), prefixFileName, true,profileConfig);
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
        List<MjrStockTransDetailDTO> lstStockTransDetails = initListTransDetail(stockManagementDTO.getLstGoods());
        stockTrans.setLstMjrStockTransDetail(lstStockTransDetails);
        //
        return stockTrans;
    }

    private String calTotalMoneyTrans(List<MjrStockTransDetailDTO> lstGoods) {
        float total = 0f;
        for (MjrStockTransDetailDTO i : lstGoods) {
            if(i.getTotalMoney() != null && !"".equals(i.getTotalMoney())){
                total += Float.parseFloat(i.getTotalMoney().replaceAll(",", ""));
            }
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
            i.setInputPrice(FunctionUtils.unformatFloat(i.getInputPrice()));
            i.setAmount(FunctionUtils.unformatFloat(i.getAmount()));
            i.setCellCode(mapCellIdCellCode.get(i.getCellCode()));
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
        }
        return lstGoods;
    }

    private MjrStockTransDTO initMjrStockTrans(MjrStockTransDTO mjrStockTransDTO, String sysdate) {
        mjrStockTransDTO.setCustId(selectedCustomer.getId());
        mjrStockTransDTO.setType(Constants.IMPORT_TYPE.IMPORT);
        mjrStockTransDTO.setStatus(Constants.STATUS.ACTIVE);
        mjrStockTransDTO.setCreatedDate(sysdate);
        mjrStockTransDTO.setCreatedUser(currentUser.getCode());
        //Doi tac cung cap
        if (mjrStockTransDTO.getPartnerName() != null && !mjrStockTransDTO.getPartnerName().trim().equals("")) {
            String[] splitPartner = mjrStockTransDTO.getPartnerName().split("\\|");
            if (splitPartner.length > 0) {
                String partnerCode = splitPartner[0];
                CatPartnerDTO catPartnerDTO = FunctionUtils.getPartner(catPartnerService, selectedCustomer.getId(), partnerCode, null);
                if (catPartnerDTO != null) {
                    String receiverName = "";
                    if (!DataUtil.isStringNullOrEmpty(catPartnerDTO.getName())){
                        receiverName = receiverName + catPartnerDTO.getName();
                    }
                    if (!DataUtil.isStringNullOrEmpty(catPartnerDTO.getTelNumber())){
                        receiverName = receiverName+ "|" + catPartnerDTO.getTelNumber();
                    }
                    mjrStockTransDTO.setPartnerId(catPartnerDTO.getId());
                    mjrStockTransDTO.setPartnerName(receiverName);
                }
            }
        }
        //
        return mjrStockTransDTO;
    }

    private boolean isGoodsModified(HttpServletRequest request) {
        if (request.getSession().getAttribute("isGoodsModifiedImportStock") == null) {
            return true;
        }
        return (boolean) request.getSession().getAttribute("isGoodsModifiedImportStock");
    }

    private boolean isStockModified(HttpServletRequest request) {
        if (request.getSession().getAttribute("isStockModifiedImportStock") == null) {
            return true;
        }
        //
        return (boolean) request.getSession().getAttribute("isStockModifiedImportStock");

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
            if (!DataUtil.isStringNullOrEmpty(i.getName())){
                namePlus.append("|").append(i.getName());
            }
            if (!DataUtil.isStringNullOrEmpty(i.getTelNumber())){
                namePlus.append("|").append(i.getTelNumber());
            }
            lstPartneName.add(namePlus.toString());
            namePlus.setLength(0);
        }
        return lstPartneName;
    }
}
