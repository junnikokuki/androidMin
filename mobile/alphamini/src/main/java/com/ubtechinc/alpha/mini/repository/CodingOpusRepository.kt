package com.ubtechinc.alpha.mini.repository

import android.content.ContentValues
import com.google.common.collect.ImmutableMap
import com.google.common.collect.Lists
import com.ubtech.utilcode.utils.LogUtils
import com.ubtechinc.alpha.CmSwitchCodeMao
import com.ubtechinc.alpha.im.IMCmdId
import com.ubtechinc.alpha.mini.database.CodingOpusProvider
import com.ubtechinc.alpha.mini.entity.CodingOpus
import com.ubtechinc.alpha.mini.entity.Product
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive
import com.ubtechinc.alpha.mini.im.Phone2RobotMsgMgr
import com.ubtechinc.alpha.mini.repository.datasource.ICodingOpusDataSource
import com.ubtechinc.alpha.mini.ui.codemao.CodeMaoErrorCode
import com.ubtechinc.codemaosdk.HandlerUtils
import com.ubtechinc.nets.http.ThrowableWrapper
import com.ubtechinc.nets.phonerobotcommunite.ICallback
import java.util.*

/**
 * @Deseription 编程作品仓库
 * @Author tanghongyu
 * @Time 2018/5/19 11:36
 */
object CodingOpusRepository : ICodingOpusDataSource {


    fun switchCodingModel(robotUserId : String, isOpen: Boolean, controlOpusCallback: ICodingOpusDataSource.ControlOpusCallback?) {
        val request = CmSwitchCodeMao.SwitchModelRequest.newBuilder()
        request.isOpen = isOpen
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_SWITCH_CODE_MAO_REQUEST, IMCmdId.IM_VERSION, request.build(),
                robotUserId, object : ICallback<CmSwitchCodeMao.SwitchModelResponse> {

            override fun onSuccess(data: CmSwitchCodeMao.SwitchModelResponse) {
                HandlerUtils.runUITask({
                    LogUtils.d("switchCodingModel = " + data.isSuccess + " code = " + data.resultCode)
                    if (controlOpusCallback != null) {
                        if (data.isSuccess) {
                            controlOpusCallback.onSuccess()
                        }else{
                            controlOpusCallback!!.onFail(ThrowableWrapper(CodeMaoErrorCode.getErrorMsg(data.resultCode), data.resultCode))
                        }

                    }
                })
            }

            override fun onError(e: ThrowableWrapper) {
                HandlerUtils.runUITask({
                    if(controlOpusCallback != null) {
                        controlOpusCallback.onFail(e)
                    }

                })
            }
        })
    }

    override fun getAllOpus(userId: String, getOpusListCallback: ICodingOpusDataSource.GetOpusListCallback) {

        if (getOpusListCallback != null) {
            var opusList = CodingOpusProvider.instance!!.getAllOpusDataByUserId(userId)
            var productList = Lists.newArrayList<Product>()
            for (codingOpus in opusList) {
                var product = Product()
                product.opusContent = codingOpus.opusContent
                product.opusName = codingOpus.opusName
                product.id = codingOpus.idLocal
                productList.add(product)
            }
            getOpusListCallback.onLoadOpusList(productList)
        }


    }

    override fun getOpusDetail(opusId: String): Product {
        var product = Product()
        var codingOpus = CodingOpusProvider.instance!!.getDataByIdLocal(opusId)
        product.opusContent = codingOpus.opusContent
        product.opusName = codingOpus.opusName
        product.id = codingOpus.idLocal
        return product
    }

    override fun deleteOpus(opusId: String): Boolean {
        CodingOpusProvider.instance!!.deleteByParam(ImmutableMap.of("idLocal", opusId))
        return true
    }

    override fun saveOpus(product: Product): String {
        var codingOpus = CodingOpus()

        codingOpus.opusContent = product.opusContent
        codingOpus.opusName = product.opusName
        codingOpus.userId = product.userId
        codingOpus.idLocal = UUID.randomUUID().toString()
        CodingOpusProvider.instance!!.saveOrUpdate(codingOpus)
        return codingOpus.idLocal
    }

    override fun modifyOpus(product: Product): Boolean {
        var codingOpus = CodingOpus()
        codingOpus.opusContent = product.opusContent
        codingOpus.opusName = product.opusName
        codingOpus.idLocal = product.id
        var contentValues = ContentValues()
        contentValues.put("opusName", codingOpus.opusName)
        contentValues.put("opusContent", codingOpus.opusContent)
        CodingOpusProvider.instance!!.updateByParam(contentValues, ImmutableMap.of("idLocal", codingOpus.idLocal))
        return true
    }


}