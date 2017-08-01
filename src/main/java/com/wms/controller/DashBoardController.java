package com.wms.controller;

import com.wms.base.BaseCommonController;
import com.wms.base.BaseController;
import com.wms.dto.AuthTokenInfo;
import com.wms.dto.CatCustomerDTO;
import com.wms.dto.ChartDTO;
import com.wms.services.interfaces.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by duyot on 5/18/2017.
 */
@Controller
@RequestMapping("/workspace/dashboard_ctr")
@Scope("session")
public class DashBoardController extends BaseController{
    @Autowired
    StatisticService statisticService;

    private AuthTokenInfo tokenInfo;
    public CatCustomerDTO selectedCustomer;

    @ModelAttribute("tokenInfo")
    public void setTokenInfo(HttpServletRequest request){
        if(tokenInfo == null){
            this.tokenInfo =  (AuthTokenInfo) request.getSession().getAttribute("tokenInfo");
        }
    }
    //
    @ModelAttribute("selectedCustomer")
    public void setSelectedCustomer(HttpServletRequest request){
        if(selectedCustomer == null){
            this.selectedCustomer =  (CatCustomerDTO) request.getSession().getAttribute("selectedCustomer");
        }
    }

    @RequestMapping()
    public String home(HttpServletRequest request){
        Object isLogin = request.getSession().getAttribute("isLogin");
        if(isLogin == null){
            return "index";
        }
        return "workspace/dashboard";
    }

    //select customer
    @RequestMapping(value = "/getRevenue/{type}",method = RequestMethod.GET)
    public @ResponseBody List<ChartDTO> getRevenue(@PathVariable("type") String type){
        return statisticService.getRevenue(selectedCustomer.getId(),type,tokenInfo);
    }

    //select customer
    @RequestMapping(value = "/getTopGoods/{type}",method = RequestMethod.GET)
    public @ResponseBody List<ChartDTO> getTopGoods(@PathVariable("type") String type){
        return statisticService.getTopGoods(selectedCustomer.getId(),type,tokenInfo,mapGoodsIdGoods);
    }


}
