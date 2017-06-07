package com.wms.services.interfaces;

import com.wms.dto.AuthTokenInfo;
import com.wms.dto.ChartDTO;

import java.util.List;

/**
 * Created by duyot on 5/18/2017.
 */
public interface StatisticService {
    List<ChartDTO> getRevenue(String custId, String type, AuthTokenInfo tokenInfo);
}
