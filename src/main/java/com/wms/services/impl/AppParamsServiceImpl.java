package com.wms.services.impl;

import com.wms.dataprovider.AppParamsDP;
import com.wms.dto.AppParamsDTO;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Created by doanlv4 on 28/03/2017.
 */
@Service("appParamsService")
public class AppParamsServiceImpl extends BaseServiceImpl<AppParamsDTO, AppParamsDP> {
    @Autowired
    AppParamsDP appParamsDP;

    @PostConstruct
    public void setupService() {
        this.tdp = appParamsDP;
    }
}
