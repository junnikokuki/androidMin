package com.ubtechinc.alpha.mini.ui.friend;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.ubtech.utilcode.utils.KeyboardUtils;
import com.ubtech.utilcode.utils.StringUtils;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.alpha.CmFaceUpdate;
import com.ubtechinc.alpha.im.IMCmdId;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseToolbarActivity;
import com.ubtechinc.alpha.mini.databinding.ActivityModifyNameBinding;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.event.FriendListChangeEvent;
import com.ubtechinc.alpha.mini.ui.PageRouter;
import com.ubtechinc.alpha.mini.im.Phone2RobotMsgMgr;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtechinc.nets.phonerobotcommunite.ICallback;

import org.greenrobot.eventbus.EventBus;

/**
 * @Date: 2017/12/20.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 修改名称
 */

public class ModifyFriendNameActivity extends BaseToolbarActivity {

    /**传入的人脸信息**/
    private FriendFaceInfo friendFaceInfo;

    /**传入名字名称**/
    private String originName;

    /**当前输入框里面的名称**/
    private String friendName;

    private ActivityModifyNameBinding binding;
    private Handler mHandler;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_modify_name);
        binding.setPresenter(new Presenter());
        mHandler = new Handler();
        initView();
    }

    private void initView() {
        initToolbar(binding.getRoot().findViewById(R.id.toolbar), getString(R.string.friend_modify_name), View.VISIBLE, true);
        initConnectFailedLayout(R.string.face_detect_mobile_modify_network_error_hint, R.string.face_detect_mobile_modify_offline_hint);

        backBtn.setImageResource(R.drawable.close_btn_selector);
        actionBtn.setImageResource(R.drawable.ic_top_confirm);
        friendFaceInfo = getIntent().getParcelableExtra(PageRouter.FRIEND_INFO);
        originName = (friendName = friendFaceInfo.getName());
        boolean isValidate = checkNameValidate(originName);
        checkDoNext(isValidate);
        if (!TextUtils.isEmpty(originName)){
            binding.etFriendName.setText(originName);
            binding.etFriendName.setSelection(originName.length());
            binding.setHasContent(true);
        }else{
            binding.setHasContent(false);
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                KeyboardUtils.showSoftInput(binding.etFriendName);
            }
        }, 500);
    }

    private void checkDoNext(boolean isValidate) {
        if (!getNetworkConnectState() || !getRobotConnect()){
            actionBtn.setClickable(false);
            actionBtn.setAlpha(0x33);
            return;
        }

        if (TextUtils.isEmpty(friendName) || friendName.equals(originName) || !isValidate){
            actionBtn.setClickable(false);
            actionBtn.setAlpha(0x33);
        }else{
            actionBtn.setClickable(true);
            actionBtn.setAlpha(0xFF);
        }
    }

    @Override
    protected void setConnectFailedLayout(boolean robotConnectState, boolean networkConnectState) {
        checkDoNext(checkNameValidate(friendName));
        super.setConnectFailedLayout(robotConnectState, networkConnectState);
    }

    @Override
    public void onBack() {
        KeyboardUtils.hideSoftInput(this);
        super.onBackPressed();
    }

    @Override
    protected void onAction(View view) {
        KeyboardUtils.hideSoftInput(this);
        CmFaceUpdate.CmFaceUpdateRequest.Builder request = CmFaceUpdate.CmFaceUpdateRequest.newBuilder();
        request.setName(friendName);
        request.setId(friendFaceInfo.getId());
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_FACE_UPDATE_REQUEST, IMCmdId.IM_VERSION, request.build(),
                MyRobotsLive.getInstance().getRobotUserId(), new ICallback<CmFaceUpdate.CmFaceUpdateResponse>(){

                    @Override
                    public void onSuccess(final CmFaceUpdate.CmFaceUpdateResponse data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (data.getResultCode() == 0){
                                    ToastUtils.showShortToast("修改成功");
                                    setResult(RESULT_OK, new Intent().putExtra(PageRouter.FRIEND_NAME, friendName));
                                    EventBus.getDefault().post(new FriendListChangeEvent(FriendListChangeEvent.FRIEND_INFO_CHANGED));
                                    finish();
                                }else{
                                    ToastUtils.showShortToast("修改失败");
                                }
                            }
                        });

                    }

                    @Override
                    public void onError(ThrowableWrapper e) {
                        ToastUtils.showShortToast("修改失败");
                    }
                });
    }

    private boolean checkNameValidate(String content) {
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


    public class Presenter{
        public void onTextChanged(CharSequence charSequence, int start, int before, int count){
            Log.d(TAG, "onTextChanged: ");
            friendName = charSequence.toString();
            if (TextUtils.isEmpty(friendName)){
                binding.setHasContent(false);
            }else{
                binding.setHasContent(true);
            }
            boolean isValidated = checkNameValidate(friendName);
            checkDoNext(isValidated);
        }



        public void clearContent(View view){
            binding.etFriendName.setText("");
        }
    }
}
