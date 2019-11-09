package com.wms.services.impl;

import com.wms.dataprovider.MjrOrderDP;
import com.wms.dataprovider.MjrStockTransDP;
import com.wms.dto.*;
import com.wms.services.interfaces.OrderExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

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

	@Override
	public List<RealExportExcelDTO> orderExportExcel(String orderId) {
		return mjrOrderDP.orderDataExport(orderId);
	}

	@Override
	public List<MjrOrderDetailDTO> getListOrderDetail(String orderId) {
		return mjrOrderDP.getListOrderDetail(orderId);
	}
}
