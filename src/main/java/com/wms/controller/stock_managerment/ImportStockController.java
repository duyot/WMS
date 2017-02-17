package com.wms.controller.stock_managerment;

import com.google.common.collect.Lists;
import com.wms.constants.Constants;
import com.wms.controller.WorkSpaceController;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.StockManagementService;
import com.wms.utils.FunctionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.List;

/**
 * Created by duyot on 2/15/2017.
 */
@Controller
@RequestMapping("/workspace/stock_management")
public class ImportStockController {
    Logger log = LoggerFactory.getLogger(WorkSpaceController.class);
    //
    @Autowired
    StockManagementService stockManagementService;
    @Autowired
    BaseService catStockService;
    //
    private CatCustomerDTO selectedCustomer;
    private AuthTokenInfo tokenInfo;
    private CatUserDTO currentUser;
    //
    @ModelAttribute("tokenInfo")
    public void setTokenInfo(HttpServletRequest request){
        this.tokenInfo =  (AuthTokenInfo) request.getSession().getAttribute("tokenInfo");
    }

    @ModelAttribute("selectedCustomer")
    public void setSelectedCustomer(HttpServletRequest request){
        this.selectedCustomer =  (CatCustomerDTO) request.getSession().getAttribute("selectedCustomer");
    }

    @ModelAttribute("currentUser")
    public void setCurrentUser(HttpServletRequest request){
        this.currentUser =  (CatUserDTO) request.getSession().getAttribute("user");
    }

    @ModelAttribute("lstStock")
    public List<CatStockDTO> getListStock(HttpServletRequest request){
        if(selectedCustomer == null){
            this.selectedCustomer =  (CatCustomerDTO) request.getSession().getAttribute("selectedCustomer");
        }
        List<Condition> lstCondition = Lists.newArrayList();
        lstCondition.add(new Condition("custId",Constants.SQL_PRO_TYPE.LONG,Constants.SQL_OPERATOR.EQUAL,selectedCustomer.getId()));
        lstCondition.add(new Condition("status",Constants.SQL_OPERATOR.EQUAL,Constants.STATUS.ACTIVE));
        return catStockService.findByCondition(lstCondition,tokenInfo);
    }

    @RequestMapping()
    public String home(Model model){
        model.addAttribute("menuName","menu.importstock");
        return "stock_management/import_stock";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public List<MjrStockTransDetailDTO> uploadFile(MultipartHttpServletRequest request, HttpServletResponse response) {
        //1. get the files from the request object
        Iterator<String> itr =  request.getFileNames();
        MultipartFile mpf = request.getFile(itr.next());
        //
        List<MjrStockTransDetailDTO> lstImportGoods = FunctionUtils.getListStockImportFromFile(mpf);

        return lstImportGoods;
    }

    @RequestMapping(value = "/import", method = RequestMethod.POST)
    @ResponseBody
    public String importStock(@RequestBody StockManagementDTO stockManagementDTO) {
        log.info(currentUser.getCode() +" import: ");
        StockTransDTO stockTrans = initStockTrans(stockManagementDTO.getLstGoods());
        stockManagementService.importStock(stockTrans);
        return "Success";
    }

    private StockTransDTO initStockTrans(List<MjrStockTransDetailDTO> lstGoods){
        StockTransDTO stockTrans = new StockTransDTO();
        //
        MjrStockTransDTO mjrStockTransDTO = initMjrStockTrans();
        //
        stockTrans.setMjrStockTransDTO(mjrStockTransDTO);
        //
        List<MjrStockTransDetailDTO> lstStockTransDetails = initListTransDetail(lstGoods);
        stockTrans.setLstMjrStockTransDetail(lstStockTransDetails);
        //
        return stockTrans;
    }

    private List<MjrStockTransDetailDTO> initListTransDetail(List<MjrStockTransDetailDTO> lstGoods){
        //todo
        return lstGoods;
    }

    private MjrStockTransDTO initMjrStockTrans(){
        MjrStockTransDTO mjrStockTrans = new MjrStockTransDTO();
        mjrStockTrans.setCustId(selectedCustomer.getId());
        mjrStockTrans.setStockId("1");
        mjrStockTrans.setType(Constants.IMPORT_TYPE.IMPORT);
        mjrStockTrans.setStatus(Constants.STATUS.ACTIVE);
        mjrStockTrans.setCreatedUser(currentUser.getCode());
        //
        return mjrStockTrans;
    }
}
