package com.wms.dto;

import java.math.BigInteger;

/**
 * Created by duyot on 5/17/2017.
 */
public class ChartDTO {
    private String name;
    private BigInteger[] data;
    private String [] xAxisData;
    private BigInteger y;

    public ChartDTO() {
    }

    public ChartDTO(String name, BigInteger y) {
        this.name = name;
        this.y = y;
    }

    public ChartDTO(String name, BigInteger[] data, String[] xAxisData, BigInteger y) {
        this.name = name;
        this.data = data;
        this.xAxisData = xAxisData;
        this.y = y;
    }

    public ChartDTO(String name, BigInteger[] data, String[] xAxisData) {
        this.name = name;
        this.data = data;
        this.xAxisData = xAxisData;
    }

    public ChartDTO(String name, BigInteger [] data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigInteger[] getData() {
        return data;
    }

    public void setData(BigInteger[] data) {
        this.data = data;
    }

    public String[] getxAxisData() {
        return xAxisData;
    }

    public void setxAxisData(String[] xAxisData) {
        this.xAxisData = xAxisData;
    }

    public BigInteger getY() {
        return y;
    }

    public void setY(BigInteger y) {
        this.y = y;
    }
}
