package com.ubtechinc.alpha.mini.ui.codemao

/**
 * @Description 编程猫错误码
 * @Author tanghongyu
 * @Time  2018/6/6 11:26
 */

object  CodeMaoErrorCode {

    private val ERROR_UNKNOWN_ERROR = 0//未知错误（ 提示：悟空忙碌中，请稍后再试）
    private val ERROR_CODE_LOW_POWER = 1//电量过低(悟空电量过低，无法执行编程动作)
    private val ERROR_ROBOT_AVATAR = 2//视频监控（ 提示：悟空正在视频监控，请稍后再试）
    private val ERROR_ROBOT_BUSY = 3//机器人忙碌（ 提示：其他人正使用悟空进行编程，请稍后再试）
    private val ERROR_ROBOT_PHONE_CALL = 4//通话中（ 提示：悟空正在通话中，请通话结束后再试）
    private val ERROR_ROBOT_HIGH_RISK_ACTION = 5//高危动作（ 提示：悟空忙碌中，请稍后再试）
    private val ERROR_ROBOT_FALL_CLIMB = 6//跌倒爬起（ 提示：悟空忙碌中，请稍后再试）
    private val ERROR_ROBOT_STAND_BY = 7//休眠状态（ 提示：悟空休息中，请唤醒后再试）
    fun getErrorMsg(code : Int) : String {

        return when(code) {

            ERROR_CODE_LOW_POWER -> "悟空电量过低，无法执行编程动作"
            ERROR_ROBOT_AVATAR -> "悟空正在视频监控，请稍后再试"
            ERROR_ROBOT_BUSY -> "其他人正使用悟空进行编程或配网，请稍后再试"
            ERROR_ROBOT_PHONE_CALL -> "悟空正在通话中，请通话结束后再试"
            ERROR_ROBOT_HIGH_RISK_ACTION -> "悟空忙碌中，请稍后再试"
            ERROR_ROBOT_FALL_CLIMB -> "悟空忙碌中，请稍后再试"
            ERROR_ROBOT_STAND_BY -> "悟空休息中，请唤醒后再试"
            else  -> "悟空忙碌中，请稍后再试"
        }
    }
}
