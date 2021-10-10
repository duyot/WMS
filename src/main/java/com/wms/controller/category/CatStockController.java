package com.wms.controller.category;

import com.google.common.collect.Lists;
import com.wms.base.BaseCommonController;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.StockService;
import com.wms.utils.DataUtil;
import com.wms.utils.FunctionUtils;
import com.wms.utils.SessionUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
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
@RequestMapping("/workspace/cat_stock_ctr")
@Scope("session")
public class CatStockController extends BaseCommonController {
    Logger log = LoggerFactory.getLogger(CatStockController.class);

    @Autowired
    BaseService catStockService;

    @Autowired
    BaseService mjrStockGoodsTotalService;

    @Autowired
    BaseService catStockCellService;

    @Autowired
    public StockService stockService;

    @Autowired
    BaseService mapUserStockServiceImpl;

    public List<CatStockDTO> lstStock;
    private LinkedHashMap<String, String> mapStock;

    @RequestMapping()
    public String home(Model model) {
        model.addAttribute("menuName", "menu.catstock");
        mapStock = new LinkedHashMap<>();
        return "category/cat_stock";
    }

    @RequestMapping(value = "/findByCondition", method = RequestMethod.GET)
    public @ResponseBody
    List<CatStockDTO> findByCondition(@RequestParam("status") String status) {
        if (currentUser != null && currentUser.getStockPermission().equals("0")) {
            this.lstStock = FunctionUtils.getListStock(catStockService, selectedCustomer);
        } else {
            this.lstStock = FunctionUtils.getListStock(stockService, currentUser);
        }
        for (CatStockDTO i : lstStock) {
            i.setStatusName(mapAppStatus.get(i.getStatus()));
            mapStock.put(i.getCode(), i.getId());
        }
        return lstStock.stream().filter(i -> i.getStatus().equalsIgnoreCase(status)).collect(Collectors.toList());
    }

    @RequestMapping(value = "/getCells", method = RequestMethod.GET)
    public @ResponseBody
    List<CatStockCellDTO> getCells(@RequestParam("stockId") String stockId) {
        List<Condition> lstCon = Lists.newArrayList();
        lstCon.add(new Condition("stockId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, stockId));
        lstCon.add(new Condition("id", Constants.SQL_OPERATOR.ORDER, "desc"));

        List<CatStockCellDTO> lstCells = catStockCellService.findByCondition(lstCon);
        for(CatStockCellDTO catStockCellDTO : lstCells){
            catStockCellDTO.setMaxVolumeValue(FunctionUtils.formatNumber(catStockCellDTO.getMaxVolume()));
            catStockCellDTO.setMaxWeightValue(FunctionUtils.formatNumber(catStockCellDTO.getMaxWeight()));
            if("2".equals(catStockCellDTO.getManyCodes())){
                catStockCellDTO.setManyCodesValue("Không");
            }else{
                catStockCellDTO.setManyCodesValue("Có");
            }
        }
        return lstCells;
    }

    @RequestMapping(value = "/addCell", method = RequestMethod.POST)
    public @ResponseBody
    String addCell(HttpServletRequest request, @RequestParam("stockId") String stockId, @RequestParam("code") String code,
                   @RequestParam("maxWeight") String maxWeight,@RequestParam("maxVolume") String maxVolume,@RequestParam("manyCodes") String manyCodes) {
        CatStockCellDTO cell = new CatStockCellDTO();
        cell.setStockId(stockId);
        cell.setCode(code.toUpperCase());
        cell.setMaxWeight(FunctionUtils.unformatFloat(maxWeight));
        cell.setMaxVolume(FunctionUtils.unformatFloat(maxVolume));
        cell.setManyCodes(manyCodes);

        ResponseObject response = catStockCellService.add(cell);
        if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
            SessionUtils.setCellModified(request);
            return "1|Thêm mới thành công";
        } else {
            return "0|Thông tin đã có trên hệ thống";
        }
    }

    @RequestMapping(value = "/deleteCell", method = RequestMethod.POST)
    public @ResponseBody
    String deleteCell(HttpServletRequest request, @RequestParam("id") String id) {
        ResponseObject response = catStockCellService.delete(Long.parseLong(id));
        if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
            SessionUtils.setCellModified(request);
            return "1|Xóa thành công";
        } else {
            return "0|Xóa không thành công";
        }
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public @ResponseBody
    String add(CatStockDTO catStockDTO, HttpServletRequest request) {
        catStockDTO.setStatus("1");
        catStockDTO.setCustId(this.selectedCustomer.getId());
        catStockDTO.setCode(catStockDTO.getCode().toUpperCase());
        ResponseObject response = catStockService.add(catStockDTO);
        if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
            log.info("Add: " + catStockDTO.toString() + " SUCCESS");
            String newStockId = response.getKey();
            //add map for current user
            String[] stockids = {newStockId};
            List<MapUserStockDTO> lstMapUserStock = new ArrayList<>();
            for (int i = 0; i < stockids.length; i++) {
                lstMapUserStock.add(new MapUserStockDTO(null, currentUser.getId(), stockids[i]));
            }
            ResponseObject addUserStock = mapUserStockServiceImpl.addList(lstMapUserStock);
            if (!Responses.SUCCESS.getName().equalsIgnoreCase(addUserStock.getStatusCode())) {
                log.info("Add " + stockids + " to for user successfully....");
            }
            //
            SessionUtils.setStockModified(request);
            return "1|Thêm mới thành công";
        } else if (Responses.ERROR_CONSTRAINT.getName().equalsIgnoreCase(response.getStatusName())) {
            log.info("Add: " + catStockDTO.toString() + " ERROR");
            return "0|Thông tin đã có trên hệ thống";
        } else {
            return "0|Lỗi hệ thống";
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody
    String update(CatStockDTO catStockDTO, HttpServletRequest request) {
        log.info("Update cat_stock info: " + catStockDTO.toString());
        catStockDTO.setCustId(selectedCustomer.getId());
        catStockDTO.setCode(catStockDTO.getCode().toUpperCase());
        if ("on".equalsIgnoreCase(catStockDTO.getStatus())) {
            catStockDTO.setStatus("1");
        } else {
            catStockDTO.setStatus("0");
        }
        ResponseObject response = catStockService.update(catStockDTO);
        if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
            log.info("SUCCESS");
            //
            SessionUtils.setStockModified(request);
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
                return "0|Xoá không thành công: kho đã được sử dụng";
            }
            //
            Long idL = Long.parseLong(id);
            //
            if (isDeleteStockAvailable(code)) {
                catStockService.delete(idL);
                SessionUtils.setStockModified(request);
                return "1|Xoá thành công";
            }
            //
            CatStockDTO deleteStock = (CatStockDTO) catStockService.findById(idL);
            deleteStock.setStatus(Constants.STATUS.DELETED);
            ResponseObject response = catStockService.update(deleteStock);
            if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
                SessionUtils.setStockModified(request);
                return "1|Xoá thành công";
            } else {
                return "0|Xoá không thành công";
            }
        } catch (NumberFormatException e) {
            return "0|Xoá không thành công";
        }
    }

    public boolean isUsed(String id) {
        List<Condition> lstCon = Lists.newArrayList();
        lstCon.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, selectedCustomer.getId()));
        lstCon.add(new Condition("stockId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, id));
        Long count = mjrStockGoodsTotalService.countByCondition(lstCon);
        return count != null && count > 0;
    }

    public boolean isDeleteStockAvailable(String code) {
        List<Condition> lstCon = Lists.newArrayList();
        lstCon.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, selectedCustomer.getId()));
        lstCon.add(new Condition("code", Constants.SQL_OPERATOR.EQUAL, code));
        lstCon.add(new Condition("status", Constants.SQL_PRO_TYPE.BYTE, Constants.SQL_OPERATOR.EQUAL, Constants.STATUS.DELETED));
        return !DataUtil.isListNullOrEmpty(catStockService.findByCondition(lstCon));
    }

    @RequestMapping(value = "/getTemplateFile")
    public void getTemplateFile(HttpServletResponse response) {
        FunctionUtils.loadFileToClient(response, profileConfig.getTemplateURL() + Constants.FILE_RESOURCE.IMPORT_CELL_TEMPLATE);
    }
    @RequestMapping(value = "/getErrorImportFile")
    public void getErrorImportFile(HttpServletRequest request, HttpServletResponse response) {
        String fileResource = profileConfig.getTempURL() + request.getSession().getAttribute("file_cell_import_error");
        FunctionUtils.loadFileToClient(response, fileResource);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public List<CatStockCellDTO> uploadFile(MultipartHttpServletRequest request) {
        //1. get the files from the request object
        Iterator<String> itr = request.getFileNames();
        MultipartFile mpf = request.getFile(itr.next());
        //
        ImportFileResultDTO importFileResult = FunctionUtils.getListCellImportFromFile(mpf, mapStock);
        if (!importFileResult.isValid()) {
            //save error file
            String prefixFileName = selectedCustomer.getId() + "_" + currentUser.getCode();
            String fileName = FunctionUtils.exportExcelImportCellError(importFileResult.getLstCell(), prefixFileName, profileConfig);
            //save in session
            request.getSession().setAttribute("file_cell_import_error", fileName);
            return null;
        }
        return importFileResult.getLstCell();
    }
    @RequestMapping(value = "/saveListCell", method = RequestMethod.POST)
    @ResponseBody
    public ResponseObject saveListCell(@RequestBody ListStockCellDTO listStockCellDTO, HttpServletRequest request) {
        log.info("------------------------------------------------------------");
        List<CatStockCellDTO> lstStockCell = listStockCellDTO.getLstStockCell();
        log.info(currentUser.getCode() + " importing stock cell with: " + lstStockCell.size() + " items.");
        List<CatStockCellDTO> lstError = Lists.newArrayList();
        //
        ResponseObject insertResponse = new ResponseObject();
        String sysdate = appParamsService.getSysDate();
        int successCount = 0;
        for (CatStockCellDTO item : lstStockCell) {
            insertResponse = catStockCellService.add(item);
            if (!Responses.SUCCESS.getName().equalsIgnoreCase(insertResponse.getStatusCode())) {
                item.setErrorInfo("Mã vị trí đã được khai báo trên hệ thống");
                lstError.add(item);
            } else {
                successCount++;
            }
        }
        //
        if (lstError.size() > 0) {
            //export file error
            String prefixFileName = selectedCustomer.getId() + "_" + currentUser.getCode();
            String fileName = FunctionUtils.exportExcelImportCellError(lstError, prefixFileName, profileConfig);
            //save in session
            request.getSession().setAttribute("file_cell_save_error", fileName);
            //
            insertResponse.setStatusCode("SUCCESS_WITH_ERROR");
            insertResponse.setTotal(lstStockCell.size() + "");
            insertResponse.setSuccess(successCount + "");
        } else {
            insertResponse.setStatusCode("SUCCESS");
            insertResponse.setSuccess(lstStockCell.size() + "");
        }
        //
        SessionUtils.setPartnerModified(request);
        return insertResponse;
    }
    @RequestMapping(value = "/getErrorSaveCell")
    public void getErrorSaveCell(HttpServletRequest request, HttpServletResponse response) {
        String fileResource = profileConfig.getTempURL() + request.getSession().getAttribute("file_cell_save_error");
        FunctionUtils.loadFileToClient(response, fileResource);
    }
}
