package com.wms.services.impl;

import com.wms.dataprovider.CatPartnerDP;
import com.wms.dto.CatPartnerDTO;
import com.wms.dto.CatStockDTO;
import com.wms.services.interfaces.PartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by duyot on 2/17/2017.
 */
@Service("catPartnerService")
public class CatPartnerServiceImpl extends BaseServiceImpl<CatPartnerDTO,CatPartnerDP> implements PartnerService{
    @Autowired
    CatPartnerDP catPartnerDP;

    @PostConstruct
    public void setupService(){
        this.tdp = catPartnerDP;
    }

    @Override
    public List<CatPartnerDTO> getPartnerByUser(Long userId ) {
        return catPartnerDP.getPartnerByUser(userId);
    }
}
