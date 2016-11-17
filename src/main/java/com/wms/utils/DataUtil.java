/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.wms.utils;

//import com.viettel.language.util.EnumWordType;
//import com.viettel.language.util.MultiLanguageNumberToWords;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author Admin
 * @version 1.0
 */
@SuppressWarnings("deprecation")
public class DataUtil {

    static Logger log = LoggerFactory.getLogger(DataUtil.class);

    public static Long[] convertLongArr(Object input){
        String inputArr = (String)input;
        String [] idArr = ((String) input).split(",");
        Long [] idLong = new Long[idArr.length];
        for(int i=0;i<idArr.length;i++){
            try {
                idLong[i] = Long.parseLong(idArr[i]);
            } catch (NumberFormatException e) {
                idLong[i] = 0L;
                log.info("Error parsing long");
            }
        }
        return idLong;
    }


    public static boolean isEmail(String email){
        return EmailValidator.getInstance().isValid(email);
    }

    public static String MD5Encrypt(String inputString)
    {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(inputString.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    
    //duyot: 22/01: bat truong hop serial truyen sang dang .0
    public static String getQuantity(String quantity) {
        //Nhung tham so con lai truyen vao doi tuong
        double dAmount = Double.parseDouble(quantity);
        long iAmount = (long) dAmount;
        double fAmount = dAmount - iAmount;
        if (fAmount == 0) {
            return String.valueOf(iAmount);
        } else {
            return String.valueOf(quantity);
        }
    }

    public static String getStringNullOrZero(String strNullOrZero) {
        return isStringNullOrEmpty(strNullOrZero) ? "" : strNullOrZero;
    }

    // xoa dau cham 
    public static String removeDotInteger(String number) {
        if (number != null) {
            if (number.contains(".")) {
                number = number.substring(0, number.lastIndexOf("."));
            }
        }
        return number;
    }

    /**
     * forward page
     *
     * @return
     * @author ThanhNT
     */
    public static String forwardPage(String pageName) {
        return "pretty:" + pageName.trim();
    }

    /*
     * Kiem tra Long bi null hoac zero
     *
     * @param value
     * @return
     */
    public static boolean isNullOrZero(Long value) {
        return (value == null || value.equals(0L));
    }

    /**
     * Kiem tra Bigdecimal bi null hoac zero
     *
     * @param value
     * @return
     */
    public static boolean isNullOrZero(BigDecimal value) {
        return (value == null || value.equals(BigDecimal.ZERO));
    }

    public static Object nvl(Object objInput, Object objOutput) {
        if (objInput != null) {
            return objInput;
        } else {
            return objOutput;
        }
    }

    /**
     * Lay ten phuong thuc getter
     *
     * @param columnName
     * @return
     */
    public static String getHibernateName(String columnName) {
        columnName = columnName.toLowerCase();
        String[] arrs = columnName.split("_");
        String method = "";
        for (String arr : arrs) {
            method += DataUtil.upperFirstChar(arr);
        }
        return method;
    }

    /**
     * Lay getter
     *
     * @param columnName
     * @return
     */
    public static String getGetterByColumnName(String columnName) {
        return "get" + getHibernateName(columnName);
    }

    //truong bx3 modify 20/04/2015 for tree
    public static String getGetterOfColumn(String column) {
        return "get" + upperFirstChar(column);

    }

    public static String getSetterOfColumn(String column) {
        return "set" + upperFirstChar(column);

    }
//       truongbx3 finish modify

    /**
     * Lay ten phuong thuc setter
     *
     * @param columnName
     * @return
     */
    public static String getSetterByColumnName(String columnName) {
        return "set" + getHibernateName(columnName);
    }

    /**
     * Upper first character
     *
     * @param input
     * @return
     */
    public static String upperFirstChar(String input) {
        if (DataUtil.isNullOrEmpty(input)) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    /**
     * Lower first characater
     *
     * @param input
     * @return
     */
    public static String lowerFirstChar(String input) {
        if (DataUtil.isNullOrEmpty(input)) {
            return input;
        }
        return input.substring(0, 1).toLowerCase() + input.substring(1);
    }

    /**
     * connect String
     *
     * @param objs Object
     * @return String
     */
    /**
     * @param obj1 Object
     * @return Long
     */
    public static Long safeToLong(Object obj1) {
        Long result = 0L;
        if (obj1 != null) {
            try {
                result = Long.parseLong(obj1.toString());
            } catch (Exception ex) {
            }
        }

        return result;
    }

    public static Double safeToDouble(Object obj1) {
        Double result = 0.0;
        if (obj1 != null) {
            try {
                result = Double.parseDouble(obj1.toString());
            } catch (Exception ex) {
            }
        }

        return result;
    }

    public static Short safeToShort(Object obj1) {
        Short result = 0;
        if (obj1 != null) {
            try {
                result = Short.parseShort(obj1.toString());
            } catch (Exception ex) {
            }
        }

        return result;
    }

    /**
     * @param obj1 Object
     * @return int
     */
    public static int safeToInt(Object obj1) {
        int result = 0;
        if (obj1 == null) {
            return 0;
        }
        try {
            result = Integer.parseInt(obj1.toString());
        } catch (Exception ex) {
        }

        return result;
    }

    /**
     * @param obj1 Object
     * @return String
     */
    public static String safeToString(Object obj1) {
        if (obj1 == null) {
            return "";
        }

        return obj1.toString();
    }

    /**
     * safe equal
     *
     * @param obj1 Long
     * @param obj2 Long
     * @return boolean
     */
    public static boolean safeEqual(Long obj1, Long obj2) {
        return ((obj1 != null) && (obj2 != null) && (obj1.compareTo(obj2) == 0));
    }

    /**
     * safe equal
     *
     * @param obj1 String
     * @param obj2 String
     * @return boolean
     */
    public static boolean safeEqual(String obj1, String obj2) {
        return ((obj1 != null) && (obj2 != null) && obj1.equals(obj2));
    }

    /**
     * increase cur no
     *
     * @param obj1 String
     * @param obj2 String
     * @return String
     */
    public static String increaseCurNo(String obj1, int obj2) {
        return String.format("%05d", Integer.parseInt(obj1) + obj2);
    }

    /**
     * create log
     *
     * @param info String
     * @return String
     */
    public static String createLog(String info) {
        //return (DateUtil.dateTime2String(DateUtil.sysDate()) + ": " + info);
        return info;
    }

    /**
     * check null or empty
     *
     * @param obj1 String
     * @return boolean
     */
    public static boolean isNullOrEmpty(String obj1) {
        return (obj1 == null || "".equals(obj1.trim()));
    }

    public static boolean isStringNullOrEmpty(Object obj1) {
        return obj1 == null || obj1.toString().trim().equals("");
    }

    /**
     * @param obj1 Object
     * @return BigDecimal
     */
    public static BigDecimal safeToBigDecimal(Object obj1) {
        BigDecimal result = new BigDecimal(0);
        if (obj1 == null) {
            return result;
        }
        try {
            result = new BigDecimal(obj1.toString());
        } catch (Exception ex) {
        }

        return result;
    }

    public static BigInteger safeToBigInterger(Object obj1) {
        BigInteger result = null;
        if (obj1 == null) {
            return null;
        }
        try {
            result = new BigInteger(obj1.toString());
        } catch (Exception ex) {
        }

        return result;
    }

    /**
     * add
     *
     * @param obj1 BigDecimal
     * @param obj2 BigDecimal
     * @return BigDecimal
     */
    public static BigDecimal add(BigDecimal obj1, BigDecimal obj2) {
        if (obj1 == null) {
            return obj2;
        } else if (obj2 == null) {
            return obj1;
        }

        return obj1.add(obj2);
    }

    /**
     * Convert an IP address to a number
     *
     * @param ipAddress Input IP address
     * @return The IP address as a number
     */
    public final static String MAX_NUMBER_RANGE = "1000000";

    public static BigInteger ipv4ToNumber(String ipAddress) {
        BigInteger result = BigInteger.valueOf(0);
        String[] atoms = ipAddress.split("\\.");

        for (int i = 3; i >= 0; i--) {
            BigInteger bi = new BigInteger(atoms[3 - i]);
            result = result.shiftLeft(8).add(bi);
        }

        return result;
    }

    public static String numberToIpv4(BigInteger ipNumber) {

        String ipString = "";
        BigInteger a = new BigInteger("FF", 16);

        for (int i = 0; i < 4; i++) {
            ipString = ipNumber.and(a).toString() + "." + ipString;

            ipNumber = ipNumber.shiftRight(8);
        }

        return ipString.substring(0, ipString.length() - 1);
    }

    public static BigInteger ipv6ToNumber(String addr) {
        int startIndex = addr.indexOf("::");

        if (startIndex != -1) {

            String firstStr = addr.substring(0, startIndex);
            String secondStr = addr.substring(startIndex + 2, addr.length());

            BigInteger first = ipv6ToNumber(firstStr);

            int x = countChar(addr, ':');
            int y = countChar(secondStr, ':');
            //first = first.shiftLeft(16 * (7 - x)).add(ipv6ToNumber(secondStr));
            first = first.shiftLeft(16 * (7 - x + y));
            first = first.add(ipv6ToNumber(secondStr));

            return first;
        }

        String[] strArr = addr.split(":");

        BigInteger retValue = BigInteger.valueOf(0);
        for (int i = 0; i < strArr.length; i++) {
            BigInteger bi = new BigInteger(strArr[i], 16);
            retValue = retValue.shiftLeft(16).add(bi);
        }
        return retValue;
    }

    public static String numberToIPv6(BigInteger ipNumber) {
        String ipString = "";
        BigInteger a = new BigInteger("FFFF", 16);

        for (int i = 0; i < 8; i++) {
            ipString = ipNumber.and(a).toString(16) + ":" + ipString;

            ipNumber = ipNumber.shiftRight(16);
        }

        return ipString.substring(0, ipString.length() - 1);

    }

    public static int countChar(String str, char reg) {
        char[] ch = str.toCharArray();
        int count = 0;
        for (int i = 0; i < ch.length; ++i) {
            if (ch[i] == reg) {
                if (ch[i + 1] == reg) {
                    ++i;
                    continue;
                }
                ++count;
            }
        }
        return count;
    }

    public static boolean checkValidateIPv4(String fromIPAddress, String toIPAddress, int mask) {

        BigInteger fromIP = ipv4ToNumber(fromIPAddress);
        BigInteger toIP = ipv4ToNumber(toIPAddress);
        BigInteger subnet = new BigInteger("FFFFFFFF", 16);

        fromIP = fromIP.shiftRight(32 - mask).shiftLeft(32 - mask);
        subnet = subnet.shiftRight(mask);

        BigInteger broadcastIP = fromIP.xor(subnet);

        if (toIP.compareTo(broadcastIP) == 1) {
            return false;
        }

        return true;
    }

    public static boolean checkLengthIPV4numberRange(String fromIPAddress, String toIPAddress) {
        BigInteger fromIP = ipv4ToNumber(fromIPAddress);
        BigInteger toIP = ipv4ToNumber(toIPAddress);
        BigInteger limit = toIP.subtract(fromIP);
        if (limit.compareTo(new BigInteger(MAX_NUMBER_RANGE)) == 1) {
            return false;
        }
        return true;
    }

    public static boolean checkValidateIPv6(String fromIPAddress, String toIPAddress, int mask) {

        BigInteger fromIP = ipv6ToNumber(fromIPAddress);
        BigInteger toIP = ipv6ToNumber(toIPAddress);
        BigInteger limit = toIP.subtract(fromIP);
        if (limit.compareTo(new BigInteger(MAX_NUMBER_RANGE)) == 1) {
            return false;
        }
        BigInteger subnet = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16);

        fromIP = fromIP.shiftRight(128 - mask).shiftLeft(128 - mask);
        subnet = subnet.shiftRight(mask);

        BigInteger broadcastIP = fromIP.xor(subnet);

        if (toIP.compareTo(broadcastIP) == 1) {
            return false;
        }

        return true;
    }

    public static String safeStringToSearch(String input) {
        return input.replace("_", "\\_").replace("-", "\\-").replace("%", "\\%");
    }
//

    public static boolean isInteger(String str) {
        if (str == null || !str.matches("[0-9]+$")) {
            return false;
        }
        return true;
    }

    public static boolean isLongNumber(BigDecimal minCar) {
        try {
            Long.parseLong(minCar.toString());
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * @param min
     * @param max
     * @return
     * @author minhvh1
     */
    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    /**
     * @param number
     * @param pattern
     * @return
     * @author KhuongDV Ham format so thuc ve dang co max la 4 chu so thap phan.
     * Trim() so 0 vo nghia
     */
    public static String getFormattedString4Digits(String number, String pattern) {
        double amount = 0;
        try {
            amount = Double.parseDouble(number);
            DecimalFormat formatter = new DecimalFormat(pattern);
            return formatter.format(amount);
        } catch (Exception ex) {
            return number;
        }
    }

    public static Character safeToCharacter(Object value) {
        return safeToCharacter(value, '0');
    }

    public static Character safeToCharacter(Object value, Character defaulValue) {
        if (value == null) {
            return defaulValue;
        }
        return String.valueOf(value).charAt(0);
    }

    public static Collection<Long> strToCollectionLong(List<String> list) {
        Collection<Long> result = new ArrayList<>();
        if (list.isEmpty()) {
            return result;
        }
        for (String s : list) {
            result.add(DataUtil.safeToLong(s));
        }
        return result;
    }

    public static Collection<Long> objLstToLongLst(List<Object> list) {
        Collection<Long> result = new ArrayList<>();
        if (!list.isEmpty()) {
            for (Object item : list) {
                result.add(safeToLong(item));
            }
        }
        return result;
    }

    public static Collection<Short> objLstToShortLst(List<Object> list) {
        Collection<Short> result = new ArrayList<>();
        if (!list.isEmpty()) {
            for (Object item : list) {
                result.add(safeToShort(item));
            }
        }
        return result;
    }

    public static Collection<BigDecimal> objLstToBigDecimalLst(List<Object> list) {
        Collection<BigDecimal> result = new ArrayList<>();
        if (!list.isEmpty()) {
            for (Object item : list) {
                result.add(safeToBigDecimal(item));
            }
        }
        return result;
    }

    public static Collection<Character> objLstToCharLst(List<Object> list) {
        Collection<Character> result = new ArrayList<>();
        if (!list.isEmpty()) {
            for (Object item : list) {
                result.add(item.toString().charAt(0));
            }
        }

        return result;
    }

    public static boolean isDelete(Character isDelete) {
        return isDelete != null && !DataUtil.isNullOrEmpty(String.valueOf(isDelete)) && Objects.equals(isDelete, 0);
    }

    /**
     * Check an object is active
     *
     * @param status status of object
     * @param isDelete isdetete status of object
     * @return
     */
    public static boolean isActive(Character status, Character isDelete) {
        return Objects.equals(status, '1') && (isDelete == null || isDelete.equals('0'));
    }

    public static <T> T getMapValue(Map<String, Object> params, String key, Class<T> type) {
        Object obj = params.get(key);
        if (obj == null) {
            return null;
        }
        if (obj.getClass().isAssignableFrom(obj.getClass())) {
            return type.cast(obj);
        }

        return null;
    }

    public static <T> T nvl(T... objs) {
        for (T obj : objs) {
            if (obj != null) {
                return obj;
            }
        }

        return null;
    }

    public static String strNvl(String... objs) {
        for (String obj : objs) {
            if (!DataUtil.isNullOrEmpty(obj)) {
                return obj;
            }
        }

        return null;
    }

    public static boolean isNullObject(Object obj1) {
        if (obj1 == null) {
            return true;
        }
        if (obj1 instanceof String) {
            return isNullOrEmpty(obj1.toString());
        }
        return false;
    }

    public static String convertToDisplayName(String parType, String index) {
        return parType.replace('_', '.').toLowerCase() + "." + index;
    }

    //ChuDV: 10/05/2015
    public static Long convertDoubleToLong(Double value) {
        return value == null ? 0L : Long.parseLong(value.toString().replace(".0", ""));
    }

    public static List<String> parseInputList(String input) {
        return Splitter.on(",").trimResults().omitEmptyStrings().splitToList(input);
    }

    public static Long[] parseInputListLong(String input) {

        List<String> lstString = parseInputList(input);
        Long[] lstLong = new Long[lstString.size()];
        for (int i = 0; i < lstString.size(); i++) {
            lstLong[i] = (Long.parseLong(lstString.get(i)));
        }
        return lstLong;
    }

    public static Double[] parseInputListDouble(String input) {
        List<String> lstString = parseInputList(input);
        Double[] lstDouble = new Double[lstString.size()];
        for (int i = 0; i < lstString.size(); i++) {
            lstDouble[i] = (Double.parseDouble(lstString.get(i)));
        }
        return lstDouble;
    }

    public static String[] parseInputListString(String input) {
        List<String> lstString = parseInputList(input);
        String[] lstStringValue = new String[lstString.size()];
        for (int i = 0; i < lstString.size(); i++) {
            lstStringValue[i] = lstString.get(i);
        }
        return lstStringValue;
    }

    /**
     *
     * @param lstObj
     * @return lstClone
     */
    public static List cloneList(List lstObj) {
        List lstClone = new ArrayList<>();
        if (DataUtil.isListNullOrEmpty(lstObj)) {
            return lstClone;
        }
        for (Object lstObj1 : lstObj) {
            Object obj = cloneObject(lstObj1);
            if (obj != null) {
                lstClone.add(obj);
            }
        }
        return lstClone;
    }

    //Add by TruongBX3: 14/05/2015 - 
    public static Object cloneObject(Object obj) {
        try {
            Object clone = obj.getClass().newInstance();
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                field.set(clone, field.get(obj));
            }
            return clone;
        } catch (Exception e) {
            return null;
        }
    }


    //
    public static String lPad(String input, String replace, int length) {
        String format = "%" + length + "s";
        return String.format(format, input).replace(" ", replace);
    }

    public static String rPad(String input, String replace, int length) {
        return String.format("%1$-" + length + "s", input).replace(" ", replace);
    }

    //
    public static Double divide(String ts, String ms) {
        if (ms.equals("0")) {
            return 0D;
        } else {
            return Double.parseDouble(ts) / Double.parseDouble(ms);
        }
    }

    //hongdq
    public static List<String> splitListFile(String strFiles) {
        List<String> lstFile = Lists.newArrayList();
        if (!isStringNullOrEmpty(strFiles)) {
            String lst[] = strFiles.split(",");
            lstFile = Arrays.asList(lst);
        }
        return lstFile;
    }

    public static boolean isListNullOrEmpty(List<?> lst) {
        return lst == null || lst.isEmpty();
    }
    

}
