package com.wms;

import com.wms.config.ProfileConfigInterface;
import com.wms.ribbon.RibbonConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;
@EnableEurekaClient
@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@ComponentScan(value = "com.wms")
//@RibbonClient(name = "ping-server", configuration = RibbonConfiguration.class)
public class SpringBootExampleApplication extends SpringBootServletInitializer {
	@Autowired
	public ProfileConfigInterface profileConfig;
	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver slr = new SessionLocaleResolver();
		slr.setDefaultLocale(new Locale("vi", "VN"));
		return slr;
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
		lci.setParamName("lang");
		return lci;
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringBootExampleApplication.class, args);
	}
	@Bean
	WebMvcConfigurer configurer () {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addResourceHandlers (ResourceHandlerRegistry registry) {
				registry.addResourceHandler("/test/**").
//						addResourceLocations("file:C:\\Users\\truong_buiXuan\\Desktop\\css\\");
			        	addResourceLocations("file:"+profileConfig.getTempURL());
			}
		};
	}
}
