package com.wms.services.impl;

import com.wms.dataprovider.CatGoodsGroupDP;
import com.wms.dto.CatGoodsGroupDTO;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by duyot on 12/7/2016.
 */
@Service("catGoodsGroupService")
public class CatGoodsGroupServiceImpl extends BaseServiceImpl<CatGoodsGroupDTO, CatGoodsGroupDP> {
    @Autowired
    CatGoodsGroupDP catGoodsGroupDP;

    @PostConstruct
    public void setupService() {
        this.tdp = catGoodsGroupDP;
    }
}
