package com.wms.base;

import com.wms.config.ProfileConfigInterface;
import com.wms.constants.Constants;
import com.wms.dto.AppParamsDTO;
import com.wms.dto.CatCustomerDTO;
import com.wms.dto.CatUserDTO;
import com.wms.services.interfaces.BaseService;
import com.wms.utils.FunctionUtils;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by duyot on 4/10/2017.
 */
@Component
@Scope("session")
public class BaseCommonController {
    @Autowired
    public BaseService appParamsService;
    @Autowired
    public ProfileConfigInterface profileConfig;
    @Autowired
    private HttpServletRequest requestCtx;

    public CatUserDTO currentUser;
    public CatCustomerDTO selectedCustomer;
    public List<AppParamsDTO> lstAppParams;
    public Map<String, String> mapAppStatus;

    //------------------------------------------------------------------------------------------------------------------
    @PostConstruct
    public void initBaseCommonBean() {
        this.currentUser = (CatUserDTO) requestCtx.getSession().getAttribute("user");
        this.selectedCustomer = (CatCustomerDTO) requestCtx.getSession().getAttribute("selectedCustomer");
        initAppParams();
    }

    //------------------------------------------------------------------------------------------------------------------
    private void initAppParams() {
        this.lstAppParams = FunctionUtils.getAppParams(appParamsService);
        this.mapAppStatus = FunctionUtils.buildMapAppParams(FunctionUtils.getAppParamByType(Constants.APP_PARAMS.STATUS, lstAppParams));
    }
}
