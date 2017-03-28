package com.wms.dataprovider;

import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.MjrStockGoodsTotalDTO;
import org.springframework.stereotype.Repository;

/**
 * Created by duyot on 3/24/2017.
 */
@Repository
public class MjrStockGoodsTotalDP extends BaseDP<MjrStockGoodsTotalDTO> {
    public MjrStockGoodsTotalDP() {
        super(MjrStockGoodsTotalDTO[].class,MjrStockGoodsTotalDTO.class, Constants.SERVICE_PREFIX.MJR_STOCK_GOODS_TOTAL_SERVICE);
    }
}
