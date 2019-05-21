package com.wms.services.interfaces;

import com.wms.dto.CatPartnerDTO;

import java.util.List;

public interface PartnerService extends BaseService<CatPartnerDTO>{
    List<CatPartnerDTO> getPartnerByUser(Long userId );
}
