package com.wms.services.interfaces;


import com.wms.dto.*;

import java.util.List;

/**
 * Created by duyot on 10/17/2016.
 */
public interface CatUserService {
    public ResponseObject register(CatUserDTO catUserDTO,AuthTokenInfo tokenInfo);
    public CatUserDTO login(CatUserDTO catUserDTO);
    List<CatCustomerDTO> getCustomer(String userId, AuthTokenInfo tokenInfo );
}
