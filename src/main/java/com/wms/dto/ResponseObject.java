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
    private String filePath;
    //
    private String total;
    private String success;


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

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "ResponseObject{" +
                "statusCode='" + statusCode + '\'' +
                ", statusName='" + statusName + '\'' +
                ", key='" + key + '\'' +
                ", filePath='" + filePath + '\'' +
                ", total='" + total + '\'' +
                ", success='" + success + '\'' +
                '}';
    }
}
