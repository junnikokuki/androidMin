package com.clj.fastble

/**
 * @Deseription
 * @Author tanghongyu
 * @Time 2018/6/8 16:10
 */
interface IConnectionControl {

    fun disconnect()
    fun sendHeartBeat(iHeartCallback: IHeartCallback)

    interface IHeartCallback {
        fun onSuccess()
        fun onFail()
    }
    interface IControlCallback {
        fun onSuccess()
        fun onFail(code : Int)
    }
}
