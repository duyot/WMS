package com.wms.dataprovider;

import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.CatGoodsGroupDTO;
import org.springframework.stereotype.Repository;

/**
 * Created by duyot on 12/7/2016.
 */
@Repository
public class CatGoodsGroupDP extends BaseDP<CatGoodsGroupDTO> {
    public CatGoodsGroupDP() {
        super(CatGoodsGroupDTO[].class, CatGoodsGroupDTO.class, Constants.SERVICE_PREFIX.CAT_GOODS_GROUP_SERVICE);
    }
}
