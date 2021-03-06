package com.wms.controller.utils;

import com.google.common.collect.Lists;
import com.wms.base.BaseController;
import com.wms.constants.Constants;
import com.wms.dto.AppParamsDTO;
import com.wms.dto.Condition;
import com.wms.dto.MjrStockTransDTO;
import com.wms.dto.MjrStockTransDetailDTO;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.StockManagementService;
import com.wms.utils.DataUtil;
import com.wms.utils.DateTimeUtils;
import com.wms.utils.FunctionUtils;
import java.io.File;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by duyot on 3/31/2017.
 */
@Controller
@RequestMapping("/workspace/utils/searchSerial")
@Scope("session")
public class SearchSerialController extends BaseController {
    @Autowired
    BaseService mjrStockGoodsSerialService;
    @Autowired
    StockManagementService stockManagementService;
    //
    public Map<String, String> mapAppStockStatus;
    private List<MjrStockTransDetailDTO> lstGoodsDetails;
    private Map<String, String> mapAppTransType;
    private List<AppParamsDTO> lstAppTransType;
    //------------------------------------------------------------------------------------------------------------------
    @PostConstruct
    public void init() {
        if (!isDataLoaded) {
            initBaseBean();
        }
        this.lstAppTransType = FunctionUtils.getAppParamByType(Constants.APP_PARAMS.TRANS_TYPE, lstAppParams);
        this.mapAppTransType = FunctionUtils.buildMapAppParams(lstAppTransType);
        mapAppStockStatus = FunctionUtils.buildMapAppParams(FunctionUtils.getAppParamByType(Constants.APP_PARAMS.STOCK_STATUS, lstAppParams));
    }

    //------------------------------------------------------------------------------------------------------------------
    @RequestMapping()
    public String home(Model model) {
        lstGoodsDetails = Lists.newArrayList();
        model.addAttribute("menuName", "menu.utils.searchserial");
        model.addAttribute("lstStock", lstStock);
        model.addAttribute("lstGoods", lstGoods);
        return "utils/search_serial";
    }

    @RequestMapping(value = "/findSerial", method = RequestMethod.GET)
    public @ResponseBody
    List<MjrStockTransDetailDTO> findSerial(@RequestParam("stockId") String stockId, @RequestParam("goodsId") String goodsId,
                                            @RequestParam("serial") String serial
    ) {
        String arrSearchSerial = preprocessSearchSerial(serial);

        List<Condition> lstCon = Lists.newArrayList();

        lstCon.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, selectedCustomer.getId()));
        if (!DataUtil.isStringNullOrEmpty(stockId) && !stockId.equals(Constants.STATS_ALL)) {
            lstCon.add(new Condition("stockId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, stockId));
        }
        if (!DataUtil.isStringNullOrEmpty(goodsId) && !goodsId.equals(Constants.STATS_ALL)) {
            lstCon.add(new Condition("goodsId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, goodsId));
        }
        lstCon.add(new Condition("serial", Constants.SQL_OPERATOR.IN, arrSearchSerial));
        lstCon.add(new Condition("importDate", Constants.SQL_OPERATOR.ORDER, "desc"));
        //
        List<MjrStockTransDetailDTO> lstResult = FunctionUtils.convertGoodsSerialToDetail(mjrStockGoodsSerialService.findByCondition(lstCon), mapAppStockStatus);
        if (DataUtil.isListNullOrEmpty(lstResult)) {
            return Lists.newArrayList();
        }
        lstGoodsDetails = FunctionUtils.setNameValueGoodsDetail(lstResult, mapGoodsIdGoods, mapStockIdStock, mapAppGoodsState);
        return lstGoodsDetails;
    }

    @RequestMapping(value = "/getSerialFile", method = RequestMethod.GET)
    public void getSerialFile(HttpServletResponse response) {

        if (DataUtil.isListNullOrEmpty(lstGoodsDetails)) {
            lstGoodsDetails.add(new MjrStockTransDetailDTO("", "", "", "", "", "", "", "", "", "", "", ""));
        }
        //
        String prefixFileName = "Thong_tin_tim_kiem_serial_";
        //
        String fileResource = exportSerialFile(lstGoodsDetails, prefixFileName);
        FunctionUtils.loadFileToClient(response, fileResource);
    }

    //------------------------------------------------------------------------------------------------------------------
    private String preprocessSearchSerial(String serial) {
        String[] arrSerial = serial.split(",");
        StringBuilder sbSerial = new StringBuilder();
        for (String i : arrSerial) {
            sbSerial.append(",").append(i.trim());
        }

        return sbSerial.toString().replaceFirst(",", "");
    }

    private String exportSerialFile(List<MjrStockTransDetailDTO> lstSerial, String prefixFileName) {
        String templatePath = profileConfig.getTemplateURL() + Constants.FILE_RESOURCE.GOODS_DETAILS_SEARCH_SERIAL_TEMPLATE;

        File file = new File(templatePath);
        String templateAbsolutePath = file.getAbsolutePath();

        Map<String, Object> beans = new HashMap<>();
        beans.put("items", lstSerial);
        beans.put("date", DateTimeUtils.convertDateTimeToString(new Date()));

        String fullFileName = prefixFileName + "_" + DateTimeUtils.getSysDateTimeForFileName() + ".xlsx";
        String reportFullPath = profileConfig.getTempURL() + fullFileName;
        //
        FunctionUtils.exportExcel(templateAbsolutePath, beans, reportFullPath);
        //
        return reportFullPath;
    }
    //==================================================================================================================
    @RequestMapping(value = "/viewSerialLog")
    public @ResponseBody
    List<MjrStockTransDTO> viewSerialLog(@RequestParam("goodsId") String goodsId, @RequestParam("serial") String serial) {
        List<MjrStockTransDTO> lstStockTrans = stockManagementService.getListTransSerial(selectedCustomer.getId(), goodsId,serial);

        if (DataUtil.isListNullOrEmpty(lstStockTrans)) {
            return Lists.newArrayList();
        }
        for (MjrStockTransDTO i : lstStockTrans) {
            try {
                i.setStockValue(FunctionUtils.getMapValue(mapStockIdStock, i.getStockId()));
                i.setTypeValue(mapAppTransType.get(i.getType()));
            } catch (Exception e) {
            }
        }
        return lstStockTrans;
    }
}
