package com.vivas.constants;

/**
 * Created by duyot on 10/30/2016.
 */
public class Constants {

    public static final String STATS_ALL = "-1";
    public static final String STATS_NONE = "";

    public static class SERVICE_METHOD{
        public static String ADD = "add";
        public static String UPDATE = "update";
        public static String DELETE = "delete/";
        public static String FIND_BY_ID = "find/";
        public static String FIND_BY_CONDITION = "findByCondition";
        public static String GET_ALL = "getAll";
    }

    public static class SQL_OPERATOR{
        public static String EQUAL = "EQUAL";
        public static String NOT_EQUAL = "NOT_EQUAL";
        public static String GREATER = "GREATER";
        public static String GREATER_EQAL = "GREATER_EQAL";
        public static String LOWER = "LOWER";
        public static String LOWER_EQUAL = "LOWER_EQUAL";
        public static String IN = "IN";
        public static String LIKE = "LIKE";
    }

}
