package com.wms.utils;

import com.wms.constants.Constants;
import javax.servlet.http.HttpServletRequest;

public class SessionUtils {
    public static void setGoodsModified(HttpServletRequest request) {
        request.getSession().setAttribute(Constants.DATA_MODIFIED.IMPORT_GOODS_MODIFIED, true);
        request.getSession().setAttribute(Constants.DATA_MODIFIED.EXPORT_GOODS_MODIFIED, true);
    }

    public static void setStockModified(HttpServletRequest request) {
        request.getSession().setAttribute(Constants.DATA_MODIFIED.IMPORT_STOCK_MODIFIED, true);
        request.getSession().setAttribute(Constants.DATA_MODIFIED.EXPORT_STOCK_MODIFIED, true);
    }

    public static void setCellModified(HttpServletRequest request) {
        request.getSession().setAttribute(Constants.DATA_MODIFIED.IMPORT_CELL_MODIFIED, true);
        request.getSession().setAttribute(Constants.DATA_MODIFIED.EXPORT_CELL_MODIFIED, true);
        request.getSession().setAttribute(Constants.DATA_MODIFIED.EXPORT_ORDER_CELL_MODIFIED, true);
    }

    public static void setGoodsGroupModified(HttpServletRequest request) {
        request.getSession().setAttribute(Constants.DATA_MODIFIED.GOODS_GROUP_MODIFIED, true);
    }


    public static void setReloadedModified(HttpServletRequest request, String properties) {
        request.getSession().setAttribute(properties, false);
    }

    public static boolean isPropertiesModified(HttpServletRequest request, String properties) {
        if (request.getSession().getAttribute(properties) == null) {
            return false;
        }
        return (boolean) request.getSession().getAttribute(properties);
    }
}
