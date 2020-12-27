package com.wms.controller.category;

import com.google.common.collect.Lists;
import com.wms.base.BaseCommonController;
import com.wms.base.BaseController;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.CatPartnerDTO;
import com.wms.dto.CatUserDTO;
import com.wms.dto.Condition;
import com.wms.dto.ResponseObject;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.CatUserService;
import com.wms.utils.DataUtil;

import java.util.HashMap;
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
}
