package com.wms.controller.category;

import com.google.common.collect.Lists;
import com.wms.base.BaseCommonController;
import com.wms.base.BaseController;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.CatUserService;
import com.wms.utils.DataUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import com.wms.utils.FunctionUtils;
import com.wms.utils.SessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * Created by duyot on 12/6/2016.
 */
@Controller
@RequestMapping("/workspace/cat_partner_ctr")
@Scope("session")
public class CatPartnerController extends BaseController {
    @Autowired
    BaseService catPartnerService;

    @Autowired
    BaseService mapUserPartnerServiceImpl;

    private Logger log = LoggerFactory.getLogger(CatPartnerController.class);

    //------------------------------------------------------------------------------------------------------------------
    @RequestMapping()
    public String home(Model model) {
        model.addAttribute("menuName", "menu.catPartner");
        model.addAttribute("lstPartner", lstPartner);
        model.addAttribute("lstUsers", lstUsers);
        return "category/cat_partner";
    }

    @RequestMapping(value = "/findByCondition", method = RequestMethod.GET)
    public @ResponseBody
    List<CatPartnerDTO> findByCondition(@RequestParam("status") String status, @RequestParam("userManagerId") String userManagerId ) {
        List<Condition> lstCon = Lists.newArrayList();
        lstCon.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, selectedCustomer.getId()));

        if (!DataUtil.isStringNullOrEmpty(userManagerId) && !userManagerId.equals(Constants.STATS_ALL)) {
            lstCon.add(new Condition("userManagerId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, userManagerId));
        }

        if (!DataUtil.isStringNullOrEmpty(status) && !status.equals(Constants.STATS_ALL)) {
            lstCon.add(new Condition("status", Constants.SQL_PRO_TYPE.BYTE, Constants.SQL_OPERATOR.EQUAL, status));
        }
        lstCon.add(new Condition("id", Constants.SQL_OPERATOR.ORDER, "desc"));

        List<CatPartnerDTO> lstCatPartner = catPartnerService.findByCondition(lstCon);

        for (CatPartnerDTO i : lstCatPartner) {
            i.setName(i.getName());
            i.setCustName(selectedCustomer.getName());
            i.setStatusName(mapAppStatus.get(i.getStatus()));
            if(i.getParentId() != null && mapPartnerIdPartner.containsKey(i.getParentId())){
                i.setParentName(mapPartnerIdPartner.get(i.getParentId()).getName());
            }
            if(i.getUserManagerId() != null && mapUserIdUser.containsKey(i.getUserManagerId())){
                i.setUserManagerName(mapUserIdUser.get(i.getUserManagerId()).getName());
            }
        }

        return lstCatPartner;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public @ResponseBody
    String add(CatPartnerDTO catPartnerDTO, HttpServletRequest request) {
        catPartnerDTO.setStatus("1");
        catPartnerDTO.setCustId(this.selectedCustomer.getId());
        ResponseObject response = catPartnerService.add(catPartnerDTO);
        if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
            log.info("Add: " + catPartnerDTO.toString() + " SUCCESS");
            request.getSession().setAttribute("isCatPartnerModified", true);
            SessionUtils.setPartnerModified(request);
            return "1|Thêm mới thành công";
        } else if (Responses.ERROR_CONSTRAINT.getName().equalsIgnoreCase(response.getStatusName())) {
            log.info("Add: " + catPartnerDTO.toString() + " ERROR");
            return "0|Thông tin đã có trên hệ thống";
        } else {
            log.info("Add: " + catPartnerDTO.toString() + " ERROR");
            return "0|Thêm mới không thành công";
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody
    String update(CatPartnerDTO catPartnerDTO, HttpServletRequest request) {
        log.info("Update cat_partner info: " + catPartnerDTO.toString());
        catPartnerDTO.setCustId(this.selectedCustomer.getId());
        if ("on".equalsIgnoreCase(catPartnerDTO.getStatus())) {
            catPartnerDTO.setStatus("1");
        } else {
            catPartnerDTO.setStatus("0");
        }
        ResponseObject response = catPartnerService.update(catPartnerDTO);
        if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
            log.info("SUCCESS");
            request.getSession().setAttribute("isPartnerModified", true);
            SessionUtils.setPartnerModified(request);
            return "1|Cập nhật thành công";
        } else if (Responses.ERROR_CONSTRAINT.getName().equalsIgnoreCase(response.getStatusName())) {
            log.info("ERROR");
            return "0|Thông tin đã có trên hệ thống";
        } else {
            log.info("ERROR");
            return "0|Cập nhật không thành công";
        }

    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public @ResponseBody
    String delete(@RequestParam("id") String id, @RequestParam("code") String code, HttpServletRequest request) {
        try {

            Long idL = Long.parseLong(id);

            CatPartnerDTO deleteObject = (CatPartnerDTO) catPartnerService.findById(idL);
            deleteObject.setStatus(Constants.STATUS.DELETED);
            ResponseObject response = catPartnerService.update(deleteObject);
            if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
                request.getSession().setAttribute("isCatPartnerModified", true);
                SessionUtils.setPartnerModified(request);
                return "1|Xoá thành công";
            } else {
                return "0|Xoá không thành công";
            }
        } catch (NumberFormatException e) {
            return "0|Xoá không thành công lỗi convert long";
        }
    }

    @RequestMapping(value = "/getTemplateFile")
    public void getTemplateFile(HttpServletResponse response) {
        FunctionUtils.loadFileToClient(response, profileConfig.getTemplateURL() + Constants.FILE_RESOURCE.IMPORT_PARTNER_TEMPLATE);
    }

    @RequestMapping(value = "/getErrorImportFile")
    public void getErrorImportFile(HttpServletRequest request, HttpServletResponse response) {
        String fileResource = profileConfig.getTempURL() + request.getSession().getAttribute("file_partner_import_error");
        FunctionUtils.loadFileToClient(response, fileResource);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public List<CatPartnerDTO> uploadFile(MultipartHttpServletRequest request) {
        //1. get the files from the request object
        Iterator<String> itr = request.getFileNames();
        MultipartFile mpf = request.getFile(itr.next());
        //
        ImportFileResultDTO importFileResult = FunctionUtils.getListPartnerImportFromFile(mpf);
        if (!importFileResult.isValid()) {
            //save error file
            String prefixFileName = selectedCustomer.getId() + "_" + currentUser.getCode();
            String fileName = FunctionUtils.exportExcelImportPartnerError(importFileResult.getLstPartner(), prefixFileName, profileConfig);
            //save in session
            request.getSession().setAttribute("file_partner_import_error", fileName);
            return null;
        }
        return importFileResult.getLstPartner();
    }

    @RequestMapping(value = "/saveListPartner", method = RequestMethod.POST)
    @ResponseBody
    public ResponseObject saveListPartner(@RequestBody ListPartnerDTO lstPartnerObject, HttpServletRequest request) {
        log.info("------------------------------------------------------------");
        List<CatPartnerDTO> lstPartner = lstPartnerObject.getLstPartner();
        log.info(currentUser.getCode() + " importing partner with: " + lstPartner.size() + " items.");
        List<CatPartnerDTO> lstError = Lists.newArrayList();
        //
        ResponseObject insertResponse = new ResponseObject();
        String sysdate = appParamsService.getSysDate();
        int successCount = 0;
        for (CatPartnerDTO item : lstPartner) {
            item.setStatus("1");
            item.setCustId(this.selectedCustomer.getId());
            insertResponse = catPartnerService.add(item);
            if (!Responses.SUCCESS.getName().equalsIgnoreCase(insertResponse.getStatusCode())) {
                item.setErrorInfo("Thông tin đối tác đã được khai báo trên hệ thống");
                lstError.add(item);
            } else {
                successCount++;
            }
        }
        //
        if (lstError.size() > 0) {
            //export file error
            String prefixFileName = selectedCustomer.getId() + "_" + currentUser.getCode();
            String fileName = FunctionUtils.exportExcelImportPartnerError(lstError, prefixFileName, profileConfig);
            //save in session
            request.getSession().setAttribute("file_partner_save_error", fileName);
            //
            insertResponse.setStatusCode("SUCCESS_WITH_ERROR");
            insertResponse.setTotal(lstPartner.size() + "");
            insertResponse.setSuccess(successCount + "");
        } else {
            insertResponse.setStatusCode("SUCCESS");
            insertResponse.setSuccess(lstPartner.size() + "");
        }
        //
        SessionUtils.setPartnerModified(request);
        return insertResponse;
    }
    @RequestMapping(value = "/getErrorSavePartner")
    public void getErrorSavePartner(HttpServletRequest request, HttpServletResponse response) {
        String fileResource = profileConfig.getTempURL() + request.getSession().getAttribute("file_partner_save_error");
        FunctionUtils.loadFileToClient(response, fileResource);
    }
}
