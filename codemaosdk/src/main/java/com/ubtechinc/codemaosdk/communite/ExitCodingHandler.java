package com.ubtechinc.codemaosdk.communite;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.notification.NotificationCenter;
import com.ubtechinc.codemao.CodeMaoControlFindFace;
import com.ubtechinc.codemao.CodeMaoRevertOrigin;
import com.ubtechinc.codemaosdk.HandlerUtils;
import com.ubtechinc.codemaosdk.Phone2RobotMsgMgr;
import com.ubtechinc.codemaosdk.Statics;
import com.ubtechinc.codemaosdk.event.ExitCodingEvent;
import com.ubtechinc.codemaosdk.event.FindFaceEvent;
import com.ubtechinc.protocollibrary.communite.ICallback;
import com.ubtechinc.protocollibrary.communite.IMsgHandler;
import com.ubtechinc.protocollibrary.communite.ProtoBufferDispose;
import com.ubtechinc.protocollibrary.communite.ThrowableWrapper;
import com.ubtechinc.protocollibrary.protocol.CmdId;
import com.ubtechinc.protocollibrary.protocol.MiniMessage;

/**
 * Created by Administrator on 2017/6/6.
 */

public class ExitCodingHandler implements IMsgHandler {
    private String TAG = "ExitCodingHandler";

    @Override
    public void handleMsg(short requestCmdId, final short responseCmdId, final MiniMessage request, final String peer) {
        LogUtils.w("exit coding");
        Phone2RobotMsgMgr.get().sendData(requestCmdId, Statics.PROTO_VERSION, null, "", null);
        NotificationCenter.defaultCenter().publish(new ExitCodingEvent());
    }
}
