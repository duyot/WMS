package com.wms.constants;

/**
 * Created by duyot on 10/30/2016.
 */
public class Constants {

    public static final String STATS_ALL = "-1";
    public static final String IS_SERIAL = "1";
    public static final String NO_SERIAL = "0";
    public static final String SUCCES_COLOR = "color:#337ab7;";
    public static final String TYPE_EXPORTED = "Phiếu xuất";
    public static final String TYPE_MANUAL = "Nhập tay";
    public static final String REVENUE_NO_VAT = "Không thuế";
    public static final String PAYMENT_NOT_COMPLETE = "Chưa thanh toán";
    public static final String PAYMENT_PROCESSING = "Đang thanh toán";
    public static final String PAYMENT_COMPLETE = "Đã thanh toán";

    public static class APP_PARAMS {
        public static String TRANS_TYPE = "TRANS_TYPE";
        public static String UNIT_TYPE = "UNIT_TYPE";
        public static String GOODS_STATE = "GOODS_STATE";
        public static String STOCK_STATUS = "STOCK_STATUS";
        public static String STATUS = "STATUS";
        public static String DELIVERY_STATUS = "DELIVERY_STATUS";

    }

    public static class RESPONSE {
        public static String UPDATE_SUSSESS = "update.success";
        public static String UPDATE_ERROR = "update.error";
        public static String INSERT_SUSSESS = "insert.success";
        public static String INSERT_ERROR = "insert.error";
        public static String DELETE_SUSSESS = "delete.success";
        public static String DELETE_ERROR = "delete.error";
        public static String REGISTER_SUSSESS = "register.success";
        public static String REGISTER_ERROR = "register.error";
    }

    public static class FILE_RESOURCE {
        public static String IMPORT_ERROR_TEMPLATE = "File_loi_nhap_hang.xlsx";
        public static String EXPORT_ERROR_TEMPLATE = "File_loi_xuat_hang.xlsx";
        public static String IMPORT_GOODS_ERROR_TEMPLATE = "File_loi_hang_hoa.xlsx";
        public static String GOODS_TOTAL_TEMPLATE = "Thong_tin_hang_trong_kho.xlsx";
        public static String GOODS_DETAILS_TEMPLATE = "Thong_tin_chi_tiet_hang_trong_kho.xlsx";
        public static String GOODS_DETAILS_SERIAL_TEMPLATE = "Thong_tin_chi_tiet_hang_trong_kho_serial.xlsx";
        public static String GOODS_STOCK_DETAILS_TEMPLATE = "Thong_tin_chi_tiet_hang_ton_kho_all.xlsx";
        public static String GOODS_DETAILS_SEARCH_SERIAL_TEMPLATE = "Thong_tin_tim_kiem_serial.xlsx";
        public static String IMPORT_TEMPLATE = "Bieu_mau_nhap_hang.xlsx";
        public static String EXPORT_TEMPLATE = "Bieu_mau_xuat_hang.xlsx";
        public static String IMPORT_SERIAL_TEMPLATE = "Import_serial.xlsx";
        public static String IMPORT_GOODS_TEMPLATE = "Bieu_mau_hang_hoa.xlsx";
        public static String LIST_TRANS_TEMPLATE = "Thong_tin_ds_giao_dich.xlsx";
        public static String LIST_REVENUE_TEMPLATE = "Thong_tin_ds_doanhthu.xlsx";
        public static String LIST_TRANS_DETAIL_TEMPLATE = "Thong_tin_chitiet_giao_dich.xlsx";
        public static String EXPORT_TRANS_TEMPLATE = "PhieuXuatKho_KhongSerial.jrxml";
        public static String IMPORT_TRANS_TEMPLATE = "PhieuNhapKho_KhongSerial.jrxml";
        public static String IMPORT_BILL = "import_bill.jasper";
        public static String EXPORT_BILL = "export_bill.jasper";
        public static String EXPORT_ORDER_BILL = "order_export_File.jasper";
        public static String INVOICE_80 = "invoice_80.jasper";
        public static String LIST_DELIVERY_TEMPLATE = "Thong_tin_ds_van_chuyen.xlsx";

    }

    public static class STATUS {
        public static String ACTIVE = "1";
        public static String IN_ACTIVE = "0";
        public static String DELETED = "-1";
    }

    public static class IMPORT_TYPE {
        public static String IMPORT = "1";
        public static String EXPORT = "2";
    }

    public static class SERIAL_TYPE {
        public static String IS_SERIAL = "1";
        public static String IS_SERIAL_NAME = "Có";
        public static String NO_SERIAL_NAME = "Không";
    }

    public static class SERVICE_METHOD {
        public static String GET_SYS_DATE = "getSysDate?access_token=";
        public static String GET_SYS_DATE_PATTERN = "getSysDateWithPattern?access_token=";
        public static String ACCESS_TOKEN = "?access_token=";
        public static String ACCESS_TOKEN_AND = "&access_token=";
        public static String ADD = "add?access_token=";
        public static String ADD_LIST = "addList?access_token=";
        public static String UPDATE = "update?access_token=";
        public static String UPDATE_BYE_PROPERTIES = "updateByProperties?access_token=";
        public static String DELETE = "delete/";
        public static String FIND_BY_ID = "find/";
        public static String FIND_BY_CONDITION = "findByCondition?access_token=";
        public static String DELETE_BY_CONDITION = "deleteByCondition?access_token=";
        public static String COUNT_BY_CONDITION = "countByCondition?access_token=";
        public static String GET_ALL = "getAll?access_token=";
    }

    public static class SERVICE_PREFIX {
        public static String ROLE_SERVICE = "sysRoleServices/";
        public static String USER_SERVICE = "catUserServices/";
        public static String CUSTOMER_SERVICE = "catCustomerServices/";
        public static String CAT_GOODS_GROUP_SERVICE = "catGoodsGroupServices/";
        public static String GOODS_SERVICE = "catGoodsServices/";
        public static String CAT_STOCK_SERVICE = "catStockServices/";
        public static String CAT_PARTNER_SERVICE = "catPartnerServices/";
        public static String CAT_REASON_SERVICE = "catReasonServices/";
        public static String CAT_DEPARTMENT_SERVICE = "catDepartmentServices/";
        public static String STOCK_MANAGEMENT_SERVICE = "stockManagementServices/";
        public static String ERR$MJR_STOCK_GOODS_SERIAL_SERVICE = "err$MjrStockGoodsSerialServices/";
        public static String MJR_STOCK_GOODS_SERVICE = "mjrStockGoodsServices/";
        public static String MJR_STOCK_GOODS_SERIAL_SERVICE = "mjrStockGoodsSerialServices/";
        public static String MJR_STOCK_GOODS_TOTAL_SERVICE = "mjrStockGoodsTotalServices/";
        public static String MJR_STOCK_TRANS_SERVICE = "mjrStockTransServices/";
        public static String MJR_ORDER_SERVICE = "mjrOrderServices/";
        public static String MJR_STOCK_TRANS_DETAIL_SERVICE = "mjrStockTransDetailServices/";
        public static String APP_PARAMS_SERVICE = "appParamsServices/";
        public static String CAT_STOCK_CELL_SERVICE = "catStockCellServices/";
        public static String SYS_ROLE_MENU_SERVICE = "sysRoleMenuServices/";
        public static String SYS_ROLE_SERVICE = "sysRoleServices/";
        public static String SYS_MENU_SERVICE = "sysMenuServices/";
        public static String MAP_USER_STOCK = "mapUserStockServices/";
        public static String MAP_USER_PARTNER = "mapUserPartnerServices/";
        public static String STATISTIC_SERVICE = "statisticServices/";
        public static String REVENUE_SERVICE = "revenueServices/";
    }

    public static class SQL_OPERATOR {
        public static String EQUAL = "EQUAL";
        public static String NOT_EQUAL = "NOT_EQUAL";
        public static String GREATER = "GREATER";
        public static String GREATER_EQUAL = "GREATER_EQUAL";
        public static String LOWER = "LOWER";
        public static String LOWER_EQUAL = "LOWER_EQUAL";
        public static String IN = "IN";
        public static String LIKE = "LIKE";
        public static String ORDER = "ORDER";
        public static String VNM_ORDER = "VNM_ORDER";
        public static String BETWEEN = "BETWEEN";
        public static String LIMIT = "LIMIT";
        public static String OFFSET = "OFFSET";
    }

    public static class SQL_PRO_TYPE {
        public static String STRING = "string";
        public static String LONG = "long";
        public static String BYTE = "byte";
        public static String DATE = "date";
    }

    public static class SQL_LOGIC {
        public static String OR = " or ";
        public static String AND = " and ";
    }

    public static class DATA_MODIFIED {
        public static String IMPORT_GOODS_MODIFIED = "import.goods.modified";
        public static String IMPORT_STOCK_MODIFIED = "import.stock.modified";
        public static String IMPORT_CELL_MODIFIED = "import.cell.modified";
        //
        public static String EXPORT_GOODS_MODIFIED = "export.goods.modified";
        public static String EXPORT_STOCK_MODIFIED = "export.stock.modified";
        public static String EXPORT_CELL_MODIFIED = "export.cell.modified";
        //
        public static String EXPORT_ORDER_CELL_MODIFIED = "export.order.cell.modified";
        //
        public static String GOODS_GROUP_MODIFIED = "goods.group.modified";

        public static String PARTNER_MODIFIED = "parner.modified";

    }
}
