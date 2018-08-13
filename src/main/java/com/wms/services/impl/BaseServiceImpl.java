package com.wms.services.impl;

import com.wms.base.BaseDP;
import com.wms.dto.Condition;
import com.wms.dto.ResponseObject;
import com.wms.services.interfaces.BaseService;

import java.util.List;

/**
 * Created by duyot on 11/9/2016.
 */
public class BaseServiceImpl<T,TDP extends BaseDP<T>> implements BaseService<T> {
    public TDP tdp;


    @Override
    public String getSysDate() {
        return tdp.getSysDate();
    }

    @Override
    public String getSysDateWithPattern(String pattern) {
        return tdp.getSysDateWithPattern(pattern);
    }

    @Override
    public ResponseObject add(T tObject ) {
        return tdp.add(tObject);
    }

    @Override
    public ResponseObject update(T tObject ) {
        return tdp.update(tObject);
    }

    @Override
    public ResponseObject delete(Long id ) {
        return tdp.delete(id);
    }

    @Override
    public T findById(Long id ) {
        return tdp.findById(id);
    }

    @Override
    public Long countByCondition(List<Condition> lstCondition) {
        return tdp.countByCondition(lstCondition);
    }

    @Override
    public List getAll( ) {
        return tdp.getAll();
    }

    @Override
    public List findByCondition(List lstCondition ) {
        return tdp.findByCondition(lstCondition);
    }

    @Override
    public String deleteByCondition(List<Condition> lstCondition) {
        return tdp.deleteByCondition(lstCondition);
    }

    @Override
    public ResponseObject addList(List<T> tObject ) {
        return tdp.addList(tObject);
    }
}
