package com.wms.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;

/**
 * Created by duyot on 11/17/2016.
 */
public class FunctionUtils {
    public static Logger log = LoggerFactory.getLogger(FunctionUtils.class);

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
}
