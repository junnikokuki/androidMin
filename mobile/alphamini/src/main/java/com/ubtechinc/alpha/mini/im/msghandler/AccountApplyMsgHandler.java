package com.ubtechinc.alpha.mini.im.msghandler;

import android.util.Log;

import com.ubtechinc.alpha.im.msghandler.IMJsonMsgHandler;
import com.ubtechinc.alpha.mini.entity.Message;
import com.ubtechinc.alpha.mini.viewmodel.MessageViewModel;
import com.ubtechinc.nets.im.modules.IMJsonMsg;
import com.ubtechinc.nets.utils.JsonUtil;

/**
 * Created by Administrator on 2017/6/6.
 */

public class AccountApplyMsgHandler implements IMJsonMsgHandler {

    private String TAG = "AccountApplyMsgHandler";

    @Override
    public void handleMsg(int requestCmdId, int responseCmdId, IMJsonMsg jsonRequest, String peer) {
        String bodyData = jsonRequest.bodyData;
        Log.d(TAG, "handle msg" + bodyData);
        Message accountApplyMsg = JsonUtil.getObject(bodyData, Message.class);
        MessageViewModel.get().onReceiverMsg(String.valueOf(jsonRequest.header.commandId), accountApplyMsg);

    }
}
