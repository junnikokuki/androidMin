package com.ubt.lancommunicationhelper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.common.channel.net.INet;
import com.common.channel.net.callback.ConnectionCallback;
import com.common.channel.net.exception.NetException;
import com.common.channel.net.listener.ConnectionStateListener;
import com.google.protobuf.Any;
import com.ubtrobot.lan.SocketConnectParam;
import com.ubtrobot.lib.communation.mobile.Channel;
import com.ubtrobot.lib.communation.mobile.CommChannelManager;
import com.ubtrobot.lib.communation.mobile.connection.Connection;
import com.ubtrobot.lib.communation.mobile.connection.callback.PushMessageListener;
import com.ubtrobot.lib.communation.mobile.connection.message.Param;
import com.ubtrobot.lib.communation.mobile.connection.message.PushMessage;
import com.ubtrobot.lib.communation.mobile.connection.transport.ProtoParam;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bob.xu on 2018/1/15.
 */

public class LanCommunicationProxy {
    private static LanCommunicationProxy instance;
    private Context context;
    private Channel mChannel;
    private static final String TAG = "LanCommunicationProxy";
    private static final int LAN_PACKET_PUB_TCP_PORT = 9000;
    private List<MessageListener> msgListenerList = new ArrayList<MessageListener>();
    private Connection mLanConnection;
    private ConnectionStateListener mConnectionStateListener = new ConnectionStateListener() {
        @Override
        public void onStateChanged(INet.ConnectionState newState) {
            Log.d("lan-test-net", " new state:" + newState);

            //Toast.makeText(mContext, "state changed:" + newState, Toast.LENGTH_SHORT).show();
        }
    };

    public static LanCommunicationProxy getInstance(Context context) {
        if (instance == null) {
            synchronized (LanCommunicationProxy.class) {
                if (instance == null) {
                    instance = new LanCommunicationProxy(context);
                }
            }
        }
        return instance;
    }

    private LanCommunicationProxy(Context context) {
        if (context != null) {
            this.context = context.getApplicationContext();
        }
        init();
    }

    private void init() {
        CommChannelManager.init(context);

        mChannel = CommChannelManager.getInstance().createChannel("test");
        mLanConnection = mChannel.createSocketConnection();

        mLanConnection.registerConnectionStateListener(mConnectionStateListener);

        //mRobotSkillsProxy = mLanConnection.createRobotSkillsProxy();
//        mMaster = mCommChannelManager.createRobotMaster(mLanConnection);
//        mRobotSkillsProxy = mMaster.createRobotSkillsProxy();

        mLanConnection.addPushMessageListener(mPushMessageListener);
    }

    private PushMessageListener mPushMessageListener = new PushMessageListener() {
        @Override
        public void onReceive(PushMessage message) {
            Log.d(TAG, "Recive a push message:" + message);
            Param param = message.getData();
            try {
                com.ubtechinc.alpha.PushMessage.MessageWrapper messageWrapper = ProtoParam.from(param).unpack(com.ubtechinc.alpha.PushMessage.MessageWrapper.class);
                callListener(messageWrapper);

            } catch (ProtoParam.InvalidProtoParamException e) {
                e.printStackTrace();
            }
        }
    };

    private void callListener(com.ubtechinc.alpha.PushMessage.MessageWrapper messageWrapper) {
        if (msgListenerList != null) {
            for (MessageListener listener : msgListenerList) {
//                String msgId = messageWrapper.getMessageId();
//                Any any = messageWrapper.getBody();
                listener.onReceiveMessage(messageWrapper);
            }
        }

    }

    public void connect2Robot(String ip) {
        mLanConnection.connect(new SocketConnectParam(new InetSocketAddress(ip,
                        LAN_PACKET_PUB_TCP_PORT)),
                new ConnectionCallback() {
                    @Override
                    public void onSuccess() {
                        Log.i(TAG, "connect success");
                        Toast.makeText(context, "connect success", Toast.LENGTH_SHORT).show();
                    }


                    @Override
                    public void onFail(@NonNull NetException ex) {
                        Log.i(TAG, "connect fail", ex);
                        Toast.makeText(context, "connect fail", Toast.LENGTH_SHORT).show();
                    }

                });
    }

    public void registerMessaggeListener(MessageListener messageListener) {
        if (messageListener == null) {
            return;
        }
        if (!msgListenerList.contains(messageListener)) {
            msgListenerList.add(messageListener);
        }
    }

    public void unRegisterMessaggeListener(MessageListener messageListener) {
        if (messageListener == null) {
            return;
        }
        if (msgListenerList.contains(messageListener)) {
            msgListenerList.remove(messageListener);
        }
    }


}
