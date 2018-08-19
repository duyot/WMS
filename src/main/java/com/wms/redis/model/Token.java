package com.wms.redis.model;

public class Token {
    String clientIp;
    AuthTokenInfo tokenInfor;

    public Token(String clientIp, AuthTokenInfo tokenInfor) {
        this.clientIp = clientIp;
        this.tokenInfor = tokenInfor;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public AuthTokenInfo getTokenInfor() {
        return tokenInfor;
    }

    public void setTokenInfor(AuthTokenInfo tokenInfor) {
        this.tokenInfor = tokenInfor;
    }
}
