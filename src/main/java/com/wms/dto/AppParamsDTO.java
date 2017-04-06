package com.wms.dto;

/**
<<<<<<< HEAD
 * Created by doanlv4 on 25/03/2017.
 */
public class AppParamsDTO {
    private String id;
    private String name;
    private String code;
    private String status;
    private String type;
    private String parOrder;



    public AppParamsDTO(String id, String code, String name, String status, String type, String parOrder ) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.type = type;
        this.parOrder = parOrder;
        this.code = code;
=======
 * Created by doanlv4 on 28/03/2017.
 */
public class AppParamsDTO {
    private String id;
    private String code;
    private String name;
    private String status;
    private String statusName;
    private String parOrder;

    public AppParamsDTO(String id, String code, String name, String status, String parOrder) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
        this.parOrder = parOrder;
>>>>>>> 82a5caa22e2998c70ccea91139b582f9c2701375
    }

    public AppParamsDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

<<<<<<< HEAD
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

=======
>>>>>>> 82a5caa22e2998c70ccea91139b582f9c2701375
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

<<<<<<< HEAD
=======
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

>>>>>>> 82a5caa22e2998c70ccea91139b582f9c2701375
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

<<<<<<< HEAD
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
=======
    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
>>>>>>> 82a5caa22e2998c70ccea91139b582f9c2701375
    }

    public String getParOrder() {
        return parOrder;
    }

    public void setParOrder(String parOrder) {
        this.parOrder = parOrder;
    }
<<<<<<< HEAD

    @Override
    public String toString() {
        return "AppParamsDTO{" +
                "id='" + id + '\'' +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                ", parOrder='" + parOrder + '\'' +
                '}';
    }
=======
>>>>>>> 82a5caa22e2998c70ccea91139b582f9c2701375
}
