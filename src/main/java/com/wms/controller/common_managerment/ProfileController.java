package com.wms.controller.common_managerment;

import com.wms.dto.AuthTokenInfo;
import com.wms.dto.CatCustomerDTO;
import com.wms.dto.CatUserDTO;
import com.wms.dto.ResponseObject;
import com.wms.services.interfaces.CatUserService;
import com.wms.utils.DataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/workspace/profile")
public class ProfileController {
    Logger log = LoggerFactory.getLogger(ProfileController.class);

    private AuthTokenInfo tokenInfo;
    public CatUserDTO currentUser;

    @Autowired
    CatUserService catUserServices;

    @ModelAttribute("tokenInfo")
    public void setTokenInfo(HttpServletRequest request){
        this.tokenInfo =  (AuthTokenInfo) request.getSession().getAttribute("tokenInfo");
    }

    @ModelAttribute("currentUser")
    public void setCurrentUser(HttpServletRequest request){
        this.currentUser =  (CatUserDTO) request.getSession().getAttribute("user");
    }

    @RequestMapping()
    public String home(){
        return "common_managerment/profile";
    }

    @RequestMapping(value = "/updateuser",method = RequestMethod.POST)
    @ResponseBody public String updateUser(@RequestBody CatUserDTO catUserDTO){
        log.info(catUserDTO.getId());
        if (!DataUtil.isStringNullOrEmpty(catUserDTO.getPassword())) {
            catUserDTO.setPassword(DataUtil.BCryptPasswordEncoder(catUserDTO.getPassword()));
        }
        ResponseObject response = catUserServices.updateUser(catUserDTO,tokenInfo);
        if (response == null) {
            return "";
        }
        return response.getStatusCode();
    }

    @RequestMapping(value = "/updatecustomer",method = RequestMethod.POST)
    @ResponseBody public String updateCustomer(@RequestBody CatCustomerDTO updateCustomer){
        log.info(updateCustomer.getId());
        ResponseObject response = catUserServices.updateCustomer(updateCustomer,tokenInfo);
        if (response == null) {
            return "";
        }
        return response.getStatusCode();
    }


}