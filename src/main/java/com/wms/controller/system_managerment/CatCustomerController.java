package com.wms.controller.system_managerment;

import com.google.common.collect.Lists;
import com.wms.base.BaseCommonController;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.CatCustomerDTO;
import com.wms.dto.Condition;
import com.wms.dto.ResponseObject;
import com.wms.services.interfaces.BaseService;
import com.wms.utils.DataUtil;
import java.util.List;
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

/**
 * Created by duyot on 12/6/2016.
 */
@Controller
@RequestMapping("/workspace/cat_customer_ctr")
@Scope("session")
public class CatCustomerController extends BaseCommonController {
    @Autowired
    BaseService catCustServicesImpl;
    @Autowired
    BaseService mapUserPartnerServiceImpl;
    private Logger log = LoggerFactory.getLogger(CatCustomerController.class);

    //------------------------------------------------------------------------------------------------------------------
    @RequestMapping()
    public String home(Model model) {
        model.addAttribute("menuName", "menu.catcustomer");
        return "system_managerment/cat_customer";
    }

    @RequestMapping(value = "/findByCondition", method = RequestMethod.GET)
    public @ResponseBody
    List<CatCustomerDTO> findByCondition(@RequestParam("status") String status, @RequestParam("keyword") String keyword) {
        List<Condition> lstCon = Lists.newArrayList();
        if (!DataUtil.isStringNullOrEmpty(status) && !status.equals(Constants.STATS_ALL)) {
            lstCon.add(new Condition("status", Constants.SQL_PRO_TYPE.BYTE, Constants.SQL_OPERATOR.EQUAL, status));
        }
        /*if(!DataUtil.isStringNullOrEmpty(keyword) && !status.equals(Constants.STATS_ALL)){
            lstCon.add(new Condition("name", Constants.SQL_OPERATOR.LIKE,keyword));
            lstCon.add(new Condition("code", Constants.SQL_OPERATOR.LIKE,keyword));
        }*/
        lstCon.add(new Condition("id", Constants.SQL_OPERATOR.ORDER, "desc"));

        List<CatCustomerDTO> lstCustomer = catCustServicesImpl.findByCondition(lstCon);

        for (CatCustomerDTO i : lstCustomer) {
            i.setCustName(selectedCustomer.getName());
            i.setStatusName(mapAppStatus.get(i.getStatus()));
        }

        return lstCustomer;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody
    String update(CatCustomerDTO catCustomerDTO, HttpServletRequest request) {
        log.info("Update menu info: " + catCustomerDTO.toString());

        if ("on".equalsIgnoreCase(catCustomerDTO.getStatus())) {
            catCustomerDTO.setStatus("1");
        } else {
            catCustomerDTO.setStatus("0");
        }
        ResponseObject response = catCustServicesImpl.update(catCustomerDTO);
        if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
            log.info("SUCCESS");
            return "1|Cập nhật thành công";
        } else if (Responses.ERROR_CONSTRAINT.getName().equalsIgnoreCase(response.getStatusName())) {
            log.info("ERROR");
            return "0|Thông tin đã có trên hệ thống";
        } else {
            log.info("ERROR");
            return "0|Cập nhật không thành công";
        }

    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public @ResponseBody
    String add(CatCustomerDTO catCustomerDTO, HttpServletRequest request) {
        catCustomerDTO.setStatus("1");
        ResponseObject response = catCustServicesImpl.add(catCustomerDTO);
        if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {

            return "1|Thêm mới thành công";
        } else if (Responses.ERROR_CONSTRAINT.getName().equalsIgnoreCase(response.getStatusName())) {
            return "0|Thông tin đã có trên hệ thống";
        } else {
            return "0|Thêm mới không thành công";
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public @ResponseBody
    String delete(@RequestParam("id") String id, HttpServletRequest request) {
        try {
            Long idL = Long.parseLong(id);
            CatCustomerDTO deleteObject = (CatCustomerDTO) catCustServicesImpl.findById(idL);
            deleteObject.setStatus(Constants.STATUS.IN_ACTIVE);
            ResponseObject response = catCustServicesImpl.update(deleteObject);

            if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
                request.getSession().setAttribute("isCatPartnerModified", true);
                return "1|Xoá thành công";
            } else {
                return "0|Xoá không thành công";
            }
        } catch (NumberFormatException e) {
            return "0|Xoá không thành công lỗi convert long";
        }
    }

}
