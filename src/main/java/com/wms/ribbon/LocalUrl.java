package com.wms.ribbon;

import com.netflix.discovery.converters.Auto;
import com.wms.config.ProfileConfigInterface;
import com.wms.redis.model.AuthTokenInfo;
import com.wms.constants.Constants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component("localUrl")
public class LocalUrl implements BaseURL{

    @Autowired
     private ProfileConfigInterface profileConfig;

    @Autowired
    CurrentUserLogIn currentUserLogIn;


    @Override
    public String getUrlToPostMethod(String servicePrefix, String serviceMethod) {
        AuthTokenInfo authTokenInfo = currentUserLogIn.getTokenInfo(profileConfig.getTokenURL());
        return profileConfig.getServiceURL() +servicePrefix +serviceMethod+ authTokenInfo.getAccess_token();
    }

    @Override
    public String getUrlToGetMethod(Long id, String servicePrefix, String serviceMethod) {
        AuthTokenInfo authTokenInfo = currentUserLogIn.getTokenInfo(profileConfig.getTokenURL());

        return  profileConfig.getServiceURL() +servicePrefix +serviceMethod+ id + Constants.SERVICE_METHOD.ACCESS_TOKEN + authTokenInfo.getAccess_token();
    }

    @Override
    public String getUrlToGetMethod(String query, String servicePrefix, String serviceMethod) {
        AuthTokenInfo authTokenInfo = currentUserLogIn.getTokenInfo(profileConfig.getTokenURL());
        return profileConfig.getServiceURL() +servicePrefix +serviceMethod+ "?" + query + Constants.SERVICE_METHOD.ACCESS_TOKEN_AND + authTokenInfo.getAccess_token();
    }

    @Override
    public String getLoginURL() {
        return null;
    }
}
