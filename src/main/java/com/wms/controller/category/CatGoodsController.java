package com.wms.controller.category;

import com.google.common.collect.Lists;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import com.wms.base.BaseCommonController;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.utils.DataUtil;
import com.wms.utils.FunctionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by duyot on 12/9/2016.
 */
@Controller
@RequestMapping("/workspace/cat_goods_ctr")
public class CatGoodsController extends BaseCommonController{
    Logger log = LoggerFactory.getLogger(CatGoodsController.class);

    public Map<String, String> mapGoodsGroup;
    public Map<String, String> mapUnitType;

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
        return mapUnitType;
    }

    @RequestMapping()
    public String home(Model model){
        model.addAttribute("menuName","menu.catgoods");
        return "category/cat_goods";
    }
    @RequestMapping(value = "/findByCondition",method = RequestMethod.GET)
    public  @ResponseBody List<CatGoodsDTO> findByCondition(@RequestParam("customerId")String custId, @RequestParam("status")String status){
        List<Condition> lstCon = Lists.newArrayList();

        if(!DataUtil.isStringNullOrEmpty(custId) && !custId.equals(Constants.STATS_ALL)){
            lstCon.add(new Condition("custId",Constants.SQL_PRO_TYPE.LONG,Constants.SQL_OPERATOR.EQUAL,custId));
        }
        if(!DataUtil.isStringNullOrEmpty(status) && !status.equals(Constants.STATS_ALL)){
            lstCon.add(new Condition("status", Constants.SQL_OPERATOR.EQUAL,status));
        }
        lstCon.add(new Condition("id",Constants.SQL_PRO_TYPE.LONG,Constants.SQL_OPERATOR.ORDER,"desc"));

        List<CatGoodsDTO> lstCatGoods = catGoodsService.findByCondition(lstCon,tokenInfo);

        String serialTypeName;
        String isSerialName = Constants.SERIAL_TYPE.IS_SERIAL_NAME;
        String isSerial = Constants.SERIAL_TYPE.IS_SERIAL;
        String noSerialName = Constants.SERIAL_TYPE.NO_SERIAL_NAME;


        for(CatGoodsDTO i: lstCatGoods){
            i.setCustName(selectedCustomer.getName());
            i.setGoodsGroupName(mapGoodsGroup.get(i.getGoodsGroupId()));

            serialTypeName =  isSerial.equals(i.getIsSerial()) ? isSerialName: noSerialName;
            i.setSerialTypeName(serialTypeName);

            i.setStatusName(mapAppStatus.get(i.getStatus()));

            i.setUnitTypeName(mapUnitType.get(i.getUnitType()));

        }

        return lstCatGoods;
    }
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public String add(CatGoodsDTO catGoods, RedirectAttributes redirectAttributes){
        catGoods.setStatus("1");
        catGoods.setCustId(this.selectedCustomer.getId());
        ResponseObject response = catGoodsService.add(catGoods,tokenInfo);
        if(Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())){
            redirectAttributes.addFlashAttribute("actionInfo","result.add.success");
            redirectAttributes.addFlashAttribute("successStyle",Constants.SUCCES_COLOR);
            log.info("Add: "+ catGoods.toString()+" SUCCESS");
        }else{
            log.info("Add: "+ catGoods.toString()+" ERROR");
            redirectAttributes.addFlashAttribute("actionInfo","result.add.fail");
        }

        return "redirect:/workspace/cat_goods_ctr";
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public String update(CatGoodsDTO catGoods, RedirectAttributes redirectAttributes){
        catGoods.setCustId(this.selectedCustomer.getId());
        log.info("Update cat_goods info: "+ catGoods.toString());
        if("on".equalsIgnoreCase(catGoods.getStatus())){
            catGoods.setStatus("1");
        }else{
            catGoods.setStatus("0");
        }
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
            redirectAttributes.addFlashAttribute("actionInfo","Lỗi hệ thống, liên hệ quản trị để được hỗ trợ!");
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
