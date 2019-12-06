package com.wms.base;

import com.google.common.collect.Lists;
import com.wms.config.ProfileConfigInterface;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.Condition;
import com.wms.dto.ResponseObject;
import com.wms.ribbon.BaseURL;
import com.wms.utils.BundleUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Created by duyot on 11/9/2016.
 */
public class BaseDP<T> {
    public final boolean lbservice = Boolean.parseBoolean(BundleUtils.getKey("lbservice"));

    @Autowired
    ProfileConfigInterface profileConfig;


    public String SERVICE_PREFIX;


    public RestTemplate restTemplate;

    public Class<T[]> valueArrayClass;
    public Class objectClass;

    Logger log = LoggerFactory.getLogger(BaseDP.class);

    public BaseDP(Class<T[]> valueArrayClass, Class objectClass, String service_prefix) {
        this.restTemplate = new RestTemplate();
        this.valueArrayClass = valueArrayClass;
        this.objectClass = objectClass;
        this.SERVICE_PREFIX = service_prefix;
    }

    public String getSysDate() {
        String url = getUrlLoadBalancing(0, Constants.SERVICE_METHOD.GET_SYS_DATE);
        ;
        try {
            return restTemplate.getForObject(url, String.class);
        } catch (RestClientException e) {
            log.error(e.toString());
            return "";
        }
    }

    public String getSysDateWithPattern(String pattern) {
        try {
            String url = getUrlLoadBalancing(0, Constants.SERVICE_METHOD.GET_SYS_DATE_PATTERN);
            return restTemplate.postForObject(url, pattern, String.class);
        } catch (RestClientException e) {
            log.info(e.toString());
            e.printStackTrace();
            return "";
        }
    }

    public ResponseObject add(T tObject) {
        try {
            String url = getUrlLoadBalancing(0, Constants.SERVICE_METHOD.ADD);
            return restTemplate.postForObject(url, tObject, ResponseObject.class);
        } catch (RestClientException e) {
            log.info(e.toString());
            e.printStackTrace();
            return new ResponseObject(Responses.ERROR.getName(), Responses.ERROR.getName(), "");
        }
    }


    public ResponseObject addList(List<T> tObject) {
        try {
            String url = getUrlLoadBalancing(0, Constants.SERVICE_METHOD.ADD_LIST);
            return restTemplate.postForObject(url, tObject, ResponseObject.class);
        } catch (RestClientException e) {
            log.info(e.toString());
            e.printStackTrace();
            return new ResponseObject(Responses.ERROR.getName(), Responses.ERROR.getName(), "");
        }
    }

    public ResponseObject update(T tObject) {
        try {
            String url = getUrlLoadBalancing(0, Constants.SERVICE_METHOD.UPDATE);
            return restTemplate.postForObject(url, tObject, ResponseObject.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            log.info(e.toString());
            return new ResponseObject(Responses.ERROR.getName(), Responses.ERROR.getName(), "");
        }
    }

    public ResponseObject updateByProperties(T tObject) {
        try {
            String url = getUrlLoadBalancing(0, Constants.SERVICE_METHOD.UPDATE_BYE_PROPERTIES);
            return restTemplate.postForObject(url, tObject, ResponseObject.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            log.info(e.toString());
            return new ResponseObject(Responses.ERROR.getName(), Responses.ERROR.getName(), "");
        }
    }

    public ResponseObject delete(Long id) {
        String url = getUrlLoadBalancing(id, Constants.SERVICE_METHOD.DELETE);
        try {
            ResponseEntity<ResponseObject> response = restTemplate.exchange(url, HttpMethod.DELETE, null, ResponseObject.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.toString());
            return new ResponseObject(Responses.ERROR.getName(), Responses.ERROR.getName(), "");
        }
    }

    public T findById(Long id) {
        String url = getUrlLoadBalancing(id, Constants.SERVICE_METHOD.FIND_BY_ID);
        try {
            return (T) restTemplate.getForObject(url, objectClass);
        } catch (RestClientException e) {
            log.error(e.toString());
            return null;
        }
    }

    public List<T> findByCondition(List<Condition> lstCondition) {
        try {
            String url = getUrlLoadBalancing(0, Constants.SERVICE_METHOD.FIND_BY_CONDITION);
            ResponseEntity<T[]> responseEntity = restTemplate.postForEntity(url, lstCondition, valueArrayClass);
            return Arrays.asList(responseEntity.getBody());
        } catch (RestClientException e) {
            log.error(e.toString());
            return new ArrayList<>();
        }
    }

    public String deleteByCondition(List<Condition> lstCondition) {
        try {
            String url = getUrlLoadBalancing(0, Constants.SERVICE_METHOD.DELETE_BY_CONDITION);
            return restTemplate.postForObject(url, lstCondition, String.class);
        } catch (RestClientException e) {
            log.info(e.toString());
            e.printStackTrace();
            return "";
        }
    }

    public Long countByCondition(List<Condition> lstCondition) {
        try {
            String url = getUrlLoadBalancing(0, Constants.SERVICE_METHOD.COUNT_BY_CONDITION);
            ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, lstCondition, Long.class);
            return responseEntity.getBody();
        } catch (RestClientException e) {
            log.error(e.toString());
            return 0L;
        }
    }

    public List<T> getAll() {
        try {
            String url = getUrlLoadBalancing(0, Constants.SERVICE_METHOD.GET_ALL);
            ResponseEntity<T[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, valueArrayClass);
            return Arrays.asList(responseEntity.getBody());
        } catch (RestClientException e) {
            log.error(e.toString());
            return Lists.newArrayList();
        }
    }

    public String getUrlLoadBalancing(long id, String serviceMethod) {
        return getUrl(id, SERVICE_PREFIX, serviceMethod, profileConfig.getBaseUrLService());
    }

    //    use for register user and customer
    public String getUrlWithoutTokenKey(String serviceMethod) {
        return profileConfig.getBaseUrLService().getPostURLWithoutTokenKey(serviceMethod);
    }

    public String getUrl(long id, String servicePrefix, String serviceMethod, BaseURL baseURL) {
        if (id == 0) {
            return baseURL.getUrlToPostMethod(servicePrefix, serviceMethod);
        } else {
            return baseURL.getUrlToGetMethod(id, servicePrefix, serviceMethod);
        }
    }

    public String getUrlLoadBalancingQuery(String query, String serviceMethod) {
        return getUrlQuery(query, SERVICE_PREFIX, serviceMethod, profileConfig.getBaseUrLService());
    }

    public String getUrlLoadBalancingQuery(String query, String servicePrefix, String serviceMethod) {
        return getUrlQuery(query, servicePrefix, serviceMethod, profileConfig.getBaseUrLService());
    }

    public String getUrlQuery(String query, String servicePrefix, String serviceMethod, BaseURL baseURL) {
        return baseURL.getUrlToGetMethod(query, servicePrefix, serviceMethod);
    }
}
