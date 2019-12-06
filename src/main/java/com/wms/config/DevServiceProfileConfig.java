package com.wms.config;
/**
 * Created by truongbx on 12/08/2018.
 */

import com.wms.ribbon.BaseURL;
import com.wms.utils.BundleUtils;
import java.net.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev_service")
public class DevServiceProfileConfig extends BaseProfile {
    @Autowired
    BaseURL loadBanlancingUrl;

    @Override
    public BaseURL getBaseUrLService() {
        return loadBanlancingUrl;
    }

    @Override
    public String getLoginURL() {
        return loadBanlancingUrl.getLoginURL();
    }

    @Override
    public String getTemplateURL() {
        return getRealURL(super.getTemplateURL());
    }

    public String getRealURL(String path) {
        URL url = BundleUtils.class.getClassLoader().getResource(path);
        String part = url.getPath();
        if (part.charAt(0) == '/') {
            part = part.replaceFirst("/", "");
        }
        return part;
    }
}
