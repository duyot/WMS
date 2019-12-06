package com.wms.services.impl;

import com.wms.dataprovider.StatisticDP;
import com.wms.dto.ChartDTO;
import com.wms.services.interfaces.StatisticService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by duyot on 5/18/2017.
 */
@Service("statisticService")
public class StatisticServiceImpl extends BaseServiceImpl<ChartDTO, StatisticDP> implements StatisticService {
    @Autowired
    StatisticDP statisticDP;

    @Override
    public List<ChartDTO> getRevenue(String custId, String type) {
        return statisticDP.getRevenue(custId, type);
    }

    @Override
    public List<ChartDTO> getTopGoods(String custId, String type) {
        List<ChartDTO> lstChart = statisticDP.getTopGoods(custId, type);
        return lstChart;
    }

    @Override
    public List<ChartDTO> getTransaction(String custId, String type, String userId) {
        List<ChartDTO> lstChart = statisticDP.getTransaction(custId, type, userId);
        return lstChart;
    }

    @Override
    public List<ChartDTO> getKPIStorage(String custId, String type, String userId) {
        List<ChartDTO> lstChart = statisticDP.getKPIStorage(custId, type, userId);
        return lstChart;
    }

}
