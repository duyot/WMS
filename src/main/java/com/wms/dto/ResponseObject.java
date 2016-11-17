package com.wms.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by duyot on 10/3/2016.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ResponseObject {
    private String statusCode;
    private String statusName;
    private String key;

    public ResponseObject(String statusCode, String statusName, String key) {
        this.statusCode = statusCode;
        this.statusName = statusName;
        this.key = key;
    }

    public ResponseObject() {
    }

    public ResponseObject(String statusCode, String statusName) {
        this.statusCode = statusCode;
        this.statusName = statusName;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
