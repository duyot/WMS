package com.wms.controller.stock_managerment;

import com.google.common.collect.Lists;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;
import java.util.*;

/**
 * Created by duyot on 2/15/2017.
 */
@Controller
@RequestMapping("/workspace/stock_management")
public class ImportStockController {
    Logger log = LoggerFactory.getLogger(ImportStockController.class);
    //
    @Autowired
    StockManagementService stockManagementService;
    @Autowired
    BaseService catStockService;
    @Autowired
    BaseService catGoodsService;
    @Autowired
    BaseService err$MjrStockGoodsSerialService;
    //
    private CatCustomerDTO selectedCustomer;
    private AuthTokenInfo tokenInfo;
    private CatUserDTO currentUser;
    private List<CatGoodsDTO> lstGoods;
    private Map<String,CatGoodsDTO> mapGoodsCodeGoods = new HashMap();
    private Map<String,CatGoodsDTO> mapGoodsIdGoods   = new HashMap();
    private HashSet<String> setGoodsCode = new HashSet<>();
    //
    @ModelAttribute("selectedCustomer")
    public void setSelectedCustomer(HttpServletRequest request){
        this.selectedCustomer =  (CatCustomerDTO) request.getSession().getAttribute("selectedCustomer");
    }

    @ModelAttribute("currentUser")
    public void setCurrentUser(HttpServletRequest request){
        this.currentUser =  (CatUserDTO) request.getSession().getAttribute("user");
    }

    @ModelAttribute("lstStock")
    public List<CatStockDTO> getListStock(HttpServletRequest request){
        if(selectedCustomer == null){
            this.selectedCustomer =  (CatCustomerDTO) request.getSession().getAttribute("selectedCustomer");
        }
        this.tokenInfo =  (AuthTokenInfo) request.getSession().getAttribute("tokenInfo");
        //
        List<Condition> lstCondition = Lists.newArrayList();
        lstCondition.add(new Condition("custId",Constants.SQL_PRO_TYPE.LONG,Constants.SQL_OPERATOR.EQUAL,selectedCustomer.getId()));
        lstCondition.add(new Condition("status",Constants.SQL_OPERATOR.EQUAL,Constants.STATUS.ACTIVE));
        return catStockService.findByCondition(lstCondition,tokenInfo);
    }

    @ModelAttribute("lstGoods")
    public List<CatGoodsDTO>  getListGoods(HttpServletRequest request){
        if(selectedCustomer == null){
            this.selectedCustomer =  (CatCustomerDTO) request.getSession().getAttribute("selectedCustomer");
        }
        if(tokenInfo == null){
            this.tokenInfo =  (AuthTokenInfo) request.getSession().getAttribute("tokenInfo");
        }
        List<Condition> lstCondition = Lists.newArrayList();
        lstCondition.add(new Condition("custId",Constants.SQL_PRO_TYPE.LONG,Constants.SQL_OPERATOR.EQUAL,selectedCustomer.getId()));
        lstCondition.add(new Condition("status",Constants.SQL_OPERATOR.EQUAL,Constants.STATUS.ACTIVE));
        lstGoods = catGoodsService.findByCondition(lstCondition,tokenInfo);
        buildMapGoods();
        return lstGoods;
    }

    @RequestMapping()
    public String home(Model model){
        model.addAttribute("menuName","menu.importstock");
        return "stock_management/import_stock";
    }

    @RequestMapping(value = "/getTemplateFile")
    public void getTemplateFile(HttpServletResponse response){
        try {
            File file = new File(BundleUtils.getkey("template_url") + Constants.FILE_RESOURCE.IMPORT_TEMPLATE);
            //
            String mimeType= URLConnection.guessContentTypeFromName(file.getName());
            response.setContentType(mimeType);
            response.setContentLength((int) file.length());//length in bytes
            response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + file.getName() +"\""));
            //
            InputStream is = new FileInputStream(file);
            FileCopyUtils.copy(is, response.getOutputStream());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/getErrorImportFile")
    public void getErrorImportFile(HttpServletRequest request,HttpServletResponse response){
        String fileName = BundleUtils.getkey("temp_url") + request.getSession().getAttribute("file_import_error");
        FunctionUtils.loadFileToClient(response,fileName);
    }

    @RequestMapping(value = "/getErrorImportStockFile/{id}")
    public void getErrorImportStockFile(@PathVariable("id")String stockTransId,HttpServletRequest request,HttpServletResponse response){
        List<Err$MjrStockGoodsSerialDTO> lstGoodsError = getListImportError(stockTransId);
        //
        if(!DataUtil.isListNullOrEmpty(lstGoodsError)){
            Err$MjrStockGoodsSerialDTO errorItem = lstGoodsError.get(0);
            String prefixFileName ="Error_"+ errorItem.getCustId() + "_"+ errorItem.getStockId() + "_"+ errorItem.getImportStockTransId();
            String fileName = FunctionUtils.exportExcelError(convertListErrorToTransDetail(lstGoodsError),prefixFileName);
            FunctionUtils.loadFileToClient(response,BundleUtils.getkey("temp_url")+ fileName);
        }
    }


    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public List<MjrStockTransDetailDTO> uploadFile(MultipartHttpServletRequest request,HttpServletResponse response) {
        //1. get the files from the request object
        Iterator<String> itr =  request.getFileNames();
        MultipartFile mpf = request.getFile(itr.next());
        //
        ImportFileResultDTO importFileResult = FunctionUtils.getListStockImportFromFile(mpf,setGoodsCode, mapGoodsCodeGoods);
        if(!importFileResult.isValid()){
            //save error file
            String prefixFileName = selectedCustomer.getId() +"_"+  currentUser.getName();
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
        log.info("Result "+ response.getStatusName() +" in "+ (System.currentTimeMillis() - startTime) + "ms");
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
        //
        MjrStockTransDTO mjrStockTransDTO = initMjrStockTrans(stockManagementDTO.getMjrStockTransDTO(),sysdate);
        //
        stockTrans.setMjrStockTransDTO(mjrStockTransDTO);
        //
        List<MjrStockTransDetailDTO> lstStockTransDetails = initListTransDetail(stockManagementDTO.getLstGoods());
        stockTrans.setLstMjrStockTransDetail(lstStockTransDetails);
        //
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

    private void buildMapGoods(){
        if(!DataUtil.isListNullOrEmpty(lstGoods)){
            for(CatGoodsDTO i: lstGoods){
                mapGoodsCodeGoods.put(i.getCode(),i);
                mapGoodsIdGoods.put(i.getId(),i);
                setGoodsCode.add(i.getCode());
            }
        }
    }

    private List<MjrStockTransDetailDTO> convertListErrorToTransDetail(List<Err$MjrStockGoodsSerialDTO> lstGoodsError){
        List<MjrStockTransDetailDTO> lstDetail = Lists.newArrayList();
        CatGoodsDTO goods;
        for(Err$MjrStockGoodsSerialDTO i: lstGoodsError){
            MjrStockTransDetailDTO detail = new MjrStockTransDetailDTO();
            goods = mapGoodsIdGoods.get(i.getGoodsId());
            if(goods != null){
                detail.setGoodsCode(goods.getCode());
                detail.setGoodsName(goods.getName());
            }
            detail.setGoodsState(i.getGoodsState());
            detail.setSerial(i.getSerial());
            detail.setAmount(i.getAmount());
            detail.setInputPrice(i.getInputPrice());
            detail.setCellCode(i.getCellCode());
            detail.setErrorInfo(convertDBMessage(i.getOraErrorMessage()));
            //
            lstDetail.add(detail);
        }
        return lstDetail;
    }

    private String convertDBMessage(String oraMessage){
        if(oraMessage.contains("WMS_DB.UN_SERIAL")){
            return "Serial đã có trên hệ thống";
        }else{
            return "Lỗi: "+ oraMessage;
        }
    }
}
