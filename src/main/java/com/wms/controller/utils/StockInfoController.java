package com.wms.controller.utils;

import com.google.common.collect.Lists;
import com.wms.base.BaseController;
import com.wms.constants.Constants;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.UtilsService;
import com.wms.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by duyot on 3/21/2017.
 */
@Controller
@RequestMapping("/workspace/utils/stockInfo")
@Scope("session")
public class StockInfoController extends BaseController{
    @Autowired
    BaseService mjrStockGoodsTotalService;

    @Autowired
    UtilsService utilsService;

    Logger log = LoggerFactory.getLogger(StockInfoController.class);
    //
    private List<MjrStockGoodsTotalDTO> lstGoodsTotal;
    private List<MjrStockTransDetailDTO> lstGoodsDetails;
    //
    private List<AppParamsDTO> lstAppTransType;
    //
    public Map<String,String> mapAppTransType = new HashMap();
    public Map<String,String> mapAppStockStatus   = new HashMap();
    public Map<String,Long> mapGoodsDetailTotal = new HashMap();

    @ModelAttribute("lstAppTransType")
    public List<AppParamsDTO> setAppGoodsState(HttpServletRequest request) {
        if (lstAppTransType != null) {
            return lstAppTransType;
        }

        if (lstAppParams == null) {
            if (tokenInfo == null) {
                this.tokenInfo = (AuthTokenInfo) request.getSession().getAttribute("tokenInfo");
            }
            lstAppParams = FunctionUtils.getAppParams(appParamsService, tokenInfo);
        }
        lstAppTransType = FunctionUtils.getAppParamByType(Constants.APP_PARAMS.TRANS_TYPE, lstAppParams);
        //
        buildMapTransType(lstAppTransType);
        buildMapStockStatus(FunctionUtils.getAppParamByType(Constants.APP_PARAMS.STOCK_STATUS, lstAppParams));
        //
        return lstAppTransType;
    }

    @RequestMapping()
    public String home(Model model){
        //clear previous data
        lstGoodsTotal   = Lists.newArrayList();
        lstGoodsDetails = Lists.newArrayList();
        //
        model.addAttribute("menuName","menu.stockinfo");
        return "utils/stock_info";
    }

    @RequestMapping(value = "/findByCondition",method = RequestMethod.GET)
    public @ResponseBody List<MjrStockGoodsTotalDTO> getStockInfo(@RequestParam("stockId")String stockId, @RequestParam("goodsId")String goodsId,
                                                                  @RequestParam("status")String status
                                                     ){
        List<Condition> lstCon = Lists.newArrayList();
        lstCon.add(new Condition("custId",Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL,selectedCustomer.getId()));
        lstCon.add(new Condition("amount",Constants.SQL_OPERATOR.GREATER,0D));

        if(!DataUtil.isStringNullOrEmpty(stockId) && !stockId.equals(Constants.STATS_ALL)){
            lstCon.add(new Condition("stockId",Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL,stockId));
        }
        if(!DataUtil.isStringNullOrEmpty(goodsId) && !goodsId.equals(Constants.STATS_ALL)){
            lstCon.add(new Condition("goodsId",Constants.SQL_PRO_TYPE.LONG,Constants.SQL_OPERATOR.EQUAL,goodsId));
        }
        if(!DataUtil.isStringNullOrEmpty(status) && !status.equals(Constants.STATS_ALL)){
            lstCon.add(new Condition("goodsState",Constants.SQL_OPERATOR.EQUAL,status));
        }
        //
        lstCon.add(new Condition("changeDate",Constants.SQL_OPERATOR.ORDER,"desc"));
        //
        List<MjrStockGoodsTotalDTO> lstResult = mjrStockGoodsTotalService.findByCondition(lstCon,tokenInfo);
        lstGoodsTotal = setNameValueInfo(lstResult);
        return lstGoodsTotal;
    }

    @RequestMapping(value = "/getGoodsDetail",method = RequestMethod.GET)
    public @ResponseBody  ServerPagingDTO getGoodsDetail(@RequestParam("stockId")String stockId, @RequestParam("goodsId")String goodsId,
                                                         @RequestParam("goodsState")String goodsState,@RequestParam("limit")String limit,
                                                         @RequestParam("offset")String offset
    ){
        if (DataUtil.isStringNullOrEmpty(goodsId)) {
            return null;
        }
        CatGoodsDTO goodsItem = mapGoodsIdGoods.get(goodsId);

        ServerPagingDTO data = new ServerPagingDTO();
        List<MjrStockTransDetailDTO> lstData = utilsService.getGoodsDetail(selectedCustomer.getId(),stockId,goodsId,goodsItem.getIsSerial(),goodsState,limit,offset,tokenInfo);
        lstGoodsDetails = setListGoodsDetailNameInfo(lstData,goodsItem);
        data.setRows(lstGoodsDetails);
        String key = selectedCustomer.getId()+stockId+goodsId+goodsItem.getIsSerial()+goodsState;
        Long totalItem = mapGoodsDetailTotal.get(key);
        if (totalItem == null) {
            totalItem = utilsService.getCountGoodsDetail(selectedCustomer.getId(),stockId,goodsId,goodsItem.getIsSerial(),goodsState,tokenInfo);
            mapGoodsDetailTotal.put(key,totalItem);
        }
        data.setTotal(totalItem);
        return data;
    }

    @RequestMapping(value = "/getTotalFile")
    public void getErrorImportFile(HttpServletResponse response){
        if(DataUtil.isListNullOrEmpty(lstGoodsTotal)){
            lstGoodsTotal.add(new MjrStockGoodsTotalDTO("","","","","","","","",""));
        }
        //
        String prefixFileName = "Thong_tin_hang_trong_kho_";
        //
        String fileResource = exportTotal(lstGoodsTotal,prefixFileName);
        FunctionUtils.loadFileToClient(response,fileResource);
    }

    @RequestMapping(value = "/getGoodsDetailFile")
    public void getGoodsDetailFile(HttpServletResponse response,@RequestParam("stockId") String stockId){
        if(DataUtil.isListNullOrEmpty(lstGoodsDetails)){
            lstGoodsDetails.add(new MjrStockTransDetailDTO("","","","","","","","","","","",""));
        }
        //
        String prefixFileName = "Thong_tin_chi_tiet_hang_trong_kho_";
        //paging -> re get all goods detail
        MjrStockTransDetailDTO item = lstGoodsDetails.get(0);
        CatGoodsDTO goodsItem = mapGoodsIdGoods.get(item.getGoodsId());
        String key = selectedCustomer.getId()+stockId+item.getGoodsId()+item.getIsSerial()+item.getGoodsState();
        Long totalItem = mapGoodsDetailTotal.get(key);
        if (totalItem == null) {
            totalItem = utilsService.getCountGoodsDetail(selectedCustomer.getId(),stockId,item.getGoodsId(),item.getIsSerial(),item.getGoodsState(),tokenInfo);
            mapGoodsDetailTotal.put(key,totalItem);
        }//
        List<MjrStockTransDetailDTO> lstGoodsDetailAlls = utilsService.getGoodsDetail(selectedCustomer.getId(),stockId,item.getGoodsId(),item.getIsSerial(),item.getGoodsState(),totalItem+"",0+"",tokenInfo);
        //
        String fileResource = exportGoodsDetails(setListGoodsDetailNameInfo(lstGoodsDetailAlls,goodsItem),prefixFileName,stockId,goodsItem.isSerial());
        FunctionUtils.loadFileToClient(response,fileResource);
    }

    //------------------------------------------------------------------------------------------------------------------
    private List<MjrStockTransDetailDTO> setListGoodsDetailNameInfo(List<MjrStockTransDetailDTO> lstStockGoods,CatGoodsDTO goodsItem){
        List<MjrStockTransDetailDTO> lstResult = Lists.newArrayList();
        for(MjrStockTransDetailDTO i:lstStockGoods){
        if (!DataUtil.isListNullOrEmpty(lstStockGoods)) {
                MjrStockTransDetailDTO temp = new MjrStockTransDetailDTO();
                temp.setGoodsId(i.getGoodsId());
                temp.setGoodsCode(goodsItem.getCode());
                temp.setGoodsName(goodsItem.getName());
                temp.setGoodsStateValue(mapAppGoodsState.get(i.getGoodsState()));
                temp.setGoodsState(i.getGoodsState());
                temp.setAmountValue(i.getAmount());
                temp.setImportDate(i.getImportDate());
                temp.setExportDate(i.getExportDate());
                temp.setInputPriceValue(i.getInputPrice());
                temp.setOutputPriceValue(i.getOutputPrice());
                temp.setStatusValue(mapAppStockStatus.get(i.getStatus()));
                temp.setSerial(i.getSerial());
                temp.setIsSerial(goodsItem.getIsSerial());
                //
                lstResult.add(temp);
            }
        }

        return lstResult;
    }

    private List<MjrStockGoodsTotalDTO> setNameValueInfo(List<MjrStockGoodsTotalDTO> lstTotal){
        if(!DataUtil.isListNullOrEmpty(lstTotal)){
            for(MjrStockGoodsTotalDTO i: lstTotal){
                i.setAmountValue(FunctionUtils.formatNumber(i.getAmount()));
                i.setStockName(FunctionUtils.getMapValue(mapStockIdStock,i.getStockId()));
                i.setGoodsStateName(mapAppGoodsState.get(i.getGoodsState()));
            }
        }else{
            return Lists.newArrayList();
        }
        return lstTotal;
    }

    //=======================================================================================================
    private  String exportTotal(List<MjrStockGoodsTotalDTO> lstGoodsTotal,String prefixFileName){
        String templatePath = BundleUtils.getKey("template_url") + Constants.FILE_RESOURCE.GOODS_TOTAL_TEMPLATE;

        File file = new File(templatePath);
        String templateAbsolutePath = file.getAbsolutePath();

        Map<String, Object> beans = new HashMap<>();
        beans.put("items", lstGoodsTotal);
        beans.put("date", DateTimeUtils.convertDateTimeToString(new Date()));

        String fullFileName = prefixFileName +"_"+ DateTimeUtils.getSysDateTimeForFileName() + ".xlsx";
        String reportFullPath = BundleUtils.getKey("temp_url") + fullFileName;
        //
        FunctionUtils.exportExcel(templateAbsolutePath,beans,reportFullPath);
        //
        return reportFullPath;
    }

    //=======================================================================================================
    private  String exportGoodsDetails(List<MjrStockTransDetailDTO> lstGoodsDetails,String prefixFileName,String stockId, boolean isSerial){
        String templatePath;
        if (DataUtil.isListNullOrEmpty(lstGoodsDetails)) {
            return "";
        }
        MjrStockTransDetailDTO goodsItem = lstGoodsDetails.get(0);
        if(isSerial){
            templatePath = BundleUtils.getKey("template_url") + Constants.FILE_RESOURCE.GOODS_DETAILS_SERIAL_TEMPLATE;
        }else{
            templatePath = BundleUtils.getKey("template_url") + Constants.FILE_RESOURCE.GOODS_DETAILS_TEMPLATE;
        }
        //
        File file = new File(templatePath);
        String templateAbsolutePath = file.getAbsolutePath();

        Map<String, Object> beans = new HashMap<>();
        beans.put("items", lstGoodsDetails);
        beans.put("date", DateTimeUtils.convertDateTimeToString(new Date()));
        beans.put("stockName", FunctionUtils.getMapValue(mapStockIdStock,stockId));
        beans.put("goodsName", goodsItem.getGoodsName());
        beans.put("goodsStateValue", goodsItem.getGoodsStateValue());

        String fullFileName = prefixFileName +"_"+ DateTimeUtils.getSysDateTimeForFileName() + ".xlsx";
        String reportFullPath = BundleUtils.getKey("temp_url") + fullFileName;
        //
        FunctionUtils.exportExcel(templateAbsolutePath,beans,reportFullPath);
        return reportFullPath;
    }
    //==================================================================================================================
    private void buildMapTransType(List<AppParamsDTO> lstAppParams){
        if(!DataUtil.isListNullOrEmpty(lstAppParams)){
            for(AppParamsDTO i: lstAppParams){
                mapAppTransType.put(i.getCode(),i.getName());
            }
        }
    }
    //
    private void buildMapStockStatus(List<AppParamsDTO> lstAppParams){
        if(!DataUtil.isListNullOrEmpty(lstAppParams)){
            for(AppParamsDTO i: lstAppParams){
                mapAppStockStatus.put(i.getCode(),i.getName());
            }
        }
    }
}
