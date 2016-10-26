package com.vivas;

import com.vivas.dto.User;
import org.springframework.http.*;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by duyot on 10/3/2016.
 */
public class ClientTest {
    public static final String TEST__PARAMS_URI = "http://localhost:8080//SpringRestServices/services/userservices/findUser/{id}";
    public static final String TEST__POST_OBJECT_URI = "http://localhost:8080//SpringRestServices/services/userservices/saveUser.json";
    public static final String TEST__DELETE_URI = "http://localhost:8080//SpringRestServices/services/userservices/deleteUser/{id}";
    public static final String TEST__FILE_URI = "http://localhost:8080//SpringRestServices/services/userservices/getFile/{fileName}";

    public static void main(String[] args) {
//        ClientTest.testSendObject(new User("sdf","sdf"));
        ClientTest.testReceiveFile("logback-spring.xml");
    }

    /*
       test send params through uri
    */
    public static void testReceiveFile(String fileName){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(
                new ByteArrayHttpMessageConverter());

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
        HttpEntity<String> httpEntity = new HttpEntity<String>(headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(
                TEST__FILE_URI, HttpMethod.GET, httpEntity, byte[].class, fileName);

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                Files.write(Paths.get(fileName), response.getBody());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    /*
        test with http header
     */
    public static void testFindUserWithHttpHeader(String id){
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> headers =
                new LinkedMultiValueMap<String, String>();
        headers.add("Accept", "application/json");
        headers.add("User-Agent", "Java/1.6.0_20");
        headers.add("Host", "localhost:8080");
        headers.add("Connection", "keep-alive");
        HttpEntity<Object> httpEntity = new HttpEntity(headers);

        ResponseEntity responseEntity = restTemplate.exchange(TEST__PARAMS_URI, HttpMethod.GET,httpEntity,User.class,2);
        User user = (User) responseEntity.getBody();

        System.out.println(user.getUsername());
    }


    /*
        test send params through uri
     */
    public static void testFindUser(String id){
        RestTemplate restTemplate = new RestTemplate();
//        User responseEntity = restTemplate.getForObject(TEST__PARAMS_URI, User.class,2);
        String responseEntity = restTemplate.getForObject(TEST__PARAMS_URI, String.class,2);

        System.out.println(responseEntity);
    }

    /*
        test submit object
     */
    public static void testSendObject(User requestUser){
        RestTemplate restTemplate = new RestTemplate();
        //get by object format
//        ResponseObject reponseUser = restTemplate.postForObject(TEST__POST_OBJECT_URI,requestUser,ResponseObject.class);
//        System.out.println(reponseUser.getStatusCode());
        //get string(json) format
        String reponseUser = restTemplate.postForObject(TEST__POST_OBJECT_URI,requestUser,String.class);
        System.out.println(reponseUser);
    }
    /*
        test delete resource
     */
    public static void testDel(Long userId){
        RestTemplate restTemplate = new RestTemplate();
        Map<String,String> mapVar = new HashMap<>();
        mapVar.put("id",userId+"");
        restTemplate.delete(TEST__DELETE_URI,mapVar);
    }
}
