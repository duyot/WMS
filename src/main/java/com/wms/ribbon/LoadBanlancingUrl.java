package com.wms.ribbon;

import com.wms.config.WMSConfigManagerment;
import com.wms.constants.Constants;
import com.wms.redis.model.AuthTokenInfo;
import com.wms.redis.repository.RedisRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("loadBanlancingUrl")
public class LoadBanlancingUrl implements BaseURL{

    Logger log = LoggerFactory.getLogger(LoadBanlancingUrl.class);


    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private LoadBalancerClient loadBalancer;

    @Autowired
    private RedisRepository redisRepository;

    public String context_path = "/WMS_Webservices/services/";
    public String login_path = "/WMS_Webservices/login";

    @Override
    public String getUrlToPostMethod(String servicePrefix, String serviceMethod) {
            List<ServiceInstance> instances = this.discoveryClient.getInstances(WMSConfigManagerment.SERVICE_ID.toLowerCase());
            if (instances == null || instances.isEmpty()) {
                log.info("No instances for service: " + WMSConfigManagerment.SERVICE_ID);
                return null;
            }
            try {
                // May be throw IllegalStateException (No instances available)
                ServiceInstance serviceInstance = this.loadBalancer.choose(WMSConfigManagerment.SERVICE_ID);
                String url =  serviceInstance.getUri()+context_path+servicePrefix +serviceMethod;
                AuthTokenInfo token = redisRepository.find(serviceInstance.getUri().toString());
                return url + token.getAccess_token();

            } catch (IllegalStateException e) {
                log.info("error no instance available ");
                e.printStackTrace();
                return null;
            } catch (Exception e) {
                log.info("error get token ");
                e.printStackTrace();
                return null;
            }


    }
    @Override
    public String getUrlToGetMethod(Long id ,String servicePrefix, String serviceMethod) {
        List<ServiceInstance> instances = this.discoveryClient.getInstances(WMSConfigManagerment.SERVICE_ID.toLowerCase());
        if (instances == null || instances.isEmpty()) {
            log.info("No instances for service: " + WMSConfigManagerment.SERVICE_ID);
            return null;
        }
        try {
            // May be throw IllegalStateException (No instances available)
            ServiceInstance serviceInstance = this.loadBalancer.choose(WMSConfigManagerment.SERVICE_ID);
            log.info("Load Balancer choose " +  serviceInstance.getUri());
            String url =  serviceInstance.getUri()+context_path+servicePrefix +serviceMethod;
            AuthTokenInfo token = redisRepository.find(serviceInstance.getUri().toString());
            return url + id + Constants.SERVICE_METHOD.ACCESS_TOKEN + token.getAccess_token();
        } catch (IllegalStateException e) {
            log.info("error no instance available ");
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            log.info("error get token ");
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public String getUrlToGetMethod(String query, String servicePrefix, String serviceMethod) {
        List<ServiceInstance> instances = this.discoveryClient.getInstances(WMSConfigManagerment.SERVICE_ID.toLowerCase());
        if (instances == null || instances.isEmpty()) {
            log.info("No instances for service: " + WMSConfigManagerment.SERVICE_ID);
            return null;
        }
        try {
            // May be throw IllegalStateException (No instances available)
            ServiceInstance serviceInstance = this.loadBalancer.choose(WMSConfigManagerment.SERVICE_ID);
            log.info("Load Balancer choose " + serviceInstance.getUri());
            String url = serviceInstance.getUri() + context_path + servicePrefix + serviceMethod;
            AuthTokenInfo token = redisRepository.find(serviceInstance.getUri().toString());
            return url + "?" + query + Constants.SERVICE_METHOD.ACCESS_TOKEN_AND + token.getAccess_token();
        } catch (IllegalStateException e) {
            log.info("error no instance available ");
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            log.info("error get token ");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getLoginURL() {
        List<ServiceInstance> instances = this.discoveryClient.getInstances(WMSConfigManagerment.SERVICE_ID.toLowerCase());
        if (instances == null || instances.isEmpty()) {
            log.info("No instances for service: " + WMSConfigManagerment.SERVICE_ID);
            return null;
        }
        try {
            // May be throw IllegalStateException (No instances available)
            ServiceInstance serviceInstance = this.loadBalancer.choose(WMSConfigManagerment.SERVICE_ID);
            log.info("Load Balancer choose " +  serviceInstance.getUri());
            String url =  serviceInstance.getUri()+login_path;
            return url;
        } catch (IllegalStateException e) {
            log.info("error no instance available ");
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            log.info("error get token ");
            e.printStackTrace();
            return null;
        }
    }
}