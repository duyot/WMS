package com.wms.ribbon;

public interface BaseURL {
     String getUrlToPostMethod(String servicePrefix, String serviceMethod);
	 String getPostURLWithoutTokenKey( String serviceMethod);
     String getUrlToGetMethod(Long id ,String servicePrefix, String serviceMethod);
     String getUrlToGetMethod(String query ,String servicePrefix, String serviceMethod);
     String getLoginURL();
     void setTokenURL(String tokenURL);
     void setServiceURL(String serviceURL);

}
