package com.wms.services.impl;

import com.wms.dataprovider.CatPartnerDP;
import com.wms.dto.CatPartnerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by duyot on 2/17/2017.
 */
@Service("catPartnerService")
public class CatPartnerServiceImpl extends BaseServiceImpl<CatPartnerDTO,CatPartnerDP>{
    @Autowired
    CatPartnerDP catPartnerDP;

    @PostConstruct
    public void setupService(){
        this.tdp = catPartnerDP;
    }
}
