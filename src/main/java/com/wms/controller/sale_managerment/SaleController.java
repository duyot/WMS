package com.wms.controller.sale_managerment;

import com.google.common.collect.Lists;
import com.wms.base.BaseController;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.StockManagementService;
import com.wms.services.interfaces.StockService;
import com.wms.utils.*;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/workspace/sale_ctr")
@Scope("session")
public class SaleController extends BaseController {

    @Autowired
    public StockService stockService;
    @Autowired
    public BaseService catGoodsService;
    @Autowired
    StockManagementService stockManagementService;
    @Autowired
    public BaseService catPartnerService;

    private Logger log = LoggerFactory.getLogger(SaleController.class);

    //------------------------------------------------------------------------------------------------------------------
    @PostConstruct
    public void init() {
        if (!isDataLoaded) {
            initBaseBean();
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    @RequestMapping()
    public String home(Model model) {
        model.addAttribute("menuName", "menu.catuser");
        model.addAttribute("controller", "/workspace/sale_ctr/");
        model.addAttribute("lstStock", lstStock);
        LinkedHashMap<String, String> mapUnitType = FunctionUtils.buildMapAppParams(FunctionUtils.getAppParamByType(Constants.APP_PARAMS.UNIT_TYPE, lstAppParams));

        model.addAttribute("lstGoods", lstGoods);
        for (CatGoodsDTO item : lstGoods) {
            item.setUnitTypeName(mapUnitType.get(item.getUnitType()));
        }
        model.addAttribute("lstPartner", getPartnerName(lstPartner));
        return "sale_managerment/sale";
    }

    @RequestMapping(value = "/exportStock", method = RequestMethod.POST)
    @ResponseBody
    public ResponseObject exportStock(HttpServletResponse servletResponse, HttpServletRequest request, @RequestBody StockManagementDTO stockManagementDTO) {
        long startTime = System.currentTimeMillis();
        log.info("------------------------------------------------------------");
        log.info(currentUser.getCode() + " export: " + stockManagementDTO.getLstGoods().size() + " items.");
        StockTransDTO stockTrans = initStockTrans(stockManagementDTO);
        System.out.println("Export request: " + JSONUtils.object2JSONString(stockTrans));
        ResponseObject response = stockManagementService.exportStock(stockTrans);
        log.info("Result " + response.getStatusCode() + " in " + (System.currentTimeMillis() - startTime) + "ms");
        if (response.getStatusCode().equalsIgnoreCase("fail")) {
            response.setStatusName(ResourceBundleUtils.getkey(response.getStatusName()) != null ? ResourceBundleUtils.getkey(response.getStatusName()) : response.getStatusName());
        } else {
            // print invoice
            if (stockManagementDTO.getPrintInvoice().equalsIgnoreCase("0")) {
                String templatePath = profileConfig.getTemplateURL() + Constants.FILE_RESOURCE.INVOICE_80;
                String prefixFileName = "invoice_" + selectedCustomer.getCode() + "_" + DateTimeUtils.getTimeStamp() + ".pdf";
                String outPutFile = profileConfig.getTempURL() + prefixFileName;
                JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(stockManagementDTO.getLstGoods());

                Map<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("itemList", itemsJRBean);
                parameters.put("customerName", selectedCustomer.getName());
                parameters.put("phone", selectedCustomer.getTelNumber());
                parameters.put("address", selectedCustomer.getAddress());
                parameters.put("shopName", stockManagementDTO.getMjrStockTransDTO().getStockName());
                parameters.put("total", stockManagementDTO.getMjrStockTransDTO().getTransMoneyTotal());
                parameters.put("discount", stockManagementDTO.getMjrStockTransDTO().getTransMoneyDiscount());
                parameters.put("money", stockManagementDTO.getMjrStockTransDTO().getTransMoneyRequire());
                parameters.put("customerMoney", stockManagementDTO.getMjrStockTransDTO().getCustomerMoney());
                parameters.put("returnMoney", stockManagementDTO.getMjrStockTransDTO().getReturnMoney());
                parameters.put("staff", currentUser.getCode());


                try {
                    parameters.put("curentTime", DateTimeUtils.getSysDateTime());
                    JasperPrint jasperPrint = JasperFillManager.fillReport(templatePath, parameters, new JREmptyDataSource());
                    log.info("exportTransInfo: fillReport ");
                    JRPdfExporter export = new JRPdfExporter();
                    export.setExporterInput(new SimpleExporterInput(jasperPrint));
                    export.setExporterOutput(new SimpleOutputStreamExporterOutput(new File(outPutFile)));
                    String returnUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "/invoice/" + prefixFileName);
                    export.exportReport();
                    response.setFilePath(returnUrl);
                    log.info("Done");
                    System.out.println("Done!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }

    @RequestMapping(value = "/addCust", method = RequestMethod.POST)
    public @ResponseBody
    String addCust(HttpServletResponse servletResponse, HttpServletRequest request, CatPartnerDTO catPartnerDTO) {
        catPartnerDTO.setStatus("1");
        catPartnerDTO.setCustId(this.selectedCustomer.getId());
        catPartnerDTO.setCode(catPartnerDTO.getTelNumber());
        ResponseObject response = catPartnerService.add(catPartnerDTO);
        if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
            return ResourceBundleUtils.getkey(Constants.RESPONSE.INSERT_SUSSESS);
        } else {
            return ResourceBundleUtils.getkey(DataUtil.isNullOrEmpty(response.getKey()) ? Constants.RESPONSE.INSERT_ERROR : response.getKey());
        }
    }

    @RequestMapping(value = "/getPartner", method = RequestMethod.GET)
    public @ResponseBody
    List<String> getPartner(@RequestParam("custId") String custId) {
        List<CatPartnerDTO> lstPartner = FunctionUtils.getListPartner(catPartnerService, selectedCustomer);
        return getPartnerName(lstPartner);
    }

    private StockTransDTO initStockTrans(StockManagementDTO stockManagementDTO) {
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

    private MjrStockTransDTO initMjrStockTrans(MjrStockTransDTO mjrStockTransDTO) {
        mjrStockTransDTO.setCustId(selectedCustomer.getId());
        mjrStockTransDTO.setType(Constants.IMPORT_TYPE.EXPORT);
        mjrStockTransDTO.setStatus(Constants.STATUS.ACTIVE);
        mjrStockTransDTO.setCreatedUser(currentUser.getCode());
        //Nguoi nhan khi xuat
        if (mjrStockTransDTO.getReceiveName() != null && !mjrStockTransDTO.getReceiveName().trim().equals("")) {
            String[] splitPartner = mjrStockTransDTO.getReceiveName().split("\\|");
            if (splitPartner.length > 0) {
                String partnerCode = splitPartner[0];
                CatPartnerDTO catPartnerDTO = mapPartnerCodePartner.get(partnerCode);
                if (catPartnerDTO != null) {
                    String receiverName = "";
                    if (!DataUtil.isStringNullOrEmpty(catPartnerDTO.getName())) {
                        receiverName = receiverName + catPartnerDTO.getName();
                    }
                    if (!DataUtil.isStringNullOrEmpty(catPartnerDTO.getTelNumber())) {
                        receiverName = receiverName + "|" + catPartnerDTO.getTelNumber();
                    }
                    mjrStockTransDTO.setReceiveId(catPartnerDTO.getId());
                    mjrStockTransDTO.setReceiveName(receiverName);
                }
            }
        }
        //Xuat hang cua doi tac
        if (mjrStockTransDTO.getPartnerId() != null) {
            CatPartnerDTO catPartnerDTO = mapPartnerIdPartner.get(mjrStockTransDTO.getPartnerId());
            if (catPartnerDTO != null) {
                String receiverName = "";
                if (!DataUtil.isStringNullOrEmpty(catPartnerDTO.getName())) {
                    receiverName = receiverName + catPartnerDTO.getName();
                }
                if (!DataUtil.isStringNullOrEmpty(catPartnerDTO.getTelNumber())) {
                    receiverName = receiverName + "|" + catPartnerDTO.getTelNumber();
                }
                mjrStockTransDTO.setPartnerId(catPartnerDTO.getId());
                mjrStockTransDTO.setPartnerName(receiverName);
            }
        }
        //
        return mjrStockTransDTO;
    }

    private List<MjrStockTransDetailDTO> initListTransDetail(List<MjrStockTransDetailDTO> lstGoods) {
        CatGoodsDTO goodsItem;
        for (MjrStockTransDetailDTO i : lstGoods) {
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

    public List<String> getPartnerName(List<CatPartnerDTO> lstPartner) {
        List<String> lstPartneName = Lists.newArrayList();
        StringBuilder namePlus = new StringBuilder();
        for (CatPartnerDTO i : lstPartner) {
            namePlus.append(i.getName()).append("|").append(i.getTelNumber());
            lstPartneName.add(namePlus.toString());
            namePlus.setLength(0);
        }
        return lstPartneName;
    }
}
