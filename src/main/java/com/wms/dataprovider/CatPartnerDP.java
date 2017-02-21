package com.wms.dataprovider;

import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.CatPartnerDTO;
import com.wms.dto.CatGoodsDTO;
import org.springframework.stereotype.Repository;

/**
 * Created by duyot on 2/17/2017.
 */
@Repository
public class CatPartnerDP extends BaseDP<CatPartnerDTO> {
    public CatPartnerDP() {
        super(CatPartnerDTO[].class, CatPartnerDTO.class, Constants.SERVICE_PREFIX.CAT_PARTNER_SERVICE);
    }
}
