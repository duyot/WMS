/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wms.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 *
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
            if (input == null) {
                log.error("Sorry, unable to find " + filename);
                return "";
            }
            Properties prop = new Properties();
            prop.load(input);
            return prop.getProperty(key);
        } catch (IOException ex) {
            log.error(ex.toString());
        }
        return key;
    }

     
}
