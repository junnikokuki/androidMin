package com.ubtechinc.codemaosdk.communite;

import com.ubtech.utilcode.utils.notification.NotificationCenter;
import com.ubtechinc.codemao.CodeMaoControlFindFace;
import com.ubtechinc.codemaosdk.event.FindFaceEvent;
import com.ubtechinc.protocollibrary.communite.IMsgHandler;
import com.ubtechinc.protocollibrary.communite.ProtoBufferDispose;
import com.ubtechinc.protocollibrary.protocol.MiniMessage;

/**
 * Created by Administrator on 2017/6/6.
 */

public class FindFaceEventHandler implements IMsgHandler {
    private String TAG = "ObserveVolumeKeyPressEventHandler";

    @Override
    public void handleMsg(short requestCmdId, final short responseCmdId, final MiniMessage request, final String peer) {

        CodeMaoControlFindFace.ControlFindFaceResponse controlFindFaceResponse = (  CodeMaoControlFindFace.ControlFindFaceResponse) ProtoBufferDispose.unPackData(
                CodeMaoControlFindFace.ControlFindFaceResponse.class, request.getDataContent());
        NotificationCenter.defaultCenter().publish(new FindFaceEvent(controlFindFaceResponse.getStatus(), controlFindFaceResponse.getFaceCount(), controlFindFaceResponse.getCode()));

    }
}
