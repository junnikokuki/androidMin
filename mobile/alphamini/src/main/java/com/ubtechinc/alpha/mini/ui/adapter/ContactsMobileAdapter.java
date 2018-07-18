package com.ubtechinc.alpha.mini.ui.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.databinding.ItemMobileContactsBinding;
import com.ubtechinc.alpha.mini.entity.Contact;
import com.ubtechinc.alpha.mini.ui.contacts.AlphaMiniMobileContactActivity;

import java.util.ArrayList;
import java.util.HashMap;
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

public class ContactsMobileAdapter extends RecyclerView.Adapter<ContactsMobileAdapter.ContactViewHolder> {

    private int[] colorBgRes = {
            R.drawable.shape_contacts_blue_bg, R.drawable.shape_contacts_green_bg, R.drawable.shape_contacts_puper_bg,
            R.drawable.shape_contacts_red_bg, R.drawable.shape_contacts_yello_bg,
    };

    private String TAG = getClass().getSimpleName();

    private List<Contact> mContacts = new ArrayList<>();//所有的联系人

    private List<Contact> mSelectableContacts = new ArrayList<Contact>();//过滤掉了已添加过的联系人

    private Map<Integer, Contact> checkedContacts = new HashMap<>();

    private LayoutInflater inflater;

    private IChooseContactsCallBack callBack;

    private Random random;


    public ContactsMobileAdapter(AlphaMiniMobileContactActivity activity) {
        this.callBack = activity;
        inflater = LayoutInflater.from(activity);
        random = new Random();
    }


    public void setMobleContacts(List<Contact> contacts) {
        mContacts.clear();
        mContacts.addAll(contacts);
        for (Contact contact : mContacts) {
            if(contact.isAdded()){
                mSelectableContacts.remove(contact);
                mSelectableContacts.add(contact);
            }
        }
        notifyDataSetChanged();
    }

    public void selectAll(boolean isSelectedAll) {
        Log.i(TAG, "selectAll: " + isSelectedAll);
        if(isSelectedAll){
            for (int i = 0; i < mContacts.size(); i++) {
                Contact contact = mContacts.get(i);
                if (!contact.isAdded()) {//没有被添加过
                    contact.setSelected(isSelectedAll);
                    if (isSelectedAll) {//如果是全选，所有的加入Map中
                        checkedContacts.put(i, contact);
                    }
                }
            }
        }
        if (!isSelectedAll) {//如果不是全选，
            checkedContacts.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_mobile_contacts, null);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ContactViewHolder holder, final int position) {
        final Contact contact = mContacts.get(position);
        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!contact.isAdded()){
                    contact.setSelected(!contact.isSelected());
                    holder.binding.setIsSelected(contact.isSelected());
                    if(contact.isSelected()){
                        checkedContacts.put(position, contact);
                    }else{
                        checkedContacts.remove(position);
                    }
                    int selectNum = checkedContacts.size();
                    callBack.choosedContacts(selectNum,selectNum + mSelectableContacts.size()==mContacts.size());
                    callBack.onSelectChange(contact);
                    holder.bind(contact);
                }

            }
        });
        holder.bind(contact);
        holder.binding.ivWarn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.onModifyName(contact);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mContacts == null ? 0: mContacts.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        ItemMobileContactsBinding binding;

        public ContactViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
        }

        public void bind(Contact contact) {
            binding.setContact(contact);
        }
    }

    public interface IChooseContactsCallBack {
        public void choosedContacts(int number,boolean isSelectAll);

        public void onSelectChange(Contact contact);

        public void onModifyName(Contact contact);
    }

}
