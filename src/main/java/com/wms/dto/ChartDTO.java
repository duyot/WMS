package com.wms.dto;

import java.math.BigInteger;

/**
 * Created by duyot on 5/17/2017.
 */
public class ChartDTO {
    private String name;
    private Double[] data;
    private String [] xAxisData;
    private Double y;

    public ChartDTO() {
    }

    public ChartDTO(String name, Double y) {
        this.name = name;
        this.y = y;
    }

    public ChartDTO(String name, Double[] data, String[] xAxisData, Double y) {
        this.name = name;
        this.data = data;
        this.xAxisData = xAxisData;
        this.y = y;
    }

    public ChartDTO(String name, Double[] data, String[] xAxisData) {
        this.name = name;
        this.data = data;
        this.xAxisData = xAxisData;
    }

    public ChartDTO(String name, Double [] data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double[] getData() {
        return data;
    }

    public void setData(Double[] data) {
        this.data = data;
    }

    public String[] getxAxisData() {
        return xAxisData;
    }

    public void setxAxisData(String[] xAxisData) {
        this.xAxisData = xAxisData;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }
}
