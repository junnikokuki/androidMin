package com.ubtechinc.alpha.mini.repository;

import com.ubtech.utilcode.utils.CollectionUtils;
import com.ubtechinc.alpha.mini.entity.Contact;
import com.ubtechinc.alpha.mini.entity.Page;
import com.ubtechinc.alpha.mini.repository.datasource.IContactDataSource;
import com.ubtechinc.alpha.mini.repository.datasource.db.DbContactDataSource;
import com.ubtechinc.alpha.mini.repository.datasource.im.IMContactDataSource;

import java.util.List;

/**
 * 联系人数据管理类
 * <p>
 * Created by ubt on 2018/2/5.
 */

public class ContactRepository implements IContactDataSource {

    private DbContactDataSource dbContactDataSource;
    private IContactDataSource imContactDataSource;

    public ContactRepository() {
        dbContactDataSource = new DbContactDataSource();
        imContactDataSource = new IMContactDataSource();
    }

    @Override
    public void getContacts(Page page, final GetContactsCallback callback) {
        imContactDataSource.getContacts(page, new GetContactsCallback() {
            @Override
            public void onSuccess(Page<Contact> page) {
                if (!CollectionUtils.isEmpty(page.getData())) {
                    dbContactDataSource.importContacts("",page.getData(), page.getRobotId(), null);
                }
                if (callback != null) {
                    callback.onSuccess(page);
                }
            }

            @Override
            public void onError(int code, String msg) {
                if (callback != null) {
                    callback.onError(code, msg);
                }
            }
        });
    }

    public void getContactsFromDB(Page page, GetContactsCallback callback) {
        dbContactDataSource.getContacts(page, callback);
    }

    @Override
    public void add(final String userId, Contact contact, final OpContactCallback callback) {
        imContactDataSource.add(userId,contact, new OpContactCallback() {
            @Override
            public void onSuccess(Contact contact) {
                dbContactDataSource.add(userId,contact, null);
                if (callback != null) {
                    callback.onSuccess(contact);
                }
            }

            @Override
            public void onError(int code, String msg) {
                if (callback != null) {
                    callback.onError(code, msg);
                }

            }
        });
    }

    @Override
    public void modify(final String userId, Contact contact, final OpContactCallback callback) {
        imContactDataSource.modify(userId, contact, new OpContactCallback() {
            @Override
            public void onSuccess(Contact contact) {
                dbContactDataSource.modify(userId, contact, null);
                if (callback != null) {
                    callback.onSuccess(contact);
                }
            }

            @Override
            public void onError(int code, String msg) {
                if (callback != null) {
                    callback.onError(code, msg);
                }

            }
        });
    }

    @Override
    public void delete(final String userId, Contact contact, final OpContactCallback callback) {
        imContactDataSource.delete(userId, contact, new OpContactCallback() {
            @Override
            public void onSuccess(Contact contact) {
                dbContactDataSource.delete(userId, contact, null);
                if (callback != null) {
                    callback.onSuccess(contact);
                }
            }

            @Override
            public void onError(int code, String msg) {
                if (callback != null) {
                    callback.onError(code, msg);
                }

            }
        });
    }

    public boolean isNameExist(String robotId,String name,String namePy){
        return dbContactDataSource.isNameExist(robotId,name,namePy);
    }

    @Override
    public void importContacts(String userId, List<Contact> contacts, String robotId, ImportContactsCallback callback) {
        imContactDataSource.importContacts(userId,contacts,robotId,callback);
    }
}
