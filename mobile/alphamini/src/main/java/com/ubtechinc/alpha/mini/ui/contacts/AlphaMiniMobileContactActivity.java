package com.ubtechinc.alpha.mini.ui.contacts;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ubtech.utilcode.utils.CollectionUtils;
import com.ubtech.utilcode.utils.KeyboardUtils;
import com.ubtech.utilcode.utils.StringUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseToolbarActivity;
import com.ubtechinc.alpha.mini.databinding.ActivityMobileContactBinding;
import com.ubtechinc.alpha.mini.entity.Contact;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.ui.adapter.ContactsMobileAdapter;
import com.ubtechinc.alpha.mini.utils.ChineseToPinYinUtils;
import com.ubtechinc.alpha.mini.viewmodel.ContactViewModel;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.ubtechinc.alpha.mini.widget.MiniDividerDecoration;
import com.ubtechinc.alpha.mini.widget.MobileConcactTitleDecoration;
import com.ubtechinc.alpha.mini.widget.sidebar.OnQuickSideBarTouchListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.ubtechinc.alpha.mini.constants.Constants.CONTACT_ERROR_INVALID_PARAMS;
import static com.ubtechinc.alpha.mini.constants.Constants.CONTACT_ERROR_OPTION_CONFILCT;

/**
 * @Date: 2018/2/6.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] :
 */

public class AlphaMiniMobileContactActivity extends BaseToolbarActivity implements OnQuickSideBarTouchListener, ContactsMobileAdapter.IChooseContactsCallBack, MiniDividerDecoration.IDividerCallBack {

    public static final int NAME_STATE_BLANK = 1;
    public static final int NAME_STATE_TOO_LONG = 2;
    public static final int NAME_STATE_CHINESE = 3;
    public static final int NAME_STATE_SAME = 4;
    public static final int NAME_STATE_EMPTY = 5;
    public static final int NAME_STATE_SUCCESS = 6;

    public static final String CHANGED = "changed";

    private ActivityMobileContactBinding binding;

    private ContactsMobileAdapter adapter;

    private boolean isSelectedAll = false;

    private List<Contact> contacts = new ArrayList<>();

    private MaterialDialog changeNameDialog;
    private MaterialDialog errorDialog;
    private DialogModifyBtnListener errorDialogBtnListener;

    private ContactViewModel contactViewModel;

    private TextView dialogNameTipsView;
    private EditText dialogNameEdit;

    private Map<String, List<Contact>> contactsMap = new HashMap<>();

    private List<Contact> selectContacts = new ArrayList<>();

    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mobile_contact);
        initView();
        contactViewModel = new ContactViewModel();
        binding.setIsEmpty(true);
        showLoadingDialog();
        ContactHelper.getMobileContacts(this, new ContactHelper.IGetMobileContactsCallBack() {
            @Override
            public void getMobileContactList(final List<Contact> mobileContacts) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        contacts.addAll(mobileContacts);
                        loadLocal();
                        dismissDialog();
                        if (CollectionUtils.isEmpty(contacts)) {
                            binding.setIsEmpty(true);
                        } else {
                            binding.setIsEmpty(false);
                            ChineseToPinYinUtils.chineseToPinYin(contacts);
                            ChineseToPinYinUtils.sortWithPinYin(contacts);
                            adapter.setMobleContacts(contacts);
                            binding.quickSideBarView2.setLetters(setSideAlphaList(contacts), sizeMap);
                        }
                    }
                });
            }
        });
    }

    private void loadLocal() {
        contactViewModel.loadAllContact().observe(this, new Observer<LiveResult>() {
            @Override
            public void onChanged(@Nullable LiveResult liveResult) {
                switch (liveResult.getState()) {
                    case SUCCESS:
                        List<Contact> contacts = (List<Contact>) liveResult.getData();
                        if (!CollectionUtils.isEmpty(contacts)) {
                            buildContactMap(contacts);
                            checkContactLegal();
                        }
                        break;
                    case FAIL:
                        break;
                }
            }
        });
    }

    private void buildContactMap(List<Contact> contacts) {
        if (contactsMap == null) {
            contactsMap = new HashMap<>();
        }
        for (Contact contact : contacts) {
            List<Contact> nameContacts = contactsMap.get(contact.getNamePy());
            if (nameContacts == null) {
                nameContacts = new ArrayList<>();
                contactsMap.put(contact.getNamePy(), nameContacts);
            }
            nameContacts.add(contact);
        }
    }

    private void checkContactLegal() {
        if (contactsMap.isEmpty() && selectContacts.isEmpty()) {
            return;
        }
        for (Contact contact : contacts) {
            if (!contactsMap.isEmpty()) {
                List<Contact> nameContacts = contactsMap.get(contact.getNamePy());
                if (!CollectionUtils.isEmpty(nameContacts)) {
                    for (Contact nameContact : nameContacts) {
                        if (nameContact.getName().equals(contact.getName())) {
                            if (nameContact.getPhone().equals(contact.getPhone())) {
                                contact.setAdded(true);
                                break;
                            }
                        } else {
                            contact.setNameInvalide(false);
                        }
                    }
                    if (contact.isAdded()) {
                        contact.setNameInvalide(true);
                    }
                }
            }
            if (!contact.isAdded() && !contact.isNameInvalide()) {
                if (!CollectionUtils.isEmpty(selectContacts)) {
                    for (Contact selectContact : selectContacts) {
                        if (selectContact.getName().equals(contact.getName())) {
                            if (selectContact.getPhone().equals(contact.getPhone())) {
                                contact.setAdded(true);
                                break;
                            }
                        } else {
                            contact.setNameInvalide(false);
                        }
                    }
                    if (contact.isAdded()) {
                        contact.setNameInvalide(true);
                    }
                }
            }
        }
    }


    private void initView() {
        adapter = new ContactsMobileAdapter(this);
        binding.importContacts.setAdapter(adapter);
        mLayoutManager = new LinearLayoutManager(this);
        binding.importContacts.setLayoutManager(mLayoutManager);
        binding.importContacts.addItemDecoration(new MobileConcactTitleDecoration(AlphaMiniMobileContactActivity.this, contacts));
        binding.importContacts.addItemDecoration(new MiniDividerDecoration(this, 2 * getResources().getDimensionPixelOffset(R.dimen.ten_dp), this));
        binding.setReponse(new EventResponse());
        binding.btnImport.setEnabled(false);
        binding.quickSideBarView2.setOnQuickSideBarTouchListener(this);
        initConnectFailedLayout(R.string.mini_contacts_network_error, R.string.mini_contacts_network_robot_connect_error);
    }

    @Override
    public void choosedContacts(int number, boolean isSelectedAll) {
        this.isSelectedAll = isSelectedAll;
        binding.setIsSelectedAll(isSelectedAll);
        if (number == 0) {
            selectContacts.clear();
            binding.textContactsTitle.setText(R.string.mini_contacts_choose_contact);
            binding.btnImport.setEnabled(false);
        } else {
            binding.textContactsTitle.setText(getString(R.string.mini_contacts_choose_contacts_number_title, number));
        }
    }

    @Override
    public void onSelectChange(Contact changeContact) {
        boolean isCanSubmit = true;
        if (changeContact.isSelected()) {
            if (!changeContact.isNameInvalide()) {
                int state = checkNameValidate(changeContact.getName());
                if (state != NAME_STATE_SUCCESS) {
                    changeContact.setNameInvalide(true);
                    showNameErrorMsg(state);
                }
            }
            selectContacts.add(changeContact);
        } else {
            changeContact.setNameInvalide(false);
            selectContacts.remove(changeContact);
        }
        if (CollectionUtils.isEmpty(selectContacts)) {
            showNameErrorMsg(NAME_STATE_SUCCESS);
            isCanSubmit = false;
        } else {
            for (Contact contact : selectContacts) {
                if (contact.isNameInvalide()) {
                    isCanSubmit = false;
                    int state = checkNameValidate(contact.getName());
                    showNameErrorMsg(state);
                    break;
                }
            }
        }
        enableImportBtn(isCanSubmit);
    }

    @Override
    public void onModifyName(final Contact contact) { //处理点击修改名称
        if (changeNameDialog == null) {
            changeNameDialog = new MaterialDialog(this);
            View contentView = LayoutInflater.from(this).inflate(R.layout.layout_contact_change_name, null);
            changeNameDialog.setView(contentView);

            changeNameDialog.setTitle(R.string.please_change_concat_name);
            dialogNameTipsView = contentView.findViewById(R.id.contact_name_tips);
            dialogNameEdit = contentView.findViewById(R.id.contact_change_name_input);
            changeNameDialog.setPositiveButton(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    contact.setName(dialogNameEdit.getText().toString());
                    contact.setNameInvalide(false);
                    ChineseToPinYinUtils.sortWithPinYin(contacts);
                    adapter.notifyDataSetChanged();
                    binding.quickSideBarView2.setLetters(setSideAlphaList(contacts), sizeMap);
                    dialogNameEdit.clearFocus();
                    KeyboardUtils.hideSoftInput(AlphaMiniMobileContactActivity.this, dialogNameEdit);
                    changeNameDialog.dismiss();
                    isCanSubmit(getRobotConnect(), getNetworkConnectState());
                }
            });
            changeNameDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogNameEdit.clearFocus();
                    KeyboardUtils.hideSoftInput(AlphaMiniMobileContactActivity.this, dialogNameEdit);
                    changeNameDialog.dismiss();
                }
            });
            dialogNameEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String name = editable.toString();
                    nameValidate(name);
                }
            });
        }
        changeNameDialog.show();
        changeNameDialog.enablePositiveBtn(false);
        dialogNameEdit.setText(contact.getName());
    }

    private void nameValidate(String name) { //检测名字是否合法
        String tips = null;
        if (TextUtils.isEmpty(name)) {
            changeNameDialog.enablePositiveBtn(false);
            tips = getString(R.string.mini_contacts_name_tips_empty);
        } else if (name.length() > 10) {
            changeNameDialog.enablePositiveBtn(false);
            tips = getString(R.string.mini_contacts_name_tips_long, name.length());
        } else {
            if (StringUtils.onlyChineseOrNumber(name)) {
                if (getNetworkConnectState() && getRobotConnect()) {
                    if (!isNameExist(name, null)) {
                        changeNameDialog.enablePositiveBtn(true);

                    } else {
                        tips = getString(R.string.mini_contacts_name_tips_same);
                        changeNameDialog.enablePositiveBtn(false);
                    }
                } else {
                    changeNameDialog.enablePositiveBtn(false);
                }
            } else if (name.contains(" ") || StringUtils.isPun(name)) {
                tips = getString(R.string.mini_contacts_name_tips_blank);
                changeNameDialog.enablePositiveBtn(false);
            } else {
                tips = getString(R.string.mini_contacts_name_tips_chinese);
                changeNameDialog.enablePositiveBtn(false);
            }
        }
        if (TextUtils.isEmpty(tips)) {
            dialogNameTipsView.setVisibility(View.GONE);
        } else {
            dialogNameTipsView.setVisibility(View.VISIBLE);
            dialogNameTipsView.setText(tips);
        }
    }

    private int checkNameValidate(String name) { //检测名字是否合法
        if (TextUtils.isEmpty(name)) {
            return NAME_STATE_BLANK;
        } else if (name.length() > 10) {
            return NAME_STATE_TOO_LONG;
        } else {
            if (StringUtils.onlyChineseOrNumber(name)) {
                if (!isNameExist(name, null)) {
                    return NAME_STATE_SUCCESS;
                } else {
                    return NAME_STATE_SAME;
                }
            } else if (name.contains(" ") || StringUtils.isPun(name)) {
                return NAME_STATE_BLANK;
            } else {
                return NAME_STATE_CHINESE;
            }
        }
    }

    private boolean isNameExist(String name, String namePy) {
        if (TextUtils.isEmpty(namePy)) {
            namePy = ChineseToPinYinUtils.chineseToPinYin(name).toUpperCase();
        }
        if (!contactsMap.isEmpty()) {
            List<Contact> nameContacts = contactsMap.get(namePy.toUpperCase());
            if (!CollectionUtils.isEmpty(nameContacts)) {
                for (Contact nameContact : nameContacts) {
                    if (!nameContact.getName().equals(name)) {
                        return true;
                    }
                }
            }
        }
        if (!CollectionUtils.isEmpty(selectContacts)) {
            for (Contact selectContact : selectContacts) {
                if (selectContact.getNamePy().equals(namePy)) {
                    if (!selectContact.getName().equals(name)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean needDivideLineAdjustToPosition(int position) {
        if (position < 0 || position >= contacts.size()) {
            Log.e("0205", "isLastInGroup: position is wrong ");
            return false;
        }


        int index = position;//减去Header索引, index对应List中的位置

        if (index == contacts.size() - 1) {//最后一个Item，也不需要分割线
            return true;
        }

        String currentTAG = contacts.get(index).getFirstLetterPy().substring(0, 1);
        String nextTAG = contacts.get(index + 1).getFirstLetterPy().substring(0, 1);

        if (currentTAG.equals(nextTAG)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onLetterChanged(String letter, int position, float y) {
        binding.quickSideBarTipsView.setText(letter, position, y);
        // TODO: 2018/1/31 存在问题所以问题，后期需要解决
        mLayoutManager.scrollToPositionWithOffset(position, 0);
    }

    @Override
    public void onLetterTouching(boolean touching) {
        binding.quickSideBarTipsView.setVisibility(touching ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    protected void setConnectFailedLayout(boolean robotConnectState, boolean networkConnectState) {
        super.setConnectFailedLayout(robotConnectState, networkConnectState);
        isCanSubmit(robotConnectState, networkConnectState);
        if (!robotConnectState || !networkConnectState) {
            if (ivColseConnectFailed != null) {
                ivColseConnectFailed.setVisibility(View.VISIBLE);
            }
        }
    }

    private void isCanSubmit(boolean robotConnectState, boolean networkConnectState) {
        if (robotConnectState && networkConnectState) {
            if (!CollectionUtils.isEmpty(selectContacts)) {
                boolean isCanSubmit = true;
                int state = NAME_STATE_SUCCESS;
                for (Contact selectContact : selectContacts) {
                    if (selectContact.isNameInvalide()) {
                        isCanSubmit = false;
                        state = checkNameValidate(selectContact.getName());
                        break;
                    }
                }
                enableImportBtn(isCanSubmit);
                showNameErrorMsg(state);
            } else {
                enableImportBtn(false);
            }
        } else {
            enableImportBtn(false);
        }
    }

    public class EventResponse {


        public void selectAllContacts(View view) {
            isSelectedAll = !isSelectedAll;
            binding.setIsSelectedAll(isSelectedAll);
            if (isSelectedAll) {
                boolean isCanSubmit = true;
                selectContacts.clear();
                int state = NAME_STATE_SUCCESS;
                int firstItemState = state;
                for (Contact contact : contacts) {
                    state = checkNameValidate(contact.getName());
                    if (firstItemState == NAME_STATE_SUCCESS) {
                        firstItemState = state;
                    }
                    if (state != NAME_STATE_SUCCESS) {
                        contact.setNameInvalide(true);
                        isCanSubmit = false;
                    } else {
                        contact.setNameInvalide(false);
                    }
                    if (!contact.isAdded()) {
                        selectContacts.add(contact);
                        contact.setSelected(true);
                    }
                    binding.textContactsTitle.setText(getString(R.string.mini_contacts_choose_contacts_number_title, selectContacts.size()));
                }
                if(CollectionUtils.isEmpty(selectContacts)){
                    isCanSubmit = false;
                    binding.textContactsTitle.setText(R.string.mini_contacts_choose_contact);
                }
                showNameErrorMsg(firstItemState);
                enableImportBtn(isCanSubmit);
            } else {
                binding.textContactsTitle.setText(R.string.mini_contacts_choose_contact);
                selectContacts.clear();
                enableImportBtn(false);
                showNameErrorMsg(NAME_STATE_SUCCESS);
                for (Contact contact : contacts) {
                    contact.setNameInvalide(false);
                    contact.setSelected(false);
                }
            }
            adapter.selectAll(isSelectedAll);
        }

        public void exitImportContacts(View view) {
            finish();
        }

        public void importContact(View view) {
            boolean isCanSubmit = true;
            int invalideCount = 0;
            int position = -1;
            for (Contact selectContact : selectContacts) {
                if(selectContact.isNameInvalide()){
                    isCanSubmit = false;
                    invalideCount ++;
                    if(position < 0){
                        position = contacts.indexOf(selectContact);
                    }
                }
            }
            if(isCanSubmit){
                if (!CollectionUtils.isEmpty(selectContacts)) {
                    doImportContacts(selectContacts);
                }
            }else{
                showErrorDialog(invalideCount,"",position); //TODO
            }

        }
    }

    private void enableImportBtn(boolean enable) {
        if(isNetworkConnectState() && isRobotConnectState()){
            if (enable) {
                showNameErrorMsg(NAME_STATE_SUCCESS);
                binding.btnImport.setEnabled(true);
            } else {
                if(CollectionUtils.isEmpty(selectContacts)){
                    binding.btnImport.setEnabled(false);
                }else{
                    binding.btnImport.setEnabled(true);
                }
            }
        }else{
            binding.btnImport.setEnabled(false);
        }

    }

    private Map<String, Integer> sizeMap;

    private List<String> setSideAlphaList(List<Contact> contacts) {
        List<String> letters = new ArrayList<String>();
        sizeMap = new TreeMap<>();
        for (int i = 0; i < contacts.size(); i++) {
            if (i == 0) {
                letters.add(contacts.get(i).getFirstLetterPy());
                sizeMap.put(contacts.get(i).getFirstLetterPy(), i);//  i + 1 ，这个1 代表的是Header，
                continue;
            }
            if (!contacts.get(i).getFirstLetterPy().equals(contacts.get(i - 1).getFirstLetterPy())) {
                letters.add(contacts.get(i).getFirstLetterPy());
                sizeMap.put(contacts.get(i).getFirstLetterPy(), i);
            }
        }
        return letters;
    }

    private void showNameErrorMsg(int nameState) {
        if (isNetworkConnectState() && isRobotConnectState()) {
            if (connectFailed != null) {
                connectFailed.setVisibility(View.VISIBLE);
                ivColseConnectFailed.setVisibility(View.INVISIBLE);
                switch (nameState) {
                    case NAME_STATE_BLANK:
                        tvConnectedText.setText(getString(R.string.mobile_contacts_name_tips_blank));
                        break;
                    case NAME_STATE_CHINESE:
                        tvConnectedText.setText(getString(R.string.mobile_contacts_name_tips_chinese));
                        break;
                    case NAME_STATE_SUCCESS:
                        connectFailed.setVisibility(View.GONE);
                        break;
                    case NAME_STATE_SAME:
                        tvConnectedText.setText(getString(R.string.mobile_contacts_name_tips_same));
                        break;
                    case NAME_STATE_TOO_LONG:
                        tvConnectedText.setText(getString(R.string.mobile_contacts_name_tips_long));
                        break;
                    default:
                        ivColseConnectFailed.setVisibility(View.VISIBLE);
                        break;
                }
            }
        } else {
            ivColseConnectFailed.setVisibility(View.VISIBLE);
        }
    }

    private void doImportContacts(List<Contact> contacts) {
        showLoadingDialog();
        contactViewModel.importContacts(contacts).observe(this, new Observer<LiveResult>() {
            @Override
            public void onChanged(@Nullable LiveResult liveResult) {
                switch (liveResult.getState()) {
                    case SUCCESS:
                        dismissDialog();
                        toastSuccess(getString(R.string.sync_success));
                        finish(true);
                        break;
                    case FAIL:
                        dismissDialog();
                        liveResult.removeObserver(this);
                        handleError(liveResult.getErrorCode());
                        break;
                }
            }
        });
    }

    private void handleError(int code) {
        switch (code) {
            case CONTACT_ERROR_INVALID_PARAMS:
                toastError(getString(R.string.params_error));
                break;
            case CONTACT_ERROR_OPTION_CONFILCT:
                toastError(getString(R.string.op_conflict));
                break;
            default:
                toastError(getString(R.string.op_fail));
                break;
        }
    }

    public void finish(boolean changed) {
        Intent intent = new Intent();
        intent.putExtra(CHANGED, changed);
        setResult(0, intent);
        finish();
    }

    private void showErrorDialog(int count, String msg, int position) {
        if (errorDialog == null) {
            errorDialog = new MaterialDialog(this);
            errorDialog.setTitle(R.string.please_change_concat_name);
            errorDialogBtnListener = new DialogModifyBtnListener(position);
            errorDialog.setPositiveButton(R.string.go_to_modify, errorDialogBtnListener);
        }
        errorDialogBtnListener.setPosition(position);
        errorDialog.setMessage(getString(R.string.contact_name_error_tips2));
        errorDialog.show();
    }

    private class DialogModifyBtnListener implements View.OnClickListener{

        private int position;

        public DialogModifyBtnListener(int position){
            this.position = position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            errorDialog.dismiss();
            mLayoutManager.scrollToPositionWithOffset(position,0);
        }
    }
}
