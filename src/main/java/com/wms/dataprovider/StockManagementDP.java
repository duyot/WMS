package com.wms.dataprovider;

import com.google.common.collect.Lists;
import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.MjrStockTransDTO;
import com.wms.dto.MjrStockTransDetailDTO;
import com.wms.dto.ResponseObject;
import com.wms.dto.StockTransDTO;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Created by duyot on 2/16/2017.
 */
@Repository
public class StockManagementDP extends BaseDP<MjrStockTransDetailDTO> {
    private final String IMPORT_STOCK_URL = "import?access_token=";
    private final String EXPORT_STOCK_URL = "export?access_token=";
    private final String GET_SERIAL_IN_STOCK_URL = "getListSerialInStock";
    private final String CANCEL_TRANS_URL = "cancelTransaction";
    private final String GET_TRANS_GOODS_URL = "getTransGoodsDetail";
    private final String GET_LIST_TRANS_GOODS_URL = "getListTransGoodsDetail";
    private final String GET_LIST_TRANS_SERIAL_URL = "getListTransSerial";
    private final String GET_LIST_SERIAL_AFTER_IMPORT_URL = "getListSerialAfterImport";
    private final String GET_LIST_TRANS_URL = "getStockTransInfo";


    public StockManagementDP() {
        super(MjrStockTransDetailDTO[].class, MjrStockTransDetailDTO.class, Constants.SERVICE_PREFIX.STOCK_MANAGEMENT_SERVICE);
    }

    public ResponseObject importStock(StockTransDTO stockTrans) {
        RestTemplate restTemplate = new RestTemplate();
        String url = getUrlLoadBalancing(0, IMPORT_STOCK_URL);
        return restTemplate.postForObject(url, stockTrans, ResponseObject.class);
    }

    public ResponseObject exportStock(StockTransDTO stockTrans) {
        RestTemplate restTemplate = new RestTemplate();
        String url = getUrlLoadBalancing(0, EXPORT_STOCK_URL);
        return restTemplate.postForObject(url, stockTrans, ResponseObject.class);
    }

    public List<String> getListSerialInStock(String custId, String stockId, String goodsId, String goodsState) {
        RestTemplate restTemplate = new RestTemplate();
        String query = "custId=" + custId + "&stockId=" + stockId + "&goodsId=" + goodsId + "&goodsState=" + goodsState;
        String url = getUrlLoadBalancingQuery(query, GET_SERIAL_IN_STOCK_URL);
        try {
            ResponseEntity<String[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, String[].class);
            return Arrays.asList(responseEntity.getBody());
        } catch (RestClientException e) {
            return Lists.newArrayList();
        }
    }

    public ResponseObject cancelTrans(String transId) {
        RestTemplate restTemplate = new RestTemplate();
        String url = getUrlLoadBalancing(0, CANCEL_TRANS_URL);
        return restTemplate.postForObject(url, transId, ResponseObject.class);
    }

    public List<MjrStockTransDetailDTO> getTransGoodsDetail(String custId, String stockId, String transId, String transType) {
        RestTemplate restTemplate = new RestTemplate();
        String query = "custId=" + custId + "&stockId=" + stockId + "&transId=" + transId + "&transType=" + transType;
        String url = getUrlLoadBalancingQuery(query, GET_LIST_TRANS_GOODS_URL);
        try {
            ResponseEntity<MjrStockTransDetailDTO[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, MjrStockTransDetailDTO[].class);
            return Arrays.asList(responseEntity.getBody());
        } catch (RestClientException e) {
            return Lists.newArrayList();
        }
    }

    public List<MjrStockTransDetailDTO> getListTransGoodsDetail(String transId) {
        RestTemplate restTemplate = new RestTemplate();
        String query = "transId=" + transId;
        String url = getUrlLoadBalancingQuery(query, GET_LIST_TRANS_GOODS_URL);
        try {
            ResponseEntity<MjrStockTransDetailDTO[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, MjrStockTransDetailDTO[].class);
            return Arrays.asList(responseEntity.getBody());
        } catch (RestClientException e) {
            return Lists.newArrayList();
        }
    }

    public List<MjrStockTransDTO> getListTransSerial(String custId, String goodsId, String serial) {
        RestTemplate restTemplate = new RestTemplate();
        String query = "custId=" + custId + "&goodsId=" + goodsId + "&serial=" + serial;
        String url = getUrlLoadBalancingQuery(query, GET_LIST_TRANS_SERIAL_URL);
        try {
            ResponseEntity<MjrStockTransDTO[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, MjrStockTransDTO[].class);
            return Arrays.asList(responseEntity.getBody());
        } catch (RestClientException e) {
            return Lists.newArrayList();
        }
    }

    public List<MjrStockTransDetailDTO> getListSerialAfterImport(String custId, String orderId, String lstSerial) {
        RestTemplate restTemplate = new RestTemplate();
        String query = "custId=" + custId + "&orderId=" + orderId + "&lstSerial=" + lstSerial;
        String url = getUrlLoadBalancingQuery(query, GET_LIST_SERIAL_AFTER_IMPORT_URL);
        try {
            ResponseEntity<MjrStockTransDetailDTO[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, MjrStockTransDetailDTO[].class);
            return Arrays.asList(responseEntity.getBody());
        } catch (RestClientException e) {
            return Lists.newArrayList();
        }
    }



    public List<MjrStockTransDTO> getStockTransInfo(String lstStockTransId) {
        RestTemplate restTemplate = new RestTemplate();
        String query = "lstStockTransId=" + lstStockTransId;
        String url = getUrlLoadBalancingQuery(query, GET_LIST_TRANS_URL);
        try {
            ResponseEntity<MjrStockTransDTO[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, MjrStockTransDTO[].class);
            return Arrays.asList(responseEntity.getBody());
        } catch (RestClientException e) {
            return Lists.newArrayList();
        }
    }

}
