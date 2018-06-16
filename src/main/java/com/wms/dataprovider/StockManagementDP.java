package com.wms.dataprovider;

import com.google.common.collect.Lists;
import com.wms.constants.Constants;
import com.wms.dto.AuthTokenInfo;
import com.wms.dto.MjrStockTransDetailDTO;
import com.wms.dto.MjrStockTransDTO;
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
    private final String CANCEL_TRANS_URL = BundleUtils.getKey("rest_service_url") + Constants.SERVICE_PREFIX.STOCK_MANAGEMENT_SERVICE + "cancelTransaction";
    private final String GET_TRANS_GOODS_URL = BundleUtils.getKey("rest_service_url") + Constants.SERVICE_PREFIX.STOCK_MANAGEMENT_SERVICE + "getTransGoodsDetail";
    private final String GET_LIST_TRANS_GOODS_URL = BundleUtils.getKey("rest_service_url") + Constants.SERVICE_PREFIX.STOCK_MANAGEMENT_SERVICE + "getListTransGoodsDetail";
    private final String GET_LIST_TRANS_URL = BundleUtils.getKey("rest_service_url") + Constants.SERVICE_PREFIX.STOCK_MANAGEMENT_SERVICE + "getStockTransInfo";

    public ResponseObject  importStock(StockTransDTO stockTrans, AuthTokenInfo tokenInfo){
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

    public ResponseObject cancelTrans(String transId, AuthTokenInfo tokenInfo){
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(CANCEL_TRANS_URL+"?access_token="+tokenInfo.getAccess_token(),transId,ResponseObject.class);
    }

    public List<MjrStockTransDetailDTO> getTransGoodsDetail(String custId, String stockId, String transId, String transType, AuthTokenInfo tokenInfo){
        RestTemplate restTemplate = new RestTemplate();
        String url = GET_TRANS_GOODS_URL + "?custId="+ custId + "&stockId=" + stockId + "&transId=" + transId + "&transType="+ transType + "&access_token="+ tokenInfo.getAccess_token();
        try {
            ResponseEntity<MjrStockTransDetailDTO[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET,null,MjrStockTransDetailDTO[].class);
            return Arrays.asList(responseEntity.getBody());
        } catch (RestClientException e) {
            return Lists.newArrayList();
        }
    }

    public List<MjrStockTransDetailDTO> getListTransGoodsDetail(String lstStockTransId, AuthTokenInfo tokenInfo){
        RestTemplate restTemplate = new RestTemplate();
        String url = GET_LIST_TRANS_GOODS_URL + "?lstStockTransId="+ lstStockTransId + "&access_token="+ tokenInfo.getAccess_token();
        try {
            ResponseEntity<MjrStockTransDetailDTO[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET,null,MjrStockTransDetailDTO[].class);
            return Arrays.asList(responseEntity.getBody());
        } catch (RestClientException e) {
            return Lists.newArrayList();
        }
    }

    public List<MjrStockTransDTO> getStockTransInfo(String lstStockTransId, AuthTokenInfo tokenInfo){
        RestTemplate restTemplate = new RestTemplate();
        String url = GET_LIST_TRANS_URL + "?lstStockTransId="+ lstStockTransId + "&access_token="+ tokenInfo.getAccess_token();
        try {
            ResponseEntity<MjrStockTransDTO[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET,null,MjrStockTransDTO[].class);
            return Arrays.asList(responseEntity.getBody());
        } catch (RestClientException e) {
            return Lists.newArrayList();
        }
    }

}
