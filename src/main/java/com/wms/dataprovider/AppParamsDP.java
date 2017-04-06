package com.wms.dataprovider;

import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.AppParamsDTO;
import org.springframework.stereotype.Repository;

/**
 * Created by duyot on 4/5/2017.
 */
@Repository
public class AppParamsDP extends BaseDP<AppParamsDTO> {
    public AppParamsDP() {
        super(AppParamsDTO[].class,AppParamsDTO.class, Constants.SERVICE_PREFIX.APP_PARAMS_SERVICE);
    }
}
