/*
 *
 *  *
 *  *  *
 *  *  * Copyright (c) 2008-2017 UBT Corporation.  All rights reserved.  Redistribution,
 *  *  *  modification, and use in source and binary forms are not permitted unless otherwise authorized by UBT.
 *  *  *
 *  *
 *
 */

package com.ubtechinc.alpha.mini.database;

import com.ubtechinc.alpha.mini.AlphaMiniApplication;
import com.ubtechinc.alpha.mini.entity.CallRecord;
import com.ubtechinc.framework.db.EntityManager;
import com.ubtechinc.framework.db.EntityManagerFactory;
import com.ubtechinc.framework.db.sqlite.Selector;

import java.util.Date;
import java.util.List;


/**
 * @ClassName CallRecordProvider
 * @Description 通话记录数据库操作类
 * @modifier
 * @modify_time
 */
public class CallRecordProvider extends BaseProvider<CallRecord> {
    private static final String TAG = "CallRecordProvider";
    //使用volatile关键字保其可见性
    volatile private static CallRecordProvider INSTANCE = null;

    public static CallRecordProvider get() {
        if (INSTANCE == null) {
            synchronized (CallRecordProvider.class) {
                if (INSTANCE == null) {//二次检查
                    INSTANCE = new CallRecordProvider();
                }
            }
        }
        return INSTANCE;
    }

    private EntityManager<CallRecord> entityManager;

    private CallRecordProvider(){
        super();
    }

    @Override
    public EntityManager<CallRecord> entityManagerFactory() {
        entityManager = EntityManagerFactory.getInstance(AlphaMiniApplication.getInstance(),
                EntityManagerHelper.DB_VERSION,
                EntityManagerHelper.DB_ACCOUNT,
                null, null)
                .getEntityManager(CallRecord.class,
                        EntityManagerHelper.DB_CALLRECORD_LIST_TABLE);
        return entityManager;
    }

    @Override
    public List<CallRecord> getAllData() {

        return entityManager.findAll();
    }


    @Override
    public CallRecord getDataById(String id) {
        return entityManager.findById(id);
    }


    public void save(CallRecord callRecord) {
        callRecord.setId(callRecord.getRobotId()+callRecord.getCallId());
        entityManager.save(callRecord);
    }

    public void saveAll(List<CallRecord> callRecords) {
        for (CallRecord callRecord : callRecords) {
            callRecord.setId(callRecord.getRobotId()+callRecord.getCallId());
        }
        entityManager.saveAll(callRecords);
    }

    @Override
    public void saveOrUpdate(CallRecord callRecord) {
        callRecord.setId(callRecord.getRobotId()+callRecord.getCallId());
        entityManager.saveOrUpdate(callRecord);
    }

    @Override
    public void saveOrUpdateAll(List<CallRecord> callRecords) {
        for (CallRecord callRecord : callRecords) {
            callRecord.setId(callRecord.getRobotId()+callRecord.getCallId());
        }
        entityManager.saveOrUpdateAll(callRecords);
    }

    public List<CallRecord> getByRobotId(String robotId, int offset, int limit) {
        Selector selector = Selector.create().where("robotId", "=", robotId);
        Date date = new Date();
        selector.and("callTime",">",date.getTime() - 15768000000l);//半年
        selector.offset(offset);
        selector.limit(limit);
        selector.orderBy("callTime", true);
        List<CallRecord> callRecords = entityManager.findAll(selector);
        return callRecords;
    }

    @Override
    public void deleteAll() {
        entityManager.deleteAll();
    }

    @Override
    public void deleteById(int id) {
        entityManager.deleteById(id + "");
    }

    @Override
    public void deleteById(String id) {
        entityManager.deleteById(id);
    }

}


