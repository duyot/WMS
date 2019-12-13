package com.wms.services.interfaces;

import com.wms.dto.*;
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
