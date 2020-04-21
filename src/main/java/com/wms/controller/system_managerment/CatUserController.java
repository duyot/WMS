package com.wms.controller.system_managerment;

import com.wms.base.BaseCommonController;
import com.wms.config.WMSConfigManagerment;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.PartnerService;
import com.wms.services.interfaces.StockService;
import com.wms.utils.DataUtil;
import com.wms.utils.ResourceBundleUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/workspace/cat_user_ctr")
@Scope("session")
public class CatUserController extends BaseCommonController {
    @Autowired
    BaseService catDeptServicesImpl;
    @Autowired
    BaseService catUserServices;
    @Autowired
    BaseService roleServiceImpl;
    @Autowired
    StockService catStockService;
    @Autowired
    PartnerService catPartnerService;
    @Autowired
    BaseService catCustServicesImpl;
    @Autowired
    BaseService mapUserStockServiceImpl;
    @Autowired
    BaseService mapUserPartnerServiceImpl;
    //
    List<TreeModel> lstTreeModal;
    Map<String, CatDepartmentDTO> mapIdDept = new HashMap<>();
    List<String> lstParentDeptId = new ArrayList<>();
    Map<String, CatCustomerDTO> mapIdCust = new HashMap<>();
    boolean isRoot = false;
    private Logger log = LoggerFactory.getLogger(CatUserController.class);

    //------------------------------------------------------------------------------------------------------------------
    @PostConstruct
    public void init() {
        initTreeDepartment();
    }

    //------------------------------------------------------------------------------------------------------------------
    @RequestMapping()
    public String home(Model model) {
        model.addAttribute("menuName", "menu.catuser");
        model.addAttribute("controller", "/workspace/cat_user_ctr/");
        model.addAttribute("lstTreeModel", lstTreeModal);
        isRoot = currentUser.getSysRoleDTO().getType().equalsIgnoreCase("1");
        if (isRoot) {
            buildMapIdCustomer();
            model.addAttribute("mapIdCust", mapIdCust);
        }
        model.addAttribute("isRoot", isRoot);
        return "system_managerment/cat_user";
    }

    @RequestMapping(value = "/findByCondition", method = RequestMethod.GET)
    public @ResponseBody
    List<CatUserDTO> findByCondition(@RequestParam("status") String status, @RequestParam("keyword") String keyword, @RequestParam("deptId") String deptId, @RequestParam("usageUnit") String usageUnit) {
        List<Condition> lstCon = new ArrayList<>();
        lstCon.add(new Condition("status", Constants.SQL_PRO_TYPE.BYTE, Constants.SQL_OPERATOR.EQUAL, status));
        if (!DataUtil.isStringNullOrEmpty(keyword)) {
            List<Condition> lstOrCondition = new ArrayList<>();
            lstOrCondition.add(new Condition("name", Constants.SQL_OPERATOR.LIKE, keyword));
            lstOrCondition.add(new Condition("code", Constants.SQL_OPERATOR.LIKE, keyword));
            lstCon.add(new Condition(Constants.SQL_LOGIC.OR, lstOrCondition));
        }
        if (!DataUtil.isStringNullOrEmpty(deptId) && !deptId.equalsIgnoreCase("0")) {
            {
                lstCon.add(new Condition("deptId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, deptId));
            }
        }
        if (!DataUtil.isStringNullOrEmpty(usageUnit) && !usageUnit.equalsIgnoreCase("0")) {
            lstCon.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, usageUnit));
        }
        if (!isRoot) {
            lstCon.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, selectedCustomer.getId()));

        } else {
            List<Condition> lstCondition = new ArrayList<>();
            lstCondition.add(new Condition("type", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, "2"));
            lstCondition.add(new Condition("status", Constants.SQL_PRO_TYPE.BYTE, Constants.SQL_OPERATOR.EQUAL, "1"));
            List<SysRoleDTO> lstRoles = roleServiceImpl.findByCondition(lstCondition);
            String roleId = "";
            for (SysRoleDTO role : lstRoles) {
                roleId = roleId + "," + role.getId();
            }
            lstCon.add(new Condition("roleId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.IN, roleId.replaceFirst(",", "")));
        }

        List<CatUserDTO> lstUsers = catUserServices.findByCondition(lstCon);
        if (!isRoot) {
            mapIdDept.clear();
            List<Condition> lstConDept = new ArrayList<>();
            lstConDept.add(new Condition("status", Constants.SQL_PRO_TYPE.BYTE, Constants.SQL_OPERATOR.EQUAL, Constants.STATUS.ACTIVE));
            List<CatDepartmentDTO> lstDepts = new ArrayList<>();
            if (!isRoot) {
                lstConDept.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, selectedCustomer.getId()));
            }
            lstDepts = catDeptServicesImpl.findByCondition(lstConDept);
            for (CatDepartmentDTO item : lstDepts) {
                mapIdDept.put(item.getId(), item);
            }

            for (CatUserDTO catUserDTO : lstUsers) {
                catUserDTO.setStatusName(mapAppStatus.get(catUserDTO.getStatus()));
                if (!DataUtil.isNullOrEmpty(catUserDTO.getDeptId())) {
                    catUserDTO.setDeptName(mapIdDept.get(catUserDTO.getDeptId()).getName());
                }
            }

        } else {
            for (CatUserDTO catUserDTO : lstUsers) {
                catUserDTO.setStatusName(mapAppStatus.get(catUserDTO.getStatus()));
                catUserDTO.setCustName(mapIdCust.get(catUserDTO.getCustId()).getName());
            }
        }

        return lstUsers;
    }

    @RequestMapping(value = "/getAllDepartment", method = RequestMethod.GET)
    public @ResponseBody
    List<CatDepartmentDTO> getAllDepartment(@RequestParam("status") String status) {

        List<Condition> lstCon = new ArrayList<>();
        List<CatDepartmentDTO> lstDepts = new ArrayList<>();
        lstCon.add(new Condition("status", Constants.SQL_PRO_TYPE.BYTE, Constants.SQL_OPERATOR.EQUAL, Constants.STATUS.ACTIVE));
        if (!isRoot) {
            lstCon.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, selectedCustomer.getId()));

            lstDepts = catDeptServicesImpl.findByCondition(lstCon);
        }

        return lstDepts;
    }

    @RequestMapping(value = "/getRoles", method = RequestMethod.GET)
    public @ResponseBody
    List<SysRoleDTO> getRoles(@RequestParam("custId") String custId) {

        List<Condition> lstCon = new ArrayList<>();
        lstCon.add(new Condition("status", Constants.SQL_PRO_TYPE.BYTE, Constants.SQL_OPERATOR.EQUAL, Constants.STATUS.ACTIVE));
        if (isRoot) {
            lstCon.add(new Condition("type", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, "2"));
        } else {
            lstCon.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, custId));
        }

        List<SysRoleDTO> lstSysRoles = new ArrayList<>();
        lstSysRoles = roleServiceImpl.findByCondition(lstCon);
        return lstSysRoles;
    }

    @RequestMapping(value = "/updateUserRole", method = RequestMethod.GET)
    public @ResponseBody
    String updateUserRole(@RequestParam("userId") String userId, @RequestParam("roleId") String roleId, @RequestParam("roleName") String roleName, @RequestParam("block") String block) {
        try {
            Long idL = Long.parseLong(userId);
            CatUserDTO catUserDTO = (CatUserDTO) catUserServices.findById(idL);
            if (!roleId.equalsIgnoreCase("-1")) {
                catUserDTO.setRoleId(roleId);
                catUserDTO.setRoleName(roleName);
            }
            catUserDTO.setBlock(block);
            ResponseObject response = catUserServices.update(catUserDTO);
            if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
                return ResourceBundleUtils.getkey(Constants.RESPONSE.UPDATE_SUSSESS);
            } else {
                return ResourceBundleUtils.getkey(Constants.RESPONSE.UPDATE_ERROR);
            }
        } catch (NumberFormatException e) {
            return ResourceBundleUtils.getkey(Constants.RESPONSE.UPDATE_ERROR);
        }
    }

    @RequestMapping(value = "/updateDepartment", method = RequestMethod.GET)
    public @ResponseBody
    String updateDepartment(@RequestParam("userId") String userId, @RequestParam("deptId") String deptId) {
        try {
            Long idL = Long.parseLong(userId);
            CatUserDTO catUserDTO = (CatUserDTO) catUserServices.findById(idL);
            catUserDTO.setDeptId(deptId);
            catUserDTO.setDeptName(mapIdDept.get(deptId).getName());
            ResponseObject response = catUserServices.update(catUserDTO);
            if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {

                return ResourceBundleUtils.getkey(Constants.RESPONSE.UPDATE_SUSSESS);
            } else {
                return ResourceBundleUtils.getkey(Constants.RESPONSE.UPDATE_ERROR);
            }
        } catch (NumberFormatException e) {
            return ResourceBundleUtils.getkey(Constants.RESPONSE.UPDATE_ERROR);
        }
    }

    @RequestMapping(value = "/updateUserStock", method = RequestMethod.GET)
    public @ResponseBody
    String updateUserStock(@RequestParam("userId") String userId, @RequestParam("stockId") String stockId, @RequestParam("stockPermission") String stockPermission) {
        try {
            boolean isError = false;
            Long idL = Long.parseLong(userId);
            CatUserDTO catUserDTO = (CatUserDTO) catUserServices.findById(idL);
            catUserDTO.setStockPermission(stockPermission);
            ResponseObject response = catUserServices.update(catUserDTO);

            stockId = stockId.replaceFirst(",", "");
            List<Condition> lstCon = new ArrayList<>();

            //        delete all stock of this user
            lstCon.add(new Condition("userId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, userId));
            String result = mapUserStockServiceImpl.deleteByCondition(lstCon);
            if (Responses.SUCCESS.getName().equalsIgnoreCase(result)) {
                if (!DataUtil.isNullOrEmpty(stockId)) {
                    List<MapUserStockDTO> lstMapUserStock = new ArrayList<>();
                    String[] stockids = stockId.split(",");
                    for (int i = 0; i < stockids.length; i++) {
                        lstMapUserStock.add(new MapUserStockDTO(null, userId, stockids[i]));
                    }
                    response = mapUserStockServiceImpl.addList(lstMapUserStock);
                    if (!Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
                        isError = true;
                    }
                }

            } else {
                isError = true;
            }
            if (!isError) {
                return ResourceBundleUtils.getkey(Constants.RESPONSE.UPDATE_SUSSESS);
            } else {
                return ResourceBundleUtils.getkey(Constants.RESPONSE.UPDATE_ERROR);
            }

        } catch (NumberFormatException e) {
            return ResourceBundleUtils.getkey(Constants.RESPONSE.UPDATE_ERROR);
        }
    }

    @RequestMapping(value = "/updateUserPartner", method = RequestMethod.GET)
    public @ResponseBody
    String updateUserPartner(@RequestParam("userId") String userId, @RequestParam("partnerId") String partnerId, @RequestParam("partnerPermission") String partnerPermission) {
        try {
            boolean isError = false;
            Long idL = Long.parseLong(userId);
            CatUserDTO catUserDTO = (CatUserDTO) catUserServices.findById(idL);
            catUserDTO.setPartnerPermission(partnerPermission);
            ResponseObject response = catUserServices.update(catUserDTO);

            partnerId = partnerId.replaceFirst(",", "");
            List<Condition> lstCon = new ArrayList<>();
            if (!Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
                isError = true;
            }

            //        delete all stock of this user
            lstCon.add(new Condition("userId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, userId));
            String result = mapUserPartnerServiceImpl.deleteByCondition(lstCon);
            if (Responses.SUCCESS.getName().equalsIgnoreCase(result)) {
                if (!DataUtil.isNullOrEmpty(partnerId)) {
                    List<MapUserPartnerDTO> lstMapPartnerStock = new ArrayList<>();
                    String[] partnerids = partnerId.split(",");
                    for (int i = 0; i < partnerids.length; i++) {
                        lstMapPartnerStock.add(new MapUserPartnerDTO(null, userId, partnerids[i]));
                    }
                    response = mapUserPartnerServiceImpl.addList(lstMapPartnerStock);
                    if (!Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
                        isError = true;
                    }
                }

            } else {
                isError = true;
            }
            if (!isError) {
                return ResourceBundleUtils.getkey(Constants.RESPONSE.UPDATE_SUSSESS);
            } else {
                return ResourceBundleUtils.getkey(Constants.RESPONSE.UPDATE_ERROR);
            }

        } catch (NumberFormatException e) {
            return ResourceBundleUtils.getkey(Constants.RESPONSE.UPDATE_ERROR);
        }
    }

    @RequestMapping(value = "/getListStock", method = RequestMethod.GET)
    public @ResponseBody
    UserStock getListStock(@RequestParam("custId") String custId, @RequestParam("userId") String userId) {
        List<Condition> lstCon = new ArrayList<>();
        lstCon.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, custId));
        lstCon.add(new Condition("status", Constants.SQL_PRO_TYPE.BYTE, Constants.SQL_OPERATOR.EQUAL, Constants.STATUS.ACTIVE));
        List<CatStockDTO> lstStock = catStockService.findByCondition(lstCon);
//        get current stocks of this user
        List<Condition> lstConUserStock = new ArrayList<>();
        lstConUserStock.add(new Condition("userId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, userId));
        List<MapUserStockDTO> lstMapUserStock = mapUserStockServiceImpl.findByCondition(lstConUserStock);
        String[] stocksId = new String[lstMapUserStock.size()];
        for (int i = 0; i < lstMapUserStock.size(); i++) {
            stocksId[i] = lstMapUserStock.get(i).getStockId();
        }
        return new UserStock(lstStock, stocksId);
    }

    @RequestMapping(value = "/getListPartner", method = RequestMethod.GET)
    public @ResponseBody
    UserPartner getListPartner(@RequestParam("custId") String custId, @RequestParam("userId") String userId) {
        List<Condition> lstCon = new ArrayList<>();
        lstCon.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, custId));
        lstCon.add(new Condition("status", Constants.SQL_PRO_TYPE.BYTE, Constants.SQL_OPERATOR.EQUAL, Constants.STATUS.ACTIVE));
        List<CatPartnerDTO> lstPartner = catPartnerService.findByCondition(lstCon);
//        get current stocks of this user
        List<Condition> lstConUserPartner = new ArrayList<>();
        lstConUserPartner.add(new Condition("userId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, userId));
        List<MapUserPartnerDTO> lstMapUserPartner = mapUserPartnerServiceImpl.findByCondition(lstConUserPartner);
        String[] partnersId = new String[lstMapUserPartner.size()];
        for (int i = 0; i < lstMapUserPartner.size(); i++) {
            partnersId[i] = lstMapUserPartner.get(i).getPartnerId();
        }
        return new UserPartner(lstPartner, partnersId);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public @ResponseBody
    String delete(@RequestParam("id") String id, HttpServletRequest request) {
        try {
            Long idL = Long.parseLong(id);
            CatUserDTO catUserDTO = (CatUserDTO) catUserServices.findById(idL);
            catUserDTO.setStatus(Constants.STATUS.IN_ACTIVE);
            ResponseObject response = catUserServices.update(catUserDTO);
            if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
                return ResourceBundleUtils.getkey(Constants.RESPONSE.DELETE_SUSSESS);
            } else {
                return ResourceBundleUtils.getkey(Constants.RESPONSE.DELETE_ERROR);
            }
        } catch (NumberFormatException e) {
            return ResourceBundleUtils.getkey(Constants.RESPONSE.DELETE_ERROR);
        }
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public @ResponseBody
    String add(CatUserDTO catUserDTO, HttpServletRequest request) {
        catUserDTO.setStatus("1");
        catUserDTO.setBlock("0");
        catUserDTO.setPartnerPermission("0");
        catUserDTO.setStockPermission("0");
        if (!isRoot) {
            catUserDTO.setCustId(selectedCustomer.getId());
        } else {
            catUserDTO.setRoleId(WMSConfigManagerment.DEFAUL_ROLEID);
            catUserDTO.setRoleName(WMSConfigManagerment.DEFAUL_ROLENAME);
            catUserDTO.setCode(catUserDTO.getCode().trim().toLowerCase());
        }

        ResponseObject response = catUserServices.add(catUserDTO);
        try {
            Long idL = Long.parseLong(response.getKey());
            return ResourceBundleUtils.getkey(Constants.RESPONSE.INSERT_SUSSESS);
        } catch (NumberFormatException e) {
            return ResourceBundleUtils.getkey(DataUtil.isNullOrEmpty(response.getKey()) ? Constants.RESPONSE.INSERT_ERROR : response.getKey());
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody
    String update(CatUserDTO catUser, HttpServletRequest request) {
        log.info("Update menu info: " + catUser.toString());
        String userId = catUser.getId();
        Long id = Long.parseLong(userId);
        CatUserDTO catUserDTO = (CatUserDTO) catUserServices.findById(id);
        if (catUserDTO == null) {
            return ResourceBundleUtils.getkey(Constants.RESPONSE.UPDATE_ERROR);
        }

        if ("on".equalsIgnoreCase(catUser.getStatus())) {
            catUserDTO.setStatus("1");
        } else {
            catUserDTO.setStatus("0");
        }
        if (isRoot) {
            catUserDTO.setCustId(catUser.getCustId());
        }
        catUserDTO.setCode(catUser.getCode());
        catUserDTO.setName(catUser.getName());
        catUserDTO.setEmail(catUser.getEmail());
        catUserDTO.setTelNumber(catUser.getTelNumber());
        ResponseObject response = catUserServices.update(catUserDTO);
        if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
            log.info("SUCCESS");
            return ResourceBundleUtils.getkey(Constants.RESPONSE.UPDATE_SUSSESS);
        } else if (Responses.ERROR_CONSTRAINT.getName().equalsIgnoreCase(response.getStatusName())) {
            log.info("ERROR");
            return ResourceBundleUtils.getkey(Constants.RESPONSE.UPDATE_ERROR);
        } else {
            log.info("ERROR");
            return ResourceBundleUtils.getkey(Constants.RESPONSE.UPDATE_ERROR);
        }

    }


    @RequestMapping(value = "/resetPassWord", method = RequestMethod.POST)
    public @ResponseBody
    String register(CatUserDTO registerCatUserDTO) {
        log.info("Register user info: " + registerCatUserDTO.toString());
        String userId = registerCatUserDTO.getId();
        Long id = Long.parseLong(userId);
        CatUserDTO catUserDTO = (CatUserDTO) catUserServices.findById(id);
        if (catUserDTO == null) {
            return ResourceBundleUtils.getkey(Constants.RESPONSE.UPDATE_ERROR);
        }
        catUserDTO.setPassword(DataUtil.BCryptPasswordEncoder(registerCatUserDTO.getPassword()));
        ResponseObject responseObject = catUserServices.update(catUserDTO);
        if (responseObject == null || !Responses.SUCCESS.getName().equalsIgnoreCase(responseObject.getStatusCode())) {
            return ResourceBundleUtils.getkey(Constants.RESPONSE.UPDATE_ERROR);
        }
        return ResourceBundleUtils.getkey(Constants.RESPONSE.UPDATE_SUSSESS);

    }

    //------------------------------------------------------------------------------------------------------------------
    private void initTreeDepartment() {
        lstTreeModal = new ArrayList<>();
        if (isRoot) {
            return;
        }
        List<Condition> lstCon = new ArrayList<>();
        lstCon.add(new Condition("status", Constants.SQL_PRO_TYPE.BYTE, Constants.SQL_OPERATOR.EQUAL, Constants.STATUS.ACTIVE));
        lstCon.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, selectedCustomer.getId()));
        List<CatDepartmentDTO> lstDepts;
        lstDepts = catDeptServicesImpl.findByCondition(lstCon);

        mapIdDept.clear();
        for (CatDepartmentDTO item : lstDepts) {
            mapIdDept.put(item.getId(), item);
        }
        lstParentDeptId.clear();
        for (CatDepartmentDTO item : lstDepts) {
            String path = buildPath(item, mapIdDept);
            if (!DataUtil.isNullOrEmpty(path)) {
                TreeModel treeModel;
                treeModel = new TreeModel(item.getId(), path.replaceFirst("/", ""), item.getName());
                lstTreeModal.add(treeModel);
            }
        }
        List<TreeModel> lstRemoveTree = new ArrayList<>();
        for (TreeModel item : lstTreeModal) {
            if (lstParentDeptId.contains(item.getId())) {
                lstRemoveTree.add(item);
            }
        }
        lstTreeModal.removeAll(lstRemoveTree);
    }

    public String buildPath(CatDepartmentDTO item, Map<String, CatDepartmentDTO> mapIdDept) {
        String path = "";
        CatDepartmentDTO parrentItem = mapIdDept.get(item.getParentId());
        if (parrentItem != null) {
            path = buildPath(parrentItem, mapIdDept) + "/" + parrentItem.getName();
            lstParentDeptId.add(parrentItem.getId());
        }
        return path;
    }

    public void buildMapIdCustomer() {
        List<Condition> lstCon = new ArrayList<>();
        lstCon.add(new Condition("status", Constants.SQL_PRO_TYPE.BYTE, Constants.SQL_OPERATOR.EQUAL, Constants.STATUS.ACTIVE));
        List<CatCustomerDTO> lstCustomer = catCustServicesImpl.findByCondition(lstCon);
        mapIdCust.clear();
        mapIdCust.put("0", new CatCustomerDTO("0", ResourceBundleUtils.getkey("label.choose")));
        for (CatCustomerDTO catCustomerDTO : lstCustomer) {
            mapIdCust.put(catCustomerDTO.getId(), catCustomerDTO);
        }
    }
}
