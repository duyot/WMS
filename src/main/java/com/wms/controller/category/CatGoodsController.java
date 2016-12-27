package com.wms.controller.category;

import com.google.common.collect.Lists;
import com.wms.constants.Constants;
import com.wms.dto.CatGoodsGroupDTO;
import com.wms.dto.Condition;
import com.wms.dto.CustomerDTO;
import com.wms.dto.GoodsDTO;
import com.wms.services.interfaces.BaseService;
import com.wms.utils.DataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by duyot on 12/9/2016.
 */
@Controller
@RequestMapping("/workspace/goods_ctr")
public class CatGoodsController {
    Logger log = LoggerFactory.getLogger(CatGoodsController.class);

    public Map<String, String> mapCustomer = new HashMap<>();

    @Autowired
    BaseService catGoodsGroupService;

    @Autowired
    BaseService goodsService;

    @ModelAttribute("lstCatGoodsGroup")
    public List<CatGoodsGroupDTO> lstCatGoodsGroup(HttpServletRequest request){
        List<CustomerDTO> lstCustomer = (List<CustomerDTO>) request.getSession().getAttribute("lstCustomers");
        if(DataUtil.isListNullOrEmpty(lstCustomer) || lstCustomer.size() >1){
            return Lists.newArrayList();
        }

        CustomerDTO customer = lstCustomer.get(0);
        List<Condition> lstCon = Lists.newArrayList();
        lstCon.add(new Condition("custId", Constants.SQL_OPERATOR.EQUAL,customer.getId()));
        lstCon.add(new Condition("status", Constants.SQL_OPERATOR.EQUAL,Constants.STATUS.ACTIVE));
        lstCon.add(new Condition("id",Constants.SQL_OPERATOR.ORDER,"desc"));
        return catGoodsGroupService.findByCondition(lstCon);
    }

    @ModelAttribute("lstCustomers")
    public List<CustomerDTO> lstCustomer(HttpServletRequest request){
        List<CustomerDTO> lstCustomer = (List<CustomerDTO>) request.getSession().getAttribute("lstCustomers");
        mapCustomer = lstCustomer.stream().collect(Collectors.toMap(CustomerDTO::getId, CustomerDTO::getName));
        return (List<CustomerDTO>) request.getSession().getAttribute("lstCustomers");
    }

    @RequestMapping()
    public String home(Model model){
        model.addAttribute("menuName","Danh mục hàng hóa");
        return "category/cat_goods";
    }

    @RequestMapping(value = "/findByCondition",method = RequestMethod.GET)
    public  @ResponseBody
    List<GoodsDTO> findByCondition(@RequestParam("customerId")String custId,
                                   @RequestParam("status")String status,@RequestParam("goodsGroupId")String goodsGroupId){
        List<Condition> lstCon = Lists.newArrayList();

        if(!DataUtil.isStringNullOrEmpty(custId) && !custId.equals(Constants.STATS_ALL)){
            lstCon.add(new Condition("custId",Constants.SQL_PRO_TYPE.LONG ,Constants.SQL_OPERATOR.EQUAL,custId));
        }

        if(!DataUtil.isStringNullOrEmpty(status) && !status.equals(Constants.STATS_ALL)){
            lstCon.add(new Condition("status", Constants.SQL_OPERATOR.EQUAL,status));
        }

        if(!DataUtil.isStringNullOrEmpty(goodsGroupId) && !goodsGroupId.equals(Constants.STATS_ALL)){
            lstCon.add(new Condition("goodsGroupId",Constants.SQL_PRO_TYPE.LONG ,Constants.SQL_OPERATOR.EQUAL,goodsGroupId));
        }

        lstCon.add(new Condition("id",Constants.SQL_OPERATOR.ORDER,"desc"));

        List<GoodsDTO> lstGoods = goodsService.findByCondition(lstCon);

        for(GoodsDTO i: lstGoods){
            i.setCustName(mapCustomer.get(i.getCustId()));
        }

        return lstGoods;
    }

}
