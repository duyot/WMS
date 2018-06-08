package com.wms.controller.utils;

import com.google.common.collect.Lists;
import com.wms.base.BaseController;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.CatUserService;
import com.wms.services.interfaces.StockManagementService;
import com.wms.utils.BundleUtils;
import com.wms.utils.DataUtil;
import com.wms.utils.DateTimeUtils;
import com.wms.utils.FunctionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.Document;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
/**
 * Created by duyot on 3/31/2017.
 */
@Controller
@RequestMapping("/workspace/utils/trans_info")
@Scope("session")
public class TransInfoController extends BaseController{
    @Autowired
    BaseService mjrStockTransService;
    @Autowired
    BaseService mjrStockTransDetailService;
    @Autowired
    StockManagementService stockManagementService;
    @Autowired
    public CatUserService catUserService;
    //
    private List<MjrStockTransDTO> lstTrans;
    private List<CatUserDTO> lstUsers;
    private List<AppParamsDTO> lstAppTransType;
    private Map<String,String> mapAppTransType = new HashMap();
    //
    private String startDate;
    private String endDate;

    @ModelAttribute("lstAppTransType")
    public List<AppParamsDTO> setAppTransType(HttpServletRequest request) {
        if (lstAppTransType != null) {
            return lstAppTransType;
        }
        if (lstAppParams == null) {
            if (selectedCustomer == null) {
                this.selectedCustomer = (CatCustomerDTO) request.getSession().getAttribute("selectedCustomer");
            }
            if (tokenInfo == null) {
                this.tokenInfo = (AuthTokenInfo) request.getSession().getAttribute("tokenInfo");
            }
            lstAppParams = FunctionUtils.getAppParams(appParamsService, tokenInfo);

        }
        lstAppTransType = FunctionUtils.getAppParamByType(Constants.APP_PARAMS.TRANS_TYPE, lstAppParams);
        buildMapTransType();
        return lstAppTransType;
    }

    @RequestMapping()
    public String home(Model model){
        lstTrans   = Lists.newArrayList();
        model.addAttribute("menuName","menu.utils.transinfo");
        return "utils/trans_info";
    }

    @ModelAttribute("lstUsers")
    public List<CatUserDTO> setUsers(HttpServletRequest request){
        if(selectedCustomer == null){
            this.selectedCustomer =  (CatCustomerDTO) request.getSession().getAttribute("selectedCustomer");
        }
        if(tokenInfo == null){
            this.tokenInfo =  (AuthTokenInfo) request.getSession().getAttribute("tokenInfo");
        }
        //
        if(lstUsers == null){
            lstUsers = FunctionUtils.getListUser(catUserService,selectedCustomer,tokenInfo);
        }
        return lstUsers;
    }

    //==================================================================================================================
    @RequestMapping(value = "/findTrans",method = RequestMethod.GET)
    public @ResponseBody
    List<MjrStockTransDTO> findTrans(@RequestParam("stockId")String stockId, @RequestParam("createdUser")String createdUser,
                                      @RequestParam("startDate")String startDate, @RequestParam("endDate")String endDate,
                                      @RequestParam("transType")String transType,@RequestParam("transCode")String transCode
    ){
        List<Condition> lstCon = Lists.newArrayList();
        lstCon.add(new Condition("status",Constants.SQL_PRO_TYPE.LONG ,Constants.SQL_OPERATOR.EQUAL,Constants.STATUS.ACTIVE));

        lstCon.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL,selectedCustomer.getId()));
        if(!DataUtil.isStringNullOrEmpty(stockId) && !stockId.equals(Constants.STATS_ALL)){
            lstCon.add(new Condition("stockId",Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL,stockId));
        }
        if(!DataUtil.isStringNullOrEmpty(startDate) && !DataUtil.isStringNullOrEmpty(endDate)){
            this.startDate = startDate;
            this.endDate = endDate;
            lstCon.add(new Condition("createdDate", Constants.SQL_OPERATOR.BETWEEN,startDate + "|"+ endDate));
        }

        if(!DataUtil.isStringNullOrEmpty(createdUser) && !createdUser.equals(Constants.STATS_ALL)){
            lstCon.add(new Condition("createdUser",Constants.SQL_OPERATOR.EQUAL,createdUser));
        }

        if(!DataUtil.isStringNullOrEmpty(transType) && !transType.equals(Constants.STATS_ALL)){
            lstCon.add(new Condition("type",Constants.SQL_PRO_TYPE.LONG,Constants.SQL_OPERATOR.EQUAL,transType));
        }

        if(!DataUtil.isStringNullOrEmpty(transCode)){
            lstCon.add(new Condition("code",Constants.SQL_OPERATOR.LIKE,transCode));
        }

        lstCon.add(new Condition("createdDate",Constants.SQL_OPERATOR.ORDER,"desc"));
        //
        lstTrans = mjrStockTransService.findByCondition(lstCon,tokenInfo);
        if(DataUtil.isListNullOrEmpty(lstTrans)){
            return Lists.newArrayList();
        }
        return setTransInfoValue(lstTrans);
    }
    //==================================================================================================================
    @RequestMapping(value = "/getListTransFile")
    public void getListTransFile(HttpServletResponse response){
        if(DataUtil.isListNullOrEmpty(lstTrans)){
            lstTrans.add(new MjrStockTransDTO("","","","","","","","","","","","","","","","","","",""));
            startDate = "";
            endDate = "";
        }
        String prefixFileName = "Thongtin_ds_giaodich_";
        String fileResource = exportListStockTrans(lstTrans,prefixFileName);
        FunctionUtils.loadFileToClient(response,fileResource);
    }
    //==================================================================================================================
    @RequestMapping(value = "/getListTransDetailFile")
    public void getListTransDetailFile(HttpServletResponse response){
        int size = 0;
        StringBuilder lstStockTransId = new StringBuilder();
        if(!DataUtil.isListNullOrEmpty(lstTrans)){
            size = lstTrans.size();
        }
        for (int i = 0; i<size;i++){
            lstStockTransId.append(lstTrans.get(i).getId());
            if(i!=size -1){
                lstStockTransId.append(",");
            }
        }
        List<MjrStockTransDetailDTO> lstTransDetail = stockManagementService.getListTransGoodsDetail(lstStockTransId.toString(),tokenInfo);
        if(DataUtil.isListNullOrEmpty(lstTransDetail)){
            lstTransDetail.add(new MjrStockTransDetailDTO());
            startDate = "";
            endDate = "";
        }
        String prefixFileName = "Thongtin_chitiet_giaodich_";
        String fileResource = exportListStockTransDetail(lstTransDetail,prefixFileName);
        FunctionUtils.loadFileToClient(response,fileResource);
    }


    //==================================================================================================================
    @RequestMapping(value = "/cancelTrans")
    public @ResponseBody String cancelTrans(@RequestParam("transId") String transId){
        ResponseObject response = stockManagementService.cancelTrans(transId,tokenInfo);
        if(Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())){
            return "1|Hủy phiếu thành công";
        }else{
            return "0|Hủy phiếu không thành công";
          }
    }


    //==================================================================================================================
    @RequestMapping(value = "/exportTransInfo")
    public void exportTransInfo(HttpServletResponse response,@RequestParam("transId") String transId){
        //Lay thong tin chung giao dich xuat kho
        List<MjrStockTransDTO> lstStockTrans = stockManagementService.getStockTransInfo(transId.toString(),tokenInfo);
        MjrStockTransDTO mjrStockTransDTO = null;
        if (lstStockTrans != null && lstStockTrans.size() >0){
            mjrStockTransDTO = lstStockTrans.get(0);
        }
        //Lay thong tin danh sach hang hoa thuoc giao dich
        List<MjrStockTransDetailDTO> lstStockTransDetail = stockManagementService.getListTransGoodsDetail(transId.toString(),tokenInfo);

        String templatePath = BundleUtils.getKey("template_url") +"\\PXK\\"+ Constants.FILE_RESOURCE.EXPORT_TRANS_TEMPLATE;
        String prefixFileName = "Thongtin_chitiet_giaodich_";
//        JasperReport jasperReport;
        try {
            /*
            jasperReport = JasperCompileManager.compileReport(templatePath);
            Map parameters = new HashMap();

            parameters.put("stockName", mjrStockTransDTO.getStockName());
            parameters.put("stockName", mjrStockTransDTO.getStockCode());
                        //
            List<MjrStockTransDetailDTO> listItems = new ArrayList<>();
            int lstGoodsSize = lstStockTransDetail.size();
            for (int i = 0; i< lstGoodsSize ; i++){
                MjrStockTransDetailDTO mjrStockTransDetailDTO =  lstStockTransDetail.get(i);
                listItems.add(new MjrStockTransDetailDTO(mjrStockTransDetailDTO.getGoodsCode(), mjrStockTransDetailDTO.getGoodsName()));
            }

            JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(listItems);
            parameters.put("goodsDataSource", itemsJRBean);
            // DataSource
            JRDataSource dataSource = new JREmptyDataSource();

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,
                    parameters, dataSource);
            //
            // Export to docx.
            JRDocxExporter exporter = new JRDocxExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "D:\\WMS\\report\\myreport.docx");
            exporter.exportReport();
            */

            System.out.println("Done!");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //==================================================================================================================
    @RequestMapping(value = "/viewTransInfo")
    public @ResponseBody List<MjrStockTransDetailDTO> getTransGoodsDetail(@RequestParam("stockId") String stockId,
                                                                          @RequestParam("transId") String transId,@RequestParam("transType") String transType
                                                                          ){
        List<MjrStockTransDetailDTO> lstTransGoodsDetail = stockManagementService.getListTransGoodsDetail(transId,tokenInfo);
        if(DataUtil.isListNullOrEmpty(lstTransGoodsDetail)){
            return  Lists.newArrayList();
        }
        return  lstTransGoodsDetail;
    }


    //==================================================================================================================
    private List<MjrStockTransDTO> setTransInfoValue(List<MjrStockTransDTO> lstTransDetail){
        for(MjrStockTransDTO i: lstTransDetail){
            i.setStockValue(FunctionUtils.getMapValue(mapStockIdStock,i.getStockId()));
            i.setTypeValue(mapAppTransType.get(i.getType()));
        }
        return lstTransDetail;
    }
    //==================================================================================================================
    private void buildMapTransType(){
        if(!DataUtil.isListNullOrEmpty(lstAppTransType)){
            for(AppParamsDTO i: lstAppTransType){
                mapAppTransType.put(i.getCode(),i.getName());
            }
        }
    }
    //==================================================================================================================
    private  String exportListStockTrans(List<MjrStockTransDTO> lstTrans,String prefixFileName){
        String templatePath = BundleUtils.getKey("template_url") + Constants.FILE_RESOURCE.LIST_TRANS_TEMPLATE;
        //
        File file = new File(templatePath);
        String templateAbsolutePath = file.getAbsolutePath();

        Map<String, Object> beans = new HashMap<>();
        beans.put("items", lstTrans);
        beans.put("startDate", startDate);
        beans.put("endDate", endDate);



        String fullFileName = prefixFileName +"_"+ DateTimeUtils.getSysDateTimeForFileName() + ".xlsx";
        String reportFullPath = BundleUtils.getKey("temp_url") + fullFileName;
        //
        FunctionUtils.exportExcel(templateAbsolutePath,beans,reportFullPath);
        return reportFullPath;
    }
    //==================================================================================================================
    private  String exportListStockTransDetail(List<MjrStockTransDetailDTO> lstTransDetail,String prefixFileName){
        String templatePath = BundleUtils.getKey("template_url") + Constants.FILE_RESOURCE.LIST_TRANS_DETAIL_TEMPLATE;
        //
        File file = new File(templatePath);
        String templateAbsolutePath = file.getAbsolutePath();

        Map<String, Object> beans = new HashMap<>();
        beans.put("items", lstTransDetail);
        beans.put("startDate", startDate);
        beans.put("endDate", endDate);
        try{
            beans.put("exportedTime", DateTimeUtils.getSysDateTime());
        }catch (Exception ex){
            System.out.println(ex.fillInStackTrace());
        }

        String fullFileName = prefixFileName +"_"+ DateTimeUtils.getSysDateTimeForFileName() + ".xlsx";
        String reportFullPath = BundleUtils.getKey("temp_url") + fullFileName;
        //
        FunctionUtils.exportExcel(templateAbsolutePath,beans,reportFullPath);
        return reportFullPath;
    }
}
