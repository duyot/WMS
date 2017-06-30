package com.wms.utils;

import com.google.gson.Gson;

/**
 * Created by duyot on 7/7/2016.
 */
public class JSONUtils {
    public static Gson gson = new Gson();

    public static String object2JSONString(Object object){
        return gson.toJson(object);
    }

//    public static JsonObject object2JSONObject(RQST object){
//        JsonParser parser = new JsonParser();
//        return parser.parse(gson.toJson(object)).getAsJsonObject();
//    }

    public static  <T> T stringToObject(String jsonString,Class objectClass){
        return (T)gson.fromJson(jsonString,objectClass);
    }

    public static void main(String[] args) {
    }

}
