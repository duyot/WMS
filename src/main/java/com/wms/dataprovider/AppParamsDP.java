package com.wms.dataprovider;

import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.AppParamsDTO;
import org.springframework.stereotype.Repository;

<<<<<<< HEAD
/**
 * Created by duyot on 4/5/2017.
=======

/**
 * Created by doanlv4 on 28/03/2017.
>>>>>>> 82a5caa22e2998c70ccea91139b582f9c2701375
 */
@Repository
public class AppParamsDP extends BaseDP<AppParamsDTO> {
    public AppParamsDP() {
<<<<<<< HEAD
        super(AppParamsDTO[].class,AppParamsDTO.class, Constants.SERVICE_PREFIX.APP_PARAMS_SERVICE);
    }
=======
        super(AppParamsDTO[].class, AppParamsDTO.class, Constants.SERVICE_PREFIX.APP_PARAMS_SERVICE);
    }

>>>>>>> 82a5caa22e2998c70ccea91139b582f9c2701375
}
