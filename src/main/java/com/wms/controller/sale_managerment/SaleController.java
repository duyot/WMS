package com.wms.controller.sale_managerment;

import com.google.common.collect.Lists;
import com.wms.base.BaseCommonController;
import com.wms.config.WMSConfigManagerment;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.StockManagementService;
import com.wms.services.interfaces.StockService;
import com.wms.utils.*;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.export.SimpleDocxReportConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.NumberFormat;
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
        List<CatPartnerDTO> lstPartner = FunctionUtils.getListPartner(catPartnerService,selectedCustomer);
        model.addAttribute("lstPartner", getPartnerName(lstPartner));
        return "sale_managerment/sale";
    }

    public List<CatStockDTO> getLstStock(){
        List<CatStockDTO> lstStock = new ArrayList<>();
        lstStock = stockService.getStockByUser(Long.parseLong(currentUser.getId()));
        return  lstStock;
    }

    @RequestMapping(value = "/exportStock", method = RequestMethod.POST)
    @ResponseBody
    public ResponseObject exportStock(HttpServletResponse servletResponse, HttpServletRequest request ,@RequestBody StockManagementDTO stockManagementDTO) {
        long startTime = System.currentTimeMillis();
        log.info("------------------------------------------------------------");
        log.info(currentUser.getCode() +" export: " + stockManagementDTO.getLstGoods().size() + " items.");
        StockTransDTO stockTrans = initStockTrans(stockManagementDTO);
        System.out.println("Export request: "+ JSONUtils.object2JSONString(stockTrans));
        ResponseObject response = stockManagementService.exportStock(stockTrans);
        log.info("Result "+ response.getStatusCode() +" in "+ (System.currentTimeMillis() - startTime) + "ms");
        if (response.getStatusCode().equalsIgnoreCase("fail")){
            response.setStatusName(ResourceBundleUtils.getkey(response.getStatusName()) != null ?  ResourceBundleUtils.getkey(response.getStatusName()):response.getStatusName());
        }else {
    // print invoice
            if (stockManagementDTO.getPrintInvoice().equalsIgnoreCase("0")){
            String templatePath = profileConfig.getTemplateURL() + Constants.FILE_RESOURCE.INVOICE_80;
            String prefixFileName = "invoice_" +selectedCustomer.getCode()+"_"+ DateTimeUtils.getTimeStamp()+".pdf";
            String outPutFile  = profileConfig.getTempURL()+ prefixFileName;
            JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(stockManagementDTO.getLstGoods());

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("itemList", itemsJRBean);
            parameters.put("customerName", selectedCustomer.getName());
            parameters.put("phone", selectedCustomer.getTelNumber());
            parameters.put("address", selectedCustomer.getAddress());
            parameters.put("shopName",stockManagementDTO.getMjrStockTransDTO().getStockName());
            parameters.put("total", stockManagementDTO.getMjrStockTransDTO().getTransMoneyTotal());
            parameters.put("discount", stockManagementDTO.getMjrStockTransDTO().getTransMoneyDiscount());
            parameters.put("money", stockManagementDTO.getMjrStockTransDTO().getTransMoneyRequire());
            parameters.put("customerMoney", stockManagementDTO.getMjrStockTransDTO().getCustomerMoney());
            parameters.put("returnMoney",stockManagementDTO.getMjrStockTransDTO().getReturnMoney());
            parameters.put("staff",currentUser.getCode());


            try {
                parameters.put("curentTime",DateTimeUtils.getSysDateTime());
                JasperPrint jasperPrint = JasperFillManager.fillReport(templatePath, parameters, new JREmptyDataSource());
                log.info("exportTransInfo: fillReport " );
                JRPdfExporter export = new JRPdfExporter();
                export.setExporterInput(new SimpleExporterInput(jasperPrint));
                export.setExporterOutput(new SimpleOutputStreamExporterOutput(new File(outPutFile )));
                String returnUrl = request.getRequestURL().toString().replace(request.getRequestURI(),"/invoice/"+prefixFileName );
                export.exportReport();
                response.setFilePath(returnUrl);
                log.info("Done" );
                System.out.println("Done!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }}
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
    public  List<String> getPartnerName(List<CatPartnerDTO> lstPartner){
        List<String> lstPartneName = Lists.newArrayList();
        StringBuilder namePlus = new StringBuilder();
        for(CatPartnerDTO i: lstPartner){
            namePlus.append(i.getCode()).append("|").append(i.getName()).append("|").append(i.getTelNumber());
            lstPartneName.add(namePlus.toString());
            namePlus.setLength(0);
        }
        return lstPartneName;
    }
}
