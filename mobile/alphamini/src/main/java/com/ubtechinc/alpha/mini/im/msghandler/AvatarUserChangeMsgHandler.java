package com.ubtechinc.alpha.mini.im.msghandler;

import com.ubtechinc.alpha.AlphaMessageOuterClass;
import com.ubtechinc.alpha.AvatarUserChange;
import com.ubtechinc.alpha.im.msghandler.IMsgHandler;
import com.ubtechinc.alpha.mini.event.AvatarUserListsChangeEvent;
import com.ubtechinc.nets.phonerobotcommunite.ProtoBufferDispose;

import org.greenrobot.eventbus.EventBus;

/**
 * @作者：liudongyang
 * @日期: 18/5/22 18:27
 * @描述:
 */

public class AvatarUserChangeMsgHandler implements IMsgHandler {

    private String TAG = getClass().getSimpleName();

    @Override
    public void handleMsg(int requestCmdId, int responseCmdId, AlphaMessageOuterClass.AlphaMessage request, String peer) {
        byte[] byts = request.getBodyData().toByteArray();
        AvatarUserChange.UserChangeResponse response = (AvatarUserChange.UserChangeResponse) ProtoBufferDispose.unPackData(AvatarUserChange.UserChangeResponse.class, byts);
        EventBus.getDefault().post(new AvatarUserListsChangeEvent(response.getAllUsersList(), response.getEnterUserIdsList(), response.getLeaveUserIdsList()));
    }
}
