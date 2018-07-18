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
import com.ubtechinc.alpha.mini.entity.Contact;
import com.ubtechinc.framework.db.EntityManager;
import com.ubtechinc.framework.db.EntityManagerFactory;
import com.ubtechinc.framework.db.sqlite.Selector;

import java.util.List;


/**
 * @ClassName ContactProvider
 * @Description 通话记录数据库操作类
 * @modifier
 * @modify_time
 */
public class ContactProvider extends BaseProvider<Contact> {
    private static final String TAG = "ContactProvider";
    //使用volatile关键字保其可见性
    volatile private static ContactProvider INSTANCE = null;

    public static ContactProvider get() {
        if (INSTANCE == null) {
            synchronized (ContactProvider.class) {
                if (INSTANCE == null) {//二次检查
                    INSTANCE = new ContactProvider();
                }
            }
        }
        return INSTANCE;
    }

    private EntityManager<Contact> entityManager;

    private ContactProvider(){
        super();
    }

    @Override
    public EntityManager<Contact> entityManagerFactory() {
        entityManager = EntityManagerFactory.getInstance(AlphaMiniApplication.getInstance(),
                EntityManagerHelper.DB_VERSION,
                EntityManagerHelper.DB_ACCOUNT,
                null, null)
                .getEntityManager(Contact.class,
                        EntityManagerHelper.DB_CONTACT_LIST_TABLE);
        return entityManager;
    }

    @Override
    public List<Contact> getAllData() {

        return entityManager.findAll();
    }


    @Override
    public Contact getDataById(String id) {
        return entityManager.findById(id);
    }

    public void save(Contact contact) {
        contact.setId(contact.getRobotId() + contact.getContactId());
        entityManager.save(contact);
    }

    @Override
    public void saveOrUpdate(Contact contact) {
        contact.setId(contact.getRobotId() + contact.getContactId());
        entityManager.saveOrUpdate(contact);
    }

    @Override
    public void saveOrUpdateAll(List<Contact> contacts) {
        for (Contact contact : contacts) {
            contact.setId(contact.getRobotId() + contact.getContactId());
        }
        entityManager.saveOrUpdateAll(contacts);
    }

    public List<Contact> getByRobotId(String robotId) {
        Selector selector = Selector.create().where("robotId", "=", robotId);
        selector.and("isDeleted", "=", 0);
        List<Contact> contacts = entityManager.findAll(selector);
        return contacts;
    }

    public Contact checkNameExist(String robotId,String name,String namePy){
        Selector selector = Selector.create().where("robotId","=",robotId)
                .and("name","!=",name)
                .and("namePy","=",namePy)
                .and("isDeleted","=",0);
        return entityManager.findFirst(selector);
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


