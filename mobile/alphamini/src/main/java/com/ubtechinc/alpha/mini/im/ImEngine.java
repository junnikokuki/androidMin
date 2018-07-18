package com.ubtechinc.alpha.mini.im;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.protobuf.Any;
import com.ubtech.utilcode.utils.SPUtils;
import com.ubtechinc.alpha.AlGetAlubmInfo;
import com.ubtechinc.alpha.AlphaMessageOuterClass;
import com.ubtechinc.alpha.im.IMCmdId;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.entity.RobotInfo;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtechinc.nets.phonerobotcommunite.ICallback;

import com.ubtechinc.nets.phonerobotcommunite.ProtoBufferDispose;
import com.ubtrobot.lib.communation.mobile.connection.message.Param;
import com.ubtrobot.lib.communation.mobile.connection.transport.ProtoParam;
import com.ubtrobot.lib.sync.engine.PacketEngine;
import com.ubtrobot.lib.sync.engine.QueryPacket;
import com.ubtrobot.lib.sync.engine.ResultPacket;


/**
 * Created by Mark.Zhang on 2017/10/30.
 */

public abstract class ImEngine<REQ, RSP> extends PacketEngine<QueryPacket<REQ>, ResultPacket<RSP>> {
    private static  final String TAG ="ImEngine";

    private String mHost;
    private String mPath;

    public static final String ALBUMS_LAST_ROBOT_ID = "albums_last_robot_id";

    public ImEngine(String host, String path) {
        this.mHost = host;
        this.mPath = path;
    }


    protected void send(final Param request) {
        String peer = null;
        Log.d(TAG, "send.....");
        RobotInfo robotInfo = MyRobotsLive.getInstance().getCurrentRobot().getValue();
        if (robotInfo != null) {
            peer = robotInfo.getRobotUserId();
        }
        AlGetAlubmInfo.AlGetAlubmInfoRequest.Builder builder=AlGetAlubmInfo.AlGetAlubmInfoRequest.newBuilder();
        try {
            builder.setHost(mHost);
            builder.setPath(mPath);
            builder.setBodyData(ProtoParam.from(request).getAny());
        } catch (ProtoParam.InvalidProtoParamException e) {
            e.printStackTrace();
        }

        final String finalPeer = peer;
        Phone2RobotMsgMgr.getInstance().sendData(IMCmdId.IM_GET_ALBUM_LIST_REQUEST,
                "1",
                builder.build(),
                peer,
                new ICallback<AlphaMessageOuterClass.AlphaMessage>() {
                    @Override
                    public void onSuccess(AlphaMessageOuterClass.AlphaMessage data) {
                        Log.d(TAG, "onSuccess....."+data);
                        byte[] bodyBytes = data.getBodyData().toByteArray();
                        AlGetAlubmInfo.AlGetAlubmInfoResponse alGetAlubmInfoResponse= (AlGetAlubmInfo.AlGetAlubmInfoResponse) ProtoBufferDispose.unPackData(AlGetAlubmInfo.AlGetAlubmInfoResponse.class,bodyBytes);
                        Any any =  alGetAlubmInfoResponse.getBodyData();
                        ImEngine.this.onSuccess(request,ProtoParam.pack(any));
                        SPUtils.get().put(ALBUMS_LAST_ROBOT_ID, finalPeer);
                    }

                    @Override
                    public void onError(ThrowableWrapper e) {
                        onFail(request,-1,e.getMessage());
                        Log.d(TAG, "onError....."+e.getMessage());
                    }
                }
        );

    }

    protected abstract void onSuccess(@NonNull Param request, @NonNull Param response);
    protected abstract void onFail(@NonNull Param request, int code, String message);

}
