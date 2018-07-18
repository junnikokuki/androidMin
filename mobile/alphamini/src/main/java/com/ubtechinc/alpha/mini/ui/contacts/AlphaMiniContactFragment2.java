package com.ubtechinc.alpha.mini.ui.contacts;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.ubtech.utilcode.utils.CollectionUtils;
import com.ubtech.utilcode.utils.network.NetworkHelper;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseFragment;
import com.ubtechinc.alpha.mini.databinding.FragmentContacts2Binding;
import com.ubtechinc.alpha.mini.entity.Contact;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.ui.PageRouter;
import com.ubtechinc.alpha.mini.ui.adapter.ContactsAdapter;
import com.ubtechinc.alpha.mini.utils.ChineseToPinYinUtils;
import com.ubtechinc.alpha.mini.viewmodel.ContactViewModel;
import com.ubtechinc.alpha.mini.widget.MiniDividerDecoration;
import com.ubtechinc.alpha.mini.widget.TitleItemDecoration;
import com.ubtechinc.alpha.mini.widget.sidebar.OnQuickSideBarTouchListener;
import com.ubtechinc.alpha.mini.entity.RobotInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Date: 2018/1/30.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : Mini联系人Fragment页面,负责通讯录的显示和UI操作
 */

public class AlphaMiniContactFragment2 extends BaseFragment implements OnQuickSideBarTouchListener, MiniDividerDecoration.IDividerCallBack {

    private FragmentContacts2Binding binding;

    private ContactsAdapter adapter;

    private List<Contact> contacts;

    private Map<String, Integer> sizeMap;//每一个字母对应列表中的position位置

    private LinearLayoutManager mLayoutManager;

    private ContactViewModel contactViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_contacts2, null, false);
        contactViewModel = new ContactViewModel();
        initView();
        return binding.getRoot();
    }

    private void initView() {
        binding.rv.setLayoutManager(mLayoutManager = new LinearLayoutManager(AlphaMiniContactFragment2.this.getActivity()));
        binding.setIsEmpty(true);
        binding.quickSideBarView.setOnQuickSideBarTouchListener(this);
        contacts = new ArrayList<>();
        adapter = new ContactsAdapter(getActivity());
        adapter.updateList(contacts);
        if (getArguments() != null) {
            String phoneNum = getArguments().getString("phoneNum");
            adapter.setPhoneNum(phoneNum);
            TextView numberText = binding.refreshLayout.findViewById(R.id.text_number);
            if (numberText != null) {
                numberText.setText(phoneNum);
            }
        }
        binding.refreshLayout.findViewById(R.id.import_contacts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toImportContact();
            }
        });
        binding.refreshLayout.findViewById(R.id.text_new_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toCreateContact();
            }
        });
        binding.rv.setAdapter(adapter);
        binding.rv.addItemDecoration(new TitleItemDecoration(contacts));
        binding.rv.addItemDecoration(new MiniDividerDecoration(AlphaMiniContactFragment2.this.getActivity(), 2 * getResources().getDimensionPixelOffset(R.dimen.ten_dp), this));
        binding.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                syncData();
            }
        });
        binding.refreshLayout.setEnableLoadmore(false);
        initDatas();
    }


    /**
     * 本地数据
     **/
    private void initDatas() {
        contactViewModel.loadAllContact().observe(this, new Observer<LiveResult>() {
            @Override
            public void onChanged(@Nullable LiveResult liveResult) {
                switch (liveResult.getState()) {
                    case SUCCESS:
                        List<Contact> localContacts = (List<Contact>) liveResult.getData();
                        contacts.removeAll(localContacts);
                        contacts.addAll(localContacts);
                        ChineseToPinYinUtils.sortWithPinYin(contacts);
                        adapter.updateList(contacts);
                        binding.quickSideBarView.setLetters(setSideAlphaList(contacts), sizeMap);
                        liveResult.removeObserver(this);
                        autoRefresh();
                        break;
                    case FAIL:
                        liveResult.removeObserver(this);
                        autoRefresh();
                        break;
                }
                binding.setIsEmpty(contacts.isEmpty());
            }
        });
    }

    private void autoRefresh() {
        RobotInfo currentRobot = MyRobotsLive.getInstance().getCurrentRobot().getValue();
        if (NetworkHelper.sharedHelper().isNetworkAvailable() && currentRobot != null
                && currentRobot.getOnlineState() == RobotInfo.ROBOT_STATE_ONLINE) {
            binding.refreshLayout.autoRefresh();
        } else {
            binding.refreshLayout.setEnableRefresh(false);
        }
    }

    private void syncData() {
        contactViewModel.sync().observe(this, new Observer<LiveResult>() {
            @Override
            public void onChanged(@Nullable LiveResult liveResult) {
                List<Contact> syncContacts = (List<Contact>) liveResult.getData();
                switch (liveResult.getState()) {
                    case SUCCESS:
                        liveResult.removeObserver(this);
                        binding.refreshLayout.finishRefresh();
                        break;
                    case FAIL:
                        liveResult.removeObserver(this);
                        binding.refreshLayout.finishRefresh();
                        break;
                    case LOADING:
                        break;
                }
                refreshContactData(syncContacts);
            }
        });
    }


    @Override
    public void onLetterChanged(String letter, int position, float y) {
        Log.i("0223", "onLetterChanged: " + position);
        binding.quickSideBarTipsView.setText(letter, position, y);
        // TODO:
        mLayoutManager.scrollToPositionWithOffset(position, 0);
    }

    @Override
    public void onLetterTouching(boolean touching) {
        binding.quickSideBarTipsView.setVisibility(touching ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public boolean needDivideLineAdjustToPosition(int position) {
        if (position < 0 || position >= contacts.size()) {
            Log.e("0205", "isLastInGroup: position is wrong ");
            return false;
        }

        if (position == 0) {//header不需要分割线，
            return false;
        }

        int index = position - 1;//减去Header索引, index对应List中的位置

        if (index == contacts.size() - 1) {//最后一个Item，也不需要分割线
            return false;
        }

        String currentTAG = contacts.get(index).getFirstLetterPy();
        String nextTAG = contacts.get(index + 1).getFirstLetterPy();
        if (TextUtils.isEmpty(currentTAG) && TextUtils.isEmpty(nextTAG)) {
            return false;
        }
        if (TextUtils.isEmpty(nextTAG)) {
            return true;
        }
        if (currentTAG.equals(nextTAG)) {
            return true;
        } else {
            return false;
        }

    }

    private void refreshContactData(List<Contact> newContacts) {
        if (CollectionUtils.isEmpty(newContacts)) {
            return;
        } else {
            contacts.removeAll(newContacts);
            List<Contact> notDeleteContacts = new ArrayList<>();
            for (Contact newContact : newContacts) {
                if (newContact.isDeleted() == 0) {
                    notDeleteContacts.add(newContact);
                }
            }
            contacts.addAll(notDeleteContacts);
            ChineseToPinYinUtils.sortWithPinYin(contacts);
            if (adapter != null) {
                adapter.updateList(contacts);
            }
            binding.quickSideBarView.setLetters(setSideAlphaList(contacts), sizeMap);
        }
        if (CollectionUtils.isEmpty(contacts)) {
            binding.setIsEmpty(true);
        } else {
            binding.setIsEmpty(false);
        }
    }

    private void refreshContactData(Contact contact) {
        if (contact != null) {
            contacts.remove(contact);
            if (contact.isDeleted() == 0) {
                contacts.add(contact);
                ChineseToPinYinUtils.sortWithPinYin(contacts);
            }
            if (adapter != null) {
                adapter.updateList(contacts);
            }
            binding.quickSideBarView.setLetters(setSideAlphaList(contacts), sizeMap);
        }
        if (CollectionUtils.isEmpty(contacts)) {
            binding.setIsEmpty(true);
        } else {
            binding.setIsEmpty(false);
        }
    }

    private List<String> setSideAlphaList(List<Contact> contacts) {
        List<String> letters = new ArrayList<String>();
        sizeMap = new TreeMap<>();
        for (int i = 0; i < contacts.size(); i++) {
            if (i == 0) {
                letters.add(contacts.get(i).getFirstLetterPy());
                sizeMap.put(contacts.get(i).getFirstLetterPy(), i + 1);//  i + 1 ，这个1 代表的是Header，
                continue;
            }
            if (!contacts.get(i).getFirstLetterPy().equals(contacts.get(i - 1).getFirstLetterPy())) {
                letters.add(contacts.get(i).getFirstLetterPy());
                sizeMap.put(contacts.get(i).getFirstLetterPy(), i + 1);
            }
        }
        return letters;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("ContactFragment", "onActivityResult");
        if (data == null) {
            return;
        }
        Contact contact = (Contact) data.getSerializableExtra(AlphaMiniContactDetailActivity.CONTACT_KEY);
        refreshContactData(contact);
        if (data.getBooleanExtra(AlphaMiniMobileContactActivity.CHANGED, false)) {
            if (binding != null && binding.refreshLayout != null) {
                autoRefresh();
            }
        }
    }

    private void toCreateContact() {
        PageRouter.toCreateContact(getActivity());
    }


    private void toImportContact() {
        ((AlphaMiniContactActivity) getActivity()).importContacts();
    }

    public void setConnectFail(boolean fail) {
        binding.refreshLayout.setEnableRefresh(!fail);
    }
}
