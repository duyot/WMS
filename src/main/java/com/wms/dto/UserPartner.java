package com.wms.dto;

import java.util.List;

public class UserPartner {
    List<CatPartnerDTO> lstCatPartners;
    String[] userPartners;

    public UserPartner(List<CatPartnerDTO> lstCatPartners, String[] userPartners) {
        this.lstCatPartners = lstCatPartners;
        this.userPartners = userPartners;
    }

    public List<CatPartnerDTO> getLstCatPartners() {
        return lstCatPartners;
    }

    public void setLstCatPartners(List<CatPartnerDTO> lstCatPartners) {
        this.lstCatPartners = lstCatPartners;
    }

    public String[] getUserPartners() {
        return userPartners;
    }

    public void setUserPartners(String[] userPartners) {
        this.userPartners = userPartners;
    }
}
