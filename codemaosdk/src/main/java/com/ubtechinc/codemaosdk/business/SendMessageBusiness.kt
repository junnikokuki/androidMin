package com.ubtechinc.codemaosdk.business

import android.util.Log

import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleWriteCallback
import com.clj.fastble.exception.BleException
import com.clj.fastble.server.HeartBeatStrategy
import com.ubtech.utilcode.utils.ConvertUtils
import com.ubtech.utilcode.utils.LogUtils
import com.ubtechinc.codemaosdk.Statics
import com.ubtechinc.protocollibrary.communite.ISendMsg
import com.ubtechinc.protocollibrary.communite.RobotPhoneCommuniteProxy
import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.Executors


/**
 * Created by Administrator on 2016/12/17.
 */
class SendMessageBusiness private constructor() : ISendMsg {


    val TAG = "SendMessageBusiness"
    private val dataQueque = LinkedBlockingQueue<MessageInfo>()

    override fun init() {

        BleManager.getInstance().initDataHandleEngine(MiniDataEngine())
        Thread(Runnable {
            while (true) {
                var message = dataQueque.take()
                LogUtils.d("take messge start send ")
                if(message != null) {
                    sendTextMessageByBle(message.requestSerialId, message.peer, message.data,  message.callback)
                }
            }

        }).start()
    }

    override fun sendMsg(requestSerialId: Int, peer: String, data: ByteArray, callback: RobotPhoneCommuniteProxy.Callback) {
        if (data == null) {
            return
        }
        LogUtils.d("add message")
        var message = MessageInfo()
        message.requestSerialId = requestSerialId
        message.callback = callback
        message.peer = peer
        message.data = data
        dataQueque.offer(message)

    }

    override fun sendHeartMsg(requestSerialId: Int, peer: String?, data: ByteArray?, callback: RobotPhoneCommuniteProxy.Callback?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun sendTextMessageByBle(requestSerialId: Int, peer: String, data: ByteArray, callback: RobotPhoneCommuniteProxy.Callback?) {
        if (!ConnectionManager.get().isConnected()) {
            return
        }
        BleManager.getInstance().write(ConnectionManager.get().connectedRobot.device, Statics.UUID_SERVICE, Statics.UUID_WRITE_CHARACTER, data, true, object : BleWriteCallback() {
            override fun onWriteSuccess(current: Int, total: Int, justWrite: ByteArray) {
                Log.i(TAG, "onWriteSuccess current = " + current + " total = " + total + " write = " + ConvertUtils.bytes2HexString(justWrite))
                callback?.onSendSuccess()

            }

            override fun onWriteFailure(exception: BleException) {
                Log.e(TAG, "onWriteFailure exception = " + exception.description)
                callback?.onSendError(requestSerialId, exception.code)

            }
        })
    }
    companion object {
        val instance: SendMessageBusiness by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            SendMessageBusiness()
        }
    }

}


