package com.wms.services.interfaces;

import com.wms.dto.MjrOrderDTO;
import com.wms.dto.MjrOrderDetailDTO;
import com.wms.dto.OrderExportDTO;
import com.wms.dto.RealExportExcelDTO;
import com.wms.dto.ResponseObject;
import java.util.List;

/**
 * Created by truongbx.
 */
public interface OrderExportService extends BaseService<MjrOrderDTO> {
    ResponseObject orderExport(OrderExportDTO orderExportDTO);

    List<RealExportExcelDTO> orderExportExcel(String orderId);

    List<MjrOrderDetailDTO> getListOrderDetail(String orderId);

    ResponseObject deleteOrder(String orderId);
}
