package com.wms.utils;

import com.wms.dto.AuthTokenInfo;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * Created by duyot on 11/17/2016.
 */
public class FunctionUtils {
    public static Logger log = LoggerFactory.getLogger(FunctionUtils.class);

    /*
         * Prepare HTTP Headers.
         */
    public static HttpHeaders getHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return headers;
    }

    /*
     * Add HTTP Authorization header, using Basic-Authentication to send client-credentials.
     */
    private static HttpHeaders getHeadersWithClientCredentials(){
        String plainClientCredentials= BundleUtils.getkey("client_id") +":"+ BundleUtils.getkey("client_secret");
        String base64ClientCredentials = new String(Base64.encodeBase64(plainClientCredentials.getBytes()));

        HttpHeaders headers = getHeaders();
        headers.add("Authorization", "Basic " + base64ClientCredentials);
        return headers;
    }

    public static AuthTokenInfo sendTokenRequest(String username, String password){
        RestTemplate restTemplate = new RestTemplate();
        String tokenURL = BundleUtils.getkey("token_url");
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


    public static boolean saveUploadedFile(MultipartFile uploadfile,String fileName){
        String saveDirectory = BundleUtils.getkey("upload_url");
        String filepath  = Paths.get(saveDirectory, fileName).toString();
        try {
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filepath)));
            stream.write(uploadfile.getBytes());
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Error save file: "+ e.toString());
            return false;
        }

        return true;
    }
}
