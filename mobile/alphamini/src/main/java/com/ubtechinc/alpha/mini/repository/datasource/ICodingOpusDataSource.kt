package com.ubtechinc.alpha.mini.repository.datasource

import com.ubtechinc.alpha.mini.entity.Product
import com.ubtechinc.nets.http.ThrowableWrapper

/**
 * @Deseription 作品库接口
 * @Author tanghongyu
 * @Time 2018/5/19 11:32
 */
interface ICodingOpusDataSource {

    fun getAllOpus(userId : String, getOpusListCallback: GetOpusListCallback)
    fun deleteOpus(opusId : String): Boolean
    fun saveOpus(product : Product): String
    fun modifyOpus(product : Product): Boolean
    fun getOpusDetail(opusId: String) : Product

    interface GetOpusListCallback {
        fun onLoadOpusList(productList: List<Product>)

        fun onDataNotAvailable(e: ThrowableWrapper)
    }

    interface GetOpusDetailCallback {
        fun onLoadProductContent(product: Product)

        fun onDataNotAvailable(e: ThrowableWrapper)
    }

    interface ControlOpusCallback {

        fun onSuccess()

        fun onFail(e: ThrowableWrapper)
    }

}