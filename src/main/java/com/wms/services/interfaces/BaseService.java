package com.wms.services.interfaces;

import com.wms.dto.AuthTokenInfo;
import com.wms.dto.Condition;
import com.wms.dto.ResponseObject;

import java.util.List;

/**
 * Created by duyot on 11/9/2016.
 */
public interface BaseService<T>{
    String getSysDate(AuthTokenInfo tokenInfo);
    String getSysDateWithPattern(String pattern,AuthTokenInfo tokenInfo);
    ResponseObject add(T tObject, AuthTokenInfo token);
    ResponseObject addList(List<T> tObject, AuthTokenInfo token);
    ResponseObject update(T tObject,AuthTokenInfo token);
    ResponseObject delete(Long id,AuthTokenInfo token);
    T findById(Long id,AuthTokenInfo token);
    List<T> findByCondition(List<Condition> lstCondition,AuthTokenInfo token);
    String deleteByCondition(List<Condition> lstCondition,AuthTokenInfo token);
    Long countByCondition(List<Condition> lstCondition,AuthTokenInfo token);
    List<T> getAll(AuthTokenInfo token);
}
