package com.wms.services.interfaces;

import com.wms.dto.Condition;

import java.util.List;

/**
 * Created by duyot on 11/9/2016.
 */
public interface BaseService<T>{
    public boolean add(T tObject);
    public boolean update(T tObject);
    public boolean delete(Long id);
    public List<T> findByCondition(List<Condition> lstCondition);
    public List<T> getAll();
}
