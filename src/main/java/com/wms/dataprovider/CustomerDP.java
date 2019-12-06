package com.wms.dataprovider;

import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.dto.CatCustomerDTO;
import com.wms.dto.ResponseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;

/**
 * Created by duyot on 12/6/2016.
 */
@Repository
public class CustomerDP extends BaseDP<CatCustomerDTO> {
    Logger log = LoggerFactory.getLogger(CustomerDP.class);

    public CustomerDP() {
        super(CatCustomerDTO[].class, CatCustomerDTO.class, Constants.SERVICE_PREFIX.CUSTOMER_SERVICE);
    }

    @Override
    public ResponseObject add(CatCustomerDTO Dto) {
        try {
            String url = getUrlWithoutTokenKey("register/addCustomer");
            return restTemplate.postForObject(url, Dto, ResponseObject.class);
        } catch (RestClientException e) {
            log.info(e.toString());
            return null;
        }
    }
}
