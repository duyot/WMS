package com.wms.dto;

import com.wms.constants.Constants;
import com.wms.utils.DataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by duyot on 10/24/2016.
 */
public class Condition {
    private String property;
    private String propertyType;
    private String operator;
    private Object value;
    private List<Condition> lstCondition;
    private  String expType ;
    public Condition() {
    }

    public Condition(String property, String operator, Object value) {
        this.property = property;
        this.operator = operator;
        this.value = value;
        this.propertyType = Constants.SQL_PRO_TYPE.STRING;
    }

    public Condition( String expType , List<Condition> lstCondition) {
        this.lstCondition = lstCondition;
        this.expType = expType;
    }

    public Condition(String property, String propertyType, String operator, Object value) {
        this.property = property;
        this.propertyType = propertyType;
        this.operator = operator;
        if(this.propertyType.equals(Constants.SQL_PRO_TYPE.LONG)){
            if(operator.equals(Constants.SQL_OPERATOR.IN)){
                this.value = DataUtil.convertLongArr(value);
            }else{
                try {
                    this.value = Long.parseLong((String)value);
                } catch (NumberFormatException e) {
                    this.value = value;
                }
            }
        } else{
            this.value = value;
        }
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

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public List<Condition> getLstCondition() {
        return lstCondition;
    }

    public void setLstCondition(List<Condition> lstCondition) {
        this.lstCondition = lstCondition;
    }

    public String getExpType() {
        return expType;
    }

    public void setExpType(String expType) {
        this.expType = expType;
    }

    @Override
    public String toString() {
        return "Condition{" +
                "property='" + property + '\'' +
                ", operator='" + operator + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
