package com.wms.dataprovider;

import com.wms.base.BaseDP;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.*;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
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
 * Created by duyot on 4/4/2017.
 */
@Repository
public class MjrOrderDP extends BaseDP<MjrOrderDTO> {
	public static final String ORDER_EXPORT = "orderExport?access_token=";
	public static final String ORDER_DATA_EXPORT = "getExportData/";
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
	public List<RealExportExcelDTO> orderDataExport(String id) {
		try {
			String url = getUrlLoadBalancing(Long.parseLong(id), ORDER_DATA_EXPORT);
			ResponseEntity<RealExportExcelDTO[]> responseEntity =  restTemplate.exchange(url, HttpMethod.GET,null,RealExportExcelDTO[].class);
			return  Arrays.asList(responseEntity.getBody());
		} catch (RestClientException e) {
			log.info(e.toString());
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
}
