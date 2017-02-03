package com.wms.services.impl;

import com.wms.base.BaseDP;
import com.wms.dto.AuthTokenInfo;
import com.wms.dto.ResponseObject;
import com.wms.services.interfaces.BaseService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by duyot on 11/9/2016.
 */
public class BaseServiceImpl<T,TDP extends BaseDP<T>> implements BaseService<T> {
    public TDP tdp;

    @Override
    public ResponseObject add(T tObject, AuthTokenInfo token) {
        return tdp.add(tObject,token);
    }

    @Override
    public ResponseObject update(T tObject,AuthTokenInfo token) {
        return tdp.update(tObject,token);
    }

    @Override
    public ResponseObject delete(Long id, AuthTokenInfo token) {
        return tdp.delete(id,token);
    }

    @Override
    public T findById(Long id,AuthTokenInfo token) {
        return tdp.findById(id,token);
    }

    @Override
    public List getAll(AuthTokenInfo token) {
        return tdp.getAll(token);
    }

    @Override
    public List findByCondition(List lstCondition,AuthTokenInfo token) {
        return tdp.findByCondition(lstCondition,token);
    }
}
