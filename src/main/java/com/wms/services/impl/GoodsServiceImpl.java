package com.wms.services.impl;

import com.wms.dataprovider.CatGoodsGroupDP;
import com.wms.dataprovider.GoodsDP;
import com.wms.dto.CatGoodsGroupDTO;
import com.wms.dto.GoodsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by duyot on 12/9/2016.
 */
@Service("goodsService")
public class GoodsServiceImpl extends BaseServiceImpl<GoodsDTO,GoodsDP>{
    @Autowired
    GoodsDP goodsDP;

    @PostConstruct
    public void setupService(){
        this.tdp = goodsDP;
    }
}
