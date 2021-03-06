package com.wms.controller.system_managerment;

import com.google.common.collect.Lists;
import com.wms.base.BaseCommonController;
import com.wms.constants.Constants;
import com.wms.constants.Responses;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.MenuService;
import com.wms.services.interfaces.SysRoleMenuService;
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
@RequestMapping("/workspace/cat_role_ctr")
@Scope("session")
public class CatRoleController extends BaseCommonController {
    @Autowired
    SysRoleMenuService sysRoleMenuServiceImpl;
    @Autowired
    BaseService roleServiceImpl;
    @Autowired
    MenuService menuService;
    @Autowired
    BaseService customerService;
    Map<String, SysMenuDTO> mapIdSysMenu = new HashMap<>();
    Map<String, String> mapCustIdName = new HashMap<>();
    Map<String, String> mapRoleIdMenu = new HashMap<>();
    List<TreeModel> lstTreeModel = new ArrayList<>();
    List<CatCustomerDTO> lstCustomer = new ArrayList<>();
    boolean isRoot = false;
    @Autowired
    private HttpServletRequest requestCtx;
    private Logger log = LoggerFactory.getLogger(CatRoleController.class);

    //------------------------------------------------------------------------------------------------------------------
    @PostConstruct
    public void init() {
        this.currentUser = (CatUserDTO) requestCtx.getSession().getAttribute("user");
        initTreeModel();
        initCustomers();
    }

    //------------------------------------------------------------------------------------------------------------------
    private void initCustomers() {
        if (!currentUser.getSysRoleDTO().getType().equalsIgnoreCase("1")) {
            return;
        }
        List<Condition> lstCon = new ArrayList<>();
        lstCon.add(new Condition("status", Constants.SQL_PRO_TYPE.BYTE, Constants.SQL_OPERATOR.EQUAL, Constants.STATUS.ACTIVE));
        lstCustomer = customerService.findByCondition(lstCon);
        for (CatCustomerDTO catCustomerDTO : lstCustomer) {
            mapCustIdName.put(catCustomerDTO.getId(), catCustomerDTO.getName());
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    @RequestMapping()
    public String home(Model model) {
        isRoot = currentUser.getSysRoleDTO().getType().equalsIgnoreCase("1");
        model.addAttribute("menuName", "menu.catrole");
        model.addAttribute("isRoot", isRoot);
        model.addAttribute("lstTreeModel", lstTreeModel);
        model.addAttribute("lstCustomer", lstCustomer);

        return "system_managerment/cat_role";
    }


    @RequestMapping(value = "/findByCondition", method = RequestMethod.GET)
    public @ResponseBody
    List<SysRoleDTO> findByCondition(@RequestParam("status") String status, @RequestParam("keyword") String keyword) {
        List<Condition> lstCon = new ArrayList<>();
        if (isRoot) {
//            user has root role can only seach admin roles
            lstCon.add(new Condition("type", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, "2"));
        } else {
//            admin and other roles only seach roles belong its customer except admin role.
            lstCon.add(new Condition("type", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, "3"));
            lstCon.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, selectedCustomer.getId()));
        }
        if (!DataUtil.isStringNullOrEmpty(status) && !status.equals(Constants.STATS_ALL)) {
            lstCon.add(new Condition("status", Constants.SQL_PRO_TYPE.BYTE, Constants.SQL_OPERATOR.EQUAL, status));
        }
        if (!DataUtil.isStringNullOrEmpty(keyword) && !status.equals(Constants.STATS_ALL)) {
            lstCon.add(new Condition("name", Constants.SQL_OPERATOR.LIKE, keyword));
            lstCon.add(new Condition("code", Constants.SQL_OPERATOR.LIKE, keyword));
        }


        List<SysRoleDTO> lstRole = roleServiceImpl.findByCondition(lstCon);
        StringBuilder roles = new StringBuilder();
        for (SysRoleDTO sysRoleDTO : lstRole) {
            sysRoleDTO.setStatusName(mapAppStatus.get(sysRoleDTO.getStatus()));
            roles.append(",").append(sysRoleDTO.getId());
        }
        List<Condition> lstRoleCondition = new ArrayList<>();
        lstRoleCondition.add(new Condition("roleId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.IN, roles.toString().replaceFirst(",", "")));
        lstRoleCondition.add(new Condition("roleId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.ORDER, "desc"));
        List<SysRoleMenuDTO> lstRoleMenu = sysRoleMenuServiceImpl.findByCondition(lstRoleCondition);

//        Collections.sort(lstRoleMenu, new sortList());
//        map role list menu
        String previousRoleId = "";
        String menuList = "";
        mapRoleIdMenu.clear();
        for (SysRoleMenuDTO roleMenu : lstRoleMenu) {
            if (!previousRoleId.equalsIgnoreCase(roleMenu.getRoleId()) && !DataUtil.isNullOrEmpty(previousRoleId)) {

                mapRoleIdMenu.put(previousRoleId, menuList.replaceFirst(",", ""));
                menuList = "";
            }
            previousRoleId = roleMenu.getRoleId();
            menuList = menuList + "," + roleMenu.getMenuId();


        }
        mapRoleIdMenu.put(previousRoleId, menuList.replaceFirst(",", ""));
        for (SysRoleDTO sysRoleDTO : lstRole) {
            sysRoleDTO.setMenuIds(mapRoleIdMenu.get(sysRoleDTO.getId()));
            sysRoleDTO.setCustName(mapCustIdName.get(sysRoleDTO.getCustId()));
        }
        return lstRole;
    }

    @RequestMapping(value = "/getMapRoleMenu", method = RequestMethod.GET)
    public @ResponseBody
    List<SysRoleDTO> getMapRoleMenu(@RequestParam("code") String roleCode) {
        List<Condition> lstCon = Lists.newArrayList();
//        if(!DataUtil.isStringNullOrEmpty(status) && !status.equals(Constants.STATS_ALL)){
//            lstCon.add(new Condition("roleCode", Constants.SQL_OPERATOR.EQUAL,roleCode));
//        }

        List<SysRoleDTO> lstRole = roleServiceImpl.findByCondition(lstCon);
        for (SysRoleDTO sysRoleDTO : lstRole) {
            sysRoleDTO.setStatusName(mapAppStatus.get(sysRoleDTO.getStatus()));
        }
        return lstRole;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public @ResponseBody
    String add(SysRoleDTO sysRoleDTO, HttpServletRequest request) {
        sysRoleDTO.setStatus("1");
        if (!isRoot) {
            sysRoleDTO.setCustId(selectedCustomer.getId());
        }
        int type = Integer.parseInt(currentUser.getSysRoleDTO().getType()) + 1;
        sysRoleDTO.setType(String.valueOf(type));
        ResponseObject response = roleServiceImpl.add(sysRoleDTO);
        if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {

            return "1|Thêm mới thành công";
        } else if (Responses.ERROR_CONSTRAINT.getName().equalsIgnoreCase(response.getStatusName())) {
            return "0|Thông tin đã có trên hệ thống";
        } else {
            return "0|Thêm mới không thành công";
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody
    String update(SysRoleDTO sysRoleDTO, HttpServletRequest request) {
        log.info("Update menu info: " + sysRoleDTO.toString());

        if ("on".equalsIgnoreCase(sysRoleDTO.getStatus())) {
            sysRoleDTO.setStatus("1");
        } else {
            sysRoleDTO.setStatus("0");
        }
        if (!isRoot) {

            sysRoleDTO.setCustId(currentUser.getCustId());
        }
        ResponseObject response = roleServiceImpl.update(sysRoleDTO);
        if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
            log.info("SUCCESS");
            return "1|Cập nhật thành công";
        } else if (Responses.ERROR_CONSTRAINT.getName().equalsIgnoreCase(response.getStatusName())) {
            log.info("ERROR");
            return "0|Thông tin đã có trên hệ thống";
        } else {
            log.info("ERROR");
            return "0|Cập nhật không thành công";
        }

    }

    @RequestMapping(value = "/assignRoleMenu", method = RequestMethod.POST)
    public @ResponseBody
    String assignRoleMenu(SysRoleDTO sysRoleDTO, HttpServletRequest request) {
        boolean isError = false;
        log.info("assignRoleMenu: " + sysRoleDTO.toString());
//        delete menu id
        String deleteMenus = findMenuDeleted(sysRoleDTO.getId(), sysRoleDTO.getMenuIds());
        if (!DataUtil.isNullOrEmpty(deleteMenus)) {
            List<Condition> lstDeleteCondition = Lists.newArrayList();
            lstDeleteCondition.add(new Condition("roleId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, sysRoleDTO.getId()));
            lstDeleteCondition.add(new Condition("menuId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.IN, deleteMenus));
            String result = sysRoleMenuServiceImpl.deleteByCondition(lstDeleteCondition);
            if (Responses.ERROR.getName().equalsIgnoreCase(result)) {
                log.info("ERROR");
                return "0|Gán quyền không thành công";
            }
        }
//        insert menu id
        String insertMenus = findMenuInsert(sysRoleDTO.getId(), sysRoleDTO.getMenuIds());
        if (!DataUtil.isNullOrEmpty(insertMenus)) {
            String[] newIds = insertMenus.split(",");
            List<SysRoleMenuDTO> lstRoleMenu = new ArrayList<>();
            for (int i = 0; i < newIds.length; i++) {
                SysRoleMenuDTO sysRoleMenuDTO = new SysRoleMenuDTO(null, newIds[i], sysRoleDTO.getId());
                lstRoleMenu.add(sysRoleMenuDTO);
            }
            ResponseObject response = sysRoleMenuServiceImpl.addList(lstRoleMenu);
            if (Responses.ERROR.getName().equalsIgnoreCase(response.getStatusCode())) {
                log.info("ERROR");
                return "0|Gán quyền không thành công";
            }
        }
//        if user is root role then delete all menus whichs belong to subrole(type = 3)
        if (isRoot && !DataUtil.isNullOrEmpty(deleteMenus)) {
//            search all submenu of this customer
            List<Condition> lstCon = Lists.newArrayList();
            lstCon.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, sysRoleDTO.getCustId()));
            lstCon.add(new Condition("type", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, "3"));
            List<SysRoleDTO> lstSubmenu = roleServiceImpl.findByCondition(lstCon);
            String roleIds = "";
            for (SysRoleDTO item : lstSubmenu) {
                roleIds = roleIds + "," + item.getId();
            }
            if (!DataUtil.isNullOrEmpty(roleIds)) {
                List<Condition> lstDelCon = Lists.newArrayList();
                lstDelCon.add(new Condition("roleId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.IN, roleIds.replaceFirst(",", "")));
                lstDelCon.add(new Condition("menuId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.IN, getRealDeleteMenus(insertMenus, deleteMenus)));
                String result = sysRoleMenuServiceImpl.deleteByCondition(lstDelCon);
                if (Responses.ERROR.getName().equalsIgnoreCase(result)) {
                    log.info("ERROR");
                    return "0|Gán quyền không thành công";
                }
            }
        }
        log.info("SUCCESS");
        return "1|Gán quyền thành công";
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public @ResponseBody
    String delete(@RequestParam("id") String id, HttpServletRequest request) {
        try {
            Long idL = Long.parseLong(id);
            SysRoleDTO deleteObject = (SysRoleDTO) roleServiceImpl.findById(idL);
            deleteObject.setStatus(Constants.STATUS.IN_ACTIVE);
            ResponseObject response = roleServiceImpl.update(deleteObject);
            if (isRoot) {

            }
            if (Responses.SUCCESS.getName().equalsIgnoreCase(response.getStatusCode())) {
                return "1|Xoá thành công";
            } else {
                return "0|Xoá không thành công";
            }
        } catch (NumberFormatException e) {
            return "0|Xoá không thành công lỗi convert long";
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    private void initTreeModel() {
        List<SysMenuDTO> lstMenu;
        if (currentUser.getSysRoleDTO().getType().equalsIgnoreCase("1")) {
//            get full menu;
            List<Condition> lstCon = Lists.newArrayList();
            lstCon.add(new Condition("status", Constants.SQL_PRO_TYPE.BYTE, Constants.SQL_OPERATOR.EQUAL, Constants.STATUS.ACTIVE));
            lstCon.add(new Condition("levels", Constants.SQL_OPERATOR.ORDER, "desc"));

            lstMenu = menuService.findByCondition(lstCon);
        } else {
//            only get roles whichs be assigned to customer'admin
            List<Condition> lstCon = Lists.newArrayList();
            lstCon.add(new Condition("roleId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, currentUser.getSysRoleDTO().getId()));
            List<SysRoleMenuDTO> lstRoleMenu = sysRoleMenuServiceImpl.findByCondition(lstCon);
            StringBuilder strMenus = new StringBuilder();
            for (SysRoleMenuDTO sysRoleMenuDTO : lstRoleMenu) {
                strMenus.append(",").append(sysRoleMenuDTO.getMenuId());
            }
            List<Condition> lstMenuCondition = new ArrayList<>();
            lstMenuCondition.add(new Condition("id", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.IN, strMenus.toString().replaceFirst(",", "")));
            lstMenu = menuService.findByCondition(lstMenuCondition);
        }

        for (SysMenuDTO menuitem : lstMenu) {
            menuitem.setLocalizationName(ResourceBundleUtils.getkey(menuitem.getName()) == null ? menuitem.getName() : ResourceBundleUtils.getkey(menuitem.getName()));
            mapIdSysMenu.put(menuitem.getId(), menuitem);
        }
        for (SysMenuDTO menuitem : lstMenu) {
            String path = buildPath(menuitem);
            if (!DataUtil.isNullOrEmpty(path)) {
                TreeModel treeModel = new TreeModel(menuitem.getId(), path.replaceFirst("/", ""), menuitem.getLocalizationName());
                lstTreeModel.add(treeModel);
            }
        }
    }

    public String buildPath(SysMenuDTO item) {
        String path = "";
        SysMenuDTO parrentItem = mapIdSysMenu.get(item.getParentId());
        if (parrentItem != null) {
            path = buildPath(parrentItem) + "/" + parrentItem.getLocalizationName();
        }
        return path;
    }

    public String findMenuInsert(String roleId, String newMenuIds) {
        String insertIds = "";
        if (DataUtil.isNullOrEmpty(newMenuIds)) {
            return insertIds;
        }

        String oldMenuIds = mapRoleIdMenu.get(roleId) == null ? "" : mapRoleIdMenu.get(roleId);
        if (oldMenuIds != null) {
            String[] idNew = newMenuIds.split(",");
            for (int i = 0; i < idNew.length; i++) {
                if (!oldMenuIds.contains(idNew[i])) {
                    insertIds = insertIds + "," + idNew[i];
                    //                find parent menu and add it
                    SysMenuDTO sysMenuDTO = mapIdSysMenu.get(idNew[i]);
                    SysMenuDTO parentMenu = mapIdSysMenu.get(sysMenuDTO.getParentId());
                    if (parentMenu != null && !insertIds.contains(parentMenu.getId())) {
                        insertIds = insertIds + "," + parentMenu.getId();
                    }

                }
            }
        }
        String parentMenu = findParentMenusNotchange(roleId, newMenuIds);
        insertIds = insertIds + "," + parentMenu;
        return insertIds.replaceFirst(",", "");
    }

    public String findMenuDeleted(String roleId, String newMenuIds) {
        String deleteIds = "";
        String oldMenuIds = mapRoleIdMenu.get(roleId);
        if (DataUtil.isNullOrEmpty(newMenuIds) || DataUtil.isNullOrEmpty(oldMenuIds)) {
            return oldMenuIds;
        }
        String[] idOld = oldMenuIds.split(",");
        for (int i = 0; i < idOld.length; i++) {
            if (!newMenuIds.contains(idOld[i])) {
                deleteIds = deleteIds + "," + idOld[i];
            }
        }
        return deleteIds.replaceFirst(",", "");
    }

    public String findParentMenusNotchange(String roleId, String newMenuIds) {
        String menusNotChange = "";
        String oldMenuIds = mapRoleIdMenu.get(roleId);
        if (DataUtil.isNullOrEmpty(newMenuIds) || DataUtil.isNullOrEmpty(oldMenuIds)) {
            return menusNotChange;
        }
        String[] idNew = newMenuIds.split(",");
        for (int i = 0; i < idNew.length; i++) {
            if (oldMenuIds.contains(idNew[i])) {
                //                find parent menu and add it
                SysMenuDTO sysMenuDTO = mapIdSysMenu.get(idNew[i]);
                SysMenuDTO parentMenu = mapIdSysMenu.get(sysMenuDTO.getParentId());
                if (parentMenu != null && !menusNotChange.contains(parentMenu.getId())) {
                    menusNotChange = menusNotChange + "," + parentMenu.getId();
                }
            }

        }
        return menusNotChange.replaceFirst(",", "");

    }

    public String getRealDeleteMenus(String insertMenus, String deleteMenus) {
        String menu = "";
        if (DataUtil.isNullOrEmpty(deleteMenus)) {
            return "";
        }
        String[] idDel = deleteMenus.split(",");
        for (int i = 0; i < idDel.length; i++) {
            if (!insertMenus.contains(idDel[i])) {
                menu = menu + "," + idDel[i];
            }
        }
        return menu.replaceFirst(",", "");
    }

}
