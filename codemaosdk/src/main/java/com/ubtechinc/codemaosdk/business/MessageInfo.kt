package com.ubtechinc.codemaosdk.business

import com.ubtechinc.protocollibrary.communite.RobotPhoneCommuniteProxy

/**
 * @Deseription
 * @Author tanghongyu
 * @Time 2018/6/26 11:16
 */
class MessageInfo {

    var requestSerialId : Int = 0
    lateinit var peer : String
    lateinit var data : ByteArray
    lateinit var callback : RobotPhoneCommuniteProxy.Callback
}