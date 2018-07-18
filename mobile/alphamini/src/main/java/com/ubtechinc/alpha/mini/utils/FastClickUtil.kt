package com.ubtechinc.alpha.mini.utils

/**
 * @Deseription
 * @Author tanghongyu
 * @Time 2018/6/14 12:10
 */
object FastClickUtil {

    private val MIN_DELAY_TIME = 500  // 两次点击间隔不能少于1000ms
    private var lastClickTime: Long = 0

    fun isFastClick(): Boolean {
        var flag = true
        val currentClickTime = System.currentTimeMillis()
        if (currentClickTime - lastClickTime >= MIN_DELAY_TIME) {
            flag = false
        }
        lastClickTime = currentClickTime
        return flag
    }
}