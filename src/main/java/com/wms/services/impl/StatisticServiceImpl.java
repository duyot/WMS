package com.wms.services.impl;

import com.wms.dataprovider.StatisticDP;
import com.wms.dto.CatGoodsDTO;
import com.wms.dto.CatStockDTO;
import com.wms.dto.ChartDTO;
import com.wms.services.interfaces.StatisticService;
import com.wms.utils.DataUtil;
import com.wms.utils.FunctionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by duyot on 5/18/2017.
 */
@Service("statisticService")
public class StatisticServiceImpl  extends BaseServiceImpl<ChartDTO,StatisticDP> implements StatisticService{
    @Autowired
    StatisticDP statisticDP;

    @Override
    public List<ChartDTO> getRevenue(String custId, String type ) {
        return statisticDP.getRevenue(custId,type);
    }

    @Override
    public List<ChartDTO> getTopGoods(String custId, String type, Map<String,CatGoodsDTO> mapGoods ) {
        List<ChartDTO> lstChart =  statisticDP.getTransaction(custId,type);
        if(!DataUtil.isListNullOrEmpty(lstChart)){
            for(ChartDTO i: lstChart){
                i.setName(FunctionUtils.getMapValue(mapGoods,i.getName()));
            }
        }
        //
        return lstChart;
    }


    @Override
    public List<ChartDTO> getKPIStorage(String custId, String type, Map<String,CatStockDTO> mapStock ) {
        List<ChartDTO> lstChart =  statisticDP.getKPIStorage(custId,type);
        if(!DataUtil.isListNullOrEmpty(lstChart)){
            for(ChartDTO i: lstChart){
                i.setName(FunctionUtils.getMapValue(mapStock,i.getName()));
            }
        }
        //
        return lstChart;
    }

    @Override
    public List<ChartDTO> getTransaction(String custId, String type , Map<String,CatStockDTO> mapStock ) {
        List<ChartDTO> lstChart =  statisticDP.getTransaction(custId,type);
        if(!DataUtil.isListNullOrEmpty(lstChart)){
            for(ChartDTO i: lstChart){
                i.setName(FunctionUtils.getMapValue(mapStock,i.getName()));
            }
        }
        //
        return lstChart;
    }

}
