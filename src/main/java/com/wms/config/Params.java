package com.wms.config;

import com.wms.utils.BundleUtils;

/**
 * Created by duyot on 10/18/2016.
 */
public class Params {
    public static String rest_service_url;

    public Params() {
        this.setRest_service_url(BundleUtils.getKey("rest_service_url"));
    }

    public String getRest_service_url() {
        return rest_service_url;
    }

    public void setRest_service_url(String rest_service_url) {
        this.rest_service_url = rest_service_url;
    }
}
