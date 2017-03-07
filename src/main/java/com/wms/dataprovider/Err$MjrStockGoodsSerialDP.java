package com.wms.dataprovider;

import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.Err$MjrStockGoodsSerialDTO;
import org.springframework.stereotype.Repository;

/**
 * Created by duyot on 3/7/2017.
 */
@Repository
public class Err$MjrStockGoodsSerialDP extends BaseDP<Err$MjrStockGoodsSerialDTO> {
    public Err$MjrStockGoodsSerialDP() {
        super(Err$MjrStockGoodsSerialDTO[].class,Err$MjrStockGoodsSerialDTO.class, Constants.SERVICE_PREFIX.ERR$MJR_STOCK_GOODS_SERIAL_SERVICE);
    }
}
