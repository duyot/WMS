package com.vivas.dto;

/**
 * Created by duyot on 10/24/2016.
 */
public class Condition {
    private String property;
    private String operator;
    private String value;

    public Condition(String property, String operator, String value) {
        this.property = property;
        this.operator = operator;
        this.value = value;
    }

    public Condition() {
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
