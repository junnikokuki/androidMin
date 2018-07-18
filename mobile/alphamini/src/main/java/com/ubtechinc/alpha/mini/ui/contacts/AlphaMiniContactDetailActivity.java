package com.ubtechinc.alpha.mini.ui.contacts;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.ubtech.utilcode.utils.KeyboardUtils;
import com.ubtech.utilcode.utils.StringUtils;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseToolbarActivity;
import com.ubtechinc.alpha.mini.databinding.ActivityContactDetailBinding;
import com.ubtechinc.alpha.mini.entity.Contact;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.viewmodel.ContactViewModel;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;

import static com.ubtechinc.alpha.mini.constants.Constants.CONTACT_ERROR_INVALID_PARAMS;
import static com.ubtechinc.alpha.mini.constants.Constants.CONTACT_ERROR_OPTION_CONFILCT;


/**
 * @Date: 2018/1/30.
 * @Author: shiyi.wu
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 机器人通讯录详情页面
 */

public class AlphaMiniContactDetailActivity extends BaseToolbarActivity {

    public static final int RESULT_CODE = 1;
    public static final int REQUEST_CODE = 2;

    public static final int STATE_NORMAL = 1;
    public static final int STATE_CREATE = 2;
    public static final int STATE_EDIT = 3;
    public static final int STATE_DELETE = 4;

    public static final String CONTACT_KEY = "contact";
    public static final String STATE_KEY = "state";

    private ActivityContactDetailBinding binding;

    private ContactViewModel contactViewModel;

    private int state;

    private Contact contact;

    MaterialDialog confirmDeleteDialog;

    private MaterialDialog errorDialog;

    private boolean isAdd;//是否添加操作
    private boolean isChanged; //是否有发生改变

    private int[] colorBgRes = {
            R.drawable.shape_contacts_blue_bg, R.drawable.shape_contacts_green_bg, R.drawable.shape_contacts_puper_bg,
            R.drawable.shape_contacts_red_bg, R.drawable.shape_contacts_yello_bg,
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contactViewModel = new ContactViewModel();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contact_detail);
        initView();
        initData();
    }

    private void initView() {
        Intent intent = getIntent();
        contact = (Contact) intent.getSerializableExtra(CONTACT_KEY);
        state = intent.getIntExtra(STATE_KEY, STATE_NORMAL);
        binding.setState(state);
        if (state == STATE_NORMAL) {
            showDetail();
        } else {
            showCreate();
        }
        initConnectFailedLayout(R.string.mini_contacts_network_error, R.string.mini_contacts_network_robot_connect_error);
        //toolbar
        if(contact != null && contact.getName() != null){
            binding.textName.setBackgroundResource(colorBgRes[Math.abs(contact.getName().hashCode())%5]);
        }
        binding.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteData(contact);
            }
        });
        binding.etContactName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG, "beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                nameValidate(editable.toString());
            }
        });
        binding.contactPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                nameValidate(binding.etContactName.getText().toString());
            }
        });
    }

    private void nameValidate(String name) {
        String tips = null;
        if (TextUtils.isEmpty(name)) {
            enableSubmitBtn(false);
        } else if (name.length() > 10) {
            enableSubmitBtn(false);
            tips = getString(R.string.mini_contacts_name_tips_long, name.length());
        } else {
            if (StringUtils.onlyChineseOrNumber(name)) {
                if (getNetworkConnectState() && getRobotConnect()) {
                    if (!isNameExist(name)  ) {
                        if(binding.contactPhone.getText().length() !=0){
                            enableSubmitBtn(true);
                        }else{
                            enableSubmitBtn(false);
                        }
                    } else {
                        tips = getString(R.string.mini_contacts_name_tips_same);
                        enableSubmitBtn(false);
                    }
                } else {
                    enableSubmitBtn(false);
                }
            } else if (name.contains(" ") || StringUtils.isPun(name)) {
                tips = getString(R.string.mini_contacts_name_tips_blank);
                enableSubmitBtn(false);
            } else {
                tips = getString(R.string.mini_contacts_name_tips_chinese);
                enableSubmitBtn(false);
            }
        }
        binding.setNameTips(tips);
    }

    private boolean isNameExist(String name) {
        return contactViewModel.isNameExist(name);
    }

    private void initData() {
        refreshData(contact);
    }

    private void refreshData(Contact contact) {
        binding.setContact(contact);
    }

    private void deleteData(Contact contact) {
        if (confirmDeleteDialog == null) {
            confirmDeleteDialog = new MaterialDialog(this);
        }
        confirmDeleteDialog.setMessage(R.string.sure_to_delete_contact);
        confirmDeleteDialog.setPositiveButton(R.string.delete, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDeleteDialog.dismiss();
                doDelete();
            }
        });
        confirmDeleteDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDeleteDialog.dismiss();
            }
        });
        confirmDeleteDialog.show();
    }

    private void doDelete(){
        showLoadingDialog();
        LiveResult result = contactViewModel.delete(contact);
        result.observe(this, new Observer<LiveResult>() {
            @Override
            public void onChanged(@Nullable LiveResult o) {
                switch (o.getState()) {
                    case SUCCESS:
                        dismissDialog();
                        o.removeObserver(this);
                        contact.setDeleted(1);
                        if(isAdd){
                            isChanged = false;
                        }else{
                            isChanged = true;
                        }
                        finish();
                        break;
                    case FAIL:
                        dismissDialog();
                        o.removeObserver(this);
                        handleError(o.getErrorCode());
                        break;
                }
            }
        });
    }

    private void switchState(int newState) {
        binding.setState(newState);
        switch (newState) {
            case STATE_NORMAL:
                showDetail();
                break;
            case STATE_CREATE:
                showCreate();
                break;
            case STATE_EDIT:
                showEdit();
                break;
            default:
                showDetail();
                break;
        }
    }

    private void modifyData(final Contact contact) {
        LiveResult result = contactViewModel.modify(contact);
        showLoadingDialog();
        result.observe(this, new Observer<LiveResult>() {
            @Override
            public void onChanged(@Nullable LiveResult o) {
                switch (o.getState()) {
                    case SUCCESS:
                        dismissDialog();
                        o.removeObserver(this);
                        switchState(STATE_NORMAL);
                        toastSuccess(getString(R.string.sync_success));
                        binding.setContact(contact);
                        isChanged = true;
                        binding.textName.setBackgroundResource(colorBgRes[Math.abs(contact.getName().hashCode())%5]);
                        break;
                    case FAIL:
                        dismissDialog();
                        o.removeObserver(this);
                        handleError(o.getErrorCode());
                        break;
                }
            }
        });
    }

    private void saveData(final Contact contact) {
        LiveResult result = contactViewModel.add(contact);
        showLoadingDialog();
        result.observe(this, new Observer<LiveResult>() {
            @Override
            public void onChanged(@Nullable LiveResult o) {
                switch (o.getState()) {
                    case SUCCESS:
                        dismissDialog();
                        o.removeObserver(this);
                        switchState(STATE_NORMAL);
                        toastSuccess(getString(R.string.sync_success));
                        binding.setContact(contact);
                        isChanged = true;
                        break;
                    case FAIL:
                        dismissDialog();
                        o.removeObserver(this);
                        handleError(o.getErrorCode());
                        break;
                }
            }
        });
    }

    private void handleError(int code){
        switch (code) {
            case CONTACT_ERROR_INVALID_PARAMS:
                showErrorDialog(getString(R.string.params_error));
                break;
            case CONTACT_ERROR_OPTION_CONFILCT:
                showErrorDialog(getString(R.string.op_conflict));
                break;
            default:
                showErrorDialog(getString(R.string.op_fail));
                break;
        }
    }

    private void showErrorDialog(String msg){
        if (errorDialog == null) {
            errorDialog = new MaterialDialog(this);
            errorDialog.setTitle(msg);
            errorDialog.setPositiveButton(R.string.i_know, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    errorDialog.dismiss();
                }
            });
        }
        errorDialog.show();
    }

    private void showDetail() {
        initToolbar(toolbar, "", View.VISIBLE, true);
        actionBtn.setImageResource(R.drawable.friend_edit_btn);
        actionBtn.setTag(STATE_NORMAL);
        KeyboardUtils.hideSoftInput(this);
        backBtn.setImageResource(R.drawable.selector_ic_top_back);
        backBtn.setTag(STATE_NORMAL);
    }

    private void showEdit() {
        initToolbar(toolbar, getString(R.string.mini_contacts_edit_contact), View.VISIBLE, true);
        actionBtn.setImageResource(R.drawable.toolbar_confirm_selector);
        actionBtn.setTag(STATE_EDIT);
        enableSubmitBtn(false);
//        showSoftInput();
        backBtn.setImageResource(R.drawable.selector_simple_ic_top_cancel);
        backBtn.setTag(STATE_EDIT);
    }

    private void showCreate() {
        initToolbar(toolbar, getString(R.string.mini_contacts_new_contact), View.VISIBLE, true);
        actionBtn.setImageResource(R.drawable.toolbar_confirm_selector);
        actionBtn.setTag(STATE_CREATE);
        enableSubmitBtn(false);
        isAdd = true;
        showSoftInput();
        backBtn.setImageResource(R.drawable.selector_simple_ic_top_cancel);

    }

    private void showSoftInput(){
        binding.etContactName.postDelayed(new Runnable() {
            @Override
            public void run() {
                KeyboardUtils.showSoftInput(binding.etContactName);
            }
        }, 600);
    }

    private void enableEditBtn(boolean enable) {
        actionBtn.setEnabled(enable);
        if (enable) {
            actionBtn.setAlpha(1f);
        } else {
            actionBtn.setAlpha(0.3f);
        }
    }

    private void enableSubmitBtn(boolean enable) {
        actionBtn.setEnabled(enable);
        if (enable) {
            actionBtn.setAlpha(1f);
        } else {
            actionBtn.setAlpha(0.3f);
        }
    }

    private void enableDeleteBtn(boolean enable) {
        binding.deleteBtn.setEnabled(enable);
        if (enable) {
            binding.deleteBtn.setAlpha(1f);
        } else {
            binding.deleteBtn.setAlpha(0.3f);
        }
    }

    private void disableAll() {
        enableEditBtn(false);
        enableSubmitBtn(false);
        enableDeleteBtn(false);
    }

    @Override
    protected void setConnectFailedLayout(boolean robotConnectState, boolean networkConnectState) {
        super.setConnectFailedLayout(robotConnectState,networkConnectState);
        if(!robotConnectState || !networkConnectState){
            disableAll();
        }else{
            enableDeleteBtn(true);
            if(actionBtn != null && actionBtn.getTag()!= null){
                if (((int)actionBtn.getTag())== STATE_NORMAL) {
                    enableEditBtn(true);
                }else{
                    nameValidate(binding.etContactName.getText().toString());
                }
            }else{
                enableEditBtn(true);
            }
        }
    }

    @Override
    protected void onAction(View view) {
        int tag = (int) view.getTag();
        switch (tag) {
            case STATE_CREATE:
                contact = new Contact();
                if (checkContactValite()){
                    saveData(contact);
                }
                break;
            case STATE_EDIT:
                if (checkContactValite()){
                    modifyData(contact);
                }
                break;
            case STATE_NORMAL:
                switchState(STATE_EDIT);
                break;
        }
    }

    private boolean checkContactValite() {
        String phone = binding.contactPhone.getText().toString();
        if (StringUtils.isLong(phone)){
            contact.setPhone(binding.contactPhone.getText().toString());
        }else{
            ToastUtils.showLongToast(R.string.invalite_phone);
            return false;
        }
        contact.setName(binding.etContactName.getText().toString());
        return true;
    }

    @Override
    protected void onBack() {
        Object o = actionBtn.getTag();
        int tag = STATE_NORMAL;
        if(o != null){
            tag = (int) o;
        }
        if(tag == STATE_EDIT){
            if(contact != null){
                binding.setContact(contact);
            }
            switchState(STATE_NORMAL);
        }else{
            super.onBack();
        }
    }

    @Override
    public void onBackPressed() {
        onBack();
    }

    @Override
    public void finish() {
        KeyboardUtils.hideSoftInput(this);
        if(isChanged){
            Intent intent = new Intent();
            intent.putExtra(CONTACT_KEY, contact);
            setResult(state, intent);
        }
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
