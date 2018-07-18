package com.ubtechinc.alpha.mini.im.msghandler;

import android.util.Log;

import com.ubtechinc.alpha.AlphaMessageOuterClass;
import com.ubtechinc.alpha.JimuCarPreparedOuterClass;
import com.ubtechinc.alpha.im.msghandler.IMsgHandler;
import com.ubtechinc.alpha.mini.event.JimuCarDriverModeEvent;
import com.ubtechinc.nets.phonerobotcommunite.ProtoBufferDispose;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by riley.zhang on 2018/7/13.
 */

public class JimuCarPreparedMsgHandler implements IMsgHandler {
    @Override
    public void handleMsg(int requestCmdId, int responseCmdId, AlphaMessageOuterClass.AlphaMessage request, String peer) {
        Log.i("msgHandler", "JimuCarPreparedMsgHandler request = " + request);
        byte[] bytes = request.getBodyData().toByteArray();
        JimuCarPreparedOuterClass.JimuCarPrepared response = (JimuCarPreparedOuterClass.JimuCarPrepared)ProtoBufferDispose.unPackData(JimuCarPreparedOuterClass.JimuCarPrepared.class, bytes);
        Log.i("msgHandler", "JimuCarPreparedMsgHandler response = " + response);
        if (response.getPrepared()) {
            EventBus.getDefault().post(new JimuCarDriverModeEvent(JimuCarDriverModeEvent.ENTER_DRIVE_MODE));
        }

    }
}
