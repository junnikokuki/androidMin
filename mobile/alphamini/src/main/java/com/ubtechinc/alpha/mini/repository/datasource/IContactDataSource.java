package com.ubtechinc.alpha.mini.repository.datasource;

import com.ubtechinc.alpha.mini.entity.Contact;
import com.ubtechinc.alpha.mini.entity.Page;

import java.util.List;

/**
 * Created by ubt on 2018/2/5.
 */

public interface IContactDataSource {

    public void getContacts(Page page, GetContactsCallback callback);

    public void add(String userId, Contact contact, OpContactCallback callback);

    public void modify(String userId, Contact contact, OpContactCallback callback);

    public void delete(String userId, Contact contact, OpContactCallback callback);

    public void importContacts(String userId, List<Contact> contacts, String robotId, ImportContactsCallback callback);

    public interface ImportContactsCallback {
        public void onSuccess();

        public void onError(int code, String msg);
    }

    public interface OpContactCallback {
        public void onSuccess(Contact contact);

        public void onError(int code, String msg);
    }

    public interface GetContactsCallback {
        public void onSuccess(Page<Contact> page);

        public void onError(int code, String msg);
    }
}
