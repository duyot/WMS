package com.wms.controller.common_managerment;

import com.wms.dto.CatCustomerDTO;
import com.wms.dto.CatUserDTO;
import com.wms.dto.ResponseObject;
import com.wms.services.interfaces.CatUserService;
import com.wms.utils.DataUtil;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/workspace/profile")
@Scope("session")
public class ProfileController {
    public CatUserDTO currentUser;
    @Autowired
    CatUserService catUserServices;
    @Autowired
    private HttpServletRequest requestCtx;
    private Logger log = LoggerFactory.getLogger(ProfileController.class);

    //------------------------------------------------------------------------------------------------------------------
    @PostConstruct
    public void init() {
        this.currentUser = (CatUserDTO) requestCtx.getSession().getAttribute("user");
    }

    @RequestMapping()
    public String home(Model model) {
        model.addAttribute("currentUser", currentUser);
        return "common_managerment/profile";
    }

    @RequestMapping(value = "/updateuser", method = RequestMethod.POST)
    @ResponseBody
    public String updateUser(@RequestBody CatUserDTO catUserDTO) {
        log.info(catUserDTO.getId());
        if (!DataUtil.isStringNullOrEmpty(catUserDTO.getPassword())) {
            catUserDTO.setPassword(DataUtil.BCryptPasswordEncoder(catUserDTO.getPassword()));
        }
        ResponseObject response = catUserServices.updateUser(catUserDTO);
        if (response == null) {
            return "";
        }
        return response.getStatusCode();
    }

    @RequestMapping(value = "/updatecustomer", method = RequestMethod.POST)
    @ResponseBody
    public String updateCustomer(@RequestBody CatCustomerDTO updateCustomer) {
        log.info(updateCustomer.getId());
        ResponseObject response = catUserServices.updateCustomer(updateCustomer);
        if (response == null) {
            return "";
        }
        return response.getStatusCode();
    }


}
