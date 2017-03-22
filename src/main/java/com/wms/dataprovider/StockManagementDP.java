package com.wms.dataprovider;

import com.wms.constants.Constants;
import com.wms.dto.AuthTokenInfo;
import com.wms.dto.ResponseObject;
import com.wms.dto.StockTransDTO;
import com.wms.utils.BundleUtils;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

/**
 * Created by duyot on 2/16/2017.
 */
@Repository
public class StockManagementDP {
    private final String IMPORT_STOCK_URL = BundleUtils.getkey("rest_service_url") + Constants.SERVICE_PREFIX.STOCK_MANAGEMENT_SERVICE + "import";
    private final String EXPORT_STOCK_URL = BundleUtils.getkey("rest_service_url") + Constants.SERVICE_PREFIX.STOCK_MANAGEMENT_SERVICE + "export";

    public ResponseObject importStock(StockTransDTO stockTrans, AuthTokenInfo tokenInfo){
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(IMPORT_STOCK_URL+"?access_token="+tokenInfo.getAccess_token(),stockTrans,ResponseObject.class);
    }

    public ResponseObject exportStock(StockTransDTO stockTrans, AuthTokenInfo tokenInfo){
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(EXPORT_STOCK_URL+"?access_token="+tokenInfo.getAccess_token(),stockTrans,ResponseObject.class);
    }

}
