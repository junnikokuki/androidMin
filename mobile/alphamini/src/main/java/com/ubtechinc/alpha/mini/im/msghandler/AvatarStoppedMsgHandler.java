package com.ubtechinc.alpha.mini.im.msghandler;

import android.util.Log;

import com.orhanobut.logger.Logger;
import com.ubtechinc.alpha.AlphaMessageOuterClass;
import com.ubtechinc.alpha.AvatarExit;
import com.ubtechinc.alpha.im.msghandler.IMsgHandler;
import com.ubtechinc.alpha.mini.event.AvatarStoppedEvent;
import com.ubtechinc.nets.phonerobotcommunite.ProtoBufferDispose;

import org.greenrobot.eventbus.EventBus;


/**
 * @ClassName AvatarStoppedMsgHandler
 * @Description Avatar退出回调
 * @modifier
 * @modify_time
 */
public class AvatarStoppedMsgHandler implements IMsgHandler {

    private static final String TAG = "AvatarStoppedMsgHandler";

    @Override
    public void handleMsg(int requestCmdId, int responseCmdId, AlphaMessageOuterClass.AlphaMessage request, String peer) {
        Logger.t(TAG).d("Avatar Stopped");
        byte[] byts = request.getBodyData().toByteArray();
        AvatarExit.AvatarExitResponse response = (AvatarExit.AvatarExitResponse) ProtoBufferDispose.unPackData(AvatarExit.AvatarExitResponse.class, byts);
        EventBus.getDefault().post(new AvatarStoppedEvent(response.getReason(), response.getReasonCode()));
    }

}
