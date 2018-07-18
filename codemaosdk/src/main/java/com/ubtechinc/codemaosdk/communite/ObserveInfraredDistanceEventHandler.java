package com.ubtechinc.codemaosdk.communite;

import com.ubtech.utilcode.utils.notification.NotificationCenter;
import com.ubtechinc.codemao.CodeMaoObserveInfraredDistance;
import com.ubtechinc.codemaosdk.event.InfraredDistanceEvent;
import com.ubtechinc.codemaosdk.event.VolumeKeyEvent;
import com.ubtechinc.protocollibrary.communite.IMsgHandler;
import com.ubtechinc.protocollibrary.communite.ProtoBufferDispose;
import com.ubtechinc.protocollibrary.protocol.MiniMessage;

/**
 * Created by Administrator on 2017/6/6.
 */

public class ObserveInfraredDistanceEventHandler implements IMsgHandler {
    private String TAG = "ObserveVolumeKeyPressEventHandler";

    @Override
    public void handleMsg(short requestCmdId, final short responseCmdId, final MiniMessage request, final String peer) {
        CodeMaoObserveInfraredDistance.ObserveInfraredDistanceResponse cmObserveInfraredDistanceResponse = (   CodeMaoObserveInfraredDistance.ObserveInfraredDistanceResponse) ProtoBufferDispose.unPackData(
                CodeMaoObserveInfraredDistance.ObserveInfraredDistanceResponse.class, request.getDataContent());
        NotificationCenter.defaultCenter().publish(new InfraredDistanceEvent(cmObserveInfraredDistanceResponse.getDistance()));

    }
}
