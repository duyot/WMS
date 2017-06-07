package com.wms.services.impl;

import com.wms.dataprovider.StatisticDP;
import com.wms.dto.AuthTokenInfo;
import com.wms.dto.ChartDTO;
import com.wms.services.interfaces.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by duyot on 5/18/2017.
 */
@Service("statisticService")
public class StatisticServiceImpl implements StatisticService{
    @Autowired
    StatisticDP statisticDP;

    @Override
    public List<ChartDTO> getRevenue(String custId, String type,AuthTokenInfo tokenInfo) {
        return statisticDP.getRevenue(custId,type,tokenInfo);
    }
}
