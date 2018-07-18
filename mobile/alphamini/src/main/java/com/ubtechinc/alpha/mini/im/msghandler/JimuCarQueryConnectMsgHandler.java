package com.ubtechinc.alpha.mini.im.msghandler;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.ubtechinc.alpha.AlphaMessageOuterClass;
import com.ubtechinc.alpha.JimuCarConnectBleCar;
import com.ubtechinc.alpha.JimuCarQueryConnectState;
import com.ubtechinc.alpha.im.msghandler.IMsgHandler;
import com.ubtechinc.alpha.mini.AlphaMiniApplication;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.event.JimuCarDisconnectRobotEvent;
import com.ubtechinc.alpha.mini.ui.car.CarActivity;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.ubtechinc.nets.phonerobotcommunite.ProtoBufferDispose;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by riley.zhang on 2018/7/3.
 */

public class JimuCarQueryConnectMsgHandler implements IMsgHandler {
    @Override
    public void handleMsg(int requestCmdId, int responseCmdId, AlphaMessageOuterClass.AlphaMessage request, String peer) {
        Log.i("msgHandler", "JimuCarQueryConnectMsgHandler request = " + request);
        byte[] bytes = request.getBodyData().toByteArray();
        JimuCarQueryConnectState.JimuCarQueryConnectStateResponse response = (JimuCarQueryConnectState.JimuCarQueryConnectStateResponse) ProtoBufferDispose.unPackData(JimuCarQueryConnectState.JimuCarQueryConnectStateResponse.class, bytes);
        if (response.getState() == JimuCarConnectBleCar.BleCarConnectState.DISCONNECT) {
            EventBus.getDefault().post(new JimuCarDisconnectRobotEvent());
        }
    }
}
