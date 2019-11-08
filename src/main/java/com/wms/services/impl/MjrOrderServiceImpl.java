package com.wms.services.impl;

import com.wms.dataprovider.MjrOrderDP;
import com.wms.dataprovider.MjrStockTransDP;
import com.wms.dto.MjrOrderDTO;
import com.wms.dto.MjrStockTransDTO;
import com.wms.dto.OrderExportDTO;
import com.wms.dto.ResponseObject;
import com.wms.services.interfaces.OrderExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by duyot on 4/4/2017.
 */
@Service("mjrOrderService")
public class MjrOrderServiceImpl extends BaseServiceImpl<MjrOrderDTO, MjrOrderDP> implements OrderExportService {

	@Autowired
	MjrOrderDP mjrOrderDP;

	@PostConstruct
	public void setupService() {
		this.tdp = mjrOrderDP;
	}

	@Override
	public ResponseObject orderExport(OrderExportDTO orderExportDTO) {
		return mjrOrderDP.orderExport(orderExportDTO);
	}
}
