package com.wms.controller.sale_managerment;

import com.wms.base.BaseCommonController;
import com.wms.config.WMSConfigManagerment;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.StockManagementService;
import com.wms.services.interfaces.StockService;
import com.wms.utils.DataUtil;
import com.wms.utils.FunctionUtils;
import com.wms.utils.JSONUtils;
import com.wms.utils.ResourceBundleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/workspace/sale_ctr")
@Scope("session")
public class SaleController extends BaseCommonController {

    private Logger log = LoggerFactory.getLogger(SaleController.class);
    @Autowired
    public StockService stockService;
    @Autowired
    public BaseService catGoodsService;
    @Autowired
    StockManagementService stockManagementService;
    @Autowired
    public BaseService catPartnerService;
    @RequestMapping()
    public String home(Model model){
        model.addAttribute("menuName","menu.catuser");
        model.addAttribute("controller","/workspace/sale_ctr/");
        model.addAttribute("lstStock", getLstStock());
         LinkedHashMap<String, String> mapUnitType = FunctionUtils.buildMapAppParams(FunctionUtils.getAppParamByType(Constants.APP_PARAMS.UNIT_TYPE,lstAppParams));
        List<CatGoodsDTO> lstCatGoods = FunctionUtils.getListGoods(catGoodsService,selectedCustomer);
        for (CatGoodsDTO item : lstCatGoods){
            item.setUnitTypeName(mapUnitType.get(item.getUnitType()));
        }
        model.addAttribute("lstGoods", lstCatGoods);
        return "sale_managerment/sale";
    }

    public List<CatStockDTO> getLstStock(){
        List<CatStockDTO> lstStock = new ArrayList<>();
        lstStock = stockService.getStockByUser(Long.parseLong(currentUser.getId()));
        return  lstStock;
    }

    @RequestMapping(value = "/exportStock", method = RequestMethod.POST)
    @ResponseBody
    public ResponseObject exportStock(@RequestBody StockManagementDTO stockManagementDTO) {
        long startTime = System.currentTimeMillis();
        log.info("------------------------------------------------------------");
        log.info(currentUser.getCode() +" export: " + stockManagementDTO.getLstGoods().size() + " items.");
        StockTransDTO stockTrans = initStockTrans(stockManagementDTO);
        System.out.println("Export request: "+ JSONUtils.object2JSONString(stockTrans));
        ResponseObject response = stockManagementService.exportStock(stockTrans);
        if (response.getStatusCode().equalsIgnoreCase("fail")){
            response.setStatusName(ResourceBundleUtils.getkey(response.getStatusName()) != null ?  ResourceBundleUtils.getkey(response.getStatusName()):response.getStatusName());
        }
        log.info("Result "+ response.getStatusCode() +" in "+ (System.currentTimeMillis() - startTime) + "ms");
        return response;
    }

    private StockTransDTO initStockTrans(StockManagementDTO stockManagementDTO){
        StockTransDTO stockTrans = new StockTransDTO();
        //
        MjrStockTransDTO mjrStockTransDTO = initMjrStockTrans(stockManagementDTO.getMjrStockTransDTO());
        //
        stockTrans.setMjrStockTransDTO(mjrStockTransDTO);
        //
        List<MjrStockTransDetailDTO> lstStockTransDetails = initListTransDetail(stockManagementDTO.getLstGoods());
        stockTrans.setLstMjrStockTransDetail(lstStockTransDetails);
        //
        return stockTrans;
    }
    private MjrStockTransDTO initMjrStockTrans(MjrStockTransDTO mjrStockTransDTO){
        mjrStockTransDTO.setCustId(selectedCustomer.getId());
        mjrStockTransDTO.setType(Constants.IMPORT_TYPE.EXPORT);
        mjrStockTransDTO.setStatus(Constants.STATUS.ACTIVE);
        mjrStockTransDTO.setCreatedUser(currentUser.getCode());
        //Nguoi nhan khi xuat
        if (mjrStockTransDTO.getReceiveName() != null && !mjrStockTransDTO.getReceiveName().trim().equals("")){
            String [] splitPartner = mjrStockTransDTO.getReceiveName().split("\\|");
            if (splitPartner.length > 0 ){
                String partnerCode = splitPartner[0];
                CatPartnerDTO catPartnerDTO = FunctionUtils.getPartner(catPartnerService,selectedCustomer.getId(), partnerCode, null );
                if (catPartnerDTO != null){
                    String partnerName = catPartnerDTO.getName()==null? "": catPartnerDTO.getName();
                    String partnerTelNumber = catPartnerDTO.getTelNumber()==null? "": catPartnerDTO.getTelNumber();
                    mjrStockTransDTO.setReceiveId(catPartnerDTO.getId());
                    mjrStockTransDTO.setReceiveName(partnerName+"|" + partnerTelNumber);
                }
            }
        }
        //Xuat hang cua doi tac
        if (mjrStockTransDTO.getPartnerId() != null){
            CatPartnerDTO catPartnerDTO = FunctionUtils.getPartner(catPartnerService,selectedCustomer.getId(), null, mjrStockTransDTO.getPartnerId() );
            if (catPartnerDTO != null) {
                String partnerName = catPartnerDTO.getName() == null ? "" : catPartnerDTO.getName();
                String partnerTelNumber = catPartnerDTO.getTelNumber() == null ? "" : catPartnerDTO.getTelNumber();
                mjrStockTransDTO.setPartnerId(catPartnerDTO.getId());
                mjrStockTransDTO.setPartnerName(partnerName + "|" + partnerTelNumber);
            }
        }
        //
        return mjrStockTransDTO;
    }
    private List<MjrStockTransDetailDTO> initListTransDetail(List<MjrStockTransDetailDTO> lstGoods){
        CatGoodsDTO goodsItem;
        for(MjrStockTransDetailDTO i: lstGoods){
//            goodsItem = mapGoodsCodeGoods.get(i.getGoodsCode());
//            if(goodsItem != null){
//                i.setGoodsId(goodsItem.getId());
//                i.setIsSerial(goodsItem.getIsSerial());
//            }
            //
            i.setAmountValue(null);
            i.setInputPriceValue(null);
            i.setOutputPriceValue(null);
            i.setGoodsStateValue(null);
            i.setErrorInfo(null);
        }
        return lstGoods;
    }
}
