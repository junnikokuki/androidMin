package com.ubtechinc.alpha.mini.viewmodel;

import android.text.TextUtils;

import com.ubtech.utilcode.utils.CollectionUtils;
import com.ubtech.utilcode.utils.SPUtils;
import com.ubtechinc.alpha.mini.constants.CodeMaoConstant;
import com.ubtechinc.alpha.mini.constants.Constants;
import com.ubtechinc.alpha.mini.entity.Contact;
import com.ubtechinc.alpha.mini.entity.Page;
import com.ubtechinc.alpha.mini.entity.observable.AuthLive;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.repository.ContactRepository;
import com.ubtechinc.alpha.mini.repository.datasource.IContactDataSource;
import com.ubtechinc.alpha.mini.utils.ChineseToPinYinUtils;

import java.util.List;

public class ContactViewModel {

    public static final String TAG = "ContactViewModel";
    public static final String VERSION_CODE_KEY = "contact_version";

    private ContactRepository contactRepository;

    public ContactViewModel() {
        contactRepository = new ContactRepository();
    }

    public LiveResult<Contact> add(Contact contact) {
        final LiveResult<Contact> result = new LiveResult<>();
        contact.setRobotId(MyRobotsLive.getInstance().getRobotUserId());
        String userId = AuthLive.getInstance().getUserId();
        contactRepository.add(userId,contact, new IContactDataSource.OpContactCallback() {
            @Override
            public void onSuccess(Contact contact) {
                result.success(contact);
            }

            @Override
            public void onError(int code, String msg) {
                result.fail(code, msg);
            }
        });
        return result;
    }

    public LiveResult<Contact> delete(Contact contact) {
        final LiveResult<Contact> result = new LiveResult<>();
        String userId = AuthLive.getInstance().getUserId();
        contactRepository.delete(userId, contact, new IContactDataSource.OpContactCallback() {
            @Override
            public void onSuccess(Contact contact) {
                contact.setDeleted(1);
                result.success(contact);
            }

            @Override
            public void onError(int code, String msg) {
                result.fail(code, msg);
            }
        });
        return result;
    }

    public LiveResult<Contact> modify(Contact contact) {
        final LiveResult<Contact> result = new LiveResult<>();
        String userId = AuthLive.getInstance().getUserId();
        contactRepository.modify(userId,contact, new IContactDataSource.OpContactCallback() {
            @Override
            public void onSuccess(Contact contact) {
                result.success(contact);
            }

            @Override
            public void onError(int code, String msg) {
                result.fail(code, msg);
            }
        });
        return result;
    }

    public LiveResult importContacts(List<Contact> contacts) {
        final LiveResult<Void> result = new LiveResult<>();
        String robotId = MyRobotsLive.getInstance().getRobotUserId();
        if (CollectionUtils.isEmpty(contacts)) {
            result.success(null);
        } else {
            doImport(contacts,robotId,0,result);
        }
        return result;
    }

    private void doImport(final List<Contact> contacts, final String robotId, int from, final LiveResult result) {
        int to = from +150;
        if(to > contacts.size()){
            to = contacts.size();
        }
        final int finalTo = to;
        List<Contact> subContacts = contacts.subList(from, finalTo);
        String userId = AuthLive.getInstance().getUserId();
        contactRepository.importContacts(userId,subContacts, robotId, new IContactDataSource.ImportContactsCallback() {

            @Override
            public void onSuccess() {
                if(finalTo >= contacts.size()){
                    result.success(null);
                }else{
                    doImport(contacts,robotId,finalTo,result);
                }
            }

            @Override
            public void onError(int code, String msg) {
                if(code == Constants.CONTACT_ERROR_OPTION_CONFILCT){
                    result.fail(code,msg);
                }else{
                    if(finalTo >= contacts.size()){
                        result.success(null);
                    }else{
                        doImport(contacts,robotId,finalTo,result);
                    }
                }

            }
        });
    }

    public LiveResult<List<Contact>> sync() {
        LiveResult<List<Contact>> result = new LiveResult<>();
        String robotId = MyRobotsLive.getInstance().getRobotUserId();
        long versionCode = SPUtils.get().getLong(VERSION_CODE_KEY + robotId, -1l);
        Page page = new Page();
        page.setCurrentPage(1);
        page.setRobotId(robotId);
        page.setVersionCode(versionCode);
        page.setFrom(0);
        syncFromRobot(result, robotId, page);
        return result;
    }

    private void syncFromRobot(final LiveResult<List<Contact>> result, final String robotId, final Page page) {
        contactRepository.getContacts(page, new IContactDataSource.GetContactsCallback() {
            @Override
            public void onSuccess(Page<Contact> page) {
                if (page.getCurrentPage() >= page.getTotalPage()) {
                    SPUtils.get().put(VERSION_CODE_KEY + robotId, page.getVersionCode());
                    result.success(page.getData());
                } else {
                    result.loading(page.getData());
                    page.setCurrentPage(page.getCurrentPage() + 1);
                    syncFromRobot(result, robotId, page);
                }
            }

            @Override
            public void onError(int code, String msg) {
                syncFromRobot(result, robotId, page);
            }
        });
    }

    public LiveResult<List<Contact>> loadAllContact() {
        final LiveResult<List<Contact>> liveResult = new LiveResult<>();
        Page page = new Page();
        page.setRobotId(MyRobotsLive.getInstance().getRobotUserId());
        contactRepository.getContactsFromDB(page, new IContactDataSource.GetContactsCallback() {
            @Override
            public void onSuccess(Page<Contact> page) {
                liveResult.success(page.getData());
            }

            @Override
            public void onError(int code, String msg) {
                liveResult.fail(code, msg);
            }
        });
        return liveResult;
    }

    public boolean isNameExist(String name){
        if(TextUtils.isEmpty(name)){
            return false;
        }
        String robotId = MyRobotsLive.getInstance().getRobotUserId();
        return contactRepository.isNameExist(robotId,name, ChineseToPinYinUtils.chineseToPinYin(name));
    }
}
