package com.wms.dto;

import java.util.List;

/**
 * Created by truongbx
 */


public class OrderExportDTO {
    MjrOrderDTO mjrOrderDTO;
    List<MjrOrderDetailDTO> lstMjrOrderDetailDTOS;

    public MjrOrderDTO getMjrOrderDTO() {
        return mjrOrderDTO;
    }

    public void setMjrOrderDTO(MjrOrderDTO mjrOrderDTO) {
        this.mjrOrderDTO = mjrOrderDTO;
    }

    public List<MjrOrderDetailDTO> getLstMjrOrderDetailDTOS() {
        return lstMjrOrderDetailDTOS;
    }

    public void setLstMjrOrderDetailDTOS(List<MjrOrderDetailDTO> lstMjrOrderDetailDTOS) {
        this.lstMjrOrderDetailDTOS = lstMjrOrderDetailDTOS;
    }
}
