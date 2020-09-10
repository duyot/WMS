package com.wms.services.interfaces;

import com.wms.dto.RevenueDTO;

import java.util.List;

public interface RevenueService extends BaseService<RevenueDTO> {
    public List<RevenueDTO> getSumRevenue(String custId, String partnerId, String startDate, String endDate);
}
