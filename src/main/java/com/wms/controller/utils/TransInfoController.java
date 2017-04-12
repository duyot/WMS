package com.wms.controller.utils;

import com.google.common.collect.Lists;
import com.wms.base.BaseController;
import com.wms.constants.Constants;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.CatUserService;
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
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public CatUserService catUserService;
    //
    private List<MjrStockTransDTO> lstTrans;
    private List<CatUserDTO> lstUsers;
    private List<AppParamsDTO> lstAppTransType;
    public Map<String,String> mapAppTransType = new HashMap();
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
    List<MjrStockTransDTO> findSerial(@RequestParam("stockId")String stockId, @RequestParam("createdUser")String createdUser,
                                      @RequestParam("startDate")String startDate, @RequestParam("endDate")String endDate,
                                      @RequestParam("transType")String transType,@RequestParam("transCode")String transCode
    ){
        List<Condition> lstCon = Lists.newArrayList();

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
    public void getGoodsDetailFile(HttpServletResponse response){
        if(DataUtil.isListNullOrEmpty(lstTrans)){
            lstTrans.add(new MjrStockTransDTO("","","","","","","","","","","","","","","","",""));
            startDate = "";
            endDate = "";
        }
    //
        String prefixFileName = "Thong_tin_ds_giao_dich_";
        //
        String fileResource = exportListStockTrans(lstTrans,prefixFileName);
        FunctionUtils.loadFileToClient(response,fileResource);
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
        String templatePath = BundleUtils.getkey("template_url") + Constants.FILE_RESOURCE.LIST_TRANS_TEMPLATE;
        //
        File file = new File(templatePath);
        String templateAbsolutePath = file.getAbsolutePath();

        Map<String, Object> beans = new HashMap<>();
        beans.put("items", lstTrans);
        beans.put("startDate", startDate);
        beans.put("endDate", endDate);

        String fullFileName = prefixFileName +"_"+ DateTimeUtils.getSysDateTimeForFileName() + ".xlsx";
        String reportFullPath = BundleUtils.getkey("temp_url") + fullFileName;
        //
        FunctionUtils.exportExcel(templateAbsolutePath,beans,reportFullPath);
        return reportFullPath;
    }
}
