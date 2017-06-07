package com.wms.constants;

/**
 * Created by duyot on 10/4/2016.
 */
public enum Responses {
    SUCCESS("SUCCESS"),ERROR("FAIL"),NOT_FOUND("NOT_FOUND"),
    ERROR_CONSTRAINT("FAIL_CONSTRAINT"),SUCCESS_WITH_ERROR("SUCCESS_WITH_ERROR"),
    //
    ERROR_SYSTEM("ERROR_SYSTEM"),
    //update
    ERROR_UPDATE_TOTAL("ERROR_UPDATE_TOTAL"),ERROR_UPDATE_STOCK_GOODS("ERROR_UPDATE_STOCK_GOODS"),
    //insert
    ERROR_CREATE_TOTAL("ERROR_CREATE_TOTAL"),ERROR_CREATE_STOCK_TRANS_DETAIL("ERROR_CREATE_STOCK_TRANS_DETAIL"),
    ERROR_CREATE_STOCK_TRANS("ERROR_CREATE_STOCK_TRANS"),ERROR_INSERT_STOCK_GOODS("ERROR_INSERT_STOCK_GOODS"),
    //not found
    ERROR_NOT_FOUND_SERIAL("ERROR_NOT_FOUND_SERIAL"),ERROR_NOT_FOUND_STOCK_GOODS("ERROR_NOT_FOUND_STOCK_GOODS"),
    ERROR_NOT_FOUND_GOODS("ERROR_NOT_FOUND_GOODS"),ERROR_NOT_FOUND_TOTAL("ERROR_NOT_FOUND_TOTAL"),
    //in valid
    ERROR_AMOUNT_NOT_VALID("ERROR_AMOUNT_NOT_VALID"),ERROR_OVER_GOODS_NUMBER("ERROR_OVER_GOODS_NUMBER"),
    ERROR_NOT_VALID_GOODS_IN_REQUEST("ERROR_NOT_VALID_GOODS_IN_REQUEST"),ERROR_TOTAL_NOT_ENOUGH("ERROR_TOTAL_NOT_ENOUGH"),
    //
    ;
    public String statusName;
    Responses(String statusName){
        this.statusName = statusName;
    }
    public String getName(){
        return this.statusName;
    }

    public static void main(String[] args) {
    }

}
