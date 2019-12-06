package com.wms.dto;


/**
 * Created by duyot on 11/2/2016.
 */
public class SysRoleDTO {
    private String id;
    private String code;
    private String name;
    private String status;
    private String statusName;
    private String custId;
    private String menuIds;
    private String type;
    private String custName;

    public SysRoleDTO() {
    }

    public SysRoleDTO(String id, String code, String name, String status, String statusName, String custId, String menuIds, String type) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
        this.statusName = statusName;
        this.custId = custId;
        this.menuIds = menuIds;
        this.type = type;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getMenuIds() {
        return menuIds;
    }

    public void setMenuIds(String menuIds) {
        this.menuIds = menuIds;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }
}
