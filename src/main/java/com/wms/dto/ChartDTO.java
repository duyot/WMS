package com.wms.dto;

import java.math.BigInteger;

/**
 * Created by duyot on 5/17/2017.
 */
public class ChartDTO {
    private String name;
    private BigInteger[] data;

    public ChartDTO() {
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
}
