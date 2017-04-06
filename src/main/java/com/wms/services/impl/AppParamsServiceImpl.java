package com.wms.services.impl;

import com.wms.dataprovider.AppParamsDP;
<<<<<<< HEAD
import com.wms.dataprovider.CatGoodsGroupDP;
import com.wms.dto.AppParamsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by duyot on 4/5/2017.
=======
import com.wms.dto.AppParamsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;

/**
 * Created by doanlv4 on 28/03/2017.
>>>>>>> 82a5caa22e2998c70ccea91139b582f9c2701375
 */
@Service("appParamsService")
public class AppParamsServiceImpl extends BaseServiceImpl<AppParamsDTO,AppParamsDP>{
    @Autowired
    AppParamsDP appParamsDP;

    @PostConstruct
    public void setupService(){
        this.tdp = appParamsDP;
    }
}
