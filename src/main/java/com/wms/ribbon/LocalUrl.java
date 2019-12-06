package com.wms.ribbon;

import com.wms.constants.Constants;
import com.wms.redis.model.AuthTokenInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component("localUrl")
public class LocalUrl implements BaseURL {

    @Autowired
    CurrentUserLogIn currentUserLogIn;

    private String tokenURL;
    private String serviceURL;


    @Override
    public String getUrlToPostMethod(String servicePrefix, String serviceMethod) {
        AuthTokenInfo authTokenInfo = currentUserLogIn.getTokenInfo(tokenURL);
        return serviceURL + servicePrefix + serviceMethod + authTokenInfo.getAccess_token();
    }

    @Override
    public String getUrlToGetMethod(Long id, String servicePrefix, String serviceMethod) {
        AuthTokenInfo authTokenInfo = currentUserLogIn.getTokenInfo(tokenURL);
        return serviceURL + servicePrefix + serviceMethod + id + Constants.SERVICE_METHOD.ACCESS_TOKEN + authTokenInfo.getAccess_token();
    }

    @Override
    public String getUrlToGetMethod(String query, String servicePrefix, String serviceMethod) {
        AuthTokenInfo authTokenInfo = currentUserLogIn.getTokenInfo(tokenURL);
        return serviceURL + servicePrefix + serviceMethod + "?" + query + Constants.SERVICE_METHOD.ACCESS_TOKEN_AND + authTokenInfo.getAccess_token();
    }

    @Override
    public String getPostURLWithoutTokenKey(String serviceMethod) {
        return serviceURL.replace("/services/", "/") + serviceMethod;
    }

    @Override
    public String getLoginURL() {
        return null;
    }

    public void setTokenURL(String tokenURL) {
        this.tokenURL = tokenURL;
    }


    public void setServiceURL(String serviceURL) {
        this.serviceURL = serviceURL;
    }
}
