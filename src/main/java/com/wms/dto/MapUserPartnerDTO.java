package com.wms.dto;


public class MapUserPartnerDTO {

    String id;
    String userId;
    String partnerId;

    public MapUserPartnerDTO(String id, String userId, String partnerId) {
        this.id = id;
        this.userId = userId;
        this.partnerId = partnerId;
    }

    public MapUserPartnerDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

}
