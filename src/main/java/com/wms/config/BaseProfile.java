package com.wms.config;

/**
 * Created by truongbx on 12/08/2018.
 * configs using for each profile
 */

import com.wms.ribbon.BaseURL;
import org.springframework.beans.factory.annotation.Value;

public class BaseProfile implements ProfileConfigInterface {
    @Value("${config.url.service:#{null}}")
    private String service;
    @Value("${config.url.login:#{null}}")
    private String login;
    @Value("${config.url.token:#{null}}")
    private String token;
    @Value("${config.url.template}")
    private String template;
    @Value("${config.url.temp}")
    private String temp;
    @Value("${config.url.upload}")
    private String upload;
    @Value("${config.security.token}")
    private String securityToken;

    @Override
    public String getServiceURL() {
        return this.service;
    }

    @Override
    public String getLoginURL() {
        return this.login;
    }

    @Override
    public String getTokenURL() {
        return this.token;
    }

    @Override
    public String getUploadURL() {
        return this.upload;
    }

    @Override
    public String getTemplateURL() {
        return this.template;
    }

    @Override
    public String getTempURL() {
        return this.temp;
    }

    @Override
    public BaseURL getBaseUrLService() {
        return null;
    }

    @Override
    public String getSecurityToken() {
        return this.securityToken;
    }
}
