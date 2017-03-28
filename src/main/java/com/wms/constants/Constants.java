package com.wms.constants;

/**
 * Created by duyot on 10/30/2016.
 */
public class Constants {

    public static final String STATS_ALL = "-1";
    public static final String STATS_NONE = "";
    public static final String SUCCES_COLOR = "color:#337ab7;";

    public static class FILE_RESOURCE{
        public static String IMPORT_TEMPLATE = "Bieu_mau_nhap_hang.xlsx";
        public static String EXPORT_TEMPLATE = "template_import.xlsx";
        public static String IMPORT_ERROR_TEMPLATE = "template_import_error.xlsx";
    }

    public static class STATUS{
        public static String ACTIVE = "1";
        public static String INACTIVE = "0";
        public static String activeName = "Hiệu lực";
        public static String inactiveName = "Hết hiệu lực";
    }

    public static class IMPORT_TYPE{
        public static String IMPORT = "1";
        public static String EXPORT = "2";
    }
    public static class SERIAL_TYPE{
        public static String IS_SERIAL = "1";
        public static String NO_SERIAL = "0";
        public static String IS_SERIAL_NAME = "Có";
        public static String NO_SERIAL_NAME = "Không";
    }

    public static class SERVICE_METHOD{
        public static String GET_SYS_DATE = "getSysDate?access_token=";
        public static String GET_SYS_DATE_PATTERN = "getSysDateWithPattern?access_token=";

        public static String ADD = "add?access_token=";
        public static String UPDATE = "update?access_token=";
        public static String DELETE = "delete/";
        public static String FIND_BY_ID = "find/";
        public static String FIND_BY_CONDITION = "findByCondition?access_token=";
        public static String GET_ALL = "getAll?access_token=";
    }

    public static class SERVICE_PREFIX{
        public static String ROLE_SERVICE = "sysRoleServices/";
        public static String USER_SERVICE = "catUserServices/";
        public static String CUSTOMER_SERVICE = "catCustomerServices/";
        public static String CAT_GOODS_GROUP_SERVICE = "catGoodsGroupServices/";
        public static String GOODS_SERVICE = "catGoodsServices/";
        public static String CAT_STOCK_SERVICE = "catStockServices/";
        public static String CAT_PARTNER_SERVICE = "catPartnerServices/";
        public static String CAT_DEPARTMENT_SERVICE = "catDepartmentServices/";
        public static String STOCK_MANAGEMENT_SERVICE = "stockManagementServices/";
        public static String ERR$MJR_STOCK_GOODS_SERIAL_SERVICE = "err$MjrStockGoodsSerialServices/";
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
