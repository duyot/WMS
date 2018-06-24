package com.wms.base;

import com.google.common.collect.Lists;
import com.wms.constants.Constants;
import com.wms.controller.category.CatGoodsController;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.StockService;
import com.wms.utils.DataUtil;
import com.wms.utils.FunctionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by duyot on 3/31/2017.
 */
@Component
@Scope("session")
public class BaseController {
    @Autowired
    public BaseService catStockService;
    @Autowired
    public BaseService catGoodsService;
    @Autowired
    public BaseService appParamsService;
    //
    @Autowired
    public StockService stockService;
    //STOCK
    public List<CatStockDTO> lstStock;
    public Map<String,CatStockDTO> mapStockIdStock;
    //GOODS
    public Map<String,CatGoodsDTO> mapGoodsCodeGoods;
    public Map<String,CatGoodsDTO> mapGoodsIdGoods;
    public List<CatGoodsDTO> lstGoods;

    //APP_PARAMS
    public List<AppParamsDTO> lstAppParams;
    public List<AppParamsDTO> lstAppGoodsState;
    public Map<String,String> mapAppGoodsState;
    //
    public Map<String,String> mapAppStatus;
    //
    public AuthTokenInfo tokenInfo;
    public CatCustomerDTO selectedCustomer;
    public CatUserDTO currentUser;


    //
    @ModelAttribute("currentUser")
    public void setCurrentUser(HttpServletRequest request){
        if (currentUser == null) {
            this.currentUser =  (CatUserDTO) request.getSession().getAttribute("user");
        }
    }

    @ModelAttribute("lstStock")
    public List<CatStockDTO> getListStock(HttpServletRequest request){
        if(selectedCustomer == null){
            this.selectedCustomer =  (CatCustomerDTO) request.getSession().getAttribute("selectedCustomer");
        }
        if(tokenInfo == null){
            this.tokenInfo =  (AuthTokenInfo) request.getSession().getAttribute("tokenInfo");
        }
        if (currentUser == null) {
            this.currentUser =  (CatUserDTO) request.getSession().getAttribute("user");
        }
        //
        if(lstStock == null){
            lstStock = FunctionUtils.getListStock(stockService,currentUser,tokenInfo);
            buildMapStock();
            request.getSession().setAttribute("isStockModified",false);
        }
        //
        return lstStock;
    }

    @ModelAttribute("lstAppParams")
    public List<AppParamsDTO>  getListAppParams(HttpServletRequest request){
        if(tokenInfo == null){
            this.tokenInfo =  (AuthTokenInfo) request.getSession().getAttribute("tokenInfo");
        }
        if(lstAppParams == null){
            lstAppParams = FunctionUtils.getAppParams(appParamsService,tokenInfo);
        }

        if(lstAppGoodsState == null){
            lstAppGoodsState = FunctionUtils.getAppParamByType(Constants.APP_PARAMS.GOODS_STATE,lstAppParams);
        }

        if(mapAppGoodsState == null){
            mapAppGoodsState = FunctionUtils.buildMapAppParams(lstAppGoodsState);
        }

        if(mapAppStatus == null){
            mapAppStatus = FunctionUtils.buildMapAppParams(FunctionUtils.getAppParamByType(Constants.APP_PARAMS.STATUS,lstAppParams));
        }

        return lstAppParams;
    }

    @ModelAttribute("lstGoods")
    public List<CatGoodsDTO>  getListGoods(HttpServletRequest request){
        if(selectedCustomer == null){
            this.selectedCustomer =  (CatCustomerDTO) request.getSession().getAttribute("selectedCustomer");
        }
        if(tokenInfo == null){
            this.tokenInfo =  (AuthTokenInfo) request.getSession().getAttribute("tokenInfo");
        }
        if(lstGoods == null){
            lstGoods = FunctionUtils.getListGoods(catGoodsService,selectedCustomer,tokenInfo);
            buildMapGoods();
            request.getSession().setAttribute("isGoodsModified",false);
        }

        return lstGoods;
    }


    //==================================================================================================================
    public void buildMapGoods(){
        mapGoodsCodeGoods = new HashMap<>();
        mapGoodsIdGoods = new HashMap<>();
        if(!DataUtil.isListNullOrEmpty(lstGoods)){
            for(CatGoodsDTO i: lstGoods){
                mapGoodsCodeGoods.put(i.getCode(),i);
                mapGoodsIdGoods.put(i.getId(),i);
            }
        }
    }

    public void buildMapStock(){
        mapStockIdStock = new HashMap<>();
        if(!DataUtil.isListNullOrEmpty(lstStock)){
            for(CatStockDTO i: lstStock){
                mapStockIdStock.put(i.getId(),i);
            }
        }
    }

}
