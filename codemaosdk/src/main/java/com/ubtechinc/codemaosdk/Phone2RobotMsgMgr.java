package com.ubtechinc.codemaosdk;


import com.clj.fastble.IConnectionControl;
import com.google.protobuf.GeneratedMessageLite;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.codemaosdk.business.ReceiveMessageBussinesss;
import com.ubtechinc.codemaosdk.business.SendMessageBusiness;
import com.ubtechinc.codemaosdk.communite.ImPhoneDispatcher;
import com.ubtechinc.protocollibrary.communite.ICallback;
import com.ubtechinc.protocollibrary.communite.RobotPhoneCommuniteProxy;


/**
* @Description 通信类
* @Author tanghongyu
* @Time  2018/3/15 9:38
*/
public class Phone2RobotMsgMgr<T> {
    private static Phone2RobotMsgMgr sInstance;
    private static String TAG = "Robot2PhoneMsgMgr";
    private RobotPhoneCommuniteProxy mConnection;
    public static Phone2RobotMsgMgr get() {
        if (sInstance == null) {
            synchronized (Phone2RobotMsgMgr.class) {
                if (sInstance == null) {
                    sInstance = new Phone2RobotMsgMgr();
                }
            }
        }
        return sInstance;
    }
    private Phone2RobotMsgMgr() {
        mConnection = RobotPhoneCommuniteProxy.getInstance();
        mConnection.init( SendMessageBusiness.Companion.getInstance(), ReceiveMessageBussinesss.getInstance());
        mConnection.setIMsgDispatcher(new ImPhoneDispatcher());
    }
    /**
    * @Description 初始化通信代理类和消息分发处理类
    * @Param
    */
    public synchronized void init( IConnectionControl iConnectionControl) {
        LogUtils.i("init");
        ReceiveMessageBussinesss.getInstance().init();
    }
    /**
     * @Description 发送消息
     * @param cmdId 消息ID
     * @param data 消息内容
     * @param dataCallback 消息回调
     */
    public void sendHeartData(short cmdId, byte version,  GeneratedMessageLite  data, String peer, ICallback<T> dataCallback) {
        RobotPhoneCommuniteProxy.getInstance().sendHeartBeat2Robot(cmdId,version, data, peer, dataCallback);
    }

    /**
     * @Description 发送消息
     * @param cmdId 消息ID
     * @param data 消息内容
     * @param dataCallback 消息回调
     */
    public void sendData(short cmdId, byte version,  GeneratedMessageLite  data, String peer, ICallback<T> dataCallback) {
        RobotPhoneCommuniteProxy.getInstance().sendMessage2Robot(cmdId,version, data, peer, dataCallback);
    }

    /**
     * @Description 发送回复的消息
     * @param cmdId
     * @param version
     * @param responseSerial
     * @param data
     * @param peer
     */
    public void sendResponseData(short cmdId, byte version, int responseSerial, GeneratedMessageLite data, String peer) {
        RobotPhoneCommuniteProxy.getInstance().sendResponseMessage(cmdId, version, responseSerial, data, peer, null);
    }


}
