package com.ubtechinc.alpha.mini.ui.contacts;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.ubtechinc.alpha.mini.entity.Contact;
import com.ubtechinc.alpha.mini.utils.ChineseToPinYinUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Date: 2018/2/6.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] :
 */

public class ContactHelper {
    

    public static void getMobileContacts(final Context context, final IGetMobileContactsCallBack callBack){
        new Thread(){
            @Override
            public void run() {
                List<Contact> mobileContact = new ArrayList<>();
                ContentResolver resolver = context.getContentResolver();
                String[] projection = new String[]{
                        ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER//取电话号码是唯一的。

                };
                Log.d("query2","start query");
                Cursor cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, null);
                Log.d("query2","end query");
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        Long id = cursor.getLong(0);
                        //获取姓名
                        String name = cursor.getString(1);
                        String phoneNumber = cursor.getString(2);
                        if(!TextUtils.isEmpty(phoneNumber)&& !TextUtils.isEmpty(name)){
                            Contact contact = new Contact();
                            contact.setName(name);
                            String onlyNumber = phoneNumber.replace("-", "").replace(" ", "");
                            contact.setPhone(onlyNumber);
                            mobileContact.add(ChineseToPinYinUtils.setContactPinYinProperty(contact));
                        }
                    } while (cursor.moveToNext());
                }
                Log.d("query2","end parse " + mobileContact.size());
                callBack.getMobileContactList(mobileContact);
            }
        }.start();
    }


    public static List<Contact> handleMobileContacts(List<Contact> mobileContacts, List<Contact> showupContacts){
        Iterator<Contact> iterator = mobileContacts.iterator();
        while (iterator.hasNext()){
            Contact contact = iterator.next();
            contact.setNameInvalide(checkContactNameValide(contact, showupContacts));
        }
        return mobileContacts;
    }

    private static boolean checkContactNameValide(Contact mobileContact, List<Contact> showupContacts) {
        boolean flag = true;
        Iterator<Contact> iterator = showupContacts.iterator();
        while (iterator.hasNext()){
            Contact contact = iterator.next();
            if (contact.getNamePy().equals(mobileContact.getNamePy())){//全部拼音
                if (contact.getName().equals(mobileContact.getName())){//
                    flag = true;
                }else{
                    flag = false;
                }
            }
        }
        return flag;
    }


    public interface IGetMobileContactsCallBack{
        void getMobileContactList(List<Contact> mobileContacts);
    }

}
