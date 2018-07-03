package com.wms.dataprovider;

import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.AuthTokenInfo;
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
    public MjrStockGoodsTotalDP() {
        super(MjrStockGoodsTotalDTO[].class,MjrStockGoodsTotalDTO.class, Constants.SERVICE_PREFIX.MJR_STOCK_GOODS_TOTAL_SERVICE);
    }

    public Long getCountGoodsDetail(String custId,String stockId, String goodsId,String isSerial,String goodsState,AuthTokenInfo tokenInfo){
        String url = SERVICE_URL + "stockManagementServices/" + "getCountGoodsDetail?custId="+custId+"&stockId="+stockId+"&goodsId="+goodsId+"&isSerial="+isSerial+
                            "&goodsState="+goodsState+"&access_token="+ tokenInfo.getAccess_token();
        try {
            return  restTemplate.getForObject(url, Long.class);
        } catch (RestClientException e) {
            log.error(e.toString());
            return 0L;
        }
    }
    //Lay theo partnerId
    public List<MjrStockTransDetailDTO> getGoodsDetail(String custId, String stockId, String goodsId,
                                                       String isSerial, String goodsState,String partnerId, String limit, String offset,AuthTokenInfo tokenInfo) {
        String url = SERVICE_URL + SERVICE_PREFIX + "getGoodsDetail?custId="+custId+"&stockId="+stockId+"&goodsId="+goodsId+"&isSerial="+isSerial+
                "&goodsState="+goodsState+"&partnerId="+partnerId+"&limit="+limit+"&offset="+offset+"&access_token="+ tokenInfo.getAccess_token();
        try {
            ResponseEntity<MjrStockTransDetailDTO[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET,null ,MjrStockTransDetailDTO[].class);
            return Arrays.asList(responseEntity.getBody());
        } catch (RestClientException e) {
            log.error(e.toString());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public List<MjrStockGoodsTotalDTO> findMoreCondition(MjrStockGoodsTotalDTO searchGoodsTotalDTO,AuthTokenInfo tokenInfo) {
        String url = SERVICE_URL + SERVICE_PREFIX + "findMoreCondition?access_token="+ tokenInfo.getAccess_token();
        try {
            ResponseEntity<MjrStockGoodsTotalDTO[]> responseEntity = restTemplate.postForEntity(url+ tokenInfo.getAccess_token(), searchGoodsTotalDTO,valueArrayClass);
            return Arrays.asList(responseEntity.getBody());
        } catch (RestClientException e) {
            log.error(e.toString());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
