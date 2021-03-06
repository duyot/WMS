package com.wms.dataprovider;

import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.ChartDTO;
import com.wms.dto.InventoryInfoDTO;
import com.wms.dto.SysMenuDTO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Created by duyot on 5/18/2017.
 */
@Repository
public class StatisticDP extends BaseDP<ChartDTO> {
    private final String GET_REVENUE_URL = "getRevenue";
    private final String GET_TOP_GOODS_URL = "getTopGoods";
    private final String GET_TRANSACTION_URL = "getTransaction";
    private final String GET_KPI_STORAGE_URL = "getKPIStorage";
    private final String GET_INVENTORY_INFO_URL = "getInventoryInfor";


    public StatisticDP() {
        super(ChartDTO[].class, SysMenuDTO.class, Constants.SERVICE_PREFIX.STATISTIC_SERVICE);
    }

    public List<ChartDTO> getRevenue(String custId, String type) {
        RestTemplate restTemplate = new RestTemplate();
        String query = "custId=" + custId + "&type=" + type;
        String url = getUrlLoadBalancingQuery(query, GET_REVENUE_URL);
        try {
            ResponseEntity<ChartDTO[]> responseEntity = restTemplate.getForEntity(url, ChartDTO[].class);
            if (responseEntity.getBody() != null) {
                return Arrays.asList(responseEntity.getBody());
            } else {
                return new ArrayList<>();
            }
        } catch (RestClientException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<ChartDTO> getTopGoods(String custId, String type) {
        RestTemplate restTemplate = new RestTemplate();
        String query = "custId=" + custId + "&type=" + type;
        String url = getUrlLoadBalancingQuery(query, GET_TOP_GOODS_URL);
        try {
            ResponseEntity<ChartDTO[]> responseEntity = restTemplate.getForEntity(url, ChartDTO[].class);
            if (responseEntity.getBody() != null) {
                return Arrays.asList(responseEntity.getBody());
            } else {
                return new ArrayList<>();
            }
        } catch (RestClientException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<ChartDTO> getKPIStorage(String custId, String type, String userId) {
        RestTemplate restTemplate = new RestTemplate();
        String query = "custId=" + custId + "&type=" + type + "&userId=" + userId;
        String url = getUrlLoadBalancingQuery(query, GET_KPI_STORAGE_URL);
        try {
            ResponseEntity<ChartDTO[]> responseEntity = restTemplate.getForEntity(url, ChartDTO[].class);
            if (responseEntity.getBody() != null) {
                return Arrays.asList(responseEntity.getBody());
            } else {
                return new ArrayList<>();
            }
        } catch (RestClientException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<ChartDTO> getTransaction(String custId, String type, String userId) {
        //todo postponse
        RestTemplate restTemplate = new RestTemplate();
        String query = "custId=" + custId + "&type=" + type + "&userId=" + userId;
        String url = getUrlLoadBalancingQuery(query, GET_TRANSACTION_URL);
        try {
            ResponseEntity<ChartDTO[]> responseEntity = restTemplate.getForEntity(url, ChartDTO[].class);
            if (responseEntity.getBody() != null) {
                return Arrays.asList(responseEntity.getBody());
            } else {
                return new ArrayList<>();
            }
        } catch (RestClientException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public InventoryInfoDTO getInventoryInfor(String custId) {
        RestTemplate restTemplate = new RestTemplate();
        String query = "custId=" + custId;
        String url = getUrlLoadBalancingQuery(query, GET_INVENTORY_INFO_URL);
        try {
            ResponseEntity<InventoryInfoDTO> responseEntity = restTemplate.getForEntity(url, InventoryInfoDTO.class);
            if (responseEntity.getBody() != null) {
                return responseEntity.getBody();
            } else {
                return new InventoryInfoDTO();
            }
        } catch (RestClientException e) {
            e.printStackTrace();
            return new InventoryInfoDTO();
        }
    }

}
