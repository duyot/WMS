package com.wms.config;

import com.wms.ribbon.BaseURL;

/**
 * Created by truongbx on 12/08/2018.
 */
public interface ProfileConfigInterface {

    String getServiceURL();

    String getLoginURL();

    String getTokenURL();

    String getUploadURL();

    String getTemplateURL();

    String getTempURL();

    BaseURL getBaseUrLService();

}
