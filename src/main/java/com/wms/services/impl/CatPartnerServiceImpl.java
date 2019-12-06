package com.wms.services.impl;

import com.wms.dataprovider.CatPartnerDP;
import com.wms.dto.CatPartnerDTO;
import com.wms.services.interfaces.PartnerService;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by duyot on 2/17/2017.
 */
@Service("catPartnerService")
public class CatPartnerServiceImpl extends BaseServiceImpl<CatPartnerDTO, CatPartnerDP> implements PartnerService {
    @Autowired
    CatPartnerDP catPartnerDP;

    @PostConstruct
    public void setupService() {
        this.tdp = catPartnerDP;
    }

    @Override
    public List<CatPartnerDTO> getPartnerByUser(Long userId) {
        return catPartnerDP.getPartnerByUser(userId);
    }

    @Override
    public List<CatPartnerDTO> getPartnerByUser(Long userId, Long partnerPermission) {
        return catPartnerDP.getPartnerByUser(userId, partnerPermission);
    }
}
