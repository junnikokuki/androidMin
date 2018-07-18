package com.ubtechinc.alpha.mini.ui.friend;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubtech.utilcode.utils.KeyboardUtils;
import com.ubtech.utilcode.utils.StringUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseToolbarActivity;
import com.ubtechinc.alpha.mini.databinding.ActivityMiniFriendnameModifyBinding;
import com.ubtechinc.alpha.mini.ui.PageRouter;


/**
 * @Date: 2017/12/19.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 更改机器人好友名称
 */

public class InputMiniFriendNameActivity extends BaseToolbarActivity {

    private ActivityMiniFriendnameModifyBinding binding;

    private String friendName;


    private Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mini_friendname_modify);
        binding.setPresenter(new Presenter());
        init();
    }

    private void init() {
        initToolbar(binding.toolbar, "", View.GONE, false);
        initConnectFailedLayout(R.string.face_detect_mobile_input_network_error_hint, R.string.face_detect_mobile_input_offline_hint);
        needDivideLine(false);
        friendName = getIntent().getStringExtra(PageRouter.FRIEND_NAME);

        boolean isValidate = checkInputNameValidate(friendName);
        checkDoNext(isValidate);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                KeyboardUtils.showSoftInput(binding.etFriendName);
            }
        },500);
    }

    @Override
    protected void setConnectFailedLayout(boolean robotConnectState, boolean networkConnectState) {
        checkDoNext(checkInputNameValidate(friendName));
        super.setConnectFailedLayout(robotConnectState, networkConnectState);
    }

    private void checkDoNext(boolean isValidate) {
        if (!getNetworkConnectState() || !getRobotConnect() ||!isValidate){
            binding.nextStep.setEnabled(false);
        }else{
            binding.nextStep.setEnabled(true);
        }
    }


    private boolean checkInputNameValidate(String content){
        boolean flag = false;
        if (TextUtils.isEmpty(content)) {
            binding.setFriendNameValidate(true);
            binding.setHintContent(getString(R.string.face_detect_modify_hint));
            return flag;
        }
        if (content.length() > 20) {//大于20的时候是不能保存的
            binding.setFriendNameValidate(false);
            binding.setHintContent(getString(R.string.face_detect_modify_name_limit_hint, content.length()));
            return flag;
        }
        if (content.trim().length() != content.length()){
            binding.setFriendNameValidate(false);
            binding.setHintContent(getString(R.string.face_detect_modify_name_no_space));
            return flag;
        }
        if (!StringUtils.onlyChineseOrEnglish(content)){
            binding.setFriendNameValidate(false);
            binding.setHintContent(getString(R.string.face_detect_modify_name_limit_language));
            return flag;
        }
        flag = true;
        binding.setFriendNameValidate(true);
        binding.setHintContent(getString(R.string.face_detect_modify_hint));
        return flag;
    }

    @Override
    public void onBack() {
        KeyboardUtils.hideSoftInput(InputMiniFriendNameActivity.this);
        super.onBackPressed();
    }



//    private void showHintDialog(@StringRes int resId){
//        final MaterialDialog materialDialog = new MaterialDialog(this);
//        materialDialog.setMessage(resId).setPositiveButton(R.string.i_know, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                materialDialog.dismiss();
//            }
//        }).show();
//    }

    //响应事件
    public class Presenter{
        public void onTextChanged(CharSequence charSequence, int start, int before, int count){
            friendName = charSequence.toString();
            if (TextUtils.isEmpty(friendName)){
                binding.setHasContent(false);
            }else{
                binding.setHasContent(true);
            }
            boolean isValidate = checkInputNameValidate(charSequence.toString());
            checkDoNext(isValidate);
        }



        public void clearContent(View view){
            binding.etFriendName.setText("");
        }

        public void nextStep(View view){
            KeyboardUtils.hideSoftInput(InputMiniFriendNameActivity.this);
            PageRouter.toFaceDetectActivity(InputMiniFriendNameActivity.this, friendName);
            InputMiniFriendNameActivity.this.finish();
        }
    }

}
