package com.wms.controller.utils;

import com.google.common.collect.Lists;
import com.sun.xml.internal.ws.util.StringUtils;
import com.wms.base.BaseController;
import com.wms.constants.Constants;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.UtilsService;
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
import java.util.*;

/**
 * Created by duyot on 3/21/2017.
 */
@Controller
@RequestMapping("/workspace/utils/update_stock_goods_info")
@Scope("session")
public class UpdateStockGoodsInfor extends BaseController {

    @Autowired
    BaseService mjrStockGoodsService;

    @Autowired
    BaseService mjrStockGoodsSerialService;

    @Autowired
    UtilsService utilsService;

    Logger log = LoggerFactory.getLogger(StockInfoController.class);
    //
    private List<MjrStockGoodsTotalDTO> lstGoodsTotal;
    private List<MjrStockTransDetailDTO> lstGoodsDetails;

    //
    @RequestMapping()
    public String home(Model model) {
        //clear previous data
        lstGoodsTotal = Lists.newArrayList();
        lstGoodsDetails = Lists.newArrayList();
        //
        model.addAttribute("menuName", "menu.update.stock.goods.info");
        return "utils/update_stock_goods_infor";
    }

    @RequestMapping(value = "/findByCondition", method = RequestMethod.GET)
    public @ResponseBody
    List<MjrStockTransDetailDTO> getStockInfo(@RequestParam("stockId") String stockId, @RequestParam("goodsId") String goodsId, @RequestParam("cellCode") String cellCode,
                                              @RequestParam("partnerId") String partnerId, @RequestParam("statusVal") String statusVal,
                                              @RequestParam("startCreateDateVal") String startCreateDateVal, @RequestParam("endCreateDateVal") String endCreateDateVal,
                                              @RequestParam("startExpireDateVal") String startExpireDateVal, @RequestParam("endExpireDateVal") String endExpireDateVal
    ) {
        List<Condition> lstCon = Lists.newArrayList();

        lstCon.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, selectedCustomer.getId()));
//        lstCon.add(new Condition("amount", Constants.SQL_OPERATOR.GREATER_EQUAL, 0));


        if (!DataUtil.isStringNullOrEmpty(stockId) && !stockId.equals(Constants.STATS_ALL)) {
            lstCon.add(new Condition("stockId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, stockId));
        }

        if (!DataUtil.isStringNullOrEmpty(cellCode) && !cellCode.equals(Constants.STATS_ALL)) {
            lstCon.add(new Condition("cellCode", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, cellCode));
        }

        if (!DataUtil.isStringNullOrEmpty(goodsId) && !goodsId.equals(Constants.STATS_ALL)) {
            lstCon.add(new Condition("goodsId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, goodsId));
        }

        if (!DataUtil.isStringNullOrEmpty(partnerId) && !partnerId.equals(Constants.STATS_ALL)) {
            lstCon.add(new Condition("partnerId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, partnerId));
        }

        if (!DataUtil.isStringNullOrEmpty(statusVal) && !statusVal.equals(Constants.STATS_ALL)) {
            lstCon.add(new Condition("goodsState", Constants.SQL_OPERATOR.EQUAL, statusVal));
        }

        if(!DataUtil.isStringNullOrEmpty(startCreateDateVal) && !DataUtil.isStringNullOrEmpty(endCreateDateVal)){
            lstCon.add(new Condition("importDate", Constants.SQL_OPERATOR.BETWEEN,startCreateDateVal + "|"+ endCreateDateVal));
        }

        if(!DataUtil.isStringNullOrEmpty(startExpireDateVal) && !DataUtil.isStringNullOrEmpty(endExpireDateVal)){
            lstCon.add(new Condition("expireDate", Constants.SQL_OPERATOR.BETWEEN,startExpireDateVal + "|"+ endExpireDateVal));
        }
        //order
        lstCon.add(new Condition("changeDate", Constants.SQL_OPERATOR.ORDER, "desc"));
        //get from stockgoods
        List<MjrStockGoodsDTO> lstStockGoods = mjrStockGoodsService.findByCondition(lstCon);
        List<MjrStockGoodsSerialDTO> lstStockGoodsSerial = mjrStockGoodsSerialService.findByCondition(lstCon);

        return sumupAllGoods(lstStockGoods, lstStockGoodsSerial);
    }

    private List<MjrStockTransDetailDTO> sumupAllGoods(List<MjrStockGoodsDTO> lstStockGoods, List<MjrStockGoodsSerialDTO> lstStockGoodsSerial){
        List<MjrStockTransDetailDTO> results = Lists.newArrayList();
        initGoodsDetailFromStockGoods(results, lstStockGoods);
        initGoodsDetailFromStockGoodsSerial(results, lstStockGoodsSerial);
        return results;
    }

    private void initGoodsDetailFromStockGoods(List<MjrStockTransDetailDTO> results, List<MjrStockGoodsDTO> lstStockGoods){
        if (!DataUtil.isListNullOrEmpty(lstStockGoods)) {
            for(MjrStockGoodsDTO i: lstStockGoods){
                MjrStockTransDetailDTO detail = new MjrStockTransDetailDTO();
                //
                detail.setId(i.getId());
                detail.setCustId(i.getCustId());

                detail.setStockId(i.getStockId());
                detail.setStockCode(FunctionUtils.getMapCodeValue(mapStockIdStock, i.getStockId()));
                detail.setStockName(FunctionUtils.getMapValue(mapStockIdStock, i.getStockId()));

                detail.setGoodsId(i.getGoodsId());
                detail.setGoodsCode(FunctionUtils.getMapCodeValue(mapGoodsIdGoods, i.getGoodsId()));
                detail.setGoodsName(FunctionUtils.getMapValue(mapGoodsIdGoods, i.getGoodsId()));
                detail.setGoodsState(i.getGoodsState());
                detail.setGoodsStateValue(mapAppGoodsState.get(i.getGoodsState()));

                detail.setCellCode(i.getCellCode());
                detail.setAmount(i.getAmount());
                detail.setAmountValue(FunctionUtils.formatNumber(i.getAmount()));
                detail.setImportDate(i.getImportDate());
                detail.setChangeDate(i.getChangeDate());
                detail.setStatus(i.getStatus());
                detail.setPartnerId(i.getPartnerId());
                detail.setInputPrice(i.getInputPrice());
                detail.setOutputPrice(i.getOutputPrice());
                detail.setExportDate(i.getExportDate());
                //
                results.add(detail);
            }
        }
    }

    private void initGoodsDetailFromStockGoodsSerial(List<MjrStockTransDetailDTO> results, List<MjrStockGoodsSerialDTO> lstStockGoodsSerial){
        if (!DataUtil.isListNullOrEmpty(lstStockGoodsSerial)) {
            for(MjrStockGoodsSerialDTO i: lstStockGoodsSerial){
                MjrStockTransDetailDTO detail = new MjrStockTransDetailDTO();
                //
                detail.setId(i.getId());
                detail.setCustId(i.getCustId());

                detail.setStockId(i.getStockId());
                detail.setStockCode(FunctionUtils.getMapCodeValue(mapStockIdStock, i.getStockId()));
                detail.setStockName(FunctionUtils.getMapValue(mapStockIdStock, i.getStockId()));

                detail.setGoodsId(i.getGoodsId());
                detail.setGoodsCode(FunctionUtils.getMapCodeValue(mapGoodsIdGoods, i.getGoodsId()));
                detail.setGoodsName(FunctionUtils.getMapValue(mapGoodsIdGoods, i.getGoodsId()));
                detail.setGoodsState(i.getGoodsState());
                detail.setGoodsStateValue(mapAppGoodsState.get(i.getGoodsState()));

                detail.setCellCode(i.getCellCode());
                detail.setAmount(i.getAmount());
                detail.setAmountValue(FunctionUtils.formatNumber(i.getAmount()));
                detail.setImportDate(i.getImportDate());
                detail.setChangeDate(i.getChangeDate());
                detail.setStatus(i.getStatus());
                detail.setPartnerId(i.getPartnerId());
                detail.setInputPrice(i.getInputPrice());
                detail.setOutputPrice(i.getOutputPrice());
                detail.setExportDate(i.getExportDate());
                //
                results.add(detail);
            }
        }
    }

    @RequestMapping(value = "/getGoodsDetail", method = RequestMethod.GET)
    public @ResponseBody
    ServerPagingDTO getGoodsDetail(@RequestParam("stockId") String stockId, @RequestParam("goodsId") String goodsId,
                                   @RequestParam("goodsState") String goodsState, @RequestParam("partnerId") String partnerId, @RequestParam("limit") String limit,
                                   @RequestParam("offset") String offset
    ) {
        if (DataUtil.isStringNullOrEmpty(goodsId)) {
            return null;
        }
        CatGoodsDTO goodsItem = mapGoodsIdGoods.get(goodsId);

        ServerPagingDTO data = new ServerPagingDTO();
        List<MjrStockTransDetailDTO> lstData = utilsService.getGoodsDetail(selectedCustomer.getId(), stockId, goodsId, goodsItem.getIsSerial(), goodsState, partnerId, limit, offset);
        lstGoodsDetails = setListGoodsDetailNameInfo(lstData, goodsItem);
        data.setRows(lstGoodsDetails);
        data.setTotal(Long.valueOf(lstGoodsDetails.size()));
        return data;
    }

    @RequestMapping(value = "/getTotalFile")
    public void getErrorImportFile(HttpServletResponse response) {
        if (DataUtil.isListNullOrEmpty(lstGoodsTotal)) {
            lstGoodsTotal.add(new MjrStockGoodsTotalDTO("", "", "", "", "", "", "", "", ""));
        }
        //
        String prefixFileName = "Thong_tin_hang_trong_kho_";
        //
        String fileResource = exportTotal(lstGoodsTotal, prefixFileName);
        FunctionUtils.loadFileToClient(response, fileResource);
    }

    //Chi tiet toan bo cac hang hoa trong kho
    @RequestMapping(value = "/getAllStockGoodsDetail")
    public void getAllStockGoodsDetail(HttpServletResponse response, @RequestParam("stockId") String stockId, @RequestParam("partnerId") String partnerId, @RequestParam("goodsId") String goodsId,
                                       @RequestParam("status") String status) {

        String prefixFileName = "Thong_tin_chi_tiet_hang_trong_kho_";
        List<MjrStockTransDetailDTO> lstGoodsDetailAlls = utilsService.getAllStockGoodsDetail(currentUser.getId(), selectedCustomer.getId(), stockId, partnerId, goodsId, status);
        String fileResource = exportAllGoodsDetails(setListGoodsDetailNameInfo(lstGoodsDetailAlls), prefixFileName);
        FunctionUtils.loadFileToClient(response, fileResource);
    }

    //Chi tiet hang hoa trong kho khi xem thong tin 1 hang hoa
    @RequestMapping(value = "/getGoodsDetailFile")
    public void getGoodsDetailFile(HttpServletResponse response, @RequestParam("stockId") String stockId, @RequestParam("partnerId") String partnerId) {
        if (DataUtil.isListNullOrEmpty(lstGoodsDetails)) {
            lstGoodsDetails.add(new MjrStockTransDetailDTO("", "", "", "", "", "", "", "", "", "", "", ""));
        }
        //
        String prefixFileName = "Thong_tin_chi_tiet_hang_trong_kho_";
        //paging -> re get all goods detail
        MjrStockTransDetailDTO item = lstGoodsDetails.get(0);
        CatGoodsDTO goodsItem = mapGoodsIdGoods.get(item.getGoodsId());
        Long totalItem = utilsService.getCountGoodsDetail(selectedCustomer.getId(), stockId, item.getGoodsId(), item.getIsSerial(), item.getGoodsState(), partnerId);
        List<MjrStockTransDetailDTO> lstGoodsDetailAlls = utilsService.getGoodsDetail(selectedCustomer.getId(), stockId, item.getGoodsId(), item.getIsSerial(), item.getGoodsState(), partnerId, totalItem + "", 0 + "");
        //
        String fileResource = exportGoodsDetails(setListGoodsDetailNameInfo(lstGoodsDetailAlls, goodsItem), prefixFileName, stockId, goodsItem.isSerial());
        FunctionUtils.loadFileToClient(response, fileResource);
    }

    //------------------------------------------------------------------------------------------------------------------
    private List<MjrStockTransDetailDTO> setListGoodsDetailNameInfo(List<MjrStockTransDetailDTO> lstStockGoods, CatGoodsDTO goodsItem) {
        List<MjrStockTransDetailDTO> lstResult = Lists.newArrayList();
        String partnerPermission = currentUser.getPartnerPermission();
        boolean fladAdd = true;
        String goodUnitId = "";
        for (MjrStockTransDetailDTO i : lstStockGoods) {
            fladAdd = true;
            if (!DataUtil.isListNullOrEmpty(lstStockGoods)) {
                if ("1".equals(partnerPermission) && !mapPartnerIdPartner.containsKey(i.getPartnerId())) {
                    fladAdd = false;
                }
                if (fladAdd) {
                    MjrStockTransDetailDTO temp = new MjrStockTransDetailDTO();
                    temp.setGoodsId(i.getGoodsId());
                    temp.setGoodsCode(goodsItem.getCode());
                    temp.setGoodsName(goodsItem.getName());
                    temp.setGoodsStateValue(mapAppGoodsState.get(i.getGoodsState()));
                    temp.setGoodsState(i.getGoodsState());
                    temp.setAmountValue(FunctionUtils.formatNumber(i.getAmount()));
                    temp.setImportDate(i.getImportDate());
                    temp.setExportDate(i.getExportDate());
                    temp.setInputPriceValue(FunctionUtils.formatNumber(i.getInputPrice()));
                    temp.setOutputPriceValue(FunctionUtils.formatNumber(i.getOutputPrice()));
                    temp.setSerial(i.getSerial());
                    temp.setIsSerial(goodsItem.getIsSerial());
                    temp.setCellCode(i.getCellCode());
                    if (!DataUtil.isStringNullOrEmpty(i.getWeight())) {
                        temp.setWeight(FunctionUtils.formatNumber(String.valueOf(Double.valueOf(i.getWeight()) * Double.valueOf(i.getAmount()))));
                    }
                    if (!DataUtil.isStringNullOrEmpty(i.getVolume())) {
                        temp.setVolume(FunctionUtils.formatNumber(String.valueOf(Double.valueOf(i.getVolume()) * Double.valueOf(i.getAmount()))));
                    }
                    goodUnitId = mapGoodsIdGoods.get(i.getGoodsId()) != null ? mapGoodsIdGoods.get(i.getGoodsId()).getUnitType() : "";
                    temp.setUnitName(mapAppParamsUnitName.get(goodUnitId));
                    if (i.getPartnerId() != null && mapPartnerIdPartner.get(i.getPartnerId()) != null) {
                        temp.setPartnerName(mapPartnerIdPartner.get(i.getPartnerId()).getName());
                    }
                    //
                    lstResult.add(temp);
                }
            }
        }

        return lstResult;
    }


    //------------------------------------------------------------------------------------------------------------------
    private List<MjrStockTransDetailDTO> setListGoodsDetailNameInfo(List<MjrStockTransDetailDTO> lstStockGoods) {
        List<MjrStockTransDetailDTO> lstResult = Lists.newArrayList();
        String goodUnitId = "";
        for (MjrStockTransDetailDTO i : lstStockGoods) {
            if (!DataUtil.isListNullOrEmpty(lstStockGoods)) {
                MjrStockTransDetailDTO temp = new MjrStockTransDetailDTO();
                temp.setStockCode(mapStockIdStock.get(i.getStockId()).getCode());
                temp.setStockName(mapStockIdStock.get(i.getStockId()).getName());
                temp.setGoodsCode(mapGoodsIdGoods.get(i.getGoodsId()).getCode());
                temp.setGoodsName(mapGoodsIdGoods.get(i.getGoodsId()).getName());
                temp.setGoodsStateValue(mapAppGoodsState.get(i.getGoodsState()));
                temp.setAmountValue(FunctionUtils.formatNumber(i.getAmount()));
                temp.setImportDate(i.getImportDate());
                temp.setChangeDate(i.getChangeDate());
                if (!DataUtil.isStringNullOrEmpty(i.getInputPrice())) {
                    temp.setInputPriceValue(FunctionUtils.formatNumber(i.getInputPrice()));
                    temp.setTotalMoney(FunctionUtils.formatNumber(String.valueOf(Double.valueOf(i.getInputPrice()) * Double.valueOf(i.getAmount()))));
                }
                temp.setSerial(i.getSerial());
                temp.setCellCode(i.getCellCode());
                if (!DataUtil.isStringNullOrEmpty(i.getWeight())) {
                    temp.setWeight(FunctionUtils.formatNumber(String.valueOf(Double.valueOf(i.getWeight()) * Double.valueOf(i.getAmount()))));
                }
                if (!DataUtil.isStringNullOrEmpty(i.getVolume())) {
                    temp.setVolume(FunctionUtils.formatNumber(String.valueOf(Double.valueOf(i.getVolume()) * Double.valueOf(i.getAmount()))));
                }
                goodUnitId = mapGoodsIdGoods.get(i.getGoodsId()) != null ? mapGoodsIdGoods.get(i.getGoodsId()).getUnitType() : "";
                temp.setUnitName(mapAppParamsUnitName.get(goodUnitId));
                if (i.getPartnerId() != null && mapPartnerIdPartner.get(i.getPartnerId()) != null) {
                    temp.setPartnerCode(mapPartnerIdPartner.get(i.getPartnerId()).getCode());
                    temp.setPartnerName(mapPartnerIdPartner.get(i.getPartnerId()).getName());
                }
                lstResult.add(temp);
            }
        }

        return lstResult;
    }

    private List<MjrStockGoodsTotalDTO> setNameValueInfo(List<MjrStockGoodsTotalDTO> lstTotal) {
        List<MjrStockGoodsTotalDTO> finalResult = new ArrayList<>();
        String goodUnitId = "";
        if (!DataUtil.isListNullOrEmpty(lstTotal)) {
            for (MjrStockGoodsTotalDTO i : lstTotal) {
                i.setAmountValue(FunctionUtils.formatNumber(i.getAmount()));
                i.setGoodsStateName(mapAppGoodsState.get(i.getGoodsState()));
                goodUnitId = mapGoodsIdGoods.get(i.getGoodsId()) != null ? mapGoodsIdGoods.get(i.getGoodsId()).getUnitType() : "";
                i.setGoodsUnitName(mapAppParamsUnitName.get(goodUnitId));
                if (!"".equals(FunctionUtils.getMapValue(mapStockIdStock, i.getStockId()))) {
                    i.setStockName(FunctionUtils.getMapValue(mapStockIdStock, i.getStockId()));
                    finalResult.add(i);
                }
            }
        } else {
            return Lists.newArrayList();
        }
        return finalResult;
    }

    //=======================================================================================================
    private String exportTotal(List<MjrStockGoodsTotalDTO> lstGoodsTotal, String prefixFileName) {
        String templatePath = profileConfig.getTemplateURL() + Constants.FILE_RESOURCE.GOODS_TOTAL_TEMPLATE;

        File file = new File(templatePath);
        String templateAbsolutePath = file.getAbsolutePath();

        Map<String, Object> beans = new HashMap<>();
        beans.put("items", lstGoodsTotal);
        beans.put("date", DateTimeUtils.convertDateTimeToString(new Date()));

        String fullFileName = prefixFileName + "_" + DateTimeUtils.getSysDateTimeForFileName() + ".xlsx";
        String reportFullPath = profileConfig.getTempURL() + fullFileName;
        //
        FunctionUtils.exportExcel(templateAbsolutePath, beans, reportFullPath);
        //
        return reportFullPath;
    }

    //=======================================================================================================
    private String exportGoodsDetails(List<MjrStockTransDetailDTO> lstGoodsDetails, String prefixFileName, String stockId, boolean isSerial) {
        String templatePath;
        if (DataUtil.isListNullOrEmpty(lstGoodsDetails)) {
            return "";
        }
        MjrStockTransDetailDTO goodsItem = lstGoodsDetails.get(0);
        if (isSerial) {
            templatePath = profileConfig.getTemplateURL() + Constants.FILE_RESOURCE.GOODS_DETAILS_SERIAL_TEMPLATE;
        } else {
            templatePath = profileConfig.getTemplateURL() + Constants.FILE_RESOURCE.GOODS_DETAILS_TEMPLATE;
        }
        //
        File file = new File(templatePath);
        String templateAbsolutePath = file.getAbsolutePath();

        Map<String, Object> beans = new HashMap<>();
        beans.put("items", lstGoodsDetails);
        beans.put("date", DateTimeUtils.convertDateTimeToString(new Date()));
        beans.put("stockName", FunctionUtils.getMapValue(mapStockIdStock, stockId));
        beans.put("goodsName", goodsItem.getGoodsName());
        beans.put("goodsStateValue", goodsItem.getGoodsStateValue());

        String fullFileName = prefixFileName + "_" + DateTimeUtils.getSysDateTimeForFileName() + ".xlsx";
        String reportFullPath = profileConfig.getTempURL() + fullFileName;
        //
        FunctionUtils.exportExcel(templateAbsolutePath, beans, reportFullPath);
        return reportFullPath;
    }

    //=======================================================================================================
    private String exportAllGoodsDetails(List<MjrStockTransDetailDTO> lstGoodsDetails, String prefixFileName) {

        if (DataUtil.isListNullOrEmpty(lstGoodsDetails)) {
            return "";
        }
        String templatePath = profileConfig.getTemplateURL() + Constants.FILE_RESOURCE.GOODS_STOCK_DETAILS_TEMPLATE;

        //
        File file = new File(templatePath);
        String templateAbsolutePath = file.getAbsolutePath();

        Map<String, Object> beans = new HashMap<>();
        beans.put("items", lstGoodsDetails);
        beans.put("date", DateTimeUtils.convertDateTimeToString(new Date()));

        String fullFileName = prefixFileName + "_" + DateTimeUtils.getSysDateTimeForFileName() + ".xlsx";
        String reportFullPath = profileConfig.getTempURL() + fullFileName;
        //
        FunctionUtils.exportExcel(templateAbsolutePath, beans, reportFullPath);
        return reportFullPath;
    }
    //==================================================================================================================
}
