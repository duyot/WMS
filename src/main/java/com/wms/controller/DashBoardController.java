package com.wms.controller;

import com.google.common.collect.Lists;
import com.wms.base.BaseController;
import com.wms.dto.ChartDTO;
import com.wms.services.interfaces.StatisticService;
import com.wms.utils.DataUtil;
import com.wms.utils.FunctionUtils;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by duyot on 5/18/2017.
 */
@Controller
@RequestMapping("/workspace/dashboard_ctr")
@Scope("session")
public class DashBoardController extends BaseController {
    @Autowired
    StatisticService statisticService;

    //------------------------------------------------------------------------------------------------------------------
    @RequestMapping()
    public String home(HttpServletRequest request) {
        Object isLogin = request.getSession().getAttribute("isLogin");
        if (isLogin == null) {
            return "index";
        }
        return "workspace/dashboard";
    }

    @RequestMapping(value = "/getRevenue/{type}", method = RequestMethod.GET)
    public @ResponseBody
    List<ChartDTO> getRevenue(@PathVariable("type") String type) {
        return statisticService.getRevenue(selectedCustomer.getId(), type);
    }

    @RequestMapping(value = "/getTopGoods/{type}", method = RequestMethod.GET)
    public @ResponseBody
    List<ChartDTO> getTopGoods(@PathVariable("type") String type) {
        List<ChartDTO> lstTopGoods = statisticService.getTopGoods(selectedCustomer.getId(), type);
        if (!DataUtil.isListNullOrEmpty(lstTopGoods)) {
            for (ChartDTO i : lstTopGoods) {
                setGoodsNameForTopGoods(i);
            }
        } else {
            lstTopGoods = Lists.newArrayList();
        }
        return lstTopGoods;
    }

    @RequestMapping(value = "/getKPIStorage/{type}", method = RequestMethod.GET)
    public @ResponseBody
    List<ChartDTO> getKPIStorage(@PathVariable("type") String type) {
        return statisticService.getKPIStorage(selectedCustomer.getId(), type, currentUser.getId());
    }

    @RequestMapping(value = "/getTransaction/{type}", method = RequestMethod.GET)
    public @ResponseBody
    List<ChartDTO> getTransaction(@PathVariable("type") String type) {
        return statisticService.getTransaction(selectedCustomer.getId(), type, currentUser.getId());
    }

    //------------------------------------------------------------------------------------------------------------------
    private void setGoodsNameForTopGoods(ChartDTO item) {
        item.setName(FunctionUtils.getMapValue(mapGoodsIdGoods, item.getName()));
    }


}
