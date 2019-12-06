package com.wms.controller.category;

import com.google.common.collect.Lists;
import com.wms.base.BaseCommonController;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.AppParamsDTO;
import com.wms.dto.Condition;
import com.wms.dto.ResponseObject;
import com.wms.services.interfaces.BaseService;
import com.wms.utils.DataUtil;
import java.util.List;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Created by doanlv4 on 28/03/2017.
 */
@Controller
@RequestMapping("/workspace/app_params_ctr")
@Scope("session")
public class AppParamsController extends BaseCommonController {
    Logger log = LoggerFactory.getLogger(CatPartnerController.class);

    @Autowired
    BaseService appParamsService;

    @RequestMapping()
    public String home(Model model) {
        model.addAttribute("menuName", "menu.appParams");
        return "category/app_params";
    }

    @RequestMapping(value = "/findByCondition", method = RequestMethod.GET)
    public @ResponseBody
    List<AppParamsDTO> findByCondition(@RequestParam("status") String status) {
        List<Condition> lstCon = Lists.newArrayList();

        if (!DataUtil.isStringNullOrEmpty(status) && !status.equals(Constants.STATS_ALL)) {
            lstCon.add(new Condition("status", Constants.SQL_PRO_TYPE.BYTE, Constants.SQL_OPERATOR.EQUAL, status));
        }

        lstCon.add(new Condition("id", Constants.SQL_OPERATOR.ORDER, "desc"));

        List<AppParamsDTO> lstAppParams = appParamsService.findByCondition(lstCon);

        for (AppParamsDTO i : lstAppParams) {
            i.setName(i.getName());
            i.setStatusName(mapAppStatus.get(i.getStatus()));
        }

        return lstAppParams;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String add(AppParamsDTO appParamsDTO, RedirectAttributes redirectAttributes) {
        appParamsDTO.setStatus("1");
        ResponseObject response = appParamsService.add(appParamsDTO);
        if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusName())) {
            redirectAttributes.addFlashAttribute("actionInfo", "result.add.success");
            redirectAttributes.addFlashAttribute("successStyle", Constants.SUCCES_COLOR);
            log.info("Add: " + appParamsDTO.toString() + " SUCCESS");
        } else if (Responses.ERROR_CONSTRAINT.getName().equalsIgnoreCase(response.getStatusName())) {
            log.info("Add: " + appParamsDTO.toString() + " ERROR");
            redirectAttributes.addFlashAttribute("actionInfo", "result.fail.constraint");
        } else {
            log.info("Add: " + appParamsDTO.toString() + " ERROR");
            redirectAttributes.addFlashAttribute("actionInfo", "result.fail.contact");
        }

        return "redirect:/workspace/app_params_ctr";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update(AppParamsDTO appParamsDTO, RedirectAttributes redirectAttributes) {
        log.info("Update cat_partner info: " + appParamsDTO.toString());

        if ("on".equalsIgnoreCase(appParamsDTO.getStatus())) {
            appParamsDTO.setStatus("1");
        } else {
            appParamsDTO.setStatus("0");
        }
        ResponseObject response = appParamsService.update(appParamsDTO);
        if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
            log.info("SUCCESS");
            redirectAttributes.addFlashAttribute("actionInfo", "result.update.success");
            redirectAttributes.addFlashAttribute("successStyle", Constants.SUCCES_COLOR);
        } else if (Responses.ERROR_CONSTRAINT.getName().equalsIgnoreCase(response.getStatusCode())) {
            log.info("ERROR");
            redirectAttributes.addFlashAttribute("actionInfo", "result.fail.constraint");
        } else {
            log.info("ERROR");
            redirectAttributes.addFlashAttribute("actionInfo", "result.fail.contact");
        }
        return "redirect:/workspace/app_params_ctr";
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public @ResponseBody
    String delete(@RequestParam("id") String id) {
        try {
            Long idL = Long.parseLong(id);
            ResponseObject response = appParamsService.delete(idL);
            if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusName())) {
                return "1|Xoá thành công";
            } else {
                return "0|Xoá không thành công";
            }
        } catch (NumberFormatException e) {
            return "0|Xoá không thành công lỗi convert long";
        }
    }
}
