package com.wms.utils;

import com.google.common.collect.Lists;
import com.wms.dto.AuthTokenInfo;
import com.wms.dto.MjrStockTransDetailDTO;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by duyot on 11/17/2016.
 */
public class FunctionUtils {
    public static Logger log = LoggerFactory.getLogger(FunctionUtils.class);

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

    public static List<MjrStockTransDetailDTO> getListStockImportFromFile(MultipartFile mpf){
        List<MjrStockTransDetailDTO> lstGoods = Lists.newArrayList();
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
            while(rowIterator.hasNext()) {
                count ++;
                MjrStockTransDetailDTO goodsItem = new MjrStockTransDetailDTO();
                goodsItem.setColumnId(count+"");
                Row row = rowIterator.next();
                //goods code
                Cell cellGoodsCode = row.getCell(0);
                cellGoodsCode.setCellType(CellType.STRING);
                goodsItem.setGoodsCode(cellGoodsCode.getStringCellValue());
                //goods name
                Cell cellGoodsName = row.getCell(1);
                cellGoodsName.setCellType(CellType.STRING);
                goodsItem.setGoodsName(cellGoodsName.getStringCellValue());
                //goods status
                Cell cellStatus = row.getCell(2);
                cellStatus.setCellType(CellType.STRING);
                goodsItem.setGoodsState(cellStatus.getStringCellValue().equalsIgnoreCase("1")?"Bình thường":"Hỏng");
                //goods serial
                Cell cellSerial = row.getCell(3);
                cellSerial.setCellType(CellType.STRING);
                goodsItem.setSerial(cellSerial.getStringCellValue());
                //goods amount
                Cell cellAmount = row.getCell(4);
                cellAmount.setCellType(CellType.STRING);
                goodsItem.setAmount(formatNumber(cellAmount.getStringCellValue()));
                //goods input
                Cell cellInputPrice = row.getCell(5);
                cellInputPrice.setCellType(CellType.STRING);
                goodsItem.setInputPrice(FunctionUtils.formatNumber(cellInputPrice.getStringCellValue()));
                //goods output
                Cell cellOutputPrice = row.getCell(6);
                cellOutputPrice.setCellType(CellType.STRING);
                goodsItem.setOutputPrice(FunctionUtils.formatNumber(cellOutputPrice.getStringCellValue()));
                //goods cell
                Cell cellCells = row.getCell(7);
                cellCells.setCellType(CellType.STRING);
                goodsItem.setCellCode(cellCells.getStringCellValue());

                lstGoods.add(goodsItem);
            }
            //
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
            log.info(e.toString());
        }
        //
        return lstGoods;
    }

    public static String formatNumber(String number){
        double dNumber = Double.valueOf(number);
        return String.format("%,.2f", dNumber);
    }

    public static void main(String[] args) {
        System.out.println(FunctionUtils.formatNumber("200000"));
    }

}
