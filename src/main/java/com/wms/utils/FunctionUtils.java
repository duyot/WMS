package com.wms.utils;

import com.google.common.collect.Lists;
import com.wms.constants.Constants;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.CatUserService;
import net.sf.jxls.transformer.Configuration;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
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
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by duyot on 11/17/2016.
 */
public class FunctionUtils {
    public static Logger log = LoggerFactory.getLogger(FunctionUtils.class);

    //build map app_params
    public static Map<String,String>  buildMapAppParams(List<AppParamsDTO> lstAppParams){
        Map<String,String> map   = new HashMap();
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
    public static List<MjrStockTransDetailDTO> convertGoodsSerialToDetail(List<MjrStockGoodsSerialDTO> lstStockGoodsSerial){
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
                temp.setStatusValue(i.getStatus().equalsIgnoreCase("1")?"Trong kho":"Đã xuất");
                temp.setStockId(i.getStockId());
                //
                lstResult.add(temp);
            }
        }

        return lstResult;
    }
    /*

     */
    public static  List<MjrStockTransDetailDTO> setNameValueGoodsDetail(List<MjrStockTransDetailDTO> lstGoodsDetail,
                                                                        Map<String,CatGoodsDTO> mapGoodsIdGoods,String stockName,Map<String,String> mapGoodsState){
        if(!DataUtil.isListNullOrEmpty(lstGoodsDetail)){
            CatGoodsDTO currentGoods;
            for(MjrStockTransDetailDTO i: lstGoodsDetail){
                currentGoods = mapGoodsIdGoods.get(i.getGoodsId());
                //
                i.setGoodsCode(currentGoods.getCode());
                i.setGoodsName(currentGoods.getName());
                i.setAmountValue(FunctionUtils.formatNumber(i.getAmount()));
                i.setInputPriceValue(FunctionUtils.formatNumber(i.getInputPrice()));
                i.setOutputPriceValue(FunctionUtils.formatNumber(i.getOutputPrice()));
                i.setGoodsStateValue(mapGoodsState.get(i.getGoodsState()));
                i.setStockValue(stockName);
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
        List<Condition> lstCondition = Lists.newArrayList();
        lstCondition.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG,Constants.SQL_OPERATOR.EQUAL,currentCustomer.getId()));
        lstCondition.add(new Condition("status",Constants.SQL_OPERATOR.EQUAL,Constants.STATUS.ACTIVE));
        return service.findByCondition(lstCondition,tokenInfo);
    }

    /*
        get user
     */
    public static List<CatUserDTO> getListUser(CatUserService catUserService, CatCustomerDTO currentCustomer, AuthTokenInfo tokenInfo){
        return catUserService.getUserByCustomer(currentCustomer.getId(),tokenInfo);
    }

    /*
       get goods
    */
    public static List<CatGoodsDTO> getListGoods(BaseService service,CatCustomerDTO currentCustomer, AuthTokenInfo tokenInfo){
        List<Condition> lstCondition = Lists.newArrayList();
        lstCondition.add(new Condition("custId",Constants.SQL_PRO_TYPE.LONG,Constants.SQL_OPERATOR.EQUAL,currentCustomer.getId()));
        lstCondition.add(new Condition("status",Constants.SQL_OPERATOR.EQUAL,Constants.STATUS.ACTIVE));
        return service.findByCondition(lstCondition,tokenInfo);
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
    public static String exportExcelError(List<MjrStockTransDetailDTO> lstError,String prefixFileName){
        String templatePath = BundleUtils.getkey("template_url") + Constants.FILE_RESOURCE.IMPORT_ERROR_TEMPLATE;
        log.info("Number of Errors: "+ lstError.size());

        File file = new File(templatePath);
        String templateAbsolutePath = file.getAbsolutePath();

        Map<String, Object> beans = new HashMap<String, Object>();
        beans.put("items", lstError);

        String fullFileName = prefixFileName +"_"+ DateTimeUtils.getSysDateTimeForFileName() + ".xlsx";
        String reportFullPath = BundleUtils.getkey("temp_url") + fullFileName;

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
        String plainClientCredentials= BundleUtils.getkey("client_id") +":"+ BundleUtils.getkey("client_secret");
        String base64ClientCredentials = new String(Base64.encodeBase64(plainClientCredentials.getBytes()));

        HttpHeaders headers = getHeaders();
        headers.add("Authorization", "Basic " + base64ClientCredentials);
        return headers;
    }

    public static AuthTokenInfo sendTokenRequest(String username, String password){
        RestTemplate restTemplate = new RestTemplate();
        String tokenURL = BundleUtils.getkey("token_url");
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
        String saveDirectory = BundleUtils.getkey("upload_url");
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
        return cell.getStringCellValue().trim();
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
            while(rowIterator.hasNext()) {
                isSerial = false;
                count ++;
                MjrStockTransDetailDTO goodsItem = new MjrStockTransDetailDTO();
                goodsItem.setColumnId(count+"");
                errorInfo = new StringBuilder();
                //
                Row row = rowIterator.next();
                //goods serial
                Cell cellSerial = row.getCell(4);
                String serial = getCellValue(cellSerial);
                goodsItem.setSerial(serial);
                //goods code
                Cell cellGoodsCode = row.getCell(1);
                String goodsCode = getCellValue(cellGoodsCode);
                if(DataUtil.isStringNullOrEmpty(goodsCode)){
                    errorInfo.append("Chưa có mã hàng");
                    isValid = false;
                }else{
                    //check if goods_code is valid
                    if(!setGoodsCode.contains(goodsCode)){
                        errorInfo.append("\n Mã hàng không hợp lệ");
                        isValid = false;
                    }else{
                        //check serial info
                        goodsDTO = mapGoods.get(goodsCode);
                        isSerial = goodsDTO.isSerial();
                        if(isSerial && DataUtil.isStringNullOrEmpty(serial)){
                            errorInfo.append("\n Hàng serial cần nhập thông tin serial");
                            isValid = false;
                        }
                    }
                }
                goodsItem.setGoodsCode(goodsCode);
                //goods name
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
                if(DataUtil.isStringNullOrEmpty(price)){
                    errorInfo.append("\n Chưa có giá");
                    isValid = false;
                }else{
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
                }
                if(isImportTransaction){
                    goodsItem.setInputPrice(price);
                }else{
                    goodsItem.setOutputPrice(price);
                }
                //goods cell
                Cell cellCells = row.getCell(7);
                goodsItem.setCellCode(getCellValue(cellCells));

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

    public static String formatNumber(String number){
        if (!DataUtil.isStringNullOrEmpty(number)) {
            double dNumber = Double.valueOf(number);
            return String.format("%,.2f", dNumber);
        }else{
            return "";
        }
    }

    public static boolean isNumberFloat(String input){
        try {
            return Float.valueOf(input) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println(Float.valueOf("20.5"));
    }

}
