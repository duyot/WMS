package com.wms.dto;

/**
 * Created by duyot on 11/2/2016.
 */
public class SysRoleMenuDTO {
    private String id;
    private String roleCode;
    private String menuId;
    private String cusId;

    public SysRoleMenuDTO(String id, String roleId, String menuId,String cusId) {
        this.id = id;
        this.roleCode = roleId;
        this.menuId = menuId;
        this.cusId= cusId;
    }

    public SysRoleMenuDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getCusId() {
        return cusId;
    }

    public void setCusId(String cusId) {
        this.cusId = cusId;
    }
}
