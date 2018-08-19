//package com.redis.repository;
//
//
//import AuthTokenInfo;
//import org.apache.tomcat.util.codec.binary.Base64;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Repository;
//import org.springframework.web.client.RestClientException;
//import org.springframework.web.client.RestTemplate;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Arrays;
//import java.util.LinkedHashMap;
//
///**
// * Created by truongbx on 11/08/2018.
// */
//@Repository
//public class TokenRepository {
//    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy ");
//    private static final Logger logger = LoggerFactory.getLogger(TokenRepository.class);
//    @Value("${token_url}")
//    private  String tokenUrl;
//    @Value("${client_id}")
//    private  String clientId;
//    @Value("${client_secret}")
//    private  String clientSecret;
//
//    @Value("${user_name}")
//    private  String username;
//    @Value("${pass_word}")
//    private  String password;
//
//    public static Logger log = LoggerFactory.getLogger(TokenRepository.class);
//
//    public TokenRepository() {
//    }
//
//    public static HttpHeaders getHeaders(){
//        HttpHeaders headers = new HttpHeaders();
//        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//        return headers;
//    }
//
//
//
//    public AuthTokenInfo sendTokenRequest(String uri){
//        RestTemplate restTemplate = new RestTemplate();
//        String tempToken = tokenUrl;
//        tempToken = tempToken.replace("@username",username);
//        tempToken = tempToken.replace("@password",password);
//        tempToken = tempToken.replace("@hosturl",uri);
//        HttpEntity<String> request = new HttpEntity<String>(getHeadersWithClientCredentials());
//        ResponseEntity<Object> response = null;
//        try {
//            response = restTemplate.exchange(tempToken, HttpMethod.POST, request, Object.class);
//        } catch (RestClientException e) {
//            logger.error("connection error " +e.getCause().getMessage());
//            e.printStackTrace();
//        }
//        LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>)response.getBody();
//        AuthTokenInfo tokenInfo = new AuthTokenInfo();
//
//        if(map == null){
//            return tokenInfo;
//        }
//
//        tokenInfo.setAccess_token((String)map.get("access_token"));
//        tokenInfo.setToken_type((String)map.get("token_type"));
//        tokenInfo.setRefresh_token((String)map.get("refresh_token"));
//        tokenInfo.setExpires_in((int)map.get("expires_in"));
//        tokenInfo.setScope((String)map.get("scope"));
//        tokenInfo.setCreatedTime( dateTimeFormatter.format(LocalDateTime.now()));
//
//        return tokenInfo;
//    }
//    private  HttpHeaders getHeadersWithClientCredentials(){
//        String plainClientCredentials= clientId +":"+ clientSecret;
//        String base64ClientCredentials = new String(Base64.encodeBase64(plainClientCredentials.getBytes()));
//
//        HttpHeaders headers = getHeaders();
//        headers.add("Authorization", "Basic " + base64ClientCredentials);
//        return headers;
//    }
//
//
//}
