package com.ubtechinc.alpha.mini.ui.friend;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.alpha.CmFaceDelete;
import com.ubtechinc.alpha.im.IMCmdId;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseToolbarActivity;
import com.ubtechinc.alpha.mini.databinding.ActivityMiniFriendInfoBinding;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.ui.PageRouter;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.ubtechinc.alpha.mini.im.Phone2RobotMsgMgr;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtechinc.nets.phonerobotcommunite.ICallback;

/**
 * @Date: 2017/12/19.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] :机器人好友信息详情页面(好友头像，好友姓名)
 */
public class MiniFriendInfoActivity extends BaseToolbarActivity {

    private ActivityMiniFriendInfoBinding binding;
    private FriendFaceInfo friend;

    private static final int REQUEST_CODE = 10000;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mini_friend_info);
        binding.setPresent(new Presenter());
        initView();
    }

    private void initView() {
        initToolbar(binding.toolbar, getString(R.string.friend_info_title), View.GONE, false);
        initConnectFailedLayout(R.string.face_detect_mobile_modify_network_error_hint,R.string.face_detect_mobile_modify_offline_hint );
        friend = getIntent().getParcelableExtra(PageRouter.FRIEND_INFO);
        setFriendName(friend.getName());
    }

    private void setFriendName(String name) {
        if (!TextUtils.isEmpty(name) && name.length() > 6){
            StringBuffer buffer = new StringBuffer();
            buffer.append(name.substring(0, 6)).append("...");
            Log.i(TAG, "setFriendName: buffer : " + buffer.toString());
            binding.tvFriendName.setText(buffer.toString());
        }else{
            Log.i(TAG, "setFriendName:  " + name);
            binding.tvFriendName.setText(name);
        }
    }
    @Override
    protected void setConnectFailedLayout(boolean robotConnectState, boolean networkConnectState) {
        setDeleteInfoButtonClickable(robotConnectState, networkConnectState);
        super.setConnectFailedLayout(robotConnectState, networkConnectState);
    }

    private void setDeleteInfoButtonClickable(boolean robotConnectState, boolean networkConnectState) {
        binding.setNetworkEnable(networkConnectState);
        binding.setRobotEnable(robotConnectState);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            String friendName =  data.getStringExtra(PageRouter.FRIEND_NAME);
            friend.setName(friendName);
            setFriendName(friendName);
        }
    }

    @Override
    protected void onBack() {
        setResult(RESULT_OK, new Intent().putExtra(PageRouter.FRIEND_NAME, friend.getName()));
        super.onBack();
    }

    public void delteFirendFace(){
        CmFaceDelete.CmFaceDeleteRequest.Builder request = CmFaceDelete.CmFaceDeleteRequest.newBuilder();
        request.addIds(friend.getId());
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_FACE_DELETE_REQUEST, IMCmdId.IM_VERSION, request.build(),
                MyRobotsLive.getInstance().getRobotUserId(), new ICallback<CmFaceDelete.CmFaceDeleteResponse>(){

                    @Override
                    public void onSuccess(final CmFaceDelete.CmFaceDeleteResponse data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i(TAG, "getResultCode: " + data.getResultCode());
                                if (data.getResultCode() == 0){
                                    setResult(AlphaMiniFriendActivity.RESULT_DELETE_FACEINFO);
                                    finish();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(ThrowableWrapper e) {
                        e.printStackTrace();
                        ToastUtils.showShortToast("删除失败!!");
                    }
                });
    }

    private void showDisconnectedWithRobotDialog(@StringRes int stringResId){
        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.setMessage(stringResId).setPositiveButton(R.string.i_know, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        }).show();
    }

    public class Presenter{
        public void modifyAvatar(View view){
            if (true){// TODO: 2018/1/19  暂时没有加入迭代中
                return;
            }
            if (!getNetworkConnectState()){
                showDisconnectedWithRobotDialog(R.string.face_detect_mobile_modify_network_error_hint);
            }else if(!getRobotConnect()){
                showDisconnectedWithRobotDialog(R.string.face_detect_mobile_modify_offline_hint);
            }else{
                PageRouter.toModifyFriendAvatar(MiniFriendInfoActivity.this);
            }
        }

        public void modifyName(View view){
            if (!getNetworkConnectState()){
                showDisconnectedWithRobotDialog(R.string.face_detect_mobile_modify_network_error_hint);
            }else if(!getRobotConnect()){
                showDisconnectedWithRobotDialog(R.string.face_detect_mobile_modify_offline_hint);
            }else{
                PageRouter.toModifyFriendName(MiniFriendInfoActivity.this, friend, REQUEST_CODE);
            }
        }

        public void deleteFriend(View view){
            if (!getNetworkConnectState()){
                showDisconnectedWithRobotDialog(R.string.face_detect_mobile_modify_network_error_hint);
            }else if(!getRobotConnect()){
                showDisconnectedWithRobotDialog(R.string.face_detect_mobile_modify_offline_hint);
            }else{
                final MaterialDialog dialog = new MaterialDialog(MiniFriendInfoActivity.this);
                dialog.setTitle(R.string.friend_info_delete_friend).setMessage(R.string.friend_info_delete_one_friend)
                        .setPositiveButton(R.string.common_button_delete, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                delteFirendFace();
                                dialog.dismiss();
                            }
                        }).setNegativeButton(R.string.common_btn_cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                }).show();
            }
        }
    }
}
