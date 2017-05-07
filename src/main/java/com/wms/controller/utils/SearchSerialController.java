package com.wms.controller.utils;

import com.google.common.collect.Lists;
import com.wms.base.BaseController;
import com.wms.constants.Constants;
import com.wms.dto.Condition;
import com.wms.dto.MjrStockTransDetailDTO;
import com.wms.services.interfaces.BaseService;
import com.wms.utils.BundleUtils;
import com.wms.utils.DataUtil;
import com.wms.utils.DateTimeUtils;
import com.wms.utils.FunctionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by duyot on 3/31/2017.
 */
@Controller
@RequestMapping("/workspace/utils/searchSerial")
@Scope("session")
public class SearchSerialController extends BaseController{
    @Autowired
    BaseService mjrStockGoodsSerialService;
    //
    private List<MjrStockTransDetailDTO> lstGoodsDetails;

    @RequestMapping()
    public String home(Model model){
        lstGoodsDetails   = Lists.newArrayList();
        model.addAttribute("menuName","menu.utils.searchserial");
        return "utils/search_serial";
    }

    //==================================================================================================================
    @RequestMapping(value = "/findSerial",method = RequestMethod.GET)
    public @ResponseBody
    List<MjrStockTransDetailDTO> findSerial(@RequestParam("stockId")String stockId, @RequestParam("goodsId")String goodsId,
                                            @RequestParam("serial")String serial
    ){
        String arrSearchSerial = preprocessSearchSerial(serial);

        List<Condition> lstCon = Lists.newArrayList();

        lstCon.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL,selectedCustomer.getId()));
        if(!DataUtil.isStringNullOrEmpty(stockId) && !stockId.equals(Constants.STATS_ALL)){
            lstCon.add(new Condition("stockId",Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL,stockId));
        }
        if(!DataUtil.isStringNullOrEmpty(goodsId) && !goodsId.equals(Constants.STATS_ALL)){
            lstCon.add(new Condition("goodsId",Constants.SQL_PRO_TYPE.LONG,Constants.SQL_OPERATOR.EQUAL,goodsId));
        }
        lstCon.add(new Condition("serial",Constants.SQL_OPERATOR.IN,arrSearchSerial));
        lstCon.add(new Condition("importDate",Constants.SQL_OPERATOR.ORDER,"desc"));
        //
        List<MjrStockTransDetailDTO> lstResult= FunctionUtils.convertGoodsSerialToDetail(mjrStockGoodsSerialService.findByCondition(lstCon,tokenInfo));
        if(DataUtil.isListNullOrEmpty(lstResult)){
            return Lists.newArrayList();
        }
        lstGoodsDetails  = FunctionUtils.setNameValueGoodsDetail(lstResult,mapGoodsIdGoods,mapStockIdStock,mapAppGoodsState);
        return lstGoodsDetails;
    }
    //==================================================================================================================
    @RequestMapping(value = "/getSerialFile",method = RequestMethod.GET)
    public void getSerialFile(HttpServletResponse response){

        if(DataUtil.isListNullOrEmpty(lstGoodsDetails)){
            lstGoodsDetails.add(new MjrStockTransDetailDTO("","","","","","","","","","","",""));
        }
        //
        String prefixFileName = "Thong_tin_tim_kiem_serial_";
        //
        String fileResource = exportSerialFile(lstGoodsDetails,prefixFileName);
        FunctionUtils.loadFileToClient(response,fileResource);
    }
    //==================================================================================================================
    private String preprocessSearchSerial(String serial){
        String [] arrSerial = serial.split(",");
        StringBuilder sbSerial = new StringBuilder();
        for(String i: arrSerial){
            sbSerial.append(",").append(i.trim());
        }

        return sbSerial.toString().replaceFirst(",","");
    }

    private  String exportSerialFile(List<MjrStockTransDetailDTO> lstSerial,String prefixFileName){
        String templatePath = BundleUtils.getKey("template_url") + Constants.FILE_RESOURCE.GOODS_DETAILS_SEARCH_SERIAL_TEMPLATE;

        File file = new File(templatePath);
        String templateAbsolutePath = file.getAbsolutePath();

        Map<String, Object> beans = new HashMap<>();
        beans.put("items", lstSerial);
        beans.put("date", DateTimeUtils.convertDateTimeToString(new Date()));

        String fullFileName = prefixFileName +"_"+ DateTimeUtils.getSysDateTimeForFileName() + ".xlsx";
        String reportFullPath = BundleUtils.getKey("temp_url") + fullFileName;
        //
        FunctionUtils.exportExcel(templateAbsolutePath,beans,reportFullPath);
        //
        return reportFullPath;
    }
}
