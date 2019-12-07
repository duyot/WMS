package com.wms.controller.category;

import com.google.common.collect.Lists;
import com.wms.base.BaseCommonController;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.CatGoodsGroupDTO;
import com.wms.dto.Condition;
import com.wms.dto.ResponseObject;
import com.wms.services.interfaces.BaseService;
import com.wms.utils.DataUtil;
import com.wms.utils.SessionUtils;
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
@RequestMapping("/workspace/cat_goods_group_ctr")
@Scope("session")
public class CatGoodsGroupController extends BaseCommonController {
    @Autowired
    BaseService catGoodsGroupService;
    @Autowired
    BaseService catGoodsService;
    private Logger log = LoggerFactory.getLogger(CatGoodsGroupController.class);
    //------------------------------------------------------------------------------------------------------------------
    @RequestMapping()
    public String home(Model model) {
        model.addAttribute("menuName", "menu.catgoodsgroup");
        return "category/cat_goods_group";
    }

    @RequestMapping(value = "/findByCondition", method = RequestMethod.GET)
    public @ResponseBody
    List<CatGoodsGroupDTO> findByCondition(@RequestParam("status") String status) {
        List<Condition> lstCon = Lists.newArrayList();
        lstCon.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, selectedCustomer.getId()));

        if (!DataUtil.isStringNullOrEmpty(status) && !status.equals(Constants.STATS_ALL)) {
            lstCon.add(new Condition("status", Constants.SQL_PRO_TYPE.BYTE, Constants.SQL_OPERATOR.EQUAL, status));
        }
        lstCon.add(new Condition("id", Constants.SQL_OPERATOR.ORDER, "desc"));

        List<CatGoodsGroupDTO> lstCatGoods = catGoodsGroupService.findByCondition(lstCon);

        for (CatGoodsGroupDTO i : lstCatGoods) {
            i.setName(i.getName());
            i.setCustName(selectedCustomer.getName());
            i.setStatusName(mapAppStatus.get(i.getStatus()));
        }

        return lstCatGoods;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public @ResponseBody
    String add(CatGoodsGroupDTO catGoodsGroup, HttpServletRequest request) {
        catGoodsGroup.setStatus("1");
        catGoodsGroup.setCustId(this.selectedCustomer.getId());
        ResponseObject response = catGoodsGroupService.add(catGoodsGroup);
        if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
            log.info("Add: " + catGoodsGroup.toString() + " SUCCESS");
            SessionUtils.setGoodsGroupModified(request);
            return "1|Thêm mới thành công";
        } else if (Responses.ERROR_CONSTRAINT.getName().equalsIgnoreCase(response.getStatusName())) {
            log.info("Add: " + catGoodsGroup.toString() + " ERROR");
            return "0|Thông tin đã có trên hệ thống";
        } else {
            log.info("Add: " + catGoodsGroup.toString() + " ERROR");
            return "0|Thêm mới không thành công";
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody
    String update(CatGoodsGroupDTO catGoodsGroup, HttpServletRequest request) {
        log.info("Update cat_goods_group info: " + catGoodsGroup.toString());
        catGoodsGroup.setCustId(this.selectedCustomer.getId());
        if ("on".equalsIgnoreCase(catGoodsGroup.getStatus())) {
            catGoodsGroup.setStatus("1");
        } else {
            catGoodsGroup.setStatus("0");
        }
        ResponseObject response = catGoodsGroupService.update(catGoodsGroup);
        if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
            log.info("SUCCESS");
            SessionUtils.setGoodsGroupModified(request);
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
            if (isUsedByGood(id)) {
                return "0|Xoá không thành công: nhóm đã được sử dụng";
            }
            Long idL = Long.parseLong(id);
            if (isDeleteGoodsGroupAvailable(code)) {
                catGoodsGroupService.delete(idL);
                return "1|Xoá thành công";
            }

            CatGoodsGroupDTO deleteObject = (CatGoodsGroupDTO) catGoodsGroupService.findById(idL);
            deleteObject.setStatus(Constants.STATUS.DELETED);
            ResponseObject response = catGoodsGroupService.update(deleteObject);
            if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
                SessionUtils.setGoodsGroupModified(request);
                return "1|Xoá thành công";
            } else {
                return "0|Xoá không thành công";
            }
        } catch (NumberFormatException e) {
            return "0|Xoá không thành công lỗi convert long";
        }
    }

    public boolean isDeleteGoodsGroupAvailable(String code) {
        List<Condition> lstCon = Lists.newArrayList();
        lstCon.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, selectedCustomer.getId()));
        lstCon.add(new Condition("name", Constants.SQL_OPERATOR.EQUAL, code));
        lstCon.add(new Condition("status", Constants.SQL_PRO_TYPE.BYTE, Constants.SQL_OPERATOR.EQUAL, Constants.STATUS.DELETED));
        return !DataUtil.isListNullOrEmpty(catGoodsGroupService.findByCondition(lstCon));
    }

    public boolean isUsedByGood(String id) {
        List<Condition> lstCon = Lists.newArrayList();
        lstCon.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, selectedCustomer.getId()));
        lstCon.add(new Condition("goodsGroupId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, id));
        lstCon.add(new Condition("status", Constants.SQL_PRO_TYPE.BYTE, Constants.SQL_OPERATOR.EQUAL, Constants.STATUS.ACTIVE));
        Long count = catGoodsService.countByCondition(lstCon);
        return count != null && count > 0;
    }
}
