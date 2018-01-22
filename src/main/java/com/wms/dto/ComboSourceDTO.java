package com.wms.dto;

public class ComboSourceDTO {
    //item in editable
    private int value;
    private String text;
    //
    private String code;
    private String name;

    public ComboSourceDTO(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public ComboSourceDTO(int value, String text, String code, String name) {
        this.value = value;
        this.text = text;
        this.code = code;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
