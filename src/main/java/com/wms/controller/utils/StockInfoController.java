package com.wms.controller.utils;

import com.google.common.collect.Lists;
import com.wms.base.BaseController;
import com.wms.constants.Constants;
import com.wms.controller.stock_managerment.ExportStockController;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.utils.BundleUtils;
import com.wms.utils.DataUtil;
import com.wms.utils.DateTimeUtils;
import com.wms.utils.FunctionUtils;
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
    BaseService mjrStockGoodsService;
    @Autowired
    BaseService mjrStockGoodsSerialService;

    Logger log = LoggerFactory.getLogger(ExportStockController.class);
    //
    private List<MjrStockGoodsTotalDTO> lstGoodsTotal;
    private List<MjrStockTransDetailDTO> lstGoodsDetails;
    private List<AppParamsDTO> lstAppTransType;
    public Map<String,String> mapAppTransType = new HashMap();
    public Map<String,String> mapAppStockStatus   = new HashMap();

    @ModelAttribute("lstAppTransType")
    public List<AppParamsDTO> setAppTransType(HttpServletRequest request) {
        if (lstAppTransType != null) {
            return lstAppTransType;
        }
        if (lstAppParams == null) {
            if (selectedCustomer == null) {
                this.selectedCustomer = (CatCustomerDTO) request.getSession().getAttribute("selectedCustomer");
            }
            if (tokenInfo == null) {
                this.tokenInfo = (AuthTokenInfo) request.getSession().getAttribute("tokenInfo");
            }
            lstAppParams = FunctionUtils.getAppParams(appParamsService, tokenInfo);
            mapAppGoodsState = FunctionUtils.buildMapAppParams(FunctionUtils.getAppParamByType(Constants.APP_PARAMS.GOODS_STATE,lstAppParams));
        }
        lstAppTransType = FunctionUtils.getAppParamByType(Constants.APP_PARAMS.TRANS_TYPE, lstAppParams);
        buildMapTransType(lstAppTransType);
        buildMapStockStatus(FunctionUtils.getAppParamByType(Constants.APP_PARAMS.STOCK_STATUS, lstAppParams));
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

        if(!DataUtil.isStringNullOrEmpty(stockId) && !stockId.equals(Constants.STATS_ALL)){
            lstCon.add(new Condition("stockId",Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL,stockId));
        }
        if(!DataUtil.isStringNullOrEmpty(goodsId) && !goodsId.equals(Constants.STATS_ALL)){
            lstCon.add(new Condition("goodsId",Constants.SQL_PRO_TYPE.LONG,Constants.SQL_OPERATOR.EQUAL,goodsId));
        }
        if(!DataUtil.isStringNullOrEmpty(status) && !status.equals(Constants.STATS_ALL)){
            lstCon.add(new Condition("goodsState",Constants.SQL_OPERATOR.EQUAL,status));
        }

        List<MjrStockGoodsTotalDTO> lstResult = mjrStockGoodsTotalService.findByCondition(lstCon,tokenInfo);
        lstGoodsTotal = setNameValueInfo(lstResult);
        return lstGoodsTotal;
    }

    @RequestMapping(value = "/getGoodsDetail",method = RequestMethod.GET)
    public @ResponseBody List<MjrStockTransDetailDTO> getGoodsDetail(@RequestParam("stockId")String stockId, @RequestParam("goodsId")String goodsId,
                                                                  @RequestParam("goodsState")String goodsState
    ){
        List<Condition> lstCon = Lists.newArrayList();
        lstCon.add(new Condition("custId",Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL,selectedCustomer.getId()));

        if(!DataUtil.isStringNullOrEmpty(stockId) && !stockId.equals(Constants.STATS_ALL)){
            lstCon.add(new Condition("stockId",Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL,stockId));
        }
        if(!DataUtil.isStringNullOrEmpty(goodsId) && !goodsId.equals(Constants.STATS_ALL)){
            lstCon.add(new Condition("goodsId",Constants.SQL_PRO_TYPE.LONG,Constants.SQL_OPERATOR.EQUAL,goodsId));
        }
        if(!DataUtil.isStringNullOrEmpty(goodsState) && !goodsState.equals(Constants.STATS_ALL)){
            lstCon.add(new Condition("goodsState",Constants.SQL_OPERATOR.EQUAL,goodsState));
        }
        //
        List<MjrStockTransDetailDTO> lstResult;
        CatGoodsDTO goodsItem = mapGoodsIdGoods.get(goodsId);
        if (goodsItem.isSerial()) {
            lstResult = FunctionUtils.convertGoodsSerialToDetail(mjrStockGoodsSerialService.findByCondition(lstCon,tokenInfo));
        }else{
            lstResult = convertGoodsToDetail(mjrStockGoodsService.findByCondition(lstCon,tokenInfo));

        }

        lstGoodsDetails = FunctionUtils.setNameValueGoodsDetail(lstResult,mapGoodsIdGoods,mapStockIdStock,mapAppGoodsState);
        return lstGoodsDetails;
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
        //
        String fileResource = exportGoodsDetails(lstGoodsDetails,prefixFileName,stockId);
        FunctionUtils.loadFileToClient(response,fileResource);
    }

    //------------------------------------------------------------------------------------------------------------------
    private List<MjrStockTransDetailDTO> convertGoodsToDetail(List<MjrStockGoodsDTO> lstStockGoods){
        List<MjrStockTransDetailDTO> lstResult = Lists.newArrayList();
        if (!DataUtil.isListNullOrEmpty(lstStockGoods)) {
            for(MjrStockGoodsDTO i:lstStockGoods){
                MjrStockTransDetailDTO temp = new MjrStockTransDetailDTO();
                temp.setGoodsId(i.getGoodsId());
                temp.setGoodsState(i.getGoodsState());
                temp.setAmount(i.getAmount());
                temp.setImportDate(i.getImportDate());
                temp.setExportDate(i.getExportDate());
                temp.setInputPrice(i.getInputPrice());
                temp.setOutputPrice(i.getOutputPrice());
                temp.setStatusValue(mapAppTransType.get(i.getStatus()));
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
        String templatePath = BundleUtils.getkey("template_url") + Constants.FILE_RESOURCE.GOODS_TOTAL_TEMPLATE;

        File file = new File(templatePath);
        String templateAbsolutePath = file.getAbsolutePath();

        Map<String, Object> beans = new HashMap<>();
        beans.put("items", lstGoodsTotal);
        beans.put("date", DateTimeUtils.convertDateTimeToString(new Date()));

        String fullFileName = prefixFileName +"_"+ DateTimeUtils.getSysDateTimeForFileName() + ".xlsx";
        String reportFullPath = BundleUtils.getkey("temp_url") + fullFileName;
        //
        FunctionUtils.exportExcel(templateAbsolutePath,beans,reportFullPath);
        //
        return reportFullPath;
    }

    //=======================================================================================================
    private  String exportGoodsDetails(List<MjrStockTransDetailDTO> lstGoodsDetails,String prefixFileName,String stockId){
        String templatePath;
        MjrStockTransDetailDTO goodsItem = lstGoodsDetails.get(0);
        CatGoodsDTO catGoods = mapGoodsIdGoods.get(goodsItem.getGoodsId());
        if(catGoods.isSerial()){
            templatePath = BundleUtils.getkey("template_url") + Constants.FILE_RESOURCE.GOODS_DETAILS_SERIAL_TEMPLATE;
        }else{
            templatePath = BundleUtils.getkey("template_url") + Constants.FILE_RESOURCE.GOODS_DETAILS_TEMPLATE;
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
        String reportFullPath = BundleUtils.getkey("temp_url") + fullFileName;
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
