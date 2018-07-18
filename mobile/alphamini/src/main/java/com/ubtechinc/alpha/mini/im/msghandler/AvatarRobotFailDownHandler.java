package com.ubtechinc.alpha.mini.im.msghandler;

import android.util.Log;

import com.ubtechinc.alpha.AlphaMessageOuterClass;
import com.ubtechinc.alpha.im.msghandler.IMsgHandler;
import com.ubtechinc.alpha.mini.event.AvatarRobotFailDownEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * @作者：liudongyang
 * @日期: 18/5/22 17:27
 * @描述: 机器人跌倒消息
 */

public class AvatarRobotFailDownHandler implements IMsgHandler {

    private static final String TAG = "AvatarStoppedMsgHandler";

    @Override
    public void handleMsg(int requestCmdId, int responseCmdId, AlphaMessageOuterClass.AlphaMessage request, String peer) {
        Log.d(TAG, "Robot Faildown Stopped");
        EventBus.getDefault().post(new AvatarRobotFailDownEvent());
    }

}
