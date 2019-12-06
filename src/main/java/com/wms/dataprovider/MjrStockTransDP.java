package com.wms.dataprovider;

import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.MjrStockTransDTO;
import org.springframework.stereotype.Repository;

/**
 * Created by duyot on 4/4/2017.
 */
@Repository
public class MjrStockTransDP extends BaseDP<MjrStockTransDTO> {
    public MjrStockTransDP() {
        super(MjrStockTransDTO[].class, MjrStockTransDTO.class, Constants.SERVICE_PREFIX.MJR_STOCK_TRANS_SERVICE);
    }
}
