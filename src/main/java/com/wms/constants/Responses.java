package com.wms.constants;

/**
 * Created by duyot on 10/4/2016.
 */
public enum Responses {
    SUCCESS("SUCCESS","200"),ERROR("FAIL","-1"),ERROR_CONSTRAINT("FAIL_CONSTRAINT","ORA-00001");
    public String statusName;
    public String statusCode;
    Responses(String statusName, String statusCode){
        this.statusCode = statusCode;
        this.statusName = statusName;
    }
    public String getName(){
        return this.statusName;
    }
    public String getCode(){
        return this.statusCode;
    }

    public static String getCodeByName(String name) {
        for(Responses e : values()) {
            if(e.statusName.equals(name)) return e.getCode();
        }
        return "";
    }

    public static void main(String[] args) {
        System.out.println(Responses.getCodeByName("SUCCESS"));
    }

}
