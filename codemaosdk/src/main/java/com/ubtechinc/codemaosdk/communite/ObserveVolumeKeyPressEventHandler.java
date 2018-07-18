package com.ubtechinc.codemaosdk.communite;

import com.ubtech.utilcode.utils.ConvertUtils;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.notification.NotificationCenter;
import com.ubtechinc.codemao.CodeMaoObserveVolumeKeyPress;
import com.ubtechinc.codemaosdk.event.VolumeKeyEvent;
import com.ubtechinc.protocollibrary.communite.IMsgHandler;
import com.ubtechinc.protocollibrary.communite.ProtoBufferDispose;
import com.ubtechinc.protocollibrary.protocol.MiniMessage;

/**
 * Created by Administrator on 2017/6/6.
 */

public class ObserveVolumeKeyPressEventHandler implements IMsgHandler {
    private String TAG = "ObserveVolumeKeyPressEventHandler";

    @Override
    public void handleMsg(short requestCmdId, final short responseCmdId, final MiniMessage request, final String peer) {
        CodeMaoObserveVolumeKeyPress.ObserveVolumeKeyPressResponse cmObserveHeadRacketResponse = (   CodeMaoObserveVolumeKeyPress.ObserveVolumeKeyPressResponse) ProtoBufferDispose.unPackData(
                CodeMaoObserveVolumeKeyPress.ObserveVolumeKeyPressResponse.class, request.getDataContent());
        LogUtils.d(TAG, "data = " + ConvertUtils.bytes2HexString(request.getDataContent()));
        NotificationCenter.defaultCenter().publish(new VolumeKeyEvent(cmObserveHeadRacketResponse.getType()));

    }
}
