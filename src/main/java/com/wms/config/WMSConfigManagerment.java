package com.wms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by truongbx on 12/08/2018.
 * configs using for all profiles
 */
@Component
public class WMSConfigManagerment {

    public static String CLIENT_ID;

    @Value("${client_id:#{null}}")
    public void setClientId(String client_id) {
        CLIENT_ID = client_id;
    }

    public static String CLIENT_SECRET;

    @Value("${client_secret:#{null}}")
    public void setClient_secret(String client_secret) {
        CLIENT_SECRET = client_secret;
    }

    public static String DEFAUL_ROLEID;

    @Value("${defaulRoleId:#{null}}")
    public void setDefaulRoleId(String defaulRoleId) {
        DEFAUL_ROLEID = defaulRoleId;
    }

    public static String DEFAUL_ROLENAME;

    @Value("${defaulRoleName:#{null}}")
    public void setDefaulRoleName(String defaulRoleName) {
        DEFAUL_ROLENAME = defaulRoleName;
    }


    public static String DEFAUL_ROLE_GUESTID;

    @Value("${defaulRoleGuestId:#{null}}")
    public void setDefaulRoleGuestId(String defaulRoleGuestId) {
        DEFAUL_ROLE_GUESTID = defaulRoleGuestId;
    }


    public static String DEFAUL_ROLE_GUESTNAME;

    @Value("${defaulRoleGuestName:#{null}}")
    public void setDefaulRoleGuestName(String defaulRoleGuestName) {
        DEFAUL_ROLE_GUESTNAME = defaulRoleGuestName;
    }

    public static String DEFAUL_CUSTID_FOR_GUEST;

    @Value("${defaulCustIdForGuest:#{null}}")
    public void setDefaulCustIdForGuest(String defaulCustIdForGuest) {
        DEFAUL_CUSTID_FOR_GUEST = defaulCustIdForGuest;
    }

    public static String SERVICE_ID;

    @Value("${service_id:#{null}}")
    public void setService_id(String service_id) {
        SERVICE_ID = service_id;
    }
}
