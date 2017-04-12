package com.wms.dto;

/**
 * Created by duyot on 4/11/2017.
 */
public class BaseDTO {
    public String code;
    public String name;

    public BaseDTO(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public BaseDTO() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
