package com.wms.services.interfaces;


import com.wms.dto.*;

import java.util.List;

/**
 * Created by duyot on 10/17/2016.
 */
public interface CatUserService extends BaseService<CatUserDTO> {
    ResponseObject register(CatUserDTO catUserDTO,AuthTokenInfo tokenInfo);
    ResponseObject updateUser(CatUserDTO catUserDTO,AuthTokenInfo tokenInfo);
    ResponseObject guestAddUser(CatUserDTO catUserDTO);
    ResponseObject updateCustomer(CatCustomerDTO catCustomerDTO,AuthTokenInfo tokenInfo);
    CatUserDTO login(CatUserDTO catUserDTO);
    List<CatCustomerDTO> getCustomer(String userId, AuthTokenInfo tokenInfo );
    List<CatUserDTO> getUserByCustomer(String custId, AuthTokenInfo tokenInfo );
}
