package com.wms.base;

import com.wms.constants.Constants;
import com.wms.dto.AppParamsDTO;
import com.wms.dto.AuthTokenInfo;
import com.wms.dto.CatCustomerDTO;
import com.wms.dto.CatUserDTO;
import com.wms.services.interfaces.BaseService;
import com.wms.utils.FunctionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by duyot on 4/10/2017.
 */
@Component
@Scope("session")
public class BaseCommonController {
    @Autowired
    public BaseService appParamsService;
    //
    public CatUserDTO currentUser;
    public AuthTokenInfo tokenInfo;
    public CatCustomerDTO selectedCustomer;
    public List<AppParamsDTO> lstAppParams;
    //
    public Map<String,String> mapAppStatus;
    //
    @ModelAttribute("currentUser")
    public void setCurrentUser(HttpServletRequest request){
        if (currentUser == null) {
            this.currentUser =  (CatUserDTO) request.getSession().getAttribute("user");
        }
        if(tokenInfo == null){
            this.tokenInfo =  (AuthTokenInfo) request.getSession().getAttribute("tokenInfo");
        }
        if(selectedCustomer == null){
            this.selectedCustomer =  (CatCustomerDTO) request.getSession().getAttribute("selectedCustomer");
        }
        if(lstAppParams == null){
            lstAppParams = FunctionUtils.getAppParams(appParamsService,tokenInfo);
        }
        if (mapAppStatus == null) {
            mapAppStatus = FunctionUtils.buildMapAppParams(FunctionUtils.getAppParamByType(Constants.APP_PARAMS.STATUS,lstAppParams));
        }
    }

}
