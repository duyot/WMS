package com.wms.services.impl;

import com.wms.base.BaseDP;
import com.wms.services.interfaces.BaseService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by duyot on 11/9/2016.
 */
public class BaseServiceImpl<T,TDP extends BaseDP<T>> implements BaseService<T> {
    public TDP tdp;

    @Override
    public boolean add(T tObject) {
        return tdp.add(tObject);
    }

    @Override
    public boolean update(T tObject) {
        return tdp.update(tObject);
    }

    @Override
    public boolean delete(Long id) {
        return tdp.delete(id);
    }

    @Override
    public List getAll() {
        return tdp.getAll();
    }

    @Override
    public List findByCondition(List lstCondition) {
        return tdp.findByCondition(lstCondition);
    }
}
