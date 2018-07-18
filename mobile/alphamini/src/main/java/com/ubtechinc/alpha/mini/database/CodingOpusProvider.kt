package com.ubtechinc.alpha.mini.database

import android.content.ContentValues
import android.util.Log

import com.ubtechinc.alpha.mini.AlphaMiniApplication
import com.ubtechinc.alpha.mini.entity.AlbumItem
import com.ubtechinc.alpha.mini.entity.CodingOpus
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive
import com.ubtechinc.alpha.mini.repository.datasource.ICodingOpusDataSource
import com.ubtechinc.framework.db.EntityManager
import com.ubtechinc.framework.db.EntityManagerFactory
import com.ubtechinc.framework.db.sqlite.Selector
import com.ubtechinc.framework.db.sqlite.WhereBuilder

/**
 * @Description 编程作品数据库
 * @Author tanghongyu
 * @Time  2018/5/19 11:24
 */
class CodingOpusProvider private constructor() : BaseProvider<CodingOpus>() {

    private var mEntityManager: EntityManager<CodingOpus>? = null

    private val TAG = "CodingOpusProvider"

    override fun entityManagerFactory(): EntityManager<CodingOpus>? {
        mEntityManager = EntityManagerFactory.getInstance(AlphaMiniApplication.getInstance(),
                EntityManagerHelper.DB_VERSION,
                EntityManagerHelper.DB_ACCOUNT, null, null)
                .getEntityManager(CodingOpus::class.java,
                        EntityManagerHelper.DB_CODING_OPUS_LIST_TABLE)
        return mEntityManager
    }


    fun getAllOpusDataByUserId(userId: String): List<CodingOpus> {
        val selector = Selector.create().where("userId", "=", userId)
        return mEntityManager!!.findAll(selector)
    }

    fun updateByParam(contentValues: ContentValues, paramMap: Map<String, Any>?) {
        var whereBuilder: WhereBuilder? = null
        if (paramMap != null) {
            var index = 0
            for ((key, value) in paramMap) {
                if (index == 0) {
                    whereBuilder = WhereBuilder.create(key, "=", value)
                    index++
                } else {
                    whereBuilder!!.and(key, "=", value)

                }

            }
        }
        mEntityManager!!.update(contentValues, whereBuilder)
    }


    fun getDataByIdLocal(idLocal : String) : CodingOpus{
        val selector = Selector.create().where("idLocal", "=", idLocal)
        return mEntityManager!!.findFirst(selector)
    }

    fun deleteByParam(paramMap: Map<String, Any>?) {
        var whereBuilder: WhereBuilder? = null
        if (paramMap != null) {
            var index = 0
            for ((key, value) in paramMap) {
                if (index == 0) {
                    whereBuilder = WhereBuilder.create(key, "=", value)
                    index++
                } else {
                    whereBuilder!!.and(key, "=", value)

                }

            }
        }

        mEntityManager!!.delete(whereBuilder)
    }


    override fun getAllData(): List<CodingOpus> {
        return mEntityManager!!.findAll()
    }

    override fun getDataById(id: String): CodingOpus {
        return mEntityManager!!.findById(id)
    }

    override fun saveOrUpdateAll(dataList: List<CodingOpus>) {
        for (opus in dataList) {
            Log.i(TAG, "saveOrUpdateAll:  item :$opus")
        }
        mEntityManager!!.saveOrUpdateAll(dataList)
    }

    override fun saveOrUpdate(item: CodingOpus) {
        Log.i(TAG, "saveOrUpdate: $item")
        mEntityManager!!.saveOrUpdate(item)
    }




    override fun deleteAll() {
        mEntityManager!!.deleteAll()
    }

    override fun deleteById(id: Int) {
        mEntityManager!!.deleteById(id)
    }

    override fun deleteById(id: String) {
        mEntityManager!!.deleteById(id)
    }

    companion object {

        @Volatile
        private var mInstance: CodingOpusProvider? = null

        val instance: CodingOpusProvider?
            get() {
                if (mInstance == null) {
                    synchronized(CodingOpusProvider::class.java) {
                        if (mInstance == null) {
                            mInstance = CodingOpusProvider()
                        }
                    }
                }
                return mInstance
            }
    }

}
