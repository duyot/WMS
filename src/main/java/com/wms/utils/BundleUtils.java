package com.wms.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author duyot
 */
public class BundleUtils {
    public static Logger log = LoggerFactory.getLogger(BundleUtils.class);

     public static String getKey(String key) {
        try {
            InputStream input;
            String filename = "config.properties";
            input = BundleUtils.class.getClassLoader().getResourceAsStream(filename);
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
