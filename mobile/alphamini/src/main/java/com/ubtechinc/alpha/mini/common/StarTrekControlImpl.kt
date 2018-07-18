package com.ubtechinc.alpha.mini.common

import com.google.common.collect.ImmutableMap
import com.ubtech.utilcode.utils.JsonUtils
import com.ubtech.utilcode.utils.LogUtils
import com.ubtechinc.alpha.mini.constants.CodeMaoConstant
import com.ubtechinc.alpha.mini.entity.ReportData
import com.ubtechinc.alpha.mini.ui.codemao.listener.IStartTrekPresenter

/**
 * @Deseription
 * @Author tanghongyu
 * @Time 2018/7/5 17:18
 */
class StarTrekControlImpl constructor(iStartTrekPresenter : IStartTrekPresenter) : IStarTrekControl {
    private var iStartTrekPresenter = iStartTrekPresenter

    override fun startPlayVideo(url: String) {
        iStartTrekPresenter.startPlayVideo(url)

        val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_IS_SUCCESS, true))
        LogUtils.d(data)
        requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_REPORT_VIDEO_PLAY_FINISH, data)))
    }

    private fun getReportDataStr(eventType: Int, dataContent: String): String {
        val reportData = ReportData()
        reportData.dataContent = dataContent
        reportData.eventType = eventType
        val data = JsonUtils.object2Json(reportData)
        LogUtils.d("getReportDataStr = $data")
        return data
    }
    private fun requestJS(js: String) {
        iStartTrekPresenter.requestJS(js)
        LogUtils.d("requestJS = $js")
    }
}