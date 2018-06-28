package com.wms.dto;

import java.util.List;

public class UserStock {
    List<CatStockDTO> lstCatStocks;
    String[] userStocks;

    public UserStock(List<CatStockDTO> lstCatStocks, String[] userStocks) {
        this.lstCatStocks = lstCatStocks;
        this.userStocks = userStocks;
    }

    public List<CatStockDTO> getLstCatStocks() {
        return lstCatStocks;
    }

    public void setLstCatStocks(List<CatStockDTO> lstCatStocks) {
        this.lstCatStocks = lstCatStocks;
    }

    public String[] getUserStocks() {
        return userStocks;
    }

    public void setUserStocks(String[] userStocks) {
        this.userStocks = userStocks;
    }
}
