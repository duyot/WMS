package com.wms.dataprovider;

import com.wms.dto.ActionMenuDTO;
import com.wms.dto.AuthTokenInfo;
import com.wms.dto.ChartDTO;
import com.wms.utils.BundleUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by duyot on 5/18/2017.
 */
@Repository
public class StatisticDP {
    private  final String SERVICE_URL    = BundleUtils.getKey("rest_service_url");
    private  final String SERVICE_PREFIX = "statisticServices/";
    private  final String GET_REVENUE_URL    = SERVICE_URL+SERVICE_PREFIX  + "getRevenue";
    private  final String GET_TOP_GOODS_URL  = SERVICE_URL+SERVICE_PREFIX  + "getTopGoods";

    public List<ChartDTO> getRevenue(String custId,String type, AuthTokenInfo tokenInfo){
        RestTemplate restTemplate = new RestTemplate();
        String getActionMenuURL =   GET_REVENUE_URL + "?" + "custId="+custId+"&type="+type + "&access_token="+tokenInfo.getAccess_token();
        try {
            ResponseEntity<ChartDTO[]> responseEntity = restTemplate.getForEntity(getActionMenuURL,ChartDTO[].class);
            if(responseEntity.getBody() != null){
                return Arrays.asList(responseEntity.getBody());
            }else{
                return new ArrayList<>();
            }
        } catch (RestClientException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<ChartDTO> getTopGoods(String custId,String type, AuthTokenInfo tokenInfo){
        RestTemplate restTemplate = new RestTemplate();
        String getActionMenuURL =   GET_TOP_GOODS_URL + "?" + "custId="+custId+"&type="+type + "&access_token="+tokenInfo.getAccess_token();
        try {
            ResponseEntity<ChartDTO[]> responseEntity = restTemplate.getForEntity(getActionMenuURL,ChartDTO[].class);
            if(responseEntity.getBody() != null){
                return Arrays.asList(responseEntity.getBody());
            }else{
                return new ArrayList<>();
            }
        } catch (RestClientException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<ChartDTO> getKPIStorage(String custId,String type, AuthTokenInfo tokenInfo){
        RestTemplate restTemplate = new RestTemplate();
        String getActionMenuURL =   GET_TOP_GOODS_URL + "?" + "custId="+custId+"&type="+type + "&access_token="+tokenInfo.getAccess_token();
        try {
            ResponseEntity<ChartDTO[]> responseEntity = restTemplate.getForEntity(getActionMenuURL,ChartDTO[].class);
            if(responseEntity.getBody() != null){
                return Arrays.asList(responseEntity.getBody());
            }else{
                return new ArrayList<>();
            }
        } catch (RestClientException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<ChartDTO> getTransaction(String custId,String type, AuthTokenInfo tokenInfo){
        RestTemplate restTemplate = new RestTemplate();
        String getActionMenuURL =   GET_TOP_GOODS_URL + "?" + "custId="+custId+"&type="+type + "&access_token="+tokenInfo.getAccess_token();
        try {
            ResponseEntity<ChartDTO[]> responseEntity = restTemplate.getForEntity(getActionMenuURL,ChartDTO[].class);
            if(responseEntity.getBody() != null){
                return Arrays.asList(responseEntity.getBody());
            }else{
                return new ArrayList<>();
            }
        } catch (RestClientException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
