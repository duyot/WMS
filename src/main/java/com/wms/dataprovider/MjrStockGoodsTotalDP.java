package com.wms.dataprovider;

import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.MjrStockGoodsTotalDTO;
import com.wms.dto.MjrStockTransDetailDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by duyot on 3/24/2017.
 */
@Repository
public class MjrStockGoodsTotalDP extends BaseDP<MjrStockGoodsTotalDTO> {
    private Logger log = LoggerFactory.getLogger(MjrStockGoodsTotalDP.class);
    private  final String GET_COUNTGOODS_DETAIL    ="getCountGoodsDetail";
    private  final String GET_GOODS_DETAIL    ="getGoodsDetail";
    private  final String GET_ALL_GOODS_DETAIL    ="getAllStockGoodsDetail";
    private  final String FIND_MORE_CONDITION    ="findMoreCondition?access_token=";
    public MjrStockGoodsTotalDP() {
        super(MjrStockGoodsTotalDTO[].class,MjrStockGoodsTotalDTO.class, Constants.SERVICE_PREFIX.MJR_STOCK_GOODS_TOTAL_SERVICE);
    }

    public Long getCountGoodsDetail(String custId,String stockId, String goodsId,String isSerial,String goodsState,String partnerId ){
        String query =   "custId="+custId+"&stockId="+stockId+"&goodsId="+goodsId+"&isSerial="+isSerial+
                "&goodsState="+goodsState+"&partnerId="+partnerId;
        String url = getUrlLoadBalancingQuery(query,Constants.SERVICE_PREFIX.STOCK_MANAGEMENT_SERVICE, GET_COUNTGOODS_DETAIL);
        try {
            return  restTemplate.getForObject(url, Long.class);
        } catch (RestClientException e) {
            log.error(e.toString());
            return 0L;
        }
    }
    //Lay theo partnerId
    public List<MjrStockTransDetailDTO> getGoodsDetail(String custId, String stockId, String goodsId,
                                                       String isSerial, String goodsState,String partnerId, String limit, String offset ) {

        String query =   "custId="+custId+"&stockId="+stockId+"&goodsId="+goodsId+"&isSerial="+isSerial+
        "&goodsState="+goodsState+"&partnerId="+partnerId+"&limit="+limit+"&offset="+offset ;
        String url = getUrlLoadBalancingQuery(query, GET_GOODS_DETAIL);
        try {
            ResponseEntity<MjrStockTransDetailDTO[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET,null ,MjrStockTransDetailDTO[].class);
            return Arrays.asList(responseEntity.getBody());
        } catch (RestClientException e) {
            log.error(e.toString());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<MjrStockTransDetailDTO> getAllStockGoodsDetail(String userId, String custId, String stockId, String partnerId, String goodsId, String status ) {

        String query = "userId="+userId+"&custId="+custId+"&stockId="+stockId+"&partnerId="+partnerId+"&goodsId="+goodsId+ "&goodsState="+status;
        String url = getUrlLoadBalancingQuery(query, GET_ALL_GOODS_DETAIL);
        try {
            ResponseEntity<MjrStockTransDetailDTO[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET,null ,MjrStockTransDetailDTO[].class);
            return Arrays.asList(responseEntity.getBody());
        } catch (RestClientException e) {
            log.error(e.toString());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public List<MjrStockGoodsTotalDTO> findMoreCondition(MjrStockGoodsTotalDTO searchGoodsTotalDTO ) {
        String url = getUrlLoadBalancing(0, FIND_MORE_CONDITION);
        try {
            ResponseEntity<MjrStockGoodsTotalDTO[]> responseEntity = restTemplate.postForEntity(url, searchGoodsTotalDTO,valueArrayClass);
            return Arrays.asList(responseEntity.getBody());
        } catch (RestClientException e) {
            log.error(e.toString());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
