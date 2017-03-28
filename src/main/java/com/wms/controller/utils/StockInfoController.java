package com.wms.controller.utils;

import com.google.common.collect.Lists;
import com.wms.constants.Constants;
import com.wms.controller.stock_managerment.ExportStockController;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.utils.BundleUtils;
import com.wms.utils.DataUtil;
import com.wms.utils.DateTimeUtils;
import com.wms.utils.FunctionUtils;
import net.sf.jxls.transformer.Configuration;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by duyot on 3/21/2017.
 */
@Controller
@RequestMapping("/workspace/utils/stockInfo")
public class StockInfoController {

    @Autowired
    BaseService catStockService;
    @Autowired
    BaseService catGoodsGroupService;
    @Autowired
    BaseService catGoodsService;
    @Autowired
    BaseService mjrStockGoodsTotalService;

    Logger log = LoggerFactory.getLogger(ExportStockController.class);
    //
    private CatCustomerDTO selectedCustomer;
    //
    private List<CatStockDTO> lstStock;
    private List<CatGoodsDTO> lstGoods;
    private AuthTokenInfo tokenInfo;
    //
    private Map<String,CatGoodsDTO> mapGoodsCodeGoods = new HashMap();
    private Map<String,CatGoodsDTO> mapGoodsIdGoods   = new HashMap();
    private Map<String,CatStockDTO> mapStockIdStock   = new HashMap();
    //
    private List<MjrStockGoodsTotalDTO> lstGoodsTotal;
    //
    @ModelAttribute("selectedCustomer")
    public void setSelectedCustomer(HttpServletRequest request){
        this.selectedCustomer =  (CatCustomerDTO) request.getSession().getAttribute("selectedCustomer");
    }

    @ModelAttribute("lstStock")
    public List<CatStockDTO> getListStock(HttpServletRequest request){
        if(selectedCustomer == null){
            this.selectedCustomer =  (CatCustomerDTO) request.getSession().getAttribute("selectedCustomer");
        }
        this.tokenInfo =  (AuthTokenInfo) request.getSession().getAttribute("tokenInfo");
        //
        lstStock = FunctionUtils.getListStock(catStockService,selectedCustomer,tokenInfo);
        buildMapStock();
        //
       return lstStock;
    }

    @ModelAttribute("lstGoods")
    public List<CatGoodsDTO>  getListGoods(HttpServletRequest request){
        if(selectedCustomer == null){
            this.selectedCustomer =  (CatCustomerDTO) request.getSession().getAttribute("selectedCustomer");
        }
        if(tokenInfo == null){
            this.tokenInfo =  (AuthTokenInfo) request.getSession().getAttribute("tokenInfo");
        }
        lstGoods = FunctionUtils.getListGoods(catGoodsService,selectedCustomer,tokenInfo);
        buildMapGoods();
        return lstGoods;
    }

    @RequestMapping()
    public String home(Model model){
        //clear previous data
        lstGoodsTotal = Lists.newArrayList();
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

    private List<MjrStockGoodsTotalDTO> setNameValueInfo(List<MjrStockGoodsTotalDTO> lstTotal){
        if(!DataUtil.isListNullOrEmpty(lstTotal)){
            for(MjrStockGoodsTotalDTO i: lstTotal){
                i.setAmountValue(FunctionUtils.formatNumber(i.getAmount()));
                i.setStockName(mapStockIdStock.get(i.getStockId()).getName());
                i.setGoodsStateName(i.getGoodsState().equalsIgnoreCase("1")?"Bình thường":"Hỏng");
            }
        }else{
            return Lists.newArrayList();
        }
        return lstTotal;
    }



    //=======================================================================================================
    /*
        export error when importing
     */
    public  String exportTotal(List<MjrStockGoodsTotalDTO> lstGoodsTotal,String prefixFileName){
        String templatePath = BundleUtils.getkey("template_url") + Constants.FILE_RESOURCE.GOODS_TOTAL_TEMPLATE;

        File file = new File(templatePath);
        String templateAbsolutePath = file.getAbsolutePath();

        Map<String, Object> beans = new HashMap<String, Object>();
        beans.put("items", lstGoodsTotal);
        beans.put("date", DateTimeUtils.convertDateTimeToString(new Date()));

        Configuration config = new Configuration();
        XLSTransformer transformer = new XLSTransformer(config);
        String fullFileName = prefixFileName +"_"+ DateTimeUtils.getSysDateTimeForFileName() + ".xlsx";
        String reportFullPath = BundleUtils.getkey("temp_url") + fullFileName;
        try {
            transformer.transformXLS(templateAbsolutePath, beans, reportFullPath);
            log.info("Finish export report file in "+ reportFullPath);
        } catch (InvalidFormatException e) {
            e.printStackTrace();
            log.error(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.toString());
        }
        return reportFullPath;
    }

    private void buildMapGoods(){
        //
        mapGoodsCodeGoods.clear();
        mapGoodsIdGoods.clear();
        //
        if(!DataUtil.isListNullOrEmpty(lstGoods)){
            for(CatGoodsDTO i: lstGoods){
                mapGoodsCodeGoods.put(i.getCode(),i);
                mapGoodsIdGoods.put(i.getId(),i);
            }
        }
    }

    private void buildMapStock(){
        //
        mapStockIdStock.clear();
        //
        if(!DataUtil.isListNullOrEmpty(lstStock)){
            for(CatStockDTO i: lstStock){
                mapStockIdStock.put(i.getId(),i);
            }
        }
    }
}
