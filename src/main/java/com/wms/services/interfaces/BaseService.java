package com.wms.services.interfaces;

import com.wms.dto.Condition;
import com.wms.dto.ResponseObject;
import java.util.List;

/**
 * Created by duyot on 11/9/2016.
 */
public interface BaseService<T> {
    String getSysDate();

    String getSysDateWithPattern(String pattern);

    ResponseObject add(T tObject);

    ResponseObject addList(List<T> tObject);

    ResponseObject update(T tObject);

    ResponseObject updateByProperties(T tObject);

    ResponseObject delete(Long id);

    T findById(Long id);

    List<T> findByCondition(List<Condition> lstCondition);

    String deleteByCondition(List<Condition> lstCondition);

    Long countByCondition(List<Condition> lstCondition);

    List<T> getAll();
}
