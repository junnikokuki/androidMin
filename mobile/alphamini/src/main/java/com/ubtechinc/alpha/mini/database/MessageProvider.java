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

import android.content.ContentValues;

import com.ubtechinc.alpha.mini.AlphaMiniApplication;
import com.ubtechinc.alpha.mini.entity.Message;
import com.ubtechinc.framework.db.EntityManager;
import com.ubtechinc.framework.db.EntityManagerFactory;
import com.ubtechinc.framework.db.sqlite.Selector;
import com.ubtechinc.framework.db.sqlite.WhereBuilder;

import java.util.List;


/**
 * @ClassName MessageProvider
 * @Description 消息数据库操作类
 * @modifier
 * @modify_time
 */
public class MessageProvider extends BaseProvider<Message> {
    private static final String TAG = "MessageProvider";
    //使用volatile关键字保其可见性
    volatile private static MessageProvider INSTANCE = null;


    public static MessageProvider get() {
        if (INSTANCE == null) {
            synchronized (MessageProvider.class) {
                if (INSTANCE == null) {//二次检查
                    INSTANCE = new MessageProvider();
                }
            }
        }
        return INSTANCE;
    }

    private MessageProvider() {
        super();
    }

    private EntityManager<Message> entityManager;

    @Override
    public EntityManager<Message> entityManagerFactory() {
        entityManager = EntityManagerFactory.getInstance(AlphaMiniApplication.getInstance(),
                EntityManagerHelper.DB_VERSION,
                EntityManagerHelper.DB_ACCOUNT,
                null, null)
                .getEntityManager(Message.class,
                        EntityManagerHelper.DB_NOTICE_MESSAGE_TABLE);
        return entityManager;
    }

    @Override
    public List<Message> getAllData() {

        return entityManager.findAll();
    }


    @Override
    public Message getDataById(String id) {
        return entityManager.findById(id);
    }


    public void save(Message message) {
        entityManager.save(message);
    }

    public void saveAll(List<Message> messages) {
        entityManager.saveAll(messages);
    }

    @Override
    public void saveOrUpdate(Message message) {
        entityManager.saveOrUpdate(message);
    }

    @Override
    public void saveOrUpdateAll(List<Message> messages) {
        entityManager.saveOrUpdateAll(messages);
    }

    public List<Message> getByUserIdAndType(String userId, int type, int offset, int limit) {
        Selector selector = Selector.create().where("toId", "=", userId);
        selector.and("noticeType", "=", type);
        selector.offset(offset);
        selector.limit(limit);
        selector.orderBy("noticeTime", true);
        List<Message> messages = entityManager.findAll(selector);
        return messages;
    }

    public Message getByNoticeId(String noticeId) {
        Selector selector = Selector.create().where("noticeId", "=", noticeId);
        Message message = entityManager.findFirst(selector);
        return message;
    }

    public void expiredMsg(String userId, String robotId, String commandId) {
        ContentValues contentValues = new ContentValues(1);
        contentValues.put("operation", String.valueOf(Message.OP_EXPIRE));
        WhereBuilder builder = WhereBuilder.create();
        builder.expr("toId", "=", userId)
                .and("robotId", "=", robotId)
                .and("commandId", "=", commandId)
                .and("operation", "=", String.valueOf(Message.OP_NORMAL));
        entityManager.update(contentValues, builder);
    }

    public void readMsg(String noticeId) {
        ContentValues contentValues = new ContentValues(1);
        contentValues.put("isRead", 1);
        WhereBuilder builder = WhereBuilder.create();
        builder.expr("noticeId", "=", noticeId);
        entityManager.update(contentValues, builder);
    }

    public void readAllMsg(String userId, int type) {
        ContentValues contentValues = new ContentValues(1);
        contentValues.put("isRead", 1);
        WhereBuilder builder = WhereBuilder.create();
        builder.expr("toId", "=", userId)
                .and("noticeType", "=", type);
        entityManager.update(contentValues, builder);
    }

    public void deleteByType(int type) {
        entityManager.delete(WhereBuilder.create().expr("noticeType", "=", type));
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


