package com.wms.dto;

import java.util.List;

/**
 * Created by duyot on 4/27/2017.
 */
public class ListPartnerDTO {
    private List<CatPartnerDTO> lstPartner;

    public ListPartnerDTO(List<CatPartnerDTO> lstPartner) {
        this.lstPartner = lstPartner;
    }

    public ListPartnerDTO() {
    }

    public List<CatPartnerDTO> getLstPartner() {
        return lstPartner;
    }

    public void setLstPartner(List<CatPartnerDTO> lstPartner) {
        this.lstPartner = lstPartner;
    }
}
