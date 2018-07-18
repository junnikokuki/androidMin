package com.ubtechinc.alpha.mini.widget;

import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.ui.contacts.AlphaMiniContactActivity;

/**
 * @Date: 2017/11/27.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 通讯录弹窗(导入通讯录， 新建联系人 的入口)
 */

public class ContactsPopupWindow extends PopupWindow implements View.OnClickListener{

    private IContactsWindowCallBack callBack;

    public ContactsPopupWindow(AlphaMiniContactActivity activity) {
        super(LayoutInflater.from(activity).inflate(R.layout.window_setting, null), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        callBack = activity;
        getContentView().findViewById(R.id.text_import_contact).setOnClickListener(this);
        getContentView().findViewById(R.id.text_new_contact).setOnClickListener(this);
    }

    public void show(View view){
        showAsDropDown(view);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        int left = anchor.getLeft();
        setFocusable(true);
        setTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setOutsideTouchable(true);
        super.showAsDropDown(anchor, xoff, yoff);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.text_import_contact:
                callBack.importContacts();
                dismiss();
                break;
            case R.id.text_new_contact:
                callBack.newContacts();
                dismiss();
                break;
        }
    }

    public interface IContactsWindowCallBack{
        public void newContacts();

        public void importContacts();
    }
}
