package com.ubtechinc.alpha.mini.ui.network;

/**
 * @desc : 网络状态切换接口
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2017/11/2
 */

public interface INetworkLoadStatus {
    void toLoading();
    void toNetworkLoadFail();
    void toShowContent();
    void toNetworkDisconnect();

    enum NetworkLoadStatas{
        NETWORK_LOAD_STATAS_LOADING,
        NETWORK_LOAD_STATAS_LOAD_FAIL,
        NETWORK_LOAD_STATAS_CONTENT,
        NETWORK_LOAD_STATAS_DISCONNECT,
    }
}
