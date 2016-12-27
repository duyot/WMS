package com.wms.dataprovider;

import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.GoodsDTO;
import org.springframework.stereotype.Repository;

/**
 * Created by duyot on 12/9/2016.
 */
@Repository
public class GoodsDP extends BaseDP<GoodsDTO> {
    public GoodsDP() {
        super(GoodsDTO[].class,GoodsDTO.class, Constants.SERVICE_PREFIX.GOODS_SERVICE);
    }
}
