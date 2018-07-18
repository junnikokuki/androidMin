package com.ubtechinc.alpha.mini.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.databinding.ItemContactBinding;
import com.ubtechinc.alpha.mini.databinding.ItemContactHeaderBinding;

import com.ubtechinc.alpha.mini.entity.Contact;
import com.ubtechinc.alpha.mini.ui.PageRouter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @Date: 2018/1/30.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 联系人适配器
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>{

    private int[] colorBgRes = {
        R.drawable.shape_contacts_blue_bg, R.drawable.shape_contacts_green_bg, R.drawable.shape_contacts_puper_bg,
            R.drawable.shape_contacts_red_bg, R.drawable.shape_contacts_yello_bg,
    };


    private List<Contact> mContacts = new ArrayList<>();//所有的联系人

    private LayoutInflater inflater;

    private Random random;

    private String phoneNum;

    private static final int item_header = 0;
    private static final int item_simple = 1;

    private Context ctx;

    public ContactsAdapter(Context ctx) {
        this.ctx = ctx;
        inflater = LayoutInflater.from(ctx);
        random = new Random();
    }


    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == item_header){
            view = inflater.inflate(R.layout.item_contact_header, null);
        }else{
            view  = inflater.inflate(R.layout.item_contact, null);
        }
        return new ContactViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(final ContactViewHolder holder, final int position) {
        if (position != 0){
            final int index = position - 1; //减去 Header
            holder.binding.textName.setBackgroundResource(colorBgRes[Math.abs(mContacts.get(index).getName().hashCode())%5]);
            holder.binding.textName.setText(mContacts.get(index).getName().substring(0, 1));
            holder.binding.textFullName.setText(mContacts.get(index).getName());
            holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PageRouter.toContactDetail(mContacts.get(index), (Activity) ctx);
                }
            });
        }else{
            holder.headerBinding.setPhoneNumber(TextUtils.isEmpty(phoneNum)?null:phoneNum);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return item_header;
        }else{
            return item_simple;
        }
    }

    @Override
    public int getItemCount() {
        return mContacts.size() + 1;
    }

    public void setPhoneNum(String phoneNum){
        this.phoneNum = phoneNum;
        notifyDataSetChanged();
    }

    public void updateList(List<Contact> contacts){
        this.mContacts.clear();
        this.mContacts.addAll(contacts);
        notifyDataSetChanged();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder{
        private ItemContactBinding binding;

        private ItemContactHeaderBinding headerBinding;

        public ContactViewHolder(View view, int type) {
            super(view);
            switch (type){
                case item_header:
                    headerBinding = DataBindingUtil.bind(view);
                    break;
                case item_simple:
                    binding = DataBindingUtil.bind(view);
                    break;
            }
        }
    }


}
