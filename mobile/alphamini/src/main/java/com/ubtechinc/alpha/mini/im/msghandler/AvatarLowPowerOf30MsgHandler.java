package com.ubtechinc.alpha.mini.im.msghandler;

import android.util.Log;

import com.ubtechinc.alpha.AlphaMessageOuterClass;
import com.ubtechinc.alpha.im.msghandler.IMsgHandler;
import com.ubtechinc.alpha.mini.event.AvatarRobotLowPowerTipsEvent;

import org.greenrobot.eventbus.EventBus;


/**
 * @作者：liudongyang
 * @日期: 18/5/22 18:22
 * @描述: 低电量提示
 */

public class AvatarLowPowerOf30MsgHandler implements IMsgHandler {

    private String TAG = getClass().getSimpleName();

    @Override
    public void handleMsg(int requestCmdId, int responseCmdId, AlphaMessageOuterClass.AlphaMessage request, String peer) {
        Log.i(TAG, "handleMsg: low power tips");
        EventBus.getDefault().post(new AvatarRobotLowPowerTipsEvent());
    }


}
