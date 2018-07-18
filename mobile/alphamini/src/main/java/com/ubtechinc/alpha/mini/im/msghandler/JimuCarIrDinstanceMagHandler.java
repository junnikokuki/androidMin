package com.ubtechinc.alpha.mini.im.msghandler;

import android.util.Log;

import com.ubtechinc.alpha.AlphaMessageOuterClass;
import com.ubtechinc.alpha.JimuCarGetIRDistance;
import com.ubtechinc.alpha.im.msghandler.IMsgHandler;
import com.ubtechinc.alpha.mini.event.JimuCarDriverModeEvent;
import com.ubtechinc.nets.phonerobotcommunite.ProtoBufferDispose;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by riley.zhang on 2018/7/12.
 */

public class JimuCarIrDinstanceMagHandler implements IMsgHandler {
    @Override
    public void handleMsg(int requestCmdId, int responseCmdId, AlphaMessageOuterClass.AlphaMessage request, String peer) {
        Log.i("msgHandler", "JimuCarIrDinstanceMagHandler request = "+ request);
        byte[] bytes = request.getBodyData().toByteArray();
        JimuCarGetIRDistance.JimuCarGetIRDistanceResponse response = (JimuCarGetIRDistance.JimuCarGetIRDistanceResponse) ProtoBufferDispose.unPackData(JimuCarGetIRDistance.JimuCarGetIRDistanceResponse.class, bytes);
        Log.i("msgHandler", "JimuCarIrDinstanceMagHandler response = " + response);

    }
}
