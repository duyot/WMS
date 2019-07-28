package com.wms.config;
/**
 * Created by truongbx on 12/08/2018.
 */

import com.wms.ribbon.BaseURL;
import com.wms.ribbon.CurrentUserLogIn;
import com.wms.utils.BundleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.URL;

@Component
@Profile("dev_local")
public class DevLocalProfileConfig extends BaseProfile {
	//
	@Autowired
	BaseURL localUrl;
	Logger log = LoggerFactory.getLogger(DevLocalProfileConfig.class);
	@Autowired
	CurrentUserLogIn currentUserLogIn;

	@PostConstruct
	public void setUpBaseURL() {
		this.localUrl.setTokenURL(getTokenURL());
		this.localUrl.setServiceURL(getServiceURL());
	}

	@Override
	public BaseURL getBaseUrLService() {
		return localUrl;
	}

	@Override
	public String getTemplateURL() {
		return getRealURL(super.getTemplateURL());
	}

	public String getRealURL(String path) {
		log.info(path);
		if (!path.contains("http")) {
			return path;
		}
		URL url = BundleUtils.class.getClassLoader().getResource(path);
		String part = url.getPath();
		if (part.charAt(0) == '/') {
			part = part.replaceFirst("/", "");
		}
		return part;
	}
}
