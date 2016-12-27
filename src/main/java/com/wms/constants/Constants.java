package com.wms.constants;

/**
 * Created by duyot on 10/30/2016.
 */
public class Constants {

    public static final String STATS_ALL = "-1";
    public static final String STATS_NONE = "";
    public static final String SUCCES_COLOR = "color:#337ab7;";

    public static class STATUS{
        public static String ACTIVE = "1";
        public static String INACTIVE = "0";
    }

    public static class SERVICE_METHOD{
        public static String ADD = "add";
        public static String UPDATE = "update";
        public static String DELETE = "delete/";
        public static String FIND_BY_ID = "find/";
        public static String FIND_BY_CONDITION = "findByCondition";
        public static String GET_ALL = "getAll";
    }

    public static class SERVICE_PREFIX{
        public static String ROLE_SERVICE = "roleservices/";
        public static String USER_SERVICE = "userservices/";
        public static String CUSTOMER_SERVICE = "customerservices/";
        public static String CAT_GOODS_GROUP_SERVICE = "catgoodsgroupservices/";
        public static String GOODS_SERVICE = "goodsservices/";
    }

    public static class SQL_OPERATOR{
        public static String EQUAL = "EQUAL";
        public static String NOT_EQUAL = "NOT_EQUAL";
        public static String GREATER = "GREATER";
        public static String GREATER_EQAL = "GREATER_EQUAL";
        public static String LOWER = "LOWER";
        public static String LOWER_EQUAL = "LOWER_EQUAL";
        public static String IN = "IN";
        public static String LIKE = "LIKE";
        public static String ORDER = "ORDER";
        public static String BETWEEN = "BETWEEN";
    }

    public static class SQL_PRO_TYPE{
        public static String STRING = "string";
        public static String LONG   = "long";
        public static String DATE   = "date";
    }

}
