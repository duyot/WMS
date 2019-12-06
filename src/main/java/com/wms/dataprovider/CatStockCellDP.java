package com.wms.dataprovider;

import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.CatStockCellDTO;
import org.springframework.stereotype.Repository;

/**
 * Created by duyot on 4/19/2017.
 */
@Repository
public class CatStockCellDP extends BaseDP<CatStockCellDTO> {
    public CatStockCellDP() {
        super(CatStockCellDTO[].class, CatStockCellDTO.class, Constants.SERVICE_PREFIX.CAT_STOCK_CELL_SERVICE);
    }
}
