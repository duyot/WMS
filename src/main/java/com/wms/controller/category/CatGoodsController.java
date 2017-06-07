package com.wms.controller.category;

import com.google.common.collect.Lists;
import com.wms.base.BaseCommonController;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.utils.BundleUtils;
import com.wms.utils.DataUtil;
import com.wms.utils.FunctionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by duyot on 12/9/2016.
 */
@Controller
@RequestMapping("/workspace/cat_goods_ctr")
@Scope("session")
public class CatGoodsController extends BaseCommonController{
    Logger log = LoggerFactory.getLogger(CatGoodsController.class);

    public Map<String, String> mapGoodsGroup;
    public Map<String, String> mapUnitType;
    public Map<String,String> mapAppGoodsState;

    @Autowired
    BaseService catGoodsGroupService;

    @Autowired
    BaseService catGoodsService;

    @ModelAttribute("mapGoodsGroup")
    public Map<String, String> setLstGoodsGroup(HttpServletRequest request){
        if(tokenInfo == null){
            this.tokenInfo =  (AuthTokenInfo) request.getSession().getAttribute("tokenInfo");
        }
        if (mapGoodsGroup == null) {
            mapGoodsGroup = new HashMap<>();
            CatCustomerDTO curCust = (CatCustomerDTO) request.getSession().getAttribute("selectedCustomer");
            List<Condition> lstCon = Lists.newArrayList();
            lstCon.add(new Condition("status", Constants.SQL_OPERATOR.EQUAL,Constants.STATUS.ACTIVE));
            lstCon.add(new Condition("custId",Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL,curCust.getId()));
            lstCon.add(new Condition("name",Constants.SQL_OPERATOR.ORDER,"desc"));
            List<CatGoodsGroupDTO> lstCatGoodsGroup = catGoodsGroupService.findByCondition(lstCon,tokenInfo);

            for(CatGoodsGroupDTO i: lstCatGoodsGroup){
                mapGoodsGroup.put(i.getId(), i.getName());
            }
        }
        //
        if (mapAppGoodsState == null) {
            if(lstAppParams == null){
                lstAppParams = FunctionUtils.getAppParams(appParamsService,tokenInfo);
            }
            mapAppGoodsState = FunctionUtils.buildMapAppParams(FunctionUtils.getAppParamByType(Constants.APP_PARAMS.GOODS_STATE,lstAppParams));
        }

        return mapGoodsGroup;
    }

    @ModelAttribute("mapUnitType")
    public Map<String, String> setMapUnitType(HttpServletRequest request){
        //
        if (mapUnitType == null) {
            if(tokenInfo == null){
                this.tokenInfo =  (AuthTokenInfo) request.getSession().getAttribute("tokenInfo");
            }
            if(lstAppParams == null){
                lstAppParams = FunctionUtils.getAppParams(appParamsService,tokenInfo);
            }
            mapUnitType = FunctionUtils.buildMapAppParams(FunctionUtils.getAppParamByType(Constants.APP_PARAMS.UNIT_TYPE,lstAppParams));
        }
        //
        //
        return mapUnitType;
    }

    @RequestMapping()
    public String home(Model model){
        model.addAttribute("menuName","menu.catgoods");
        return "category/cat_goods";
    }


    //
    @RequestMapping(value = "/getTemplateFile")
    public void getTemplateFile(HttpServletResponse response){
        FunctionUtils.loadFileToClient(response, BundleUtils.getKey("template_url") + Constants.FILE_RESOURCE.IMPORT_GOODS_TEMPLATE);
    }
    //
    @RequestMapping(value = "/getErrorImportFile")
    public void getErrorImportFile(HttpServletRequest request,HttpServletResponse response){
        String fileResource = BundleUtils.getKey("temp_url") + request.getSession().getAttribute("file_goods_import_error");
        FunctionUtils.loadFileToClient(response,fileResource);
    }
    //
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public List<CatGoodsDTO> uploadFile(MultipartHttpServletRequest request) {
        //1. get the files from the request object
        Iterator<String> itr =  request.getFileNames();
        MultipartFile mpf = request.getFile(itr.next());
        //
        ImportFileResultDTO importFileResult = FunctionUtils.getListGoodsImportFromFile(mpf,mapAppGoodsState);
        if(!importFileResult.isValid()){
            //save error file
            String prefixFileName = selectedCustomer.getId() +"_"+  currentUser.getCode();
            String fileName = FunctionUtils.exportExcelImportGoodsError(importFileResult.getLstGoods(),prefixFileName);
            //save in session
            request.getSession().setAttribute("file_goods_import_error",fileName);
            return null;
        }
        return importFileResult.getLstGoods();
    }
    //
    @RequestMapping(value = "/findByCondition",method = RequestMethod.GET)
    public  @ResponseBody List<CatGoodsDTO> findByCondition(@RequestParam("status")String status){
        List<Condition> lstCon = Lists.newArrayList();

        lstCon.add(new Condition("custId",Constants.SQL_PRO_TYPE.LONG,Constants.SQL_OPERATOR.EQUAL,selectedCustomer.getId()));
        if(!DataUtil.isStringNullOrEmpty(status) && !status.equals(Constants.STATS_ALL)){
            lstCon.add(new Condition("status", Constants.SQL_OPERATOR.EQUAL,status));
        }
        lstCon.add(new Condition("createdDate",Constants.SQL_OPERATOR.ORDER,"desc"));

        List<CatGoodsDTO> lstCatGoods = catGoodsService.findByCondition(lstCon,tokenInfo);

        String serialTypeName;
        String isSerialName = Constants.SERIAL_TYPE.IS_SERIAL_NAME;
        String isSerial = Constants.SERIAL_TYPE.IS_SERIAL;
        String noSerialName = Constants.SERIAL_TYPE.NO_SERIAL_NAME;

        for(CatGoodsDTO i: lstCatGoods){
            i.setCode(StringEscapeUtils.escapeHtml(i.getCode()));
            i.setName(StringEscapeUtils.escapeHtml(i.getName()));
            i.setCustName(selectedCustomer.getName());
            i.setGoodsGroupName(mapGoodsGroup.get(i.getGoodsGroupId()));
            serialTypeName =  isSerial.equals(i.getIsSerial()) ? isSerialName: noSerialName;
            i.setIsSerialName(serialTypeName);
            i.setStatusName(mapAppStatus.get(i.getStatus()));
            i.setUnitTypeName(mapUnitType.get(i.getUnitType()));
            i.setInPriceValue(FunctionUtils.formatNumber(i.getInPrice()));
            i.setOutPriceValue(FunctionUtils.formatNumber(i.getOutPrice()));

        }

        return lstCatGoods;
    }
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public String add(CatGoodsDTO catGoods, RedirectAttributes redirectAttributes){
        catGoods.setStatus("1");
        catGoods.setIsSerial(FunctionUtils.getValueFromToggle(catGoods.getIsSerial()));
        catGoods.setCustId(this.selectedCustomer.getId());
        catGoods.setCreatedDate(catGoodsService.getSysDate(tokenInfo));
        //
        ResponseObject response = catGoodsService.add(catGoods,tokenInfo);
        if(Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())){
            redirectAttributes.addFlashAttribute("actionInfo","result.add.success");
            redirectAttributes.addFlashAttribute("successStyle",Constants.SUCCES_COLOR);
            log.info("Add: "+ catGoods.toString()+" SUCCESS");
        }else if(Responses.ERROR_CONSTRAINT.getName().equalsIgnoreCase(response.getStatusName())){
            log.info("ERROR");
            redirectAttributes.addFlashAttribute("actionInfo","result.fail.constraint");
        }else{
            log.info("Add: "+ catGoods.toString()+" ERROR");
            redirectAttributes.addFlashAttribute("actionInfo","result.add.fail");
        }

        return "redirect:/workspace/cat_goods_ctr";
    }
    //
    @RequestMapping(value = "/saveListGoods", method = RequestMethod.POST)
    @ResponseBody
    public ResponseObject importStock(@RequestBody ListGoodsDTO lstGoodsObject,HttpServletRequest request) {
        log.info("------------------------------------------------------------");
        List<CatGoodsDTO> lstGoods = lstGoodsObject.getLstGoods();
        log.info(currentUser.getCode() +" importting goods with: " + lstGoods.size() + " items.");
        List<CatGoodsDTO> lstError = Lists.newArrayList();
        //
        ResponseObject insertResponse = new ResponseObject();
        String sysdate = appParamsService.getSysDate(tokenInfo);
        int successCount = 0;
        for (CatGoodsDTO item: lstGoods) {
            item.setStatus("1");
            item.setCreatedDate(sysdate);
            item.setCustId(this.selectedCustomer.getId());
            insertResponse = catGoodsService.add(item,tokenInfo);
            if(!Responses.SUCCESS.getName().equalsIgnoreCase(insertResponse.getStatusCode())){
                item.setErrorInfo("Thông tin hàng đã được khai báo trên hệ thống");
                lstError.add(item);
            }else{
                successCount ++;
            }
        }
        //
        if(lstError.size()>0){
            //export file error
            String prefixFileName = selectedCustomer.getId() +"_"+  currentUser.getCode();
            String fileName = FunctionUtils.exportExcelImportGoodsError(lstError,prefixFileName);
            //save in session
            request.getSession().setAttribute("file_goods_save_error",fileName);
            //
            insertResponse.setStatusCode("SUCCESS_WITH_ERROR");
            insertResponse.setTotal(lstGoods.size()+"");
            insertResponse.setSuccess(successCount+"");
        }else{
            insertResponse.setStatusCode("SUCCESS");
            insertResponse.setSuccess(lstGoods.size()+"");
        }
        //
        return insertResponse;
    }

    //
    @RequestMapping(value = "/getErrorSaveGoods")
    public void getErrorSaveGoods(HttpServletRequest request,HttpServletResponse response){
        String fileResource = BundleUtils.getKey("temp_url") + request.getSession().getAttribute("file_goods_save_error");
        FunctionUtils.loadFileToClient(response,fileResource);
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public String update(CatGoodsDTO catGoods, RedirectAttributes redirectAttributes){
        String sysdate = appParamsService.getSysDate(tokenInfo);
        catGoods.setCustId(selectedCustomer.getId());
        catGoods.setStatus(FunctionUtils.getValueFromToggle(catGoods.getStatus()));
        catGoods.setIsSerial(FunctionUtils.getValueFromToggle(catGoods.getIsSerial()));
        catGoods.setCreatedDate(sysdate);
        log.info("Update cat_goods info: "+ catGoods.toString());
        ResponseObject response = catGoodsService.update(catGoods,tokenInfo);
        if(Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())){
            log.info("SUCCESS");
            redirectAttributes.addFlashAttribute("actionInfo", "result.update.success");
            redirectAttributes.addFlashAttribute("successStyle",Constants.SUCCES_COLOR);
        }else if(Responses.ERROR_CONSTRAINT.getName().equalsIgnoreCase(response.getStatusCode())){
            log.info("ERROR");
            redirectAttributes.addFlashAttribute("actionInfo","result.update.fail");
        }
        else{
            log.info("ERROR");
            redirectAttributes.addFlashAttribute("actionInfo","result.fail.contact");
        }
        return  "redirect:/workspace/cat_goods_ctr";
    }

    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    public @ResponseBody String delete(@RequestParam("id")String id){
        try {
            Long idL = Long.parseLong(id);
            ResponseObject response = catGoodsService.delete(idL,tokenInfo);
            if(Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())){
                return "1|Xoá thành công";
            }else{
                return "0|Xoá không thành công";
            }
        } catch (NumberFormatException e) {
            return "0|Xoá không thành công lỗi convert long";
        }
    }



}
