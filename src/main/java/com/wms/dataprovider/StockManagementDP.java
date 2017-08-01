package com.wms.dataprovider;

import com.google.common.collect.Lists;
import com.wms.constants.Constants;
import com.wms.dto.AuthTokenInfo;
import com.wms.dto.ResponseObject;
import com.wms.dto.StockTransDTO;
import com.wms.utils.BundleUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * Created by duyot on 2/16/2017.
 */
@Repository
public class StockManagementDP {
    private final String IMPORT_STOCK_URL = BundleUtils.getKey("rest_service_url") + Constants.SERVICE_PREFIX.STOCK_MANAGEMENT_SERVICE + "import";
    private final String EXPORT_STOCK_URL = BundleUtils.getKey("rest_service_url") + Constants.SERVICE_PREFIX.STOCK_MANAGEMENT_SERVICE + "export";
    private final String GET_SERIAL_IN_STOCK_URL = BundleUtils.getKey("rest_service_url") + Constants.SERVICE_PREFIX.STOCK_MANAGEMENT_SERVICE + "getListSerialInStock";

    public ResponseObject importStock(StockTransDTO stockTrans, AuthTokenInfo tokenInfo){
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(IMPORT_STOCK_URL+"?access_token="+tokenInfo.getAccess_token(),stockTrans,ResponseObject.class);
    }

    public ResponseObject exportStock(StockTransDTO stockTrans, AuthTokenInfo tokenInfo){
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(EXPORT_STOCK_URL+"?access_token="+tokenInfo.getAccess_token(),stockTrans,ResponseObject.class);
    }

    public List<String> getListSerialInStock(String custId, String stockId, String goodsId, String goodsState, AuthTokenInfo tokenInfo){
        RestTemplate restTemplate = new RestTemplate();
        String url = GET_SERIAL_IN_STOCK_URL + "?custId="+ custId + "&stockId=" + stockId + "&goodsId=" + goodsId + "&goodsState="+ goodsState + "&access_token="+ tokenInfo.getAccess_token();
        try {
                ResponseEntity<String[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET,null,String[].class);
                return Arrays.asList(responseEntity.getBody());
            } catch (RestClientException e) {
                return Lists.newArrayList();
            }
    }

}
