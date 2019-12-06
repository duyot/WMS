package com.wms.config;

import com.wms.ribbon.BaseURL;

/**
 * Created by truongbx on 12/08/2018.
 */
public interface ProfileConfigInterface {

    public String getServiceURL();

    public String getLoginURL();

    public String getTokenURL();

    public String getUploadURL();

    public String getTemplateURL();

    public String getTempURL();

    public BaseURL getBaseUrLService();

}
