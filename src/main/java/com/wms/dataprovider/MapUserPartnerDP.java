package com.wms.dataprovider;

import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.MapUserPartnerDTO;
import org.springframework.stereotype.Repository;

@Repository
public class MapUserPartnerDP extends BaseDP<MapUserPartnerDTO> {

    public MapUserPartnerDP() {
        super(MapUserPartnerDTO[].class, MapUserPartnerDTO.class, Constants.SERVICE_PREFIX.MAP_USER_PARTNER);
    }
}
