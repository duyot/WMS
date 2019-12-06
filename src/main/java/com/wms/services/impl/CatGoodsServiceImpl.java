package com.wms.services.impl;

import com.wms.dataprovider.CatGoodsDP;
import com.wms.dto.CatGoodsDTO;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by duyot on 12/9/2016.
 */
@Service("catGoodsService")
public class CatGoodsServiceImpl extends BaseServiceImpl<CatGoodsDTO, CatGoodsDP> {
    @Autowired
    CatGoodsDP goodsDP;

    @PostConstruct
    public void setupService() {
        this.tdp = goodsDP;
    }
}
