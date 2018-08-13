package com.wms.config;
/**
 * Created by truongbx on 12/08/2018.
 */
import com.wms.ribbon.BaseURL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class ProductProfileConfig  extends BaseProfile{
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
}
