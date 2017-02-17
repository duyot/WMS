package com.wms.dataprovider;

import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.CatStockDTO;
import com.wms.dto.GoodsDTO;
import org.springframework.stereotype.Repository;

/**
 * Created by duyot on 2/17/2017.
 */
@Repository
public class CatStockDP extends BaseDP<CatStockDTO> {
    public CatStockDP() {
        super(CatStockDTO[].class, CatStockDTO.class, Constants.SERVICE_PREFIX.CAT_STOCK_SERVICE);
    }
}
