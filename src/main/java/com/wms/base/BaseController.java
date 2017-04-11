package com.wms.base;

import com.google.common.collect.Lists;
import com.wms.constants.Constants;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
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
    public List<CatStockDTO> lstStock;
    public List<CatGoodsDTO> lstGoods;
    public List<AppParamsDTO> lstAppParams;
    public AuthTokenInfo tokenInfo;
    public CatCustomerDTO selectedCustomer;
    public CatUserDTO currentUser;
    //
    public Map<String,CatGoodsDTO> mapGoodsCodeGoods = new HashMap();
    public Map<String,CatGoodsDTO> mapGoodsIdGoods   = new HashMap();
    public Map<String,CatStockDTO> mapStockIdStock   = new HashMap();
    public Map<String,String> mapAppGoodsState   = new HashMap();

    //
    @ModelAttribute("currentUser")
    public void setCurrentUser(HttpServletRequest request){
        this.currentUser =  (CatUserDTO) request.getSession().getAttribute("user");
    }

    @ModelAttribute("lstStock")
    public List<CatStockDTO> getListStock(HttpServletRequest request){
        if(selectedCustomer == null){
            this.selectedCustomer =  (CatCustomerDTO) request.getSession().getAttribute("selectedCustomer");
        }
        if(tokenInfo == null){
            this.tokenInfo =  (AuthTokenInfo) request.getSession().getAttribute("tokenInfo");
        }
        //
        if(lstStock == null){
            lstStock = FunctionUtils.getListStock(catStockService,selectedCustomer,tokenInfo);
            buildMapStock();
        }
        //
        return lstStock;
    }

    @ModelAttribute("lstAppParams")
    public List<AppParamsDTO>  getListAppParams(HttpServletRequest request){
        if(selectedCustomer == null){
            this.selectedCustomer =  (CatCustomerDTO) request.getSession().getAttribute("selectedCustomer");
        }
        if(tokenInfo == null){
            this.tokenInfo =  (AuthTokenInfo) request.getSession().getAttribute("tokenInfo");
        }
        if(lstAppParams == null){
            lstAppParams = FunctionUtils.getAppParams(appParamsService,tokenInfo);
            mapAppGoodsState = FunctionUtils.buildMapAppParams(FunctionUtils.getAppParamByType(Constants.APP_PARAMS.GOODS_STATE,lstAppParams));
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
        }
        return lstGoods;
    }
    //==================================================================================================================
    private void buildMapGoods(){
        if(!DataUtil.isListNullOrEmpty(lstGoods)){
            for(CatGoodsDTO i: lstGoods){
                mapGoodsCodeGoods.put(i.getCode(),i);
                mapGoodsIdGoods.put(i.getId(),i);
            }
        }
    }

    private void buildMapStock(){
        if(!DataUtil.isListNullOrEmpty(lstStock)){
            for(CatStockDTO i: lstStock){
                mapStockIdStock.put(i.getId(),i);
            }
        }
    }

}
