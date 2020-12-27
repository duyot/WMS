package com.wms.base;

import com.google.common.collect.Lists;
import com.wms.config.ProfileConfigInterface;
import com.wms.constants.Constants;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.CatUserService;
import com.wms.services.interfaces.PartnerService;
import com.wms.services.interfaces.StockService;
import com.wms.utils.DataUtil;
import com.wms.utils.FunctionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
    @Autowired
    public StockService stockService;
    @Autowired
    public PartnerService partnerService;
    @Autowired
    BaseService catStockCellService;
    @Autowired
    private HttpServletRequest requestCtx;
    @Autowired
    public BaseService catReasonService;
    @Autowired
    public CatUserService catUserService;

    //STOCK
    public List<CatStockDTO> lstStock;
    public Map<String, CatStockDTO> mapStockIdStock;
    //CELLS
    public Map<String, List<CatStockCellDTO>> mapStockIdCells;
    //GOODS
    public Map<String, CatGoodsDTO> mapGoodsCodeGoods;
    public Map<String, CatGoodsDTO> mapGoodsIdGoods;
    public Map<String, CatReasonDTO> mapReasonIdReason;
    public Map<String, CatReasonDTO> mapReasonIdReasonImport;
    public Map<String, CatReasonDTO> mapReasonIdReasonExport;
    public List<CatGoodsDTO> lstGoods;
    public List<CatReasonDTO> lstReason;
    public List<CatReasonDTO> lstReasonImport;
    public List<CatReasonDTO> lstReasonExport;

    //PARTNER
    public List<CatPartnerDTO> lstPartner;
    public Map<String, CatPartnerDTO> mapPartnerIdPartner;
    public Map<String, CatPartnerDTO> mapPartnerCodePartner;
    public String lstPartnerIds;
    //APP_PARAMS
    public List<AppParamsDTO> lstAppParams;
    public List<AppParamsDTO> lstAppGoodsState;
    public Map<String, String> mapAppGoodsState;
    public Map<String, String> mapAppStatus;
    public Map<String, String> mapAppParamsUnitName;
    //
    public CatCustomerDTO selectedCustomer;
    public CatUserDTO currentUser;
    //
    public boolean isDataLoaded = false;

    //User
    public List<CatUserDTO> lstUsers;
    public Map<String, CatUserDTO> mapUserIdUser;

    //------------------------------------------------------------------------------------------------------------------
    @PostConstruct
    public void initBaseBean() {
        System.out.println("Running in initBaseBean...");
        this.currentUser = (CatUserDTO) requestCtx.getSession().getAttribute("user");
        this.selectedCustomer = (CatCustomerDTO) requestCtx.getSession().getAttribute("selectedCustomer");
        initAppParams();
        initStocks();
        initCells();
        initGoods();
        initPartner();
        initReason();
        initUser();
        isDataLoaded = true;
    }

    //------------------------------------------------------------------------------------------------------------------
    public void initStocks() {
        if (currentUser != null && currentUser.getStockPermission().equals("0")) {
            this.lstStock = FunctionUtils.getListStock(catStockService, selectedCustomer);
        } else {
            this.lstStock = FunctionUtils.getListStock(stockService, currentUser);
        }
        buildMapStock();
    }

    public void initCells() {
        mapStockIdCells = new HashMap<>();
        if (!DataUtil.isListNullOrEmpty(lstStock)) {
            List<Condition> conditions = Lists.newArrayList();
            List<CatStockCellDTO> cells;
            for (CatStockDTO i : lstStock) {
                conditions.clear();
                conditions.add(new Condition("stockId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, i.getId()));
                cells = catStockCellService.findByCondition(conditions);
                this.mapStockIdCells.put(i.getId(), cells);
            }
        }
    }

    public void initGoods() {
        this.lstGoods = FunctionUtils.getListGoods(catGoodsService, selectedCustomer);
        buildMapGoods();
    }

    public void initReason() {
        this.lstReason = FunctionUtils.getListReason(catReasonService, selectedCustomer);
        buildMapReason();
    }

    public void initPartner() {
        if (currentUser != null && currentUser.getPartnerPermission().equals("0")) {
            this.lstPartner = FunctionUtils.getListPartner(catPartnerService, selectedCustomer);
        } else {
            this.lstPartner = FunctionUtils.getListPartner(partnerService, currentUser);
        }
        buildMapPartner();
    }

    public void initUser() {
        this.lstUsers = FunctionUtils.getCustomerUsers(catUserService, selectedCustomer);
        buildMapUser();
    }

    private void initAppParams() {
        if (lstAppParams == null) {
            this.lstAppParams = FunctionUtils.getAppParams(appParamsService);
        }

        if (lstAppGoodsState == null) {
            this.lstAppGoodsState = FunctionUtils.getAppParamByType(Constants.APP_PARAMS.GOODS_STATE, lstAppParams);
        }

        if (mapAppGoodsState == null) {
            this.mapAppGoodsState = FunctionUtils.buildMapAppParams(lstAppGoodsState);
        }

        if (mapAppParamsUnitName == null) {
            this.mapAppParamsUnitName = FunctionUtils.buildMapAppParams(Constants.APP_PARAMS.UNIT_TYPE, lstAppParams);
        }

        if (mapAppStatus == null) {
            this.mapAppStatus = FunctionUtils.buildMapAppParams(FunctionUtils.getAppParamByType(Constants.APP_PARAMS.STATUS, lstAppParams));
        }
    }

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

    public void buildMapReason() {
        mapReasonIdReason = new HashMap<>();
        mapReasonIdReasonImport = new HashMap<>();
        mapReasonIdReasonExport = new HashMap<>();
        lstReasonImport = new ArrayList<CatReasonDTO>();
        lstReasonExport = new ArrayList<CatReasonDTO>();

        if (!DataUtil.isListNullOrEmpty(lstReason)) {
            for (CatReasonDTO i : lstReason) {
                mapReasonIdReason.put(i.getId(), i);
                if("1".equals(i.getType())){
                    mapReasonIdReasonImport.put(i.getId(), i);
                    lstReasonImport.add(i);
                }else{
                    mapReasonIdReasonExport.put(i.getId(), i);
                    lstReasonExport.add(i);
                }
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
        lstPartnerIds = "";
        if (!DataUtil.isListNullOrEmpty(lstPartner)) {
            mapPartnerIdPartner = new HashMap<>();
            mapPartnerCodePartner = new HashMap<>();
            int size = lstPartner.size();
            for (int i = 0; i < size; i++) {
                CatPartnerDTO catPartnerDTO = lstPartner.get(i);
                mapPartnerIdPartner.put(catPartnerDTO.getId(), catPartnerDTO);
                mapPartnerCodePartner.put(catPartnerDTO.getCode(), catPartnerDTO);
                if (i == size - 1) {
                    lstPartnerIds = lstPartnerIds + catPartnerDTO.getId();
                } else {
                    lstPartnerIds = lstPartnerIds + catPartnerDTO.getId() + ",";
                }
            }
        }
    }
    public void buildMapUser() {
        if (!DataUtil.isListNullOrEmpty(lstUsers)) {
            mapUserIdUser = new HashMap<>();
            int size = lstUsers.size();
            for (int i = 0; i < size; i++) {
                CatUserDTO catUserDTO = lstUsers.get(i);
                mapUserIdUser.put(catUserDTO.getId(), catUserDTO);
            }
        }
    }
    //==================================================================================================================
}
