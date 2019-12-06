package com.wms.services.interfaces;

import com.wms.dto.ChartDTO;
import java.util.List;

/**
 * Created by duyot on 5/18/2017.
 */
public interface StatisticService {
    List<ChartDTO> getRevenue(String custId, String type);

    List<ChartDTO> getTopGoods(String custId, String type);

    List<ChartDTO> getKPIStorage(String custId, String type, String userId);

    List<ChartDTO> getTransaction(String custId, String type, String userId);
}
