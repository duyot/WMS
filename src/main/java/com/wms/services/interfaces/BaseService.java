package com.wms.services.interfaces;

import com.wms.dto.AuthTokenInfo;
import com.wms.dto.Condition;
import com.wms.dto.ResponseObject;

import java.util.List;

/**
 * Created by duyot on 11/9/2016.
 */
public interface BaseService<T>{
    public String getSysDate(AuthTokenInfo tokenInfo);
    public String getSysDateWithPattern(String pattern,AuthTokenInfo tokenInfo);
    public ResponseObject add(T tObject, AuthTokenInfo token);
    public ResponseObject update(T tObject,AuthTokenInfo token);
    public ResponseObject delete(Long id,AuthTokenInfo token);
    public T findById(Long id,AuthTokenInfo token);
    public List<T> findByCondition(List<Condition> lstCondition,AuthTokenInfo token);
    public List<T> getAll(AuthTokenInfo token);
}
