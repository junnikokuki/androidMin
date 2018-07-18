package com.ubtechinc.alpha.mini.repository.datasource.db;

import com.ubtechinc.alpha.mini.entity.Contact;
import com.ubtechinc.alpha.mini.entity.Page;
import com.ubtechinc.alpha.mini.repository.datasource.IContactDataSource;
import com.ubtechinc.alpha.mini.database.ContactProvider;

import java.util.List;

/**
 * 通讯录本地数据库数据源
 * Created by ubt on 2018/2/5.
 */

public class DbContactDataSource implements IContactDataSource {

    @Override
    public void getContacts(Page page, GetContactsCallback callback) {
        List<Contact> callRecords = ContactProvider.get().getByRobotId(page.getRobotId());
        page.setData(callRecords);
        if (callback != null) {
            callback.onSuccess(page);
        }
    }

    @Override
    public void add(String userId, Contact contact, OpContactCallback callback) {
        ContactProvider.get().saveOrUpdate(contact);
        if (callback != null) {
            callback.onSuccess(contact);
        }
    }

    @Override
    public void modify(String userId, Contact contact, OpContactCallback callback) {
        ContactProvider.get().saveOrUpdate(contact);
        if (callback != null) {
            callback.onSuccess(contact);
        }
    }

    @Override
    public void delete(String userId, Contact contact, OpContactCallback callback) {
        ContactProvider.get().deleteById(contact.getId());
        if (callback != null) {
            callback.onSuccess(contact);
        }
    }

    @Override
    public void importContacts(String userId, List<Contact> contacts, String robotId, ImportContactsCallback callback) {
        ContactProvider.get().saveOrUpdateAll(contacts);
        if (callback != null) {
            callback.onSuccess();
        }
    }

    public boolean isNameExist(String robotId,String name ,String namePy){
        Contact contact = ContactProvider.get().checkNameExist(robotId,name,namePy);
        return contact != null;
    }
}
