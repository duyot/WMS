package com.wms.controller.category;

import com.google.common.collect.Lists;
import com.wms.base.BaseCommonController;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.utils.DataUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
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
 * Created by duyot on 12/6/2016.
 */
@Controller
@RequestMapping("/workspace/cat_stock_ctr")
@Scope("session")
public class CatStockController extends BaseCommonController{
    Logger log = LoggerFactory.getLogger(CatStockController.class);

    @Autowired
    BaseService catStockService;

    @RequestMapping()
    public String home(Model model){
        model.addAttribute("menuName","menu.catstock");
        return "category/cat_stock";
    }

    @RequestMapping(value = "/findByCondition",method = RequestMethod.GET)
    public  @ResponseBody List<CatStockDTO> findByCondition(@RequestParam("customerId")String custId, @RequestParam("status")String status){
        List<Condition> lstCon = Lists.newArrayList();

        if(!DataUtil.isStringNullOrEmpty(custId) && !custId.equals(Constants.STATS_ALL)){
            lstCon.add(new Condition("custId",Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL,custId));
        }

        if(!DataUtil.isStringNullOrEmpty(status) && !status.equals(Constants.STATS_ALL)){
            lstCon.add(new Condition("status", Constants.SQL_OPERATOR.EQUAL,status));
        }

        lstCon.add(new Condition("id",Constants.SQL_OPERATOR.ORDER,"desc"));

        List<CatStockDTO> lstCatStock = catStockService.findByCondition(lstCon,tokenInfo);

        for(CatStockDTO i: lstCatStock){
            i.setName(StringEscapeUtils.escapeHtml(i.getName()));
            i.setCode(StringEscapeUtils.escapeHtml(i.getCode()));
            i.setAddress(StringEscapeUtils.escapeHtml(i.getAddress()));
            i.setCustName(selectedCustomer.getName());
            i.setStatusName(mapAppStatus.get(i.getStatus()));
        }

        return lstCatStock;
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public @ResponseBody String add(CatStockDTO catStockDTO, HttpServletRequest request){
        catStockDTO.setStatus("1");
        catStockDTO.setCustId(this.selectedCustomer.getId());
        ResponseObject response = catStockService.add(catStockDTO,tokenInfo);
        if(Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())){
            log.info("Add: "+ catStockDTO.toString()+" SUCCESS");
            //
            request.getSession().setAttribute("isStockModifiedImportStock",true);
            request.getSession().setAttribute("isStockModifiedExportStock",true);
            return "1|Thêm mới thành công";
        }else if(Responses.ERROR_CONSTRAINT.getName().equalsIgnoreCase(response.getStatusCode()))
        {
            log.info("Add: "+ catStockDTO.toString()+" ERROR");
            return "0|Thông tin đã có trên hệ thống";
        }else{
            log.info("Add: "+ catStockDTO.toString()+" ERROR");
            return "0|Lỗi hệ thống";
        }
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public @ResponseBody  String update(CatStockDTO catStockDTO, HttpServletRequest request){
        log.info("Update cat_stock info: "+ catStockDTO.toString());
		catStockDTO.setCustId(this.selectedCustomer.getId());
        if("on".equalsIgnoreCase(catStockDTO.getStatus())){
            catStockDTO.setStatus("1");
        }else{
            catStockDTO.setStatus("0");
        }
        ResponseObject response = catStockService.update(catStockDTO,tokenInfo);
        if(Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())){
            log.info("SUCCESS");
            //
            request.getSession().setAttribute("isStockModifiedImportStock",true);
            request.getSession().setAttribute("isStockModifiedExportStock",true);
            return "1|Cập nhật thành công";
        }else if(Responses.ERROR_CONSTRAINT.getName().equalsIgnoreCase(response.getStatusCode())){
            log.info("ERROR");
            return "0|Thông tin đã có trên hệ thống";
        }
        else{
            log.info("ERROR");
            return "0|Lỗi hệ thống";
        }
    }

    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    public @ResponseBody String delete(@RequestParam("id")String id,HttpServletRequest request){
        try {
            Long idL = Long.parseLong(id);
            ResponseObject response = catStockService.delete(idL,tokenInfo);
            if(Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())){
                //
                request.getSession().setAttribute("isStockModifiedImportStock",true);
                request.getSession().setAttribute("isStockModifiedExportStock",true);
                return "1|Xoá thành công";
            }else{
                return "0|Xoá không thành công";
            }
        } catch (NumberFormatException e) {
            return "0|Xoá không thành công lỗi convert long";
        }
    }



}
