package com.ubtechinc.alpha.mini.repository.datasource.im;

import android.util.Log;

import com.ubtechinc.alpha.CmAddContact;
import com.ubtechinc.alpha.CmDeleteContact;
import com.ubtechinc.alpha.CmImportPhoneContact;
import com.ubtechinc.alpha.CmModifyContact;
import com.ubtechinc.alpha.CmQueryContactList;
import com.ubtechinc.alpha.im.IMCmdId;
import com.ubtechinc.alpha.mini.entity.Contact;
import com.ubtechinc.alpha.mini.entity.Page;
import com.ubtechinc.alpha.mini.repository.datasource.IContactDataSource;
import com.ubtechinc.alpha.mini.im.Phone2RobotMsgMgr;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtechinc.nets.phonerobotcommunite.ICallback;

import java.util.ArrayList;
import java.util.List;

/**
 * 通讯录IM数据源
 * <p>
 * Created by ubt on 2018/2/5.
 */

public class IMContactDataSource implements IContactDataSource {

    public static final String TAG = "IMContactDataSource";

    @Override
    public void getContacts(final Page page, final GetContactsCallback callback) {
        CmQueryContactList.CmQueryContactListRequest.Builder builder = CmQueryContactList.CmQueryContactListRequest.newBuilder();
        builder.setCurrentPage(page.getCurrentPage());
        builder.setVersionCode(page.getVersionCode());
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_CONTACT_QUERY_REQUEST, IMCmdId.IM_VERSION, builder.build(), page.getRobotId(), new ICallback<CmQueryContactList.CmQueryContactListResponse>() {
            @Override
            public void onSuccess(CmQueryContactList.CmQueryContactListResponse data) {
                Log.d(TAG,"getContact success" + data.toString());
                List<Contact> contacts = new ArrayList<Contact>();
                for (CmQueryContactList.CmContactInfo cmContactInfo : data.getContactListList()) {
                    Contact contact = new Contact();
                    contact.setRobotId(page.getRobotId());
                    contact.setContactId(cmContactInfo.getContactId());
                    contact.setName(cmContactInfo.getName());
                    contact.setPhone(cmContactInfo.getPhone());
                    contact.setDeleted(cmContactInfo.getIsDelete());
                    contacts.add(contact);
                }
                page.setData(contacts);
                page.setTotalPage(data.getTotalPage());
                if (page.getCurrentPage() == data.getTotalPage()) {
                    page.setVersionCode(data.getVersionCode());
                }
                if (callback != null) {
                    callback.onSuccess(page);
                }
            }

            @Override
            public void onError(ThrowableWrapper e) {
                Log.d(TAG,"getContact onError" + e.toString());
                if (callback != null) {
                    callback.onError(e.getErrorCode(), e.getMessage());
                }
            }
        });
    }

    @Override
    public void add(String userId,final Contact contact, final OpContactCallback callback) {
        CmAddContact.CmAddContactRequest.Builder builder = CmAddContact.CmAddContactRequest.newBuilder();
        builder.setName(contact.getName()).setPhone(contact.getPhone());
        builder.setUserId(userId);
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_CONTACT_ADD_REQUEST, IMCmdId.IM_VERSION, builder.build(), contact.getRobotId(), new ICallback<CmAddContact.CmAddContactResponse>() {
            @Override
            public void onSuccess(CmAddContact.CmAddContactResponse data) {
                Log.d(TAG,"add success = " +data.toString());
                if (callback != null) {
                    if (data.getIsSuccess()) {
                        contact.setContactId(data.getContactId());
                        callback.onSuccess(contact);
                    } else {
                        callback.onError(data.getResultCode(), "");
                    }
                }
            }

            @Override
            public void onError(ThrowableWrapper e) {
                Log.d(TAG,"add onError = " +e.toString());
                if (callback != null) {
                    callback.onError(e.getErrorCode(), e.getMessage());
                }
            }
        });
    }

    @Override
    public void modify(String userId,final Contact contact, final OpContactCallback callback) {
        CmModifyContact.CmModifyContactRequest.Builder builder = CmModifyContact.CmModifyContactRequest.newBuilder();
        builder.setName(contact.getName()).setPhone(contact.getPhone());
        builder.setUserId(userId);
        builder.setContactId(contact.getContactId());
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_CONTACT_MODIFY_REQUEST, IMCmdId.IM_VERSION, builder.build(), contact.getRobotId(), new ICallback<CmModifyContact.CmModifyContactResponse>() {
            @Override
            public void onSuccess(CmModifyContact.CmModifyContactResponse data) {
                Log.d(TAG,"modify onSuccess = " +data.toString());
                if (callback != null) {
                    if (data.getIsSuccess()) {
                        callback.onSuccess(contact);
                    } else {
                        callback.onError(data.getResultCode(), "");
                    }
                }
            }

            @Override
            public void onError(ThrowableWrapper e) {
                Log.d(TAG,"modify onError = " +e.toString());
                if (callback != null) {
                    callback.onError(e.getErrorCode(), e.getMessage());
                }
            }
        });
    }

    @Override
    public void delete(String userId,final Contact contact, final OpContactCallback callback) {
        CmDeleteContact.CmDeleteContactRequest.Builder builder = CmDeleteContact.CmDeleteContactRequest.newBuilder();
        builder.setContactId(contact.getContactId());
        builder.setUserId(userId);
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_CONTACT_DELETE_REQUEST, IMCmdId.IM_VERSION, builder.build(), contact.getRobotId(), new ICallback<CmDeleteContact.CmDeleteContactResponse>() {
            @Override
            public void onSuccess(CmDeleteContact.CmDeleteContactResponse data) {
                Log.d(TAG,"delete onSuccess = " + data.toString());
                if (callback != null) {
                    if (data.getIsSuccess()) {
                        callback.onSuccess(contact);
                    } else {
                        callback.onError(data.getResultCode(), "");
                    }
                }
            }

            @Override
            public void onError(ThrowableWrapper e) {
                Log.d(TAG,"delete onError = " + e.toString());
                if (callback != null) {
                    callback.onError(e.getErrorCode(), e.getMessage());
                }
            }
        });
    }

    @Override
    public void importContacts(String userId,List<Contact> contacts, String robotId, final  ImportContactsCallback callback) {
        CmImportPhoneContact.CmImportPhoneContactRequest.Builder builder = CmImportPhoneContact.CmImportPhoneContactRequest.newBuilder();
        for (Contact contact : contacts) {
            CmQueryContactList.CmContactInfo.Builder valueBuilder = CmQueryContactList.CmContactInfo.newBuilder();
            valueBuilder.setName(contact.getName());
            valueBuilder.setPhone(contact.getPhone());
            builder.addContactList(valueBuilder);
        }
        builder.setUserId(userId);
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_CONTACT_IMOPORT_REQUEST, IMCmdId.IM_VERSION, builder.build(), robotId, new ICallback<CmImportPhoneContact.CmImportPhoneContactResponse>() {
            @Override
            public void onSuccess(CmImportPhoneContact.CmImportPhoneContactResponse data) {
                Log.d(TAG,"importContacts onSuccess = " +data.toString());
                if (callback != null) {
                    if (data.getIsSuccess()) {
                        callback.onSuccess();
                    } else {
                        callback.onError(data.getResultCode(), "");
                    }
                }
            }

            @Override
            public void onError(ThrowableWrapper e) {
                Log.d(TAG,"importContacts onError = " +e.toString());
                if (callback != null) {
                    callback.onError(e.getErrorCode(), e.getMessage());
                }
            }
        });
    }
}
