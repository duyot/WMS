package com.wms.services.interfaces;


import com.wms.dto.CatUserDTO;
import com.wms.dto.Condition;
import com.wms.dto.ResponseObject;

import java.util.List;

/**
 * Created by duyot on 10/17/2016.
 */
public interface UserService {
    List<CatUserDTO> getAlls();

    public ResponseObject register(CatUserDTO catUserDTO);

    public CatUserDTO login(CatUserDTO catUserDTO);

    public List<CatUserDTO> findUserByCondition(List<Condition> lstCondition);

    public boolean update(CatUserDTO catUserDTO);

    public boolean delelte(Long id);
}
