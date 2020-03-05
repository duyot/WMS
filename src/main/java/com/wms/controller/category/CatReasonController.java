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
@RequestMapping("/workspace/cat_reason_ctr")
@Scope("session")
public class CatReasonController extends BaseController {
    @Autowired
    BaseService catReasonService;

    private Logger log = LoggerFactory.getLogger(CatReasonController.class);

    //------------------------------------------------------------------------------------------------------------------
    @RequestMapping()
    public String home(Model model) {
        model.addAttribute("menuName", "menu.catreason");
        return "category/cat_reason";
    }

    @RequestMapping(value = "/findByCondition", method = RequestMethod.GET)
    public @ResponseBody
    List<CatReasonDTO> findByCondition(@RequestParam("status") String status) {
        List<Condition> lstCon = Lists.newArrayList();
        lstCon.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, selectedCustomer.getId()));

        if (!DataUtil.isStringNullOrEmpty(status) && !status.equals(Constants.STATS_ALL)) {
            lstCon.add(new Condition("status", Constants.SQL_PRO_TYPE.BYTE, Constants.SQL_OPERATOR.EQUAL, status));
        }
        lstCon.add(new Condition("id", Constants.SQL_OPERATOR.ORDER, "desc"));

        List<CatReasonDTO> lstCatGoods = catReasonService.findByCondition(lstCon);

        for (CatReasonDTO i : lstCatGoods) {
            i.setCustName(selectedCustomer.getName());
            i.setStatusName(mapAppStatus.get(i.getStatus()));
            if("1".equals(i.getType())){
                i.setTypeName("Nhập kho");
            }else{
                i.setTypeName("Xuất kho");
            }
        }
        return lstCatGoods;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public @ResponseBody
    String add(CatReasonDTO catReasonDTO, HttpServletRequest request) {
        catReasonDTO.setStatus("1");
        catReasonDTO.setCustId(this.selectedCustomer.getId());
        ResponseObject response = catReasonService.add(catReasonDTO);
        if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
            log.info("Add: " + catReasonDTO.toString() + " SUCCESS");
            //SessionUtils.setGoodsGroupModified(request);
            return "1|Thêm mới thành công";
        } else if (Responses.ERROR_CONSTRAINT.getName().equalsIgnoreCase(response.getStatusName())) {
            log.info("Add: " + catReasonDTO.toString() + " ERROR");
            return "0|Thông tin đã có trên hệ thống";
        } else {
            log.info("Add: " + catReasonDTO.toString() + " ERROR");
            return "0|Thêm mới không thành công";
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody
    String update(CatReasonDTO catReasonDTO, HttpServletRequest request) {
        log.info("Update cat_reason info: " + catReasonDTO.toString());
        catReasonDTO.setCustId(this.selectedCustomer.getId());
        if ("on".equalsIgnoreCase(catReasonDTO.getStatus())) {
            catReasonDTO.setStatus("1");
        } else {
            catReasonDTO.setStatus("0");
        }
        ResponseObject response = catReasonService.update(catReasonDTO);
        if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
            log.info("SUCCESS");
            //SessionUtils.setGoodsGroupModified(request);
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
            CatReasonDTO deleteObject = (CatReasonDTO) catReasonService.findById(idL);
            deleteObject.setStatus(Constants.STATUS.DELETED);
            ResponseObject response = catReasonService.update(deleteObject);
            if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
                //SessionUtils.setGoodsGroupModified(request);
                return "1|Xoá thành công";
            } else {
                return "0|Xoá không thành công";
            }
        } catch (NumberFormatException e) {
            return "0|Xoá không thành công lỗi convert long";
        }
    }

}
