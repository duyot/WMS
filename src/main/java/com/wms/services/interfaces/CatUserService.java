package com.wms.services.interfaces;


import com.wms.dto.*;
import java.util.List;

/**
 * Created by duyot on 10/17/2016.
 */
public interface CatUserService extends BaseService<CatUserDTO> {
    ResponseObject register(CatUserDTO catUserDTO );
    ResponseObject updateUser(CatUserDTO catUserDTO);
    ResponseObject updateCustomer(CatCustomerDTO catCustomerDTO );
    CatUserDTO login(CatUserDTO catUserDTO);
    List<CatCustomerDTO> getCustomer(String userId  );
    List<CatUserDTO> getUserByCustomer(String custId );
}
