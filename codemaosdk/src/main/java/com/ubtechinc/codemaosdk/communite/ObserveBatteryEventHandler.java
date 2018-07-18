package com.ubtechinc.codemaosdk.communite;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.notification.NotificationCenter;
import com.ubtechinc.codemao.CodeMaoObserveButteryStatus;
import com.ubtechinc.codemaosdk.event.ButteryEvent;
import com.ubtechinc.protocollibrary.communite.IMsgHandler;
import com.ubtechinc.protocollibrary.communite.ProtoBufferDispose;
import com.ubtechinc.protocollibrary.protocol.MiniMessage;

/**
 * Created by Administrator on 2017/6/6.
 */

public class ObserveBatteryEventHandler implements IMsgHandler {
    private String TAG = "ObserveHeadRacketEventHandler";

    @Override
    public void handleMsg(short requestCmdId, final short responseCmdId, final MiniMessage request, final String peer) {
        CodeMaoObserveButteryStatus.ObserveButteryStatusResponse cmObserveButteryStatusResponse = ( CodeMaoObserveButteryStatus.ObserveButteryStatusResponse) ProtoBufferDispose.unPackData(
                CodeMaoObserveButteryStatus.ObserveButteryStatusResponse.class, request.getDataContent());
        LogUtils.d("ObserveBatteryEventHandler =  " + cmObserveButteryStatusResponse.getLevel() + " status = " + cmObserveButteryStatusResponse.getStatus());
        NotificationCenter.defaultCenter().publish(new ButteryEvent(cmObserveButteryStatusResponse.getLevel(), cmObserveButteryStatusResponse.getStatus()));

    }
}
