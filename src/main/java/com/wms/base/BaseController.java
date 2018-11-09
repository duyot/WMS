package com.wms.base;

import com.wms.config.ProfileConfigInterface;
import com.wms.constants.Constants;
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
    public BaseService catPartnerService;
    @Autowired
    public BaseService appParamsService;
    @Autowired
    public ProfileConfigInterface profileConfig;
    //
    @Autowired
    public StockService stockService;
    //STOCK
    public List<CatStockDTO> lstStock;
    public Map<String, CatStockDTO> mapStockIdStock;

    //PARTNER
    public List<CatPartnerDTO> lstPartner;
    public Map<String, CatPartnerDTO> mapPartnerIdPartner;
    //GOODS
    public Map<String, CatGoodsDTO> mapGoodsCodeGoods;
    public Map<String, CatGoodsDTO> mapGoodsIdGoods;
    public List<CatGoodsDTO> lstGoods;

    //APP_PARAMS
    public List<AppParamsDTO> lstAppParams;
    public List<AppParamsDTO> lstAppGoodsState;
    public Map<String, String> mapAppGoodsState;
    //
    public Map<String, String> mapAppStatus;

    public CatCustomerDTO selectedCustomer;
    public CatUserDTO currentUser;


    //
    @ModelAttribute("currentUser")
    public void setCurrentUser(HttpServletRequest request) {
        if (currentUser == null) {
            this.currentUser = (CatUserDTO) request.getSession().getAttribute("user");
        }
    }

    @ModelAttribute("lstStock")
    public List<CatStockDTO> getListStock(HttpServletRequest request) {
        if (selectedCustomer == null) {
            this.selectedCustomer = (CatCustomerDTO) request.getSession().getAttribute("selectedCustomer");
        }

        if (currentUser == null) {
            this.currentUser = (CatUserDTO) request.getSession().getAttribute("user");
        }
        //
        if (lstStock == null) {
            lstStock = FunctionUtils.getListStock(stockService, currentUser);
            buildMapStock();
            request.getSession().setAttribute("isStockModified", false);
        }
        //
        return lstStock;
    }

    @ModelAttribute("lstPartner")
    public List<CatPartnerDTO> getListPartner(HttpServletRequest request) {
        if (selectedCustomer == null) {
            this.selectedCustomer = (CatCustomerDTO) request.getSession().getAttribute("selectedCustomer");
        }
        if (currentUser == null) {
            this.currentUser = (CatUserDTO) request.getSession().getAttribute("user");
        }
        lstPartner = FunctionUtils.getListPartner(catPartnerService, selectedCustomer);
        buildMapPartner();

        return lstPartner;
    }

    @ModelAttribute("lstAppParams")
    public List<AppParamsDTO> getListAppParams(HttpServletRequest request) {

        if (lstAppParams == null) {
            lstAppParams = FunctionUtils.getAppParams(appParamsService);
        }

        if (lstAppGoodsState == null) {
            lstAppGoodsState = FunctionUtils.getAppParamByType(Constants.APP_PARAMS.GOODS_STATE, lstAppParams);
        }

        if (mapAppGoodsState == null) {
            mapAppGoodsState = FunctionUtils.buildMapAppParams(lstAppGoodsState);
        }

        if (mapAppStatus == null) {
            mapAppStatus = FunctionUtils.buildMapAppParams(FunctionUtils.getAppParamByType(Constants.APP_PARAMS.STATUS, lstAppParams));
        }

        return lstAppParams;
    }

    @ModelAttribute("lstGoods")
    public List<CatGoodsDTO> getListGoods(HttpServletRequest request) {
        if (selectedCustomer == null) {
            this.selectedCustomer = (CatCustomerDTO) request.getSession().getAttribute("selectedCustomer");
        }

        if (lstGoods == null) {
            lstGoods = FunctionUtils.getListGoods(catGoodsService, selectedCustomer);
            buildMapGoods();
            request.getSession().setAttribute("isGoodsModified", false);
        }

        return lstGoods;
    }


    //==================================================================================================================
    public void buildMapGoods() {
        mapGoodsCodeGoods = new HashMap<>();
        mapGoodsIdGoods = new HashMap<>();
        if (!DataUtil.isListNullOrEmpty(lstGoods)) {
            for (CatGoodsDTO i : lstGoods) {
                mapGoodsCodeGoods.put(i.getCode(), i);
                mapGoodsIdGoods.put(i.getId(), i);
            }
        }
    }

    public void buildMapStock() {
        mapStockIdStock = new HashMap<>();
        if (!DataUtil.isListNullOrEmpty(lstStock)) {
            for (CatStockDTO i : lstStock) {
                mapStockIdStock.put(i.getId(), i);
            }
        }
    }

    public void buildMapPartner() {
        mapPartnerIdPartner = new HashMap<>();
        if (!DataUtil.isListNullOrEmpty(lstPartner)) {
            for (CatPartnerDTO i : lstPartner) {
                mapPartnerIdPartner.put(i.getId(), i);
            }
        }
    }

}
