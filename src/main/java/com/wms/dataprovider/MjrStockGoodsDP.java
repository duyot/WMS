package com.wms.dataprovider;

import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.MjrStockGoodsDTO;
import org.springframework.stereotype.Repository;

/**
 * Created by duyot on 3/24/2017.
 */
@Repository
public class MjrStockGoodsDP extends BaseDP<MjrStockGoodsDTO> {

    public MjrStockGoodsDP() {
        super(MjrStockGoodsDTO[].class, MjrStockGoodsDTO.class, Constants.SERVICE_PREFIX.MJR_STOCK_GOODS_SERVICE);
    }
}
