package com.wms.dataprovider;
import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.MjrStockGoodsTotalDTO;
import com.wms.dto.MjrStockTransDTO;
import com.wms.dto.MjrStockTransDetailDTO;
import org.springframework.stereotype.Repository;

@Repository
public class MjrStockTransDetailDP extends BaseDP<MjrStockTransDetailDTO> {
    public MjrStockTransDetailDP() {
        super(MjrStockTransDetailDTO[].class,MjrStockTransDetailDTO.class, Constants.SERVICE_PREFIX.MJR_STOCK_TRANS_DETAIL_SERVICE);
    }
}
