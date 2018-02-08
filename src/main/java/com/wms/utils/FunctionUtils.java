package com.wms.utils;

import com.google.common.collect.Lists;
import com.wms.constants.Constants;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.CatUserService;
import net.sf.jxls.transformer.Configuration;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by duyot on 11/17/2016.
 */
public class FunctionUtils {
    public static Logger log = LoggerFactory.getLogger(FunctionUtils.class);

    public static String getValueFromToggle(String toggleValue){
        return "on".equalsIgnoreCase(toggleValue)?"1":"0";
    }

    public static <T extends BaseDTO>  String getMapValue(Map<String,T> map,String key){
        BaseDTO obj = map.get(key);
        if(obj == null){
            return "";
        }
        return obj.getName();
    }

    //build map app_params
    public static LinkedHashMap<String,String>  buildMapAppParams(List<AppParamsDTO> lstAppParams){
        LinkedHashMap<String,String> map   = new LinkedHashMap<>();
        for(AppParamsDTO i: lstAppParams){
            map.put(i.getCode(),i.getName());
        }
        return map;
    }

    /*
       get AppParams
    */
    public static List<AppParamsDTO> getAppParams(BaseService service,AuthTokenInfo tokenInfo){
        List<Condition> lstCondition = Lists.newArrayList();
        lstCondition.add(new Condition("status",Constants.SQL_OPERATOR.EQUAL,Constants.STATUS.ACTIVE));
        lstCondition.add(new Condition("name",Constants.SQL_OPERATOR.VNM_ORDER,"asc"));
        return service.findByCondition(lstCondition,tokenInfo);
    }

    public static List<AppParamsDTO> getAppParamByType(String type,List<AppParamsDTO> lstAppParams){
        List<AppParamsDTO> lstResult = Lists.newArrayList();
        for(AppParamsDTO i: lstAppParams){
            if(i.getType().equalsIgnoreCase(type)){
                lstResult.add(i);
            }
        }
        return lstResult;
    }

    private static String convertDBMessage(String oraMessage){
        if(oraMessage.contains("WMS_DB.UN_SERIAL")){
            return "Serial đã có trên hệ thống";
        }else{
            return "Lỗi: "+ oraMessage;
        }
    }
    /*

     */
    public static List<MjrStockTransDetailDTO> convertListErrorToTransDetail(List<Err$MjrStockGoodsSerialDTO> lstGoodsError,Map<String,CatGoodsDTO> mapGoodsIdGoods){
        List<MjrStockTransDetailDTO> lstDetail = Lists.newArrayList();
        CatGoodsDTO goods;
        for(Err$MjrStockGoodsSerialDTO i: lstGoodsError){
            MjrStockTransDetailDTO detail = new MjrStockTransDetailDTO();
            goods = mapGoodsIdGoods.get(i.getGoodsId());
            if(goods != null){
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
    public static void exportExcel(String templateAbsolutePath,Map<String, Object> beans,String fullPath){
        Configuration config = new Configuration();
        XLSTransformer transformer = new XLSTransformer(config);
        try {
            transformer.transformXLS(templateAbsolutePath, beans, fullPath);
            log.info("Finish export report file in "+ fullPath);
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
    public static List<MjrStockTransDetailDTO> convertGoodsSerialToDetail(List<MjrStockGoodsSerialDTO> lstStockGoodsSerial, Map<String,String> mapAppStockStatus){
        List<MjrStockTransDetailDTO> lstResult = Lists.newArrayList();
        if (!DataUtil.isListNullOrEmpty(lstStockGoodsSerial)) {
            for(MjrStockGoodsSerialDTO i:lstStockGoodsSerial){
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
                //
                lstResult.add(temp);
            }
        }

        return lstResult;
    }
    /*

     */
    public static  List<MjrStockTransDetailDTO> setNameValueGoodsDetail(List<MjrStockTransDetailDTO> lstGoodsDetail,
                                                                        Map<String,CatGoodsDTO> mapGoodsIdGoods,Map<String,CatStockDTO> mapStockIdStock,Map<String,String> mapGoodsState){
        if(!DataUtil.isListNullOrEmpty(lstGoodsDetail)){
            CatGoodsDTO currentGoods;
            for(MjrStockTransDetailDTO i: lstGoodsDetail){
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
                i.setGoodsStateValue(mapGoodsState.get(i.getGoodsState()));
                i.setStockValue(getMapValue(mapStockIdStock,i.getStockId() ));
            }
        }else{
            return Lists.newArrayList();
        }
        return lstGoodsDetail;
    }

    /*
        get stock
     */
    public static List<CatStockDTO> getListStock(BaseService service,CatCustomerDTO currentCustomer, AuthTokenInfo tokenInfo){
        return service.findByCondition(getBaseConditions(currentCustomer.getId()),tokenInfo);
    }

    /*
        get user
     */
    public static List<CatUserDTO> getListUser(CatUserService catUserService, CatCustomerDTO currentCustomer, AuthTokenInfo tokenInfo){
        return catUserService.getUserByCustomer(currentCustomer.getId(),tokenInfo);
    }

    private static List<Condition> getBaseConditions(String custId){
        List<Condition> lstCondition = Lists.newArrayList();
        lstCondition.add(new Condition("custId",Constants.SQL_PRO_TYPE.LONG,Constants.SQL_OPERATOR.EQUAL, custId));
        lstCondition.add(new Condition("status",Constants.SQL_OPERATOR.EQUAL,Constants.STATUS.ACTIVE));
        lstCondition.add(new Condition("name",Constants.SQL_OPERATOR.VNM_ORDER,"asc"));
        return lstCondition;
    }


    /*
       get goods
    */
    public static List<CatGoodsDTO> getListGoods(BaseService service,CatCustomerDTO currentCustomer, AuthTokenInfo tokenInfo){
        return service.findByCondition(getBaseConditions(currentCustomer.getId()),tokenInfo);
    }

    /*

     */
     public static void loadFileToClient(HttpServletResponse response, String fileResource){
        try {
            File file = new File(fileResource);
            //
            String mimeType= URLConnection.guessContentTypeFromName(file.getName());
            response.setContentType(mimeType);
            response.setContentLength((int) file.length());//length in bytes
            response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + file.getName() +"\""));
            //
            InputStream is = new FileInputStream(file);
            FileCopyUtils.copy(is, response.getOutputStream());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
        export error when importing
     */
    public static String exportExcelError(List<MjrStockTransDetailDTO> lstError,String prefixFileName, boolean isImportTrans){
        String templatePath;
        if (isImportTrans) {
            templatePath = BundleUtils.getKey("template_url") + Constants.FILE_RESOURCE.IMPORT_ERROR_TEMPLATE;
        }else{
            templatePath = BundleUtils.getKey("template_url") + Constants.FILE_RESOURCE.EXPORT_ERROR_TEMPLATE;
        }
        log.info("Number of Errors: "+ lstError.size());

        File file = new File(templatePath);
        String templateAbsolutePath = file.getAbsolutePath();

        Map<String, Object> beans = new HashMap<>();
        beans.put("items", lstError);

        String fullFileName = prefixFileName +"_"+ DateTimeUtils.getSysDateTimeForFileName() + ".xlsx";
        String reportFullPath = BundleUtils.getKey("temp_url") + fullFileName;

        exportExcel(templateAbsolutePath,beans,reportFullPath);

        return fullFileName;
    }

    /*
        export error when importing
     */
    public static String exportExcelImportGoodsError(List<CatGoodsDTO> lstError,String prefixFileName){
        String templatePath = BundleUtils.getKey("template_url") + Constants.FILE_RESOURCE.IMPORT_GOODS_ERROR_TEMPLATE;
        log.info("Number of Errors: "+ lstError.size());

        File file = new File(templatePath);
        String templateAbsolutePath = file.getAbsolutePath();

        Map<String, Object> beans = new HashMap<String, Object>();
        beans.put("items", lstError);

        String fullFileName = prefixFileName +"_"+ DateTimeUtils.getSysDateTimeForFileName() + ".xlsx";
        String reportFullPath = BundleUtils.getKey("temp_url") + fullFileName;

        exportExcel(templateAbsolutePath,beans,reportFullPath);

        return fullFileName;
    }

    /*
         * Prepare HTTP Headers.
         */
    public static HttpHeaders getHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return headers;
    }

    /*
     * Add HTTP Authorization header, using Basic-Authentication to send client-credentials.
     */
    private static HttpHeaders getHeadersWithClientCredentials(){
        String plainClientCredentials= BundleUtils.getKey("client_id") +":"+ BundleUtils.getKey("client_secret");
        String base64ClientCredentials = new String(Base64.encodeBase64(plainClientCredentials.getBytes()));

        HttpHeaders headers = getHeaders();
        headers.add("Authorization", "Basic " + base64ClientCredentials);
        return headers;
    }

    public static AuthTokenInfo sendTokenRequest(String username, String password){
        RestTemplate restTemplate = new RestTemplate();
        String tokenURL = BundleUtils.getKey("token_url");
        tokenURL = tokenURL.replace("@username",username);
        tokenURL = tokenURL.replace("@password",password);
        HttpEntity<String> request = new HttpEntity<String>(getHeadersWithClientCredentials());
        ResponseEntity<Object> response = null;
        try {
            response = restTemplate.exchange(tokenURL, HttpMethod.POST, request, Object.class);
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>)response.getBody();
        AuthTokenInfo tokenInfo = new AuthTokenInfo();

        if(map == null){
            return tokenInfo;
        }

        tokenInfo.setAccess_token((String)map.get("access_token"));
        tokenInfo.setToken_type((String)map.get("token_type"));
        tokenInfo.setRefresh_token((String)map.get("refresh_token"));
        tokenInfo.setExpires_in((int)map.get("expires_in"));
        tokenInfo.setScope((String)map.get("scope"));

        return tokenInfo;
    }


    public static boolean saveUploadedFile(MultipartFile uploadfile,String fileName){
        String saveDirectory = BundleUtils.getKey("upload_url");
        String filepath  = Paths.get(saveDirectory, fileName).toString();
        try {
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filepath)));
            stream.write(uploadfile.getBytes());
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Error save file: "+ e.toString());
            return false;
        }

        return true;
    }

    public static File convertMultipartToFile(MultipartFile file)
    {
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

    public static String getCellValue(Cell cell){
        if(cell == null){
            return "";
        }
        cell.setCellType(CellType.STRING);
        return trimNoBreakingSpace(cell.getStringCellValue());
    }

    public static String trimNoBreakingSpace(String str){
        return str.replace(String.valueOf((char) 160), "").trim();
    }

    public static ImportFileResultDTO getListStockImportFromFile(MultipartFile mpf, HashSet<String> setGoodsCode,
                                                                 Map<String,CatGoodsDTO> mapGoods,Map<String,String> mapGoodsState,boolean isImportTransaction){
        ImportFileResultDTO importResult = new ImportFileResultDTO();
        List<MjrStockTransDetailDTO> lstGoods = Lists.newArrayList();
        boolean isValid = true;
        try {
            Workbook wb = WorkbookFactory.create(new FileInputStream(FunctionUtils.convertMultipartToFile(mpf)));
            Sheet sheet = null;
            if (wb instanceof HSSFWorkbook) {
                HSSFWorkbook workbook = (HSSFWorkbook) wb;
                sheet = workbook.getSheetAt(0);
            }else if (wb instanceof XSSFWorkbook) {
                XSSFWorkbook workbook = (XSSFWorkbook) wb;
                sheet = workbook.getSheetAt(0);
            }

            Iterator<Row> rowIterator = sheet.iterator();
            if(rowIterator.hasNext()){//read from second row!
                rowIterator.next();
            }
            int count = 0;
            StringBuilder errorInfo;
            CatGoodsDTO goodsDTO;
            boolean isSerial;
            String serial;
            List<String> lstCheckSerial = Lists.newArrayList();
            String serialCheckKey;
            while(rowIterator.hasNext()) {
                isSerial = false;
                goodsDTO = null;
                count ++;
                MjrStockTransDetailDTO goodsItem = new MjrStockTransDetailDTO();
                goodsItem.setColumnId(count+"");
                errorInfo = new StringBuilder();
                //
                Row row = rowIterator.next();
                //CODE
                Cell cellGoodsCode = row.getCell(1);
                String goodsCode = getCellValue(cellGoodsCode);
                if(DataUtil.isStringNullOrEmpty(goodsCode)){
                    errorInfo.append("Chưa có mã hàng");
                    isValid = false;
                }else{
                    //check whether goods_code is valid
                    if(!setGoodsCode.contains(goodsCode)){
                        errorInfo.append("\n Mã hàng không hợp lệ");
                        isValid = false;
                    }else{
                        goodsDTO = mapGoods.get(goodsCode);
                        isSerial = goodsDTO.isSerial();
                    }
                }
                goodsItem.setGoodsCode(goodsCode);
                //SERIAL
                if (isSerial) {
                    Cell cellSerial = row.getCell(4);
                    serial = getCellValue(cellSerial);
                    if(DataUtil.isStringNullOrEmpty(serial)){
                        errorInfo.append("\n Hàng serial cần nhập thông tin serial");
                        isValid = false;
                    }else{
                        //CHECK ENTERED SERIAL
                        serialCheckKey = goodsCode + goodsCode + serial;
                        if(lstCheckSerial.contains(serialCheckKey)){
                            errorInfo.append("\n Serial đã được nhập");
                            isValid = false;
                        }else{
                            lstCheckSerial.add(serialCheckKey);
                        }
                    }
                    goodsItem.setSerial(serial);
                }
                //NAME
                Cell cellGoodsName = row.getCell(2);
                goodsItem.setGoodsName(getCellValue(cellGoodsName));
                //STATE
                Cell cellStatus = row.getCell(3);
                String goodsState = getCellValue(cellStatus);
                if(DataUtil.isStringNullOrEmpty(goodsState)){
                    errorInfo.append("\n Chưa có trạng thái hàng");
                    isValid = false;
                }else{
                    if(!goodsState.equalsIgnoreCase("1") && !goodsState.equalsIgnoreCase("2")){
                        errorInfo.append("\n Trạng thái hàng không hợp lệ(1: Bình thường,2:Hỏng");
                        isValid = false;
                    }
                }
                goodsItem.setGoodsState(goodsState);
                goodsItem.setGoodsStateValue(mapGoodsState.get(goodsState));
                //AMOUNT
                Cell cellAmount = row.getCell(5);
                String amount = getCellValue(cellAmount);
                if(DataUtil.isStringNullOrEmpty(amount)){
                    errorInfo.append("\n Chưa có số lượng hàng");
                    isValid = false;
                }else{
                    if(!isNumberFloat(amount)){
                        errorInfo.append("\n Số lượng hàng phải là số và >0");
                        isValid = false;
                    }else{
                        goodsItem.setAmountValue(formatNumber(amount));
                    }
                    if(isSerial && !amount.equalsIgnoreCase("1")){
                        errorInfo.append("\n Hàng quản lý serial số lượng nhập phải là 1");
                        isValid = false;
                    }
                }
                goodsItem.setAmount(amount);
                //PRICE
                Cell cellPrice = row.getCell(6);
                String price = getCellValue(cellPrice);
                if(!DataUtil.isStringNullOrEmpty(price)){
                    if(!isNumberFloat(price)){
                        errorInfo.append("\n Giá phải là số và >0");
                        isValid = false;
                    }else{
                        if(isImportTransaction){
                            goodsItem.setInputPriceValue(formatNumber(price));
                        }else{
                            goodsItem.setOutputPriceValue(formatNumber(price));
                        }
                    }
                }else{
                    if(goodsDTO != null){
                        if(isImportTransaction){
                            goodsItem.setInputPriceValue(formatNumber(goodsDTO.getInPrice()));
                        }else{
                            goodsItem.setOutputPriceValue(formatNumber(goodsDTO.getOutPrice()));
                        }
                    }
                }

                if(isImportTransaction){
                    goodsItem.setInputPrice(price);
                }else{
                    goodsItem.setOutputPrice(price);
                }
                //CELL
                Cell cellCells = row.getCell(7);
                goodsItem.setCellCode(getCellValue(cellCells));
                //
                if(!isValid){
                    goodsItem.setErrorInfo(errorInfo.toString());
                }

                lstGoods.add(goodsItem);
            }
            //
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
        //
        importResult.setValid(isValid);
        importResult.setLstGoodsImport(lstGoods);
        return importResult;
    }

    public static ImportFileResultDTO getListGoodsImportFromFile(MultipartFile mpf,Map<String,String> mapGoodsState,
                                                                 Map<String,String> mapGoodsGroup,Map<String,String> mapUnitType){
        ImportFileResultDTO importResult = new ImportFileResultDTO();
        List<CatGoodsDTO> lstGoods = Lists.newArrayList();
        boolean isValid = true;
        try {
            Workbook wb = WorkbookFactory.create(new FileInputStream(FunctionUtils.convertMultipartToFile(mpf)));
            Sheet sheet = null;
            if (wb instanceof HSSFWorkbook) {
                HSSFWorkbook workbook = (HSSFWorkbook) wb;
                sheet = workbook.getSheetAt(0);
            }else if (wb instanceof XSSFWorkbook) {
                XSSFWorkbook workbook = (XSSFWorkbook) wb;
                sheet = workbook.getSheetAt(0);
            }

            Iterator<Row> rowIterator = sheet.iterator();
            if(rowIterator.hasNext()){//read from second row!
                rowIterator.next();
            }
            int count = 0;
            StringBuilder errorInfo;
            CatGoodsDTO goodsDTO;
            while(rowIterator.hasNext()) {
                count ++;
                goodsDTO = new CatGoodsDTO();
                goodsDTO.setColumnId(count+"");
                errorInfo = new StringBuilder();
                //
                Row row = rowIterator.next();
                //goods serial
                Cell cellSerial = row.getCell(5);
                String serial = getCellValue(cellSerial);
                if(DataUtil.isStringNullOrEmpty(serial)){
                    errorInfo.append("\n Chưa có thông tin quản lý serial");
                    isValid = false;
                }else{
                    if(!serial.equalsIgnoreCase("1") && !serial.equalsIgnoreCase("0")){
                        errorInfo.append("\n Thông tin serial không hợp lệ(1: QL theo serial,0:Không quản lý theo serial");
                        isValid = false;
                    }
                }
                String serialTypeName =  "1".equals(serial) ? "Có": "Không";
                goodsDTO.setIsSerial(serial);
                goodsDTO.setIsSerialName(serialTypeName);
                //goods code
                Cell cellGoodsCode = row.getCell(1);
                String goodsCode = getCellValue(cellGoodsCode);
                if(DataUtil.isStringNullOrEmpty(goodsCode)){
                    errorInfo.append("\n Chưa có mã hàng");
                    isValid = false;
                }
                goodsDTO.setCode(goodsCode);
                //goods name
                Cell cellGoodsName = row.getCell(2);
                String goodsName = getCellValue(cellGoodsName);
                if(DataUtil.isStringNullOrEmpty(goodsName)){
                    errorInfo.append("Chưa có tên hàng");
                    isValid = false;
                }
                goodsDTO.setName(goodsName);
                //STATE
                Cell cellStatus = row.getCell(6);
                String goodsState = getCellValue(cellStatus);
                if(DataUtil.isStringNullOrEmpty(goodsState)){
                    errorInfo.append("\n Chưa có trạng thái hàng");
                    isValid = false;
                }else{
                    if(!goodsState.equalsIgnoreCase("1") && !goodsState.equalsIgnoreCase("2")){
                        errorInfo.append("\n Trạng thái hàng không hợp lệ(1:Bình thường,2:Hỏng");
                        isValid = false;
                    }
                }
                goodsDTO.setStatus(goodsState);
                goodsDTO.setStatusName(mapGoodsState.get(goodsState));
                //UNIT TYPE
                Cell cellUnitType = row.getCell(3);
                String unitType = getCellValue(cellUnitType);
                if (DataUtil.isStringNullOrEmpty(unitType)) {
                    unitType = "1";
                }
                String unitTypeName = mapUnitType.get(unitType);
                if (unitTypeName == null) {
                    goodsDTO.setUnitType("1");
                    goodsDTO.setUnitTypeName(mapUnitType.get("1"));
                }else{
                    goodsDTO.setUnitType(unitType);
                    goodsDTO.setUnitTypeName(unitTypeName);
                }
                //GOODS GROUP
                Cell cellGoodsGroup = row.getCell(4);
                String goodsGroup = getCellValue(cellGoodsGroup);
                if (DataUtil.isStringNullOrEmpty(goodsGroup)) {
                    errorInfo.append("\n Chưa có nhóm hàng");
                    isValid = false;
                }else{
                    String groupName = mapGoodsGroup.get(goodsGroup);
                    if (DataUtil.isStringNullOrEmpty(groupName)) {
                        errorInfo.append("\n Nhóm hàng chưa đúng");
                        isValid = false;
                    }
                    goodsDTO.setGoodsGroupId(goodsGroup);
                    goodsDTO.setGoodsGroupName(groupName);
                }
                //In PRICE
                Cell cellInPrice = row.getCell(7);
                String price = getCellValue(cellInPrice);
                if(DataUtil.isStringNullOrEmpty(price)){
                    errorInfo.append("\n Chưa có giá nhập");
                    isValid = false;
                }else{
                    if(!isNumberFloat(price)){
                        errorInfo.append("\n Giá phải là số và >0");
                        isValid = false;
                    }else{
                        goodsDTO.setInPriceValue(formatNumber(price));
                    }
                }
                goodsDTO.setInPrice(price);
                //Out PRICE
                Cell cellOutPrice = row.getCell(8);
                String outPrice = getCellValue(cellOutPrice);
                outPrice = outPrice.trim();
                if(DataUtil.isStringNullOrEmpty(outPrice)){
                    errorInfo.append("\n Chưa có giá xuất");
                    isValid = false;
                }else{
                    if(!isNumberFloat(price)){
                        errorInfo.append("\n Giá phải là số và >0");
                        isValid = false;
                    }else{
                        goodsDTO.setOutPriceValue(formatNumber(outPrice));
                    }
                }
                goodsDTO.setOutPrice(outPrice);
                //
                if(!isValid){
                    goodsDTO.setErrorInfo(errorInfo.toString());
                }
                //
                lstGoods.add(goodsDTO);
            }
            //
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
        //
        importResult.setValid(isValid);
        importResult.setLstGoods(lstGoods);
        return importResult;
    }

    /*
        valid pattern: 123 | 133.000
     */
    public static boolean isInteger(String value){
        double number = Double.parseDouble(value);
        return number%1 == 0;
    }

    public static String formatNumber(String number){
        if (!DataUtil.isStringNullOrEmpty(number)) {
                String plainNumber = removeScientificNotation(number);
                double dNumber = Double.valueOf(plainNumber);
                if (dNumber%1 != 0) {
                    return String.format("%,.4f", dNumber);
                }else{
                return String.format("%,.0f", dNumber);
            }
        }else{
            return "";
        }
    }

    public static String removeScientificNotation(String number){
        BigDecimal num = new BigDecimal(number);
        return num.toPlainString();
    }

    public static boolean isNumberFloat(String input){
        try {
            return Float.valueOf(input) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    //13,000 -> 13000
    public static String unformatFloat(String input){
        return input.replaceAll(",","");
    }

    public static void main(String[] args) {
        System.out.println(Float.valueOf("20.5"));
    }

}
