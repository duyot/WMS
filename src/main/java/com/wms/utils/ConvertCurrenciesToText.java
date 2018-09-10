package com.wms.utils;

import java.text.ParseException;
import java.util.HashMap;

import org.apache.commons.lang.NumberUtils;

import com.ibm.icu.text.DecimalFormat;

public class ConvertCurrenciesToText {

    public static HashMap<String, String> hm_tien = new HashMap<String, String>() {
        {
            put("0", "không");
            put("1", "một");
            put("2", "hai");
            put("3", "ba");
            put("4", "bốn");
            put("5", "năm");
            put("6", "sáu");
            put("7", "bảy");
            put("8", "tám");
            put("9", "chín");
        }
    };
    public static HashMap<String, String> hm_hanh = new HashMap<String, String>() {
        {
            put("1", "");
            put("2", "mươi");
            put("3", "trăm");
            put("4", "nghìn");
            put("5", "mươi");
            put("6", "trăm");
            put("7", "triệu");
            put("8", "mươi");
            put("9", "trăm");
            put("10", "tỷ");
            put("11", "mươi");
            put("12", "trăm");
            put("13", "nghìn");
            put("14", "mươi");
            put("15", "trăm");

        }
    };

    public static void main(String[] args) throws ParseException {
        String tien = "59614.00";
        String kq = convertToText(tien);
        System.out.println(currencyFormat(tien));
        System.out.println(kq);

    }

    public static String convertToText(String x) {
        String kq = "";
        x = x.replace(",", "");
        String arr_temp[] = x.split("\\.");
        if (!NumberUtils.isNumber(arr_temp[0])) {
            return "";
        }
        String m = arr_temp[0];
        kq = kq+ numberToText(arr_temp[0]);
        if (arr_temp.length ==2){
            String decimal = numberToText(arr_temp[1]);
            if (!DataUtil.isNullOrEmpty(decimal))
              kq = kq + "phẩy " +decimal ;
        }

        return kq.substring(0,1).toUpperCase() + kq.substring(1).toLowerCase() + "đồng";
    }

    public static String currencyFormat(String curr) {

        Double f = Double.parseDouble(curr.replace(",", ""));
        String format =( new DecimalFormat("###,###.##")).format(f);
        return format;
    }
    public static String numberToText(String m) {
        String kq = "";
        int dem = m.length();
        String dau = "";
        int flag10 = 1;
        while (!m.equals("")) {
            if (m.length() <= 3 && m.length() > 1 && Long.parseLong(m) == 0) {

            } else {
                dau = m.substring(0, 1);
                if (dem % 3 == 1 && m.startsWith("1") && flag10 == 0) {
                    kq += "mốt ";
                    flag10 = 0;
                } else if (dem % 3 == 2 && m.startsWith("1")) {
                    kq += "mười ";
                    flag10 = 1;
                } else if (dem % 3 == 2 && m.startsWith("0") && m.length() >= 2 && !m.substring(1, 2).equals("0")) {
                    //System.out.println("a  "+m.substring(1, 2));
                    kq += "lẻ ";
                    flag10 = 1;
                } else {
                    if (!m.startsWith("0")) {
                        kq += hm_tien.get(dau) + " ";
                        flag10 = 0;
                    }
                }
                if (dem%3!=1 &&m.startsWith("0") && m.length()>1) {
                } else {
                    if (dem % 3 == 2 && (m.startsWith("1") || m.startsWith("0"))) {//mười
                    } else {
                        if (!m.startsWith("0")){
                        kq += hm_hanh.get(dem + "") + " ";
                        }
                    }
                }
            }
            m = m.substring(1);
            dem = m.length();
        }
        kq=kq.substring(0, kq.length() - 1);
      return kq;
    }
}
