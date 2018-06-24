package com.wms.dto;

/**
 * Created by duyot on 11/2/2016.
 */
public class SysRoleMenuDTO extends BaseDTO {
    private String id;
    private String menuId;
    private String roleId;

    public SysRoleMenuDTO() {
    }

    public SysRoleMenuDTO(String id, String menuId, String roleId) {
        this.id = id;
        this.menuId = menuId;
        this.roleId = roleId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
}
