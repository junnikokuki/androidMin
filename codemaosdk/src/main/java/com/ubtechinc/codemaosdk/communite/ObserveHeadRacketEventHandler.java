package com.ubtechinc.codemaosdk.communite;

import com.ubtech.utilcode.utils.notification.NotificationCenter;
import com.ubtechinc.codemao.CodeMaoObserveHeadRacket;
import com.ubtechinc.codemaosdk.event.HeadRacketEvent;
import com.ubtechinc.protocollibrary.communite.IMsgHandler;
import com.ubtechinc.protocollibrary.communite.ProtoBufferDispose;
import com.ubtechinc.protocollibrary.protocol.MiniMessage;

/**
 * Created by Administrator on 2017/6/6.
 */

public class ObserveHeadRacketEventHandler implements IMsgHandler {
    private String TAG = "ObserveHeadRacketEventHandler";

    @Override
    public void handleMsg(short requestCmdId, final short responseCmdId, final MiniMessage request, final String peer) {
        CodeMaoObserveHeadRacket.ObserveHeadRacketResponse cmObserveHeadRacketResponse = (  CodeMaoObserveHeadRacket.ObserveHeadRacketResponse) ProtoBufferDispose.unPackData(
                CodeMaoObserveHeadRacket.ObserveHeadRacketResponse.class, request.getDataContent());
        NotificationCenter.defaultCenter().publish(new HeadRacketEvent(cmObserveHeadRacketResponse.getType()));
    }
}
