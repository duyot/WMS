package com.wms.controller.stock_managerment;

import com.google.common.collect.Lists;
import com.wms.base.BaseController;
import com.wms.constants.Constants;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.StockManagementService;
import com.wms.utils.BundleUtils;
import com.wms.utils.DataUtil;
import com.wms.utils.FunctionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by duyot on 2/15/2017.
 */
@Controller
@RequestMapping("/workspace/stock_management/import")
@Scope("session")
public class ImportStockController extends BaseController{
    private Logger log = LoggerFactory.getLogger(ImportStockController.class);
    //
    @Autowired
    StockManagementService stockManagementService;
    @Autowired
    BaseService err$MjrStockGoodsSerialService;
    @Autowired
    BaseService catStockCellService;
    //
    private HashSet<String> setGoodsCode = new HashSet<>();
    //
    @ModelAttribute("setGoodsCode")
    public void setGoodsCode(HttpServletRequest request){
        if(selectedCustomer == null){
            this.selectedCustomer =  (CatCustomerDTO) request.getSession().getAttribute("selectedCustomer");
        }
        if(tokenInfo == null){
            this.tokenInfo =  (AuthTokenInfo) request.getSession().getAttribute("tokenInfo");
        }
        if(lstGoods == null || isGoodsModified(request)){
            lstGoods = FunctionUtils.getListGoods(catGoodsService,selectedCustomer,tokenInfo);
            buildMapGoods();
            //
            for(CatGoodsDTO i: lstGoods){
                setGoodsCode.add(i.getCode());
            }
            request.getSession().setAttribute("isGoodsModifiedImportStock",false);
        }
    }

    @ModelAttribute("getStock")
    public void getStock(HttpServletRequest request){
        if(selectedCustomer == null){
            this.selectedCustomer =  (CatCustomerDTO) request.getSession().getAttribute("selectedCustomer");
        }
        if(tokenInfo == null){
            this.tokenInfo =  (AuthTokenInfo) request.getSession().getAttribute("tokenInfo");
        }
        //
        if(lstStock == null || isStockModified(request)){
            lstStock = FunctionUtils.getListStock(catStockService,selectedCustomer,tokenInfo);
            buildMapStock();
            request.getSession().setAttribute("isStockModifiedImportStock",false);
        }
    }

    @RequestMapping()
    public String home(Model model){
        model.addAttribute("menuName","menu.importstock");
        return "stock_management/import_stock";
    }

    @RequestMapping(value = "/getTemplateFile")
    public void getTemplateFile(HttpServletResponse response){
        FunctionUtils.loadFileToClient(response,BundleUtils.getKey("template_url") + Constants.FILE_RESOURCE.IMPORT_TEMPLATE);
    }

    @RequestMapping(value = "/getErrorImportFile")
    public void getErrorImportFile(HttpServletRequest request,HttpServletResponse response){
        String fileResource = BundleUtils.getKey("temp_url") + request.getSession().getAttribute("file_import_error");
        FunctionUtils.loadFileToClient(response,fileResource);
    }

    @RequestMapping(value = "/getErrorImportStockFile/{id}")
    public void getErrorImportStockFile(@PathVariable("id")String stockTransId, HttpServletResponse response){
        List<Err$MjrStockGoodsSerialDTO> lstGoodsError = getListImportError(stockTransId);
        //
        if(!DataUtil.isListNullOrEmpty(lstGoodsError)){
            Err$MjrStockGoodsSerialDTO errorItem = lstGoodsError.get(0);
            String prefixFileName ="Error_"+ errorItem.getCustId() + "_"+ errorItem.getStockId() + "_"+ errorItem.getImportStockTransId();
            String fileName = FunctionUtils.exportExcelError(FunctionUtils.convertListErrorToTransDetail(lstGoodsError,mapGoodsIdGoods),prefixFileName);
            FunctionUtils.loadFileToClient(response,BundleUtils.getKey("temp_url")+ fileName);
        }
    }

    @RequestMapping(value = "/isSerial",method = RequestMethod.GET)
    public @ResponseBody CatGoodsDTO isSerial(@RequestParam("code")String code){
        CatGoodsDTO catGoodsDTO = mapGoodsCodeGoods.get(code);
        catGoodsDTO.setInPrice(FunctionUtils.removeScientificNotation(catGoodsDTO.getInPrice()));
        catGoodsDTO.setOutPrice(FunctionUtils.removeScientificNotation(catGoodsDTO.getOutPrice()));
        return catGoodsDTO;
    }

    @RequestMapping(value = "/getCells")
    public @ResponseBody List<CatStockCellDTO> getCellByStock(@RequestParam("stockId")String stockId){
        List<Condition> lstCon = Lists.newArrayList();
        lstCon.add(new Condition("stockId",Constants.SQL_PRO_TYPE.LONG,Constants.SQL_OPERATOR.EQUAL,stockId));
        return catStockCellService.findByCondition(lstCon,tokenInfo);
    }

    @RequestMapping(value = "/getListSerialInStock")
    public @ResponseBody List<String> getListSerialInStock(@RequestParam("stockId")String stockId,
                                                           @RequestParam("goodsId")String goodsId,@RequestParam("goodsState")String goodsState
                                                           ){
        return stockManagementService.getListSerialInStock(selectedCustomer.getId(),stockId,goodsId,goodsState,tokenInfo);
    }


    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public List<MjrStockTransDetailDTO> uploadFile(MultipartHttpServletRequest request) {
        //1. get the files from the request object
        Iterator<String> itr =  request.getFileNames();
        MultipartFile mpf = request.getFile(itr.next());
        //
        ImportFileResultDTO importFileResult = FunctionUtils.getListStockImportFromFile(mpf,setGoodsCode, mapGoodsCodeGoods,mapAppGoodsState,true);
        if(!importFileResult.isValid()){
            //save error file
            String prefixFileName = selectedCustomer.getId() +"_"+  currentUser.getCode();
            String fileName = FunctionUtils.exportExcelError(importFileResult.getLstGoodsImport(),prefixFileName);
            //save in session
            request.getSession().setAttribute("file_import_error",fileName);
            return null;
        }
        return importFileResult.getLstGoodsImport();
    }

    @RequestMapping(value = "/import", method = RequestMethod.POST)
    @ResponseBody
    public ResponseObject importStock(@RequestBody StockManagementDTO stockManagementDTO) {
        long startTime = System.currentTimeMillis();
        log.info("------------------------------------------------------------");
        log.info(currentUser.getCode() +" import: " + stockManagementDTO.getLstGoods().size() + " items.");
        String sysdate = catStockService.getSysDate(tokenInfo);
        StockTransDTO stockTrans = initStockTrans(stockManagementDTO,sysdate);
        ResponseObject response = stockManagementService.importStock(stockTrans,tokenInfo);
        log.info("Result "+ response.getStatusCode() +" in "+ (System.currentTimeMillis() - startTime) + "ms");
        return response;
    }
    //@Support function--------------------------------------------------------------------------------------------------
    private List<Err$MjrStockGoodsSerialDTO> getListImportError(String stockTransId){
        List<Condition> lstCon = Lists.newArrayList();
        lstCon.add(new Condition("importStockTransId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL,stockTransId));
        return err$MjrStockGoodsSerialService.findByCondition(lstCon,tokenInfo);
    }

    private StockTransDTO initStockTrans(StockManagementDTO stockManagementDTO,String sysdate){
        StockTransDTO stockTrans = new StockTransDTO();
        MjrStockTransDTO mjrStockTransDTO = initMjrStockTrans(stockManagementDTO.getMjrStockTransDTO(),sysdate);
        stockTrans.setMjrStockTransDTO(mjrStockTransDTO);
        List<MjrStockTransDetailDTO> lstStockTransDetails = initListTransDetail(stockManagementDTO.getLstGoods());
        stockTrans.setLstMjrStockTransDetail(lstStockTransDetails);
        return stockTrans;
    }

    private List<MjrStockTransDetailDTO> initListTransDetail(List<MjrStockTransDetailDTO> lstGoods){
        CatGoodsDTO goodsItem;
        for(MjrStockTransDetailDTO i: lstGoods){
            goodsItem = mapGoodsCodeGoods.get(i.getGoodsCode());
            if(goodsItem != null){
                i.setGoodsId(goodsItem.getId());
                i.setIsSerial(goodsItem.getIsSerial());
            }
            //
            i.setAmountValue(null);
            i.setInputPriceValue(null);
            i.setOutputPriceValue(null);
            i.setGoodsStateValue(null);
            i.setErrorInfo(null);
        }
        return lstGoods;
    }

    private MjrStockTransDTO initMjrStockTrans(MjrStockTransDTO mjrStockTransDTO,String sysdate){
        mjrStockTransDTO.setCustId(selectedCustomer.getId());
        mjrStockTransDTO.setType(Constants.IMPORT_TYPE.IMPORT);
        mjrStockTransDTO.setStatus(Constants.STATUS.ACTIVE);
        mjrStockTransDTO.setCreatedDate(sysdate);
        mjrStockTransDTO.setCreatedUser(currentUser.getCode());
        //
        return mjrStockTransDTO;
    }

    private boolean isGoodsModified(HttpServletRequest request){
        return (boolean) request.getSession().getAttribute("isGoodsModifiedImportStock");
    }

    private boolean isStockModified(HttpServletRequest request){
        return (boolean) request.getSession().getAttribute("isStockModifiedImportStock");
    }
}
