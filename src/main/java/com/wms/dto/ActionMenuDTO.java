package com.wms.dto;

import java.util.List;

/**
 * Created by duyot on 11/3/2016.
 */
public class ActionMenuDTO {
    private SysMenuDTO parentAction;
    private List<SysMenuDTO> lstSubAction;

    public ActionMenuDTO(SysMenuDTO parentAction, List<SysMenuDTO> lstSubAction) {
        this.parentAction = parentAction;
        this.lstSubAction = lstSubAction;
    }

    public ActionMenuDTO() {
    }

    public SysMenuDTO getParentAction() {
        return parentAction;
    }

    public void setParentAction(SysMenuDTO parentAction) {
        this.parentAction = parentAction;
    }

    public List<SysMenuDTO> getLstSubAction() {
        return lstSubAction;
    }

    public void setLstSubAction(List<SysMenuDTO> lstSubAction) {
        this.lstSubAction = lstSubAction;
    }
}
