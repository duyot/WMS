/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wms.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author duyot
 */
public class ResourceBundleUtils {
    public static Logger log = LoggerFactory.getLogger(ResourceBundleUtils.class);

    private static ResourceBundle rsConfig = null;
    public static final String CASCASCAS = "cas";

    //     public static String getStringCas(String key) {
//        rsConfig = ResourceBundle.getBundle(CAS);
//        return rsConfig.getString(key);
//    }
    public static String getkey(String key) {
        try {
            InputStream input = null;
            String filename = "lang_vi.properties";
            input = ResourceBundleUtils.class.getClassLoader().getResourceAsStream(filename);
            Reader reader = new InputStreamReader(input, "UTF-8");
            if (input == null) {
                log.error("Sorry, unable to find " + filename);
                return "";
            }
            Properties prop = new Properties();
            prop.load(reader);
            return prop.getProperty(key);
        } catch (IOException ex) {
            log.error(ex.toString());
        }
        return key;
    }


}
