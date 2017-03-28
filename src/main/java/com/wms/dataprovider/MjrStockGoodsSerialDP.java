package com.wms.dataprovider;

import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.MjrStockGoodsDTO;
import com.wms.dto.MjrStockGoodsSerialDTO;
import org.springframework.stereotype.Repository;

/**
 * Created by duyot on 3/24/2017.
 */
@Repository
public class MjrStockGoodsSerialDP extends BaseDP<MjrStockGoodsSerialDTO> {
    public MjrStockGoodsSerialDP() {
        super(MjrStockGoodsSerialDTO[].class,MjrStockGoodsSerialDTO.class, Constants.SERVICE_PREFIX.MJR_STOCK_GOODS_SERIAL_SERVICE);
    }
}
