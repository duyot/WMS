package com.wms.dataprovider;

import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.MjrOrderDTO;
import com.wms.dto.MjrStockTransDTO;
import com.wms.dto.OrderExportDTO;
import com.wms.dto.ResponseObject;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;

/**
 * Created by duyot on 4/4/2017.
 */
@Repository
public class MjrOrderDP extends BaseDP<MjrOrderDTO> {
	public static final String ORDER_EXPORT = "orderExport?access_token=";
	Logger log = LoggerFactory.getLogger(MjrOrderDP.class);

	public MjrOrderDP() {
		super(MjrOrderDTO[].class, MjrOrderDTO.class, Constants.SERVICE_PREFIX.MJR_ORDER_SERVICE);
	}

	public ResponseObject orderExport(OrderExportDTO orderExportDTO) {
		try {
			String url = getUrlLoadBalancing(0, ORDER_EXPORT);
			return restTemplate.postForObject(url, orderExportDTO, ResponseObject.class);
		} catch (RestClientException e) {
			log.info(e.toString());
			e.printStackTrace();
			return new ResponseObject(Responses.ERROR.getName(), Responses.ERROR.getName(), "");
		}
	}
}
