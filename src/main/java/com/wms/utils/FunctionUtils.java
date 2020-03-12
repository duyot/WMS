package com.wms.utils;

import com.google.common.collect.Lists;
import com.wms.config.ProfileConfigInterface;
import com.wms.constants.Constants;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.CatUserService;
import com.wms.services.interfaces.PartnerService;
import com.wms.services.interfaces.StockService;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import net.sf.jxls.transformer.Configuration;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by duyot on 11/17/2016.
 */
public class FunctionUtils {
    public static Logger log = LoggerFactory.getLogger(FunctionUtils.class);

    public static <K, V> Map<K, V> clone(Map<K, V> original) {
        return original.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue));
    }

    public static String getValueFromToggle(String toggleValue) {
        return "on".equalsIgnoreCase(toggleValue) ? "1" : "0";
    }

    public static <T extends BaseDTO> String getMapValue(Map<String, T> map, String key) {
        BaseDTO obj = map.get(key);
        if (obj == null) {
            return "";
        }
        return obj.getName();
    }

    public static <T extends BaseDTO> String getMapCodeValue(Map<String, T> map, String key) {
        BaseDTO obj = map.get(key);
        if (obj == null) {
            return "";
        }
        return obj.getCode();
    }

    //build map app_params
    public static LinkedHashMap<String, String> buildMapAppParams(List<AppParamsDTO> lstAppParams) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        for (AppParamsDTO i : lstAppParams) {
            map.put(i.getCode(), i.getName());
        }
        return map;
    }

    public static LinkedHashMap<String, String> buildMapAppParams(String type, List<AppParamsDTO> lstAppParams) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        for (AppParamsDTO i : lstAppParams) {
            if (i.getType().equalsIgnoreCase(type)) {
                map.put(i.getCode(), i.getName());
            }
        }
        return map;
    }

    /*
       get AppParams
    */
    public static List<AppParamsDTO> getAppParams(BaseService service) {
        List<Condition> lstCondition = Lists.newArrayList();
        lstCondition.add(new Condition("status", Constants.SQL_PRO_TYPE.BYTE, Constants.SQL_OPERATOR.EQUAL, Constants.STATUS.ACTIVE));
        lstCondition.add(new Condition("name", Constants.SQL_OPERATOR.VNM_ORDER, "asc"));
        return service.findByCondition(lstCondition);
    }

    public static List<AppParamsDTO> getAppParamByType(String type, List<AppParamsDTO> lstAppParams) {
        List<AppParamsDTO> lstResult = Lists.newArrayList();
        for (AppParamsDTO i : lstAppParams) {
            if (i.getType().equalsIgnoreCase(type)) {
                lstResult.add(i);
            }
        }
        return lstResult;
    }

    private static String convertDBMessage(String oraMessage) {
        if (oraMessage.contains("WMS_DB.UN_SERIAL")) {
            return "Serial đã có trên hệ thống";
        } else {
            return "Lỗi: " + oraMessage;
        }
    }

    /*

     */
    public static List<MjrStockTransDetailDTO> convertListErrorToTransDetail(List<Err$MjrStockGoodsSerialDTO> lstGoodsError, Map<String, CatGoodsDTO> mapGoodsIdGoods) {
        List<MjrStockTransDetailDTO> lstDetail = Lists.newArrayList();
        CatGoodsDTO goods;
        for (Err$MjrStockGoodsSerialDTO i : lstGoodsError) {
            MjrStockTransDetailDTO detail = new MjrStockTransDetailDTO();
            goods = mapGoodsIdGoods.get(i.getGoodsId());
            if (goods != null) {
                detail.setGoodsCode(goods.getCode());
                detail.setGoodsName(goods.getName());
            }
            detail.setGoodsState(i.getGoodsState());
            detail.setSerial(i.getSerial());
            detail.setAmount(i.getAmount());
            detail.setInputPrice(i.getInputPrice());
            detail.setCellCode(i.getCellCode());
            detail.setErrorInfo(convertDBMessage(i.getOraErrorMessage()));
            //
            lstDetail.add(detail);
        }
        return lstDetail;
    }

    /*

     */
    public static void exportExcel(String templateAbsolutePath, Map<String, Object> beans, String fullPath) {
        Configuration config = new Configuration();
        XLSTransformer transformer = new XLSTransformer(config);
        try {
            transformer.transformXLS(templateAbsolutePath, beans, fullPath);
            log.info("Finish export report file in " + fullPath);
        } catch (InvalidFormatException e) {
            e.printStackTrace();
            log.error(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.toString());
        }
    }

    /*


     */
    public static List<MjrStockTransDetailDTO> convertGoodsSerialToDetail(List<MjrStockGoodsSerialDTO> lstStockGoodsSerial, Map<String, String> mapAppStockStatus) {
        List<MjrStockTransDetailDTO> lstResult = Lists.newArrayList();
        if (!DataUtil.isListNullOrEmpty(lstStockGoodsSerial)) {
            for (MjrStockGoodsSerialDTO i : lstStockGoodsSerial) {
                MjrStockTransDetailDTO temp = new MjrStockTransDetailDTO();
                temp.setGoodsId(i.getGoodsId());
                temp.setGoodsState(i.getGoodsState());
                temp.setSerial(i.getSerial());
                temp.setAmount(i.getAmount());
                temp.setImportDate(i.getImportDate());
                temp.setExportDate(i.getExportDate());
                temp.setInputPrice(i.getInputPrice());
                temp.setOutputPrice(i.getOutputPrice());
                temp.setStatusValue(mapAppStockStatus.get(i.getStatus()));
                temp.setStockId(i.getStockId());
                temp.setCellCode(i.getCellCode());
                temp.setContent(i.getContent());

                //
                lstResult.add(temp);
            }
        }

        return lstResult;
    }

    /*

     */
    public static List<MjrStockTransDetailDTO> setNameValueGoodsDetail(List<MjrStockTransDetailDTO> lstGoodsDetail,
                                                                       Map<String, CatGoodsDTO> mapGoodsIdGoods, Map<String, CatStockDTO> mapStockIdStock, Map<String, String> mapGoodsState) {
        if (!DataUtil.isListNullOrEmpty(lstGoodsDetail)) {
            CatGoodsDTO currentGoods;
            for (MjrStockTransDetailDTO i : lstGoodsDetail) {
                currentGoods = mapGoodsIdGoods.get(i.getGoodsId());
                if (currentGoods == null) {
                    currentGoods = new CatGoodsDTO();
                    currentGoods.setCode("");
                    currentGoods.setName("");
                }
                //
                i.setGoodsCode(currentGoods.getCode());
                i.setGoodsName(currentGoods.getName());
                i.setAmountValue(formatNumber(i.getAmount()));
                i.setInputPriceValue(formatNumber(i.getInputPrice()));
                i.setOutputPriceValue(formatNumber(i.getOutputPrice()));
                i.setGoodsStateValue(i.getGoodsState());
                i.setStockValue(getMapValue(mapStockIdStock, i.getStockId()));
            }
        } else {
            return Lists.newArrayList();
        }
        return lstGoodsDetail;
    }

    /*
        get stock
     */
    public static List<CatStockDTO> getListStock(StockService stockService, CatUserDTO currentUser) {
        return stockService.getStockByUser(Long.parseLong(currentUser.getId()));
    }

    /*
        get partner by user
     */
    public static List<CatPartnerDTO> getListPartner(PartnerService partnerService, CatUserDTO currentUser) {
        if (DataUtil.isStringNullOrEmpty(currentUser.getPartnerPermission())) {
            currentUser.setPartnerPermission("0");
        }
        return partnerService.getPartnerByUser(Long.parseLong(currentUser.getId()), Long.parseLong(currentUser.getPartnerPermission()));
    }

    /*
        get user
     */
    public static List<CatUserDTO> getCustomerUsers(CatUserService catUserService, CatCustomerDTO currentCustomer) {
        return catUserService.getUserByCustomer(currentCustomer.getId());
    }

    private static List<Condition> getBaseConditions(String custId) {
        List<Condition> lstCondition = Lists.newArrayList();
        lstCondition.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, custId));
        lstCondition.add(new Condition("status", Constants.SQL_PRO_TYPE.BYTE, Constants.SQL_OPERATOR.EQUAL, Constants.STATUS.ACTIVE));
        lstCondition.add(new Condition("name", Constants.SQL_OPERATOR.VNM_ORDER, "asc"));
        return lstCondition;
    }

    /*
       get goods
    */
    public static List<CatGoodsDTO> getListGoods(BaseService service, CatCustomerDTO currentCustomer) {
        return service.findByCondition(getBaseConditions(currentCustomer.getId()));
    }

    /*
       get goods
    */
    public static List<CatReasonDTO> getListReason(BaseService service, CatCustomerDTO currentCustomer) {
        return service.findByCondition(getBaseConditions(currentCustomer.getId()));
    }

    /*
      get Partner
   */
    public static List<CatPartnerDTO> getListPartner(BaseService service, CatCustomerDTO currentCustomer) {
        return service.findByCondition(getBaseConditions(currentCustomer.getId()));
    }

    /*
      get Stock
   */
    public static List<CatStockDTO> getListStock(BaseService service, CatCustomerDTO currentCustomer) {
        return service.findByCondition(getBaseConditions(currentCustomer.getId()));
    }


    /*

     */
    public static void loadFileToClient(HttpServletResponse response, String fileResource) {
        try {
            File file = new File(fileResource);
            //
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            response.setContentType(mimeType);
            response.setContentLength((int) file.length());//length in bytes
            response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + file.getName() + "\""));
            //
            InputStream is = new FileInputStream(file);
            FileCopyUtils.copy(is, response.getOutputStream());
            //
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("Test");
    }


    /*
        export error when importing
     */
    public static String exportExcelError(List<MjrStockTransDetailDTO> lstError, String prefixFileName, boolean isImportTrans, ProfileConfigInterface profileConfig) {
        String templatePath;
        if (isImportTrans) {
            templatePath = profileConfig.getTemplateURL() + Constants.FILE_RESOURCE.IMPORT_ERROR_TEMPLATE;
        } else {
            templatePath = profileConfig.getTemplateURL() + Constants.FILE_RESOURCE.EXPORT_ERROR_TEMPLATE;
        }
        log.info("Number of Errors: " + lstError.size());

        File file = new File(templatePath);
        String templateAbsolutePath = file.getAbsolutePath();

        Map<String, Object> beans = new HashMap<>();
        beans.put("items", lstError);

        String fullFileName = prefixFileName + "_" + DateTimeUtils.getSysDateTimeForFileName() + ".xlsx";
        String reportFullPath = profileConfig.getTempURL() + fullFileName;

        exportExcel(templateAbsolutePath, beans, reportFullPath);

        return fullFileName;
    }

    /*
        export error when importing
     */
    public static String exportExcelImportGoodsError(List<CatGoodsDTO> lstError, String prefixFileName, ProfileConfigInterface profileConfig) {
        String templatePath = profileConfig.getTemplateURL() + Constants.FILE_RESOURCE.IMPORT_GOODS_ERROR_TEMPLATE;
        log.info("Number of Errors: " + lstError.size());

        File file = new File(templatePath);
        String templateAbsolutePath = file.getAbsolutePath();

        Map<String, Object> beans = new HashMap<String, Object>();
        beans.put("items", lstError);

        String fullFileName = prefixFileName + "_" + DateTimeUtils.getSysDateTimeForFileName() + ".xlsx";
        String reportFullPath = profileConfig.getTempURL() + fullFileName;

        exportExcel(templateAbsolutePath, beans, reportFullPath);

        return fullFileName;
    }


    public static boolean saveUploadedFile(MultipartFile uploadfile, String fileName, ProfileConfigInterface profileConfigInterface) {
        String filepath = Paths.get(profileConfigInterface.getUploadURL(), fileName).toString();
        try {
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filepath)));
            stream.write(uploadfile.getBytes());
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Error save file: " + e.toString());
            return false;
        }

        return true;
    }

    public static File convertMultipartToFile(MultipartFile file) {
        try {
            File convFile = new File(file.getOriginalFilename());
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();
            return convFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        cell.setCellType(CellType.STRING);
        return trimNoBreakingSpace(cell.getStringCellValue());
    }

    public static String trimNoBreakingSpace(String str) {
        return str.replace(String.valueOf((char) 160), "").trim();
    }

    public static MjrStockTransDetailDTO initGoodsItemFromRow(Row row, int count, boolean isImportTransaction) {
        MjrStockTransDetailDTO goodsItem = new MjrStockTransDetailDTO();
        //
        goodsItem.setColumnId(count + "");
        goodsItem.setGoodsCode(getCellValue(row.getCell(1)));
        goodsItem.setGoodsName(getCellValue(row.getCell(2)));
        goodsItem.setGoodsState(getCellValue(row.getCell(3)));
        goodsItem.setSerial(getCellValue(row.getCell(4)));
        goodsItem.setAmount(getCellValue(row.getCell(5)));
        String price = getCellValue(row.getCell(6));
        if (isImportTransaction) {
            goodsItem.setInputPrice(price);
            goodsItem.setContent(getCellValue(row.getCell(10)));
        } else {
            goodsItem.setOutputPrice(price);
        }
        goodsItem.setCellCode(getCellValue(row.getCell(7)));
        goodsItem.setProduceDate(getCellValue(row.getCell(8)));
        goodsItem.setExpireDate(getCellValue(row.getCell(9)));
        goodsItem.setDescription("noted");
        //
        return goodsItem;
    }

    public static ImportFileResultDTO getListStockImportFromFile(MultipartFile mpf, HashSet<String> setGoodsCode,
                                                                 Map<String, CatGoodsDTO> mapGoods, Map<String, String> mapGoodsState, boolean isImportTransaction) {
        ImportFileResultDTO importResult = new ImportFileResultDTO();
        List<MjrStockTransDetailDTO> lstGoods = Lists.newArrayList();
        boolean isValid = true;
        try {
            Workbook wb = WorkbookFactory.create(new FileInputStream(FunctionUtils.convertMultipartToFile(mpf)));
            Sheet sheet = null;
            if (wb instanceof HSSFWorkbook) {
                HSSFWorkbook workbook = (HSSFWorkbook) wb;
                sheet = workbook.getSheetAt(0);
            } else if (wb instanceof XSSFWorkbook) {
                XSSFWorkbook workbook = (XSSFWorkbook) wb;
                sheet = workbook.getSheetAt(0);
            }

            Iterator<Row> rowIterator = sheet.iterator();
            if (rowIterator.hasNext()) {//read from second row!
                rowIterator.next();
            }
            //
            int count = 0;
            StringBuilder errorInfo;
            CatGoodsDTO goodsDTO;
            boolean isSerial;
            List<String> lstCheckSerial = Lists.newArrayList();
            String serialCheckKey;
            MjrStockTransDetailDTO goodsItem;
            while (rowIterator.hasNext()) {
                count++;
                Row row = rowIterator.next();
                errorInfo = new StringBuilder();
                //
                goodsItem = initGoodsItemFromRow(row, count, isImportTransaction);
                //CODE
                if (DataUtil.isStringNullOrEmpty(goodsItem.getGoodsCode())) {
                    errorInfo.append("Chưa có mã hàng");
                    goodsItem.setErrorInfo(errorInfo.toString());
                    isValid = false;
                    lstGoods.add(goodsItem);
                    continue;
                }
                //
                goodsDTO = mapGoods.get(goodsItem.getGoodsCode());
                if (!setGoodsCode.contains(goodsItem.getGoodsCode()) || goodsDTO == null) {
                    errorInfo.append("Mã hàng không hợp lệ");
                    goodsItem.setErrorInfo(errorInfo.toString());
                    isValid = false;
                    lstGoods.add(goodsItem);
                    continue;
                }
                //SERIAL
                isSerial = goodsDTO.isSerial();
                if (isSerial) {
                    if (DataUtil.isStringNullOrEmpty(goodsItem.getSerial())) {
                        errorInfo.append("Hàng serial cần nhập thông tin serial");
                        isValid = false;
                    } else {
                        //CHECK ENTERED SERIAL
                        serialCheckKey = goodsItem.getGoodsCode() + goodsItem.getSerial();
                        if (lstCheckSerial.contains(serialCheckKey)) {
                            errorInfo.append("\n Serial đã được nhập");
                            isValid = false;
                        } else {
                            lstCheckSerial.add(serialCheckKey);
                        }
                    }
                }
                //NAME
                goodsItem.setGoodsName(goodsDTO.getName());
                //STATE
                String goodsState = goodsItem.getGoodsState();
                if (DataUtil.isStringNullOrEmpty(goodsState)) {
                    errorInfo.append("\n Chưa có tình trạng hàng");
                    isValid = false;
                } else {
                    if (!goodsState.equalsIgnoreCase("1") && !goodsState.equalsIgnoreCase("2")) {
                        errorInfo.append("\n Tình trạng hàng hóa không hợp lệ(1: Bình thường, 2:Hỏng");
                        isValid = false;
                    }
                }
                //AMOUNT
                String amount = goodsItem.getAmount();
                if (DataUtil.isStringNullOrEmpty(amount)) {
                    errorInfo.append("\n Chưa có số lượng hàng");
                    isValid = false;
                } else {
                    if (!isNumberFloat(amount)) {
                        errorInfo.append("\n Số lượng hàng phải là số và >0");
                        isValid = false;
                    } else {
                        goodsItem.setAmountValue(formatNumber(amount));
                    }
                    if (isSerial && !amount.equalsIgnoreCase("1")) {
                        errorInfo.append("\n Hàng quản lý serial số lượng nhập phải là 1");
                        isValid = false;
                    }
                }
                //PRICE
                String price = isImportTransaction ? goodsItem.getInputPrice() : goodsItem.getOutputPrice();
                if (!DataUtil.isStringNullOrEmpty(price)) {
                    if (!isNumberFloat(price)) {
                        errorInfo.append("\n Giá phải là số và >= 0");
                        isValid = false;
                    } else {
                        if (isImportTransaction) {
                            goodsItem.setInputPriceValue(formatNumber(price));
                        } else {
                            goodsItem.setOutputPriceValue(formatNumber(price));
                        }
                    }
                } else {//set default value
                    if (isImportTransaction) {
                        goodsItem.setInputPriceValue(formatNumber(goodsDTO.getInPrice()));
                        goodsItem.setInputPrice(goodsDTO.getInPrice());
                        price = goodsDTO.getInPrice();
                    } else {
                        goodsItem.setOutputPriceValue(formatNumber(goodsDTO.getOutPrice()));
                        goodsItem.setOutputPrice(goodsDTO.getOutPrice());
                        price = goodsDTO.getOutPrice();
                    }
                }
                //
                goodsItem.setTotalMoney(calTotalMoney(goodsItem.getAmount(), price) + "");
                //
                goodsItem.setWeight(getWeightFromGoodsItem(goodsDTO, amount));
                goodsItem.setVolume(getVolumeFromGoodsItem(goodsDTO, amount));
                //produce date
                String procedureDate = goodsItem.getProduceDate();
                if (!DataUtil.isStringNullOrEmpty(procedureDate) && !DateTimeUtils.isValidDateFormat(procedureDate, "dd/MM/yyyy")) {
                    errorInfo.append("\n Ngày sản xuất không đúng định dạng");
                    isValid = false;
                }
                //expire date
                String expireDate = goodsItem.getExpireDate();
                if (!DataUtil.isStringNullOrEmpty(expireDate) && !DateTimeUtils.isValidDateFormat(expireDate, "dd/MM/yyyy")) {
                    errorInfo.append("\n Hạn dùng không đúng định dạng");
                    isValid = false;
                }
                //
                goodsItem.setContent(goodsItem.getContent());
                //
                if (!isValid) {
                    goodsItem.setErrorInfo(errorInfo.toString());
                }

                lstGoods.add(goodsItem);
            }
        } catch (IOException e) {
            log.info(e.toString());
            e.printStackTrace();
            log.info(e.toString());
            return null;
        } catch (Exception e) {
            log.info(e.toString());
            e.printStackTrace();
            return null;
        }
        //
        importResult.setValid(isValid);
        importResult.setLstGoodsImport(lstGoods);
        return importResult;
    }

    public static String getWeightFromGoodsItem(CatGoodsDTO goodsItem, String amountStr) {
        float weight = 0;
        int amount = 0;
        if (!DataUtil.isStringNullOrEmpty(goodsItem.getWeight())) {
            weight = Float.parseFloat(goodsItem.getWeight());
        }

        if (!DataUtil.isStringNullOrEmpty(amountStr) && isNumberFloat(amountStr)) {
            amount = Integer.parseInt(amountStr);
        }
        float totalWeight = weight * amount;
        return totalWeight == 0 ? "" : totalWeight + "";
    }

    public static String getVolumeFromGoodsItem(CatGoodsDTO goodsItem, String amountStr) {
        float volume = 0;
        int amount = 0;
        if (!DataUtil.isStringNullOrEmpty(goodsItem.getVolume())) {
            volume = Float.parseFloat(goodsItem.getVolume());
        }

        if (!DataUtil.isStringNullOrEmpty(amountStr) && isNumberFloat(amountStr)) {
            amount = Integer.parseInt(amountStr);
        }
        float totalVolume = volume * amount;
        return totalVolume == 0 ? "" : totalVolume + "";
    }

    public static ImportFileResultDTO getListGoodsImportFromFile(MultipartFile mpf, Map<String, String> mapGoodsState,
                                                                 Map<String, String> mapGoodsGroup, Map<String, String> mapUnitType) {
        ImportFileResultDTO importResult = new ImportFileResultDTO();
        List<CatGoodsDTO> lstGoods = Lists.newArrayList();
        boolean isValid = true;
        try {
            Workbook wb = WorkbookFactory.create(new FileInputStream(FunctionUtils.convertMultipartToFile(mpf)));
            Sheet sheet = null;
            if (wb instanceof HSSFWorkbook) {
                HSSFWorkbook workbook = (HSSFWorkbook) wb;
                sheet = workbook.getSheetAt(0);
            } else if (wb instanceof XSSFWorkbook) {
                XSSFWorkbook workbook = (XSSFWorkbook) wb;
                sheet = workbook.getSheetAt(0);
            }

            Iterator<Row> rowIterator = sheet.iterator();
            if (rowIterator.hasNext()) {//read from second row!
                rowIterator.next();
            }
            int count = 0;
            StringBuilder errorInfo;
            CatGoodsDTO goodsDTO;
            Cell cellValue;
            Row row;
            while (rowIterator.hasNext()) {
                count++;
                goodsDTO = new CatGoodsDTO();
                goodsDTO.setColumnId(count + "");
                errorInfo = new StringBuilder();
                //
                row = rowIterator.next();
                //goods serial
                cellValue = row.getCell(5);
                String serial = getCellValue(cellValue);
                if (DataUtil.isStringNullOrEmpty(serial)) {
                    errorInfo.append("\n Chưa có thông tin quản lý serial");
                    isValid = false;
                } else {
                    if (!serial.equalsIgnoreCase("1") && !serial.equalsIgnoreCase("0")) {
                        errorInfo.append("\n Thông tin serial không hợp lệ(1: QL theo serial,0:Không quản lý theo serial");
                        isValid = false;
                    }
                }
                String serialTypeName = "1".equals(serial) ? "Có" : "Không";
                goodsDTO.setIsSerial(serial);
                goodsDTO.setIsSerialName(serialTypeName);
                //goods code
                cellValue = row.getCell(1);
                String goodsCode = getCellValue(cellValue);
                if (DataUtil.isStringNullOrEmpty(goodsCode)) {
                    errorInfo.append("\n Chưa có mã hàng");
                    isValid = false;
                }
                goodsDTO.setCode(goodsCode);
                //goods name
                cellValue = row.getCell(2);
                String goodsName = getCellValue(cellValue);
                if (DataUtil.isStringNullOrEmpty(goodsName)) {
                    errorInfo.append("Chưa có tên hàng");
                    isValid = false;
                }
                goodsDTO.setName(goodsName);
                //STATE
                /*Cell cellStatus = row.getCell(6);
                String goodsState = getCellValue(cellStatus);
                if (DataUtil.isStringNullOrEmpty(goodsState)) {
                    errorInfo.append("\n Chưa có trạng thái hàng");
                    isValid = false;
                } else {
                    if (!goodsState.equalsIgnoreCase("1") && !goodsState.equalsIgnoreCase("2")) {
                        errorInfo.append("\n Trạng thái hàng không hợp lệ(1:Bình thường,2:Hỏng");
                        isValid = false;
                    }
                }
                */
                goodsDTO.setStatus("1");
                //goodsDTO.setStatusName(mapGoodsState.get(goodsState));

                //UNIT TYPE
                cellValue = row.getCell(3);
                String unitType = getCellValue(cellValue);
                if (DataUtil.isStringNullOrEmpty(unitType)) {
                    unitType = "1";
                }
                String unitTypeName = mapUnitType.get(unitType);
                if (unitTypeName == null) {
                    goodsDTO.setUnitType("1");
                    goodsDTO.setUnitTypeName(mapUnitType.get("1"));
                } else {
                    goodsDTO.setUnitType(unitType);
                    goodsDTO.setUnitTypeName(unitTypeName);
                }
                //GOODS GROUP
                cellValue = row.getCell(4);
                String goodsGroup = getCellValue(cellValue);
                if (DataUtil.isStringNullOrEmpty(goodsGroup)) {
                    errorInfo.append("\n Chưa có nhóm hàng");
                    isValid = false;
                } else {
                    String groupName = mapGoodsGroup.get(goodsGroup);
                    if (DataUtil.isStringNullOrEmpty(groupName)) {
                        errorInfo.append("\n Nhóm hàng chưa đúng");
                        isValid = false;
                    }
                    goodsDTO.setGoodsGroupId(goodsGroup);
                    goodsDTO.setGoodsGroupName(groupName);
                }
                //In PRICE
                cellValue = row.getCell(6);
                String price = getCellValue(cellValue);
                if (!DataUtil.isStringNullOrEmpty(price)) {
                    if (!isNumberFloat(price)) {
                        errorInfo.append("\n Giá phải là số dương");
                        isValid = false;
                    } else {
                        goodsDTO.setInPriceValue(formatNumber(price));
                    }
                }
                goodsDTO.setInPrice(price);
                //Out PRICE
                cellValue = row.getCell(7);
                String outPrice = getCellValue(cellValue);
                outPrice = outPrice.trim();
                if (!DataUtil.isStringNullOrEmpty(outPrice)) {
                    if (!isNumberFloat(outPrice)) {
                        errorInfo.append("\n Giá phải là số dương");
                        isValid = false;
                    } else {
                        goodsDTO.setOutPriceValue(formatNumber(outPrice));
                    }
                }
                goodsDTO.setOutPrice(outPrice);
                //Length
                cellValue = row.getCell(8);
                String length = getCellValue(cellValue);
                length = length.trim();
                if (!DataUtil.isStringNullOrEmpty(length)) {
                    if (!isNumberFloat(length)) {
                        errorInfo.append("\n Chiều dài phải là số dương");
                        isValid = false;
                    }
                }
                goodsDTO.setLength(length);
                //Width
                cellValue = row.getCell(9);
                String width = getCellValue(cellValue);
                width = width.trim();
                if (!DataUtil.isStringNullOrEmpty(width)) {
                    if (!isNumberFloat(width)) {
                        errorInfo.append("\n Chiều rộng phải là số dương");
                        isValid = false;
                    }
                }
                goodsDTO.setWidth(width);
                //Hight
                cellValue = row.getCell(10);
                String hight = getCellValue(cellValue);
                hight = hight.trim();
                if (!DataUtil.isStringNullOrEmpty(hight)) {
                    if (!isNumberFloat(hight)) {
                        errorInfo.append("\n Chiều cao phải là số dương");
                        isValid = false;
                    }
                }
                goodsDTO.setHigh(hight);
                //Weight
                cellValue = row.getCell(11);
                String weight = getCellValue(cellValue);
                weight = weight.trim();
                if (!DataUtil.isStringNullOrEmpty(weight)) {
                    if (!isNumberFloat(weight)) {
                        errorInfo.append("\n Trọng lượng phải là số dương");
                        isValid = false;
                    }
                }
                goodsDTO.setWeight(weight);
                //
                if (!isValid) {
                    goodsDTO.setErrorInfo(errorInfo.toString());
                }
                //
                lstGoods.add(goodsDTO);
            }
            //
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        importResult.setValid(isValid);
        importResult.setLstGoods(lstGoods);
        return importResult;
    }

    /*
        valid pattern: 123 | 133.000
     */
    public static boolean isInteger(String value) {
        double number = Double.parseDouble(value);
        return number % 1 == 0;
    }

    public static String formatNumber(String number) {
        if (!DataUtil.isStringNullOrEmpty(number)) {
            String plainNumber = removeScientificNotation(number);
            double dNumber = Double.valueOf(plainNumber);
            if (dNumber % 1 != 0) {
                return String.format("%,.4f", dNumber);
            } else {
                return String.format("%,.0f", dNumber);
            }
        } else {
            return "";
        }
    }

    public static String removeScientificNotation(String number) {
        if (DataUtil.isStringNullOrEmpty(number)) {
            return "";
        }
        BigDecimal num = new BigDecimal(number);
        return num.toPlainString();
    }

    public static boolean isNumberFloat(String input) {
        try {
            return Float.valueOf(input) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    //13,000 -> 13000
    public static String unformatFloat(String input) {
        return input.replaceAll(",", "");
    }

    public static float calTotalMoney(String amount, String price) {
        float amountF = 0f;
        try {
            if (!DataUtil.isNullOrEmpty(amount)) {
                amountF = Float.valueOf(amount);
            }
            amountF = Float.valueOf(amount);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        //
        float priceF = 0f;
        try {
            if (!DataUtil.isNullOrEmpty(price)) {
                priceF = Float.valueOf(price);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return amountF * priceF;
    }

    /*
       get partnerId from
    */
    public static CatPartnerDTO getPartner(BaseService service, String custId, String partnerCode, String partnerId) {
        List<Condition> lstCondition = Lists.newArrayList();
        lstCondition.add(new Condition("status", Constants.SQL_PRO_TYPE.BYTE, Constants.SQL_OPERATOR.EQUAL, Constants.STATUS.ACTIVE));
        if (partnerCode != null && !partnerCode.trim().equalsIgnoreCase("")) {
            lstCondition.add(new Condition("code", Constants.SQL_OPERATOR.EQUAL, partnerCode.trim()));
        }
        if (!DataUtil.isStringNullOrEmpty(partnerId)) {
            lstCondition.add(new Condition("id", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, partnerId));
        }
        lstCondition.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, custId));
        CatPartnerDTO catPartnerDTO = new CatPartnerDTO();
        List<CatPartnerDTO> lstPartnerDTOS = service.findByCondition(lstCondition);
        if (!DataUtil.isListNullOrEmpty(lstPartnerDTOS)) {
            catPartnerDTO = lstPartnerDTOS.get(0);
        }
        return catPartnerDTO;
    }
}
