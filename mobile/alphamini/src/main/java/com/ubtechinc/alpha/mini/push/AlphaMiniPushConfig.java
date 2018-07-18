package com.ubtechinc.alpha.mini.push;

/**
 * @Date: 2017/11/17.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 推送的配置参数，包含登录token，当前用户id
 */

public class AlphaMiniPushConfig {

    public String token;
    public String userId;

    public AlphaMiniPushConfig(String token, String userId) {
        this.token = token;
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
