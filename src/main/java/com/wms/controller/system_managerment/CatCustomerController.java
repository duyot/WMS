package com.wms.controller.system_managerment;

import com.google.common.collect.Lists;
import com.wms.base.BaseCommonController;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.CatCustomerDTO;
import com.wms.dto.CatPartnerDTO;
import com.wms.dto.Condition;
import com.wms.dto.ResponseObject;
import com.wms.services.interfaces.BaseService;
import com.wms.utils.DataUtil;
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
import java.util.List;

/**
 * Created by duyot on 12/6/2016.
 */
@Controller
@RequestMapping("/workspace/cat_customer_ctr")
@Scope("session")
public class CatCustomerController extends BaseCommonController{
    private Logger log = LoggerFactory.getLogger(CatCustomerController.class);

    @Autowired
    BaseService catCustServicesImpl;

    @Autowired
    BaseService mapUserPartnerServiceImpl;


    @RequestMapping()
    public String home(Model model){
        model.addAttribute("menuName","menu.catcustomer");
        return "system_managerment/cat_customer";
    }

    @RequestMapping(value = "/findByCondition",method = RequestMethod.GET)
    public  @ResponseBody List<CatCustomerDTO> findByCondition(@RequestParam("status")String status){
        List<Condition> lstCon = Lists.newArrayList();
        if(!DataUtil.isStringNullOrEmpty(status) && !status.equals(Constants.STATS_ALL)){
            lstCon.add(new Condition("status", Constants.SQL_PRO_TYPE.BYTE, Constants.SQL_OPERATOR.EQUAL,status));
        }
        lstCon.add(new Condition("id",Constants.SQL_OPERATOR.ORDER,"desc"));

        List<CatCustomerDTO> lstCustomer = catCustServicesImpl.findByCondition(lstCon);

        for(CatCustomerDTO i: lstCustomer){
            i.setCustName(selectedCustomer.getName());
            i.setStatusName(mapAppStatus.get(i.getStatus()));
        }

        return lstCustomer;
    }

}
