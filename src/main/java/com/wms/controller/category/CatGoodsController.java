package com.wms.controller.category;

import com.google.common.collect.Lists;
import com.wms.base.BaseController;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.utils.DataUtil;
import com.wms.utils.FunctionUtils;
import com.wms.utils.JSONUtils;
import com.wms.utils.SessionUtils;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * Created by duyot on 12/9/2016.
 */
@Controller
@RequestMapping("/workspace/cat_goods_ctr")
@Scope("session")
public class CatGoodsController extends BaseController {
    @Autowired
    BaseService catGoodsGroupService;
    @Autowired
    BaseService catGoodsService;
    @Autowired
    BaseService mjrStockGoodsTotalService;

    private LinkedHashMap<String, String> mapGoodsGroup;
    private Logger log = LoggerFactory.getLogger(CatGoodsController.class);

    //------------------------------------------------------------------------------------------------------------------
    @PostConstruct
    public void init() {
        if (!isDataLoaded) {
            initBaseBean();
        }
        initmapGoodsGroup();
    }

    //------------------------------------------------------------------------------------------------------------------
    @ModelAttribute("data-reload")
    public void checkReloadData(HttpServletRequest request) {
        if (SessionUtils.isPropertiesModified(request, Constants.DATA_MODIFIED.GOODS_GROUP_MODIFIED)) {
            initmapGoodsGroup();
            SessionUtils.setReloadedModified(request, Constants.DATA_MODIFIED.GOODS_GROUP_MODIFIED);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    @RequestMapping()
    public String home(Model model) {
        model.addAttribute("menuName", "menu.catgoods");
        model.addAttribute("mapUnitType", mapAppParamsUnitName);
        model.addAttribute("mapGoodsGroup", mapGoodsGroup);
        return "category/cat_goods";
    }

    @RequestMapping(value = "/getTemplateFile")
    public void getTemplateFile(HttpServletResponse response) {
        FunctionUtils.loadFileToClient(response, profileConfig.getTemplateURL() + Constants.FILE_RESOURCE.IMPORT_GOODS_TEMPLATE);
    }

    @RequestMapping(value = "/getErrorImportFile")
    public void getErrorImportFile(HttpServletRequest request, HttpServletResponse response) {
        String fileResource = profileConfig.getTempURL() + request.getSession().getAttribute("file_goods_import_error");
        FunctionUtils.loadFileToClient(response, fileResource);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public List<CatGoodsDTO> uploadFile(MultipartHttpServletRequest request) {
        //1. get the files from the request object
        Iterator<String> itr = request.getFileNames();
        MultipartFile mpf = request.getFile(itr.next());
        //
        ImportFileResultDTO importFileResult = FunctionUtils.getListGoodsImportFromFile(mpf, mapAppGoodsState, mapGoodsGroup, mapAppParamsUnitName);
        if (!importFileResult.isValid()) {
            //save error file
            String prefixFileName = selectedCustomer.getId() + "_" + currentUser.getCode();
            String fileName = FunctionUtils.exportExcelImportGoodsError(importFileResult.getLstGoods(), prefixFileName, profileConfig);
            //save in session
            request.getSession().setAttribute("file_goods_import_error", fileName);
            return null;
        }
        return importFileResult.getLstGoods();
    }

    @RequestMapping(value = "/findByCondition", method = RequestMethod.GET)
    public @ResponseBody
    List<CatGoodsDTO> findByCondition(@RequestParam("status") String status, @RequestParam("goodsCode") String goodsCode, @RequestParam("goodsGroupId") String goodsGroupId) {
        List<Condition> lstCon = Lists.newArrayList();

        lstCon.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, selectedCustomer.getId()));

        if (!DataUtil.isStringNullOrEmpty(status) && !status.equals(Constants.STATS_ALL)) {
            lstCon.add(new Condition("status", Constants.SQL_PRO_TYPE.BYTE, Constants.SQL_OPERATOR.EQUAL, Byte.parseByte(status)));
        }

        if (!DataUtil.isStringNullOrEmpty(goodsGroupId) && !goodsGroupId.equals(Constants.STATS_ALL)) {
            lstCon.add(new Condition("goodsGroupId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, goodsGroupId));
        }

        if (!DataUtil.isStringNullOrEmpty(goodsCode)) {
            lstCon.add(new Condition("code", Constants.SQL_OPERATOR.LIKE, goodsCode));
        }

        lstCon.add(new Condition("id", Constants.SQL_OPERATOR.ORDER, "desc"));

        List<CatGoodsDTO> lstCatGoods = catGoodsService.findByCondition(lstCon);

        for (CatGoodsDTO i : lstCatGoods) {
            i.setCode(i.getCode());
            i.setName(i.getName());
            i.setCustName(selectedCustomer.getName());
            i.setGoodsGroupName(mapGoodsGroup.get(i.getGoodsGroupId()));
            i.setIsSerialName(i.getIsSerial().equals("1") ? "Có" : "Không");
            i.setStatusName(mapAppStatus.get(i.getStatus()));
            i.setUnitTypeName(mapAppParamsUnitName.get(i.getUnitType()));
            i.setInPriceValue(FunctionUtils.formatNumber(i.getInPrice()));
            i.setOutPriceValue(FunctionUtils.formatNumber(i.getOutPrice()));
            i.setVolumeFromSize();
            i.setLength(FunctionUtils.formatNumber(i.getLength()));
            i.setWidth(FunctionUtils.formatNumber(i.getWidth()));
            i.setHigh(FunctionUtils.formatNumber(i.getHigh()));
            i.setWeight(FunctionUtils.formatNumber(i.getWeight()));
            i.setAmountStorageQuota(FunctionUtils.formatNumber(i.getAmountStorageQuota()));
        }

        return lstCatGoods;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public @ResponseBody
    String add(CatGoodsDTO catGoods, HttpServletRequest request) {
        catGoods.setStatus("1");
        catGoods.setIsSerial(FunctionUtils.getValueFromToggle(catGoods.getIsSerial()));
        catGoods.setCustId(this.selectedCustomer.getId());
        catGoods.setCreatedDate(catGoodsService.getSysDate());
        catGoods.setCode(catGoods.getCode().toUpperCase());
        catGoods.setInPrice(catGoods.getInPrice().replaceAll(",", ""));
        catGoods.setOutPrice(catGoods.getOutPrice().replaceAll(",", ""));
        catGoods.setLength(catGoods.getLength().replaceAll(",", ""));
        catGoods.setWidth(catGoods.getWidth().replaceAll(",", ""));
        catGoods.setHigh(catGoods.getHigh().replaceAll(",", ""));
        catGoods.setVolumeFromSize();
        catGoods.setWeight(catGoods.getWeight().replaceAll(",", ""));
        catGoods.setAmountStorageQuota(catGoods.getAmountStorageQuota().replaceAll(",", ""));

        //
        ResponseObject response = catGoodsService.add(catGoods);
        if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
            SessionUtils.setGoodsModified(request);
            log.info("Add: " + catGoods.toString() + " SUCCESS");
            return "1|Thêm mới thành công";
        } else if (Responses.ERROR_CONSTRAINT.getName().equalsIgnoreCase(response.getStatusName())) {
            log.info("ERROR");
            return "0|Thông tin đã có trên hệ thống";
        } else {
            log.info("Add: " + catGoods.toString() + " ERROR");
            return "0|Lỗi hệ thống";
        }
    }

    @RequestMapping(value = "/saveListGoods", method = RequestMethod.POST)
    @ResponseBody
    public ResponseObject importStock(@RequestBody ListGoodsDTO lstGoodsObject, HttpServletRequest request) {
        log.info("------------------------------------------------------------");
        List<CatGoodsDTO> lstGoods = lstGoodsObject.getLstGoods();
        log.info(currentUser.getCode() + " importing goods with: " + lstGoods.size() + " items.");
        List<CatGoodsDTO> lstError = Lists.newArrayList();
        //
        ResponseObject insertResponse = new ResponseObject();
        String sysdate = appParamsService.getSysDate();
        int successCount = 0;
        for (CatGoodsDTO item : lstGoods) {
            item.setStatus("1");
            item.setCreatedDate(sysdate);
            item.setCustId(this.selectedCustomer.getId());
            item.setUnitTypeName(null);
            item.setGoodsGroupName(null);
            insertResponse = catGoodsService.add(item);
            if (!Responses.SUCCESS.getName().equalsIgnoreCase(insertResponse.getStatusCode())) {
                item.setErrorInfo("Thông tin hàng đã được khai báo trên hệ thống");
                lstError.add(item);
            } else {
                successCount++;
            }
        }
        //
        if (lstError.size() > 0) {
            //export file error
            String prefixFileName = selectedCustomer.getId() + "_" + currentUser.getCode();
            String fileName = FunctionUtils.exportExcelImportGoodsError(lstError, prefixFileName, profileConfig);
            //save in session
            request.getSession().setAttribute("file_goods_save_error", fileName);
            //
            insertResponse.setStatusCode("SUCCESS_WITH_ERROR");
            insertResponse.setTotal(lstGoods.size() + "");
            insertResponse.setSuccess(successCount + "");
        } else {
            insertResponse.setStatusCode("SUCCESS");
            insertResponse.setSuccess(lstGoods.size() + "");
        }
        //
        SessionUtils.setGoodsModified(request);
        return insertResponse;
    }

    @RequestMapping(value = "/getErrorSaveGoods")
    public void getErrorSaveGoods(HttpServletRequest request, HttpServletResponse response) {
        String fileResource = profileConfig.getTempURL() + request.getSession().getAttribute("file_goods_save_error");
        FunctionUtils.loadFileToClient(response, fileResource);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody
    String update(CatGoodsDTO catGoods, HttpServletRequest request) {
        String sysdate = appParamsService.getSysDate();
        catGoods.setCustId(selectedCustomer.getId());
        catGoods.setStatus(FunctionUtils.getValueFromToggle(catGoods.getStatus()));
        catGoods.setIsSerial(FunctionUtils.getValueFromToggle(catGoods.getIsSerial()));
        catGoods.setCreatedDate(sysdate);
        catGoods.setCode(catGoods.getCode().toUpperCase());
        catGoods.setInPrice(catGoods.getInPrice() != null ? catGoods.getInPrice().replaceAll(",", "") : "");
        catGoods.setOutPrice(catGoods.getOutPrice().replaceAll(",", ""));
        catGoods.setLength(catGoods.getLength().replaceAll(",", ""));
        catGoods.setWidth(catGoods.getWidth().replaceAll(",", ""));
        catGoods.setHigh(catGoods.getHigh().replaceAll(",", ""));
        catGoods.setWeight(catGoods.getWeight().replaceAll(",", ""));
        catGoods.setVolumeFromSize();
        catGoods.setAmountStorageQuota(catGoods.getAmountStorageQuota().replaceAll(",", ""));


        log.info("Update cat_goods info: " + JSONUtils.object2JSONString(catGoods));
        ResponseObject response = catGoodsService.update(catGoods);
        if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
            SessionUtils.setGoodsModified(request);
            log.info("SUCCESS");
            return "1|Cập nhật thành công";
        } else if (Responses.ERROR_CONSTRAINT.getName().equalsIgnoreCase(response.getStatusName())) {
            log.info("ERROR");
            return "0|Thông tin đã có trên hệ thống";
        } else {
            log.info("ERROR");
            return "0|Lỗi hệ thống";
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public @ResponseBody
    String delete(@RequestParam("id") String id, @RequestParam("code") String code, HttpServletRequest request) {
        try {
            //
            if (isUsed(id)) {
                return "0|Xoá không thành công: hàng đã được sử dụng";
            }
            //
            Long idL = Long.parseLong(id);
            //
            if (isDeleteGoodsAvailable(code)) {
                catGoodsService.delete(idL);
                return "1|Xoá thành công";
            }
            //
            CatGoodsDTO deletedGoods = (CatGoodsDTO) catGoodsService.findById(idL);
            deletedGoods.setStatus(Constants.STATUS.DELETED);
            ResponseObject response = catGoodsService.update(deletedGoods);
            if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
                SessionUtils.setGoodsModified(request);
                return "1|Xoá thành công";
            } else {
                return "0|Xoá không thành công";
            }
        } catch (NumberFormatException e) {
            return "0|Xoá không thành công lỗi convert long";
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    private void initmapGoodsGroup() {
        mapGoodsGroup = new LinkedHashMap<>();
        List<CatGoodsGroupDTO> lstCatGoodsGroup = getCatGroup();
        if (!DataUtil.isListNullOrEmpty(lstCatGoodsGroup)) {
            for (CatGoodsGroupDTO i : lstCatGoodsGroup) {
                mapGoodsGroup.put(i.getId(), i.getName());
            }
        }
    }

    private List<CatGoodsGroupDTO> getCatGroup() {
        List<Condition> lstCon = Lists.newArrayList();
        lstCon.add(new Condition("status", Constants.SQL_PRO_TYPE.BYTE, Constants.SQL_OPERATOR.EQUAL, Constants.STATUS.ACTIVE));
        lstCon.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, selectedCustomer.getId()));
        lstCon.add(new Condition("name", "VNM_ORDER", "asc"));
        return catGoodsGroupService.findByCondition(lstCon);
    }

    public boolean isUsed(String id) {
        List<Condition> lstCon = Lists.newArrayList();
        lstCon.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, selectedCustomer.getId()));
        lstCon.add(new Condition("goodsId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, id));
        Long count = mjrStockGoodsTotalService.countByCondition(lstCon);
        return count != null && count > 0;
    }

    public boolean isDeleteGoodsAvailable(String code) {
        List<Condition> lstCon = Lists.newArrayList();
        lstCon.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, selectedCustomer.getId()));
        lstCon.add(new Condition("code", Constants.SQL_OPERATOR.EQUAL, code));
        lstCon.add(new Condition("status", Constants.SQL_PRO_TYPE.BYTE, Constants.SQL_OPERATOR.EQUAL, Constants.STATUS.DELETED));
        return !DataUtil.isListNullOrEmpty(catGoodsService.findByCondition(lstCon));
    }
}
