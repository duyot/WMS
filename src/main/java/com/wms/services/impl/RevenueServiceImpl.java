package com.wms.services.impl;

import com.wms.dataprovider.RevenueDP;
import com.wms.dto.RevenueDTO;
import java.util.List;
import javax.annotation.PostConstruct;

import com.wms.services.interfaces.RevenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by duyot on 2/17/2017.
 */
@Service("revenueService")
public class RevenueServiceImpl extends BaseServiceImpl<RevenueDTO, RevenueDP> implements RevenueService {
    @Autowired
    RevenueDP revenueDP;

    @PostConstruct
    public void setupService() {
        this.tdp = revenueDP;
    }

    @Override
    public List<RevenueDTO> getSumRevenue( String custId, String partnerId, String startDate, String endDate) {
        return revenueDP.getSumRevenue(custId,partnerId, startDate, endDate);
    }
}
