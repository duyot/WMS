package com.wms.dataprovider;

import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.CatGoodsDTO;
import org.springframework.stereotype.Repository;

/**
 * Created by duyot on 12/9/2016.
 */
@Repository
public class CatGoodsDP extends BaseDP<CatGoodsDTO> {
    public CatGoodsDP() {
        super(CatGoodsDTO[].class, CatGoodsDTO.class, Constants.SERVICE_PREFIX.GOODS_SERVICE);
    }
}
