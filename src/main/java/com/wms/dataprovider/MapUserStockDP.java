package com.wms.dataprovider;

import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.MapUserStockDTO;
import org.springframework.stereotype.Repository;

@Repository
public class MapUserStockDP extends BaseDP<MapUserStockDTO> {

    public MapUserStockDP() {
        super(MapUserStockDTO[].class, MapUserStockDTO.class, Constants.SERVICE_PREFIX.MAP_USER_STOCK);
    }
}
