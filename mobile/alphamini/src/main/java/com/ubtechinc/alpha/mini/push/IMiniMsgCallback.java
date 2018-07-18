package com.ubtechinc.alpha.mini.push;

import com.ubtechinc.alpha.mini.entity.Message;

/**
 * @Date: 2017/11/6.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : App端接受推送消息的回调接口
 */

public interface IMiniMsgCallback {

    public void onReceiverMsg(String commandId, Message msg);
}
