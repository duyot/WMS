package com.wms.dto;

import com.wms.base.BaseDTO;
import com.wms.base.BaseModel;
import com.wms.persistents.model.Customer;
import com.wms.utils.DateTimeUtils;
import com.wms.utils.StringUtils;

/**
 * Created by duyot on 12/6/2016.
 */
public class CustomerDTO extends BaseDTO{
    private String id;
    private String code;
    private String name;
    private String type;
    private String telNumber;
    private String email;
    private String bankName;
    private String bankAccountCode;
    private String address;
    private String status;
    private String createDate;

    public CustomerDTO(String id, String code, String name, String type, String telNumber, String email, String bankName, String bankAccountCode, String address, String status, String createDate) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.type = type;
        this.telNumber = telNumber;
        this.email = email;
        this.bankName = bankName;
        this.bankAccountCode = bankAccountCode;
        this.address = address;
        this.status = status;
        this.createDate = createDate;
    }

    public CustomerDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTelNumber() {
        return telNumber;
    }

    public void setTelNumber(String telNumber) {
        this.telNumber = telNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAccountCode() {
        return bankAccountCode;
    }

    public void setBankAccountCode(String bankAccountCode) {
        this.bankAccountCode = bankAccountCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    @Override
    public BaseModel toModel() {
        return new Customer(!StringUtils.validString(id) ? null:Long.valueOf(id),
                code,name,type,telNumber,email,bankName,bankAccountCode,address,
                !StringUtils.validString(status) ? null:Long.valueOf(status),
                !StringUtils.validString(createDate) ? null: DateTimeUtils.convertStringToDate(createDate));
    }
}
