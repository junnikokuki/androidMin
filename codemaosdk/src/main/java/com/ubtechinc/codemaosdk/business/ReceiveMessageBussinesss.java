package com.ubtechinc.codemaosdk.business;


import android.util.Log;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.server.HeartBeatStrategy;
import com.ubtech.utilcode.utils.ConvertUtils;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.codemaosdk.Statics;
import com.ubtechinc.protocollibrary.communite.IMsgHandleEngine;
import com.ubtechinc.protocollibrary.communite.IReceiveMsg;
import com.ubtechinc.protocollibrary.communite.ImMsgDispathcer;
import com.ubtechinc.protocollibrary.communite.MsgHandleTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by Administrator on 2017/5/25.
 */

public class ReceiveMessageBussinesss implements IReceiveMsg {

    private ExecutorService receiveMsgThreadPool;

    private static ReceiveMessageBussinesss sInstance;
    private static String TAG = "ReceiveMessageBussinesss";
    public static ReceiveMessageBussinesss getInstance() {
        if (sInstance == null) {
            synchronized (ReceiveMessageBussinesss.class) {
                sInstance = new ReceiveMessageBussinesss();
            }
        }
        return sInstance;
    }
    private ReceiveMessageBussinesss() {
        receiveMsgThreadPool = Executors.newCachedThreadPool();

    }

    @Override
    public void init() {
        BleManager.getInstance().notify(
                ConnectionManager.get().getConnectedRobot().getDevice(),
                Statics.UUID_SERVICE,
                Statics.UUID_READ_CHARACTER,onRevieveMessageListenenr);

    }

    @Override
    public void setIMsgDispatcher(ImMsgDispathcer msgDispathcer){
        IMsgHandleEngine.getInstance().setIMsgDispatcher(msgDispathcer);
    }

    private BleNotifyCallback onRevieveMessageListenenr = new BleNotifyCallback() {
        @Override
        public void onNotifySuccess() {
            // 打开通知操作成功
            Log.i(TAG, "onNotifySuccess  " );
        }

        @Override
        public void onNotifyFailure(BleException exception) {
            // 打开通知操作失败
            Log.i(TAG, "onNotifyFailure exception = " + exception.getDescription());
        }

        @Override
        public void onCharacteristicChanged(byte[] data) {
            Log.i(TAG, "receive onCharacteristicChanged " + ConvertUtils.bytes2HexString(data));
            MsgHandleTask parserTask = new MsgHandleTask(data,ConnectionManager.get().getConnectedRobot().getDevice().getMac());
            receiveMsgThreadPool.execute(parserTask);


        }
    };

    /**
     * 处理接收到的IM消息
     * 从后台推送的消息是Json格式(IM给后台提供的接口不支持传byte[]),客户端与机器人互传的是byte[]格式，可转成Protobuffer
     * byte[] ---> String --->byte[]并不一定是等价的，会存在数据丢失
     * @param data
     */
    private void handleReceivedMessage(byte[] data, String peer) {
        LogUtils.d(TAG,"handleReceivedMessage");
        MsgHandleTask parserTask = new MsgHandleTask(data, peer);
        receiveMsgThreadPool.execute(parserTask);
    }
}
