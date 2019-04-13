package com.wms.ribbon;

import com.wms.config.WMSConfigManagerment;
import com.wms.dto.CatUserDTO;
import com.wms.redis.model.AuthTokenInfo;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.LinkedHashMap;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CurrentUserLogIn {

    private CatUserDTO currentUser;
    private AuthTokenInfo tokenInfo;


    public CatUserDTO getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(CatUserDTO currentUser) {
        this.currentUser = currentUser;
    }

    public AuthTokenInfo getTokenInfo(String tokenURL) {
        if (tokenInfo == null){
            tokenInfo = sendTokenRequest(getCurrentUser().getCode(), getCurrentUser().getPassword(), tokenURL);
        }
        return tokenInfo;
    }
    public static AuthTokenInfo sendTokenRequest(String username, String password,String tokenURL){
        RestTemplate restTemplate = new RestTemplate();
        tokenURL = tokenURL.replace("@username",username);
        tokenURL = tokenURL.replace("@password",password);
        HttpEntity<String> request = new HttpEntity<String>(getHeadersWithClientCredentials());
        ResponseEntity<Object> response = null;
        try {
            response = restTemplate.exchange(tokenURL, HttpMethod.POST, request, Object.class);
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>)response.getBody();
        AuthTokenInfo tokenInfo = new AuthTokenInfo();

        if(map == null){
            return tokenInfo;
        }

        tokenInfo.setAccess_token((String)map.get("access_token"));
        tokenInfo.setToken_type((String)map.get("token_type"));
        tokenInfo.setRefresh_token((String)map.get("refresh_token"));
        tokenInfo.setExpires_in((int)map.get("expires_in"));
        tokenInfo.setScope((String)map.get("scope"));

        return tokenInfo;
    }

    /*
     * Add HTTP Authorization header, using Basic-Authentication to send client-credentials.
     */
    private static HttpHeaders getHeadersWithClientCredentials(){
        String plainClientCredentials= WMSConfigManagerment.CLIENT_ID+":"+ WMSConfigManagerment.CLIENT_SECRET;
        String base64ClientCredentials = new String(Base64.encodeBase64(plainClientCredentials.getBytes()));

        HttpHeaders headers = getHeaders();
        headers.add("Authorization", "Basic " + base64ClientCredentials);
        return headers;
    }
    /*
     * Prepare HTTP Headers.
     */
    public static HttpHeaders getHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return headers;
    }

    public void setTokenInfo(AuthTokenInfo tokenInfo) {
        this.tokenInfo = tokenInfo;
    }
}
