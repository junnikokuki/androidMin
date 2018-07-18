package com.ubtechinc.alpha.mini.avatar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.alpha.mini.avatar.entity.User;
import com.ubtechinc.alpha.mini.avatar.viewutils.AvatarUserListManger;
import com.ubtechinc.alpha.mini.avatar.viewutils.ControlPanelHUtil;
import com.ubtechinc.alpha.mini.avatar.viewutils.MaskLayHUtil;
import com.ubtechinc.alpha.mini.avatar.viewutils.OnMaskClickListener;
import com.ubtechinc.alpha.mini.avatar.viewutils.VideoPanelUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.ubtechinc.alpha.mini.avatar.AvatarStateManager.State.CONNECTFAILED;
import static com.ubtechinc.alpha.mini.avatar.AvatarStateManager.State.NETWORK_ERROR;
import static com.ubtechinc.alpha.mini.avatar.AvatarStateManager.State.NORMAL;


public class AvatarFragmentH extends AvatarBaseFragment implements VideoPanelUtil.VideoPanelListener,
        AvatarControlListener, AvatarStateManager.IAvatarStateListener, OnMaskClickListener,AvatarUserListManger.IAvatarUserChangeListener {

    private static final int MIC_OPEN_HINT_EVENT = 1000;
    private AvatarActivity avatarActivity;

    private VideoPanelUtil videoPanelUtil;

    private ControlPanelHUtil controlPanelHUtil;

    private MaskLayHUtil layHUtil;

    private ImageView ivOne;
    private ImageView ivTwo;
    private ImageView ivThree;
    private ImageView ivFour;
    private ImageView ivFive;
    private ImageView ivSix;
    private TextView  tvCount;

    private List<ImageView> avatarCount = new ArrayList<>();

    private MyHandler myHandler;


    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_avatar_h, null);
        layHUtil = new MaskLayHUtil(view);
        AvatarStateManager.State state = ((AvatarActivity)getActivity()).getManager().getState();
        layHUtil.setOnMaskClickListener(this);
        currentState(state);
        initAvatar(view);
        return view;
    }

    private void initAvatar(View view) {
        ivOne = view.findViewById(R.id.iv_user_one_h);
        ivTwo = view.findViewById(R.id.iv_user_two_h);
        ivThree = view.findViewById(R.id.iv_user_three_h);
        ivFour = view.findViewById(R.id.iv_user_four_h);
        ivFive = view.findViewById(R.id.iv_user_five_h);
        ivSix = view.findViewById(R.id.iv_user_six_h);
        avatarCount.add(ivOne);
        avatarCount.add(ivTwo);
        avatarCount.add(ivThree);
        avatarCount.add(ivFour);
        avatarCount.add(ivFive);
        avatarCount.add(ivSix);

        tvCount = view.findViewById(R.id.tv_user_count_h);
    }


    @Override
    public void onStart() {
        AvatarUserListManger.get().setIAvatarUserChangeListener(this);
        if (avatarActivity != null){
            boolean enable = avatarActivity.getCurrentMicState();
            if (enable){
                controlPanelHUtil.setMicEnableDrawble();
            }else{
                controlPanelHUtil.setMicDisbleDrawble();
            }
        }
        super.onStart();
    }

    @Override
    public void onStop() {
        AvatarUserListManger.get().removeAvatarUserChangeListener();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        videoContainer.removeView(videoView);
        videoView = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        avatarActivity = (AvatarActivity) getActivity();
        myHandler = new MyHandler(this);
        if (videoPanelUtil == null) {
            videoPanelUtil = new VideoPanelUtil(avatarActivity, this);
        }

        if (controlPanelHUtil == null) {
            controlPanelHUtil = new ControlPanelHUtil(avatarActivity, this);
        }

        if (videoView != null) {
            videoPanelUtil.setVideoView(videoView);
        }
        controlPanelHUtil.setMicState(rtcManager.micEnable);
    }


    @Override
    public View getHeaderControl() {
        return controlPanelHUtil.getHeadController();
    }

    @Override
    public View getWalkControl() {
        return controlPanelHUtil.getWalkController();
    }



    @Override
    public void doStand() {
        ((AvatarActivity)getActivity()).robotStand();
        controlPanelHUtil.disableAllControlPanel();
    }


    @Override
    public void onStandCommandReponse() {
        controlPanelHUtil.enableAllControlPanel();
    }


    @Override
    public void doTakePhoto() {
        videoPanelUtil.screenshot();
    }

    @Override
    public void doMicStatusChange() {
        rtcManager.micStatusSwitch();
    }

    @Override
    public void changeMicState(boolean enable) {
        super.changeMicState(enable);
        controlPanelHUtil.setMicState(enable);
        if (enable){
            showToastMessage(getString(R.string.robot_mic_enable));
            myHandler.sendEmptyMessageDelayed(MIC_OPEN_HINT_EVENT, 3000);
            controlPanelHUtil.setMicEnableDrawble();
        }else{
            showToastMessage(getString(R.string.robot_mic_disable));
            controlPanelHUtil.setMicDisbleDrawble();
        }
    }

    @Override
    public void onAudioVolumeIndication(int totalVolume) {
        if(controlPanelHUtil != null){
            controlPanelHUtil.volumeIndication(totalVolume);
        }
    }

    @Override
    public void currentState(AvatarStateManager.State state) {
        if (layHUtil == null){
            Log.i(TAG, "currentState: layHUtil is null" );
            return;
        }
        switch (state) {
            case NORMAL:
                layHUtil.close();
                break;
            case CONNECTING:
                layHUtil.showLoading();
                break;
            case CONNECTFAILED:
                layHUtil.connectFailed();
                break;
            case NETWORK_SUCK:
                layHUtil.networkSuck();
                break;
            case START_FAILED:
                layHUtil.connectFailed();
                break;
            case NETWORK_ERROR:
                layHUtil.networkError();
                break;
        }

        if (controlPanelHUtil == null){
            return;
        }
        if (NORMAL == state){
            controlPanelHUtil.enableAllControlPanel();
        }else{
            controlPanelHUtil.disableAllControlPanel();
        }
    }

    @Override
    public void doStart() {
        ((AvatarActivity)getActivity()).doRetry();
    }

    @Override
    public void onGetAllUser(List<String> onlineUsers) {
        if (onlineUsers == null || onlineUsers.size() == 0){
            return;
        }

        List<User> bindUsers = AvatarUserListManger.get().getBindUsers();
        if (bindUsers == null || bindUsers.size() == 0){
            return;
        }

        if (getActivity() == null){
            return;
        }

        hideAllUserAvatar();

        showOnlineUserAvatar(onlineUsers, bindUsers);

    }

    private void hideAllUserAvatar(){
        for (ImageView imageView : avatarCount){
            imageView.setVisibility(View.GONE);
        }
        if (tvCount != null){
            tvCount.setVisibility(View.GONE);
        }
    }

    private void showOnlineUserAvatar(List<String> onlineUsers, List<User> bindUsers){
        for (int i = 0; i < onlineUsers.size(); i++){
            if (avatarCount != null){
                avatarCount.get(i).setVisibility(View.VISIBLE);
            }
            for (int j = 0; j < bindUsers.size() ; j++){
                if (!TextUtils.isEmpty(onlineUsers.get(i))  && onlineUsers.get(i).equalsIgnoreCase(bindUsers.get(j).getId())){
                    setAvatarUrl(i, bindUsers.get(j).getAvtar());
                    continue;
                }
            }
        }

        if(tvCount != null && onlineUsers.size() != 0){
            tvCount.setVisibility(View.VISIBLE);
            tvCount.setText(getString(R.string.robot_avatar_user_number, onlineUsers.size()));
        }

        //如果只有自己在线，那么不显示
        if (onlineUsers.size() == 1 && onlineUsers.get(0).equalsIgnoreCase(AvatarUserListManger.get().getCurrentUserId())){
            tvCount.setVisibility(View.GONE);
            avatarCount.get(0).setVisibility(View.GONE);
        }
    }

    private void setAvatarUrl(int position, String url) {
        switch (position){
            case 0:
                Glide.with(getActivity()).load(url).bitmapTransform(new CropCircleTransformation(getActivity())).crossFade(1000).into(ivOne);
                break;
            case 1:
                Glide.with(getActivity()).load(url).bitmapTransform(new CropCircleTransformation(getActivity())).crossFade(1000).into(ivTwo);
                break;
            case 2:
                Glide.with(getActivity()).load(url).bitmapTransform(new CropCircleTransformation(getActivity())).crossFade(1000).into(ivThree);
                break;
            case 3:
                Glide.with(getActivity()).load(url).bitmapTransform(new CropCircleTransformation(getActivity())).crossFade(1000).into(ivFour);
                break;
            case 4:
                Glide.with(getActivity()).load(url).bitmapTransform(new CropCircleTransformation(getActivity())).crossFade(1000).into(ivFive);
                break;
            case 5:
                Glide.with(getActivity()).load(url).bitmapTransform(new CropCircleTransformation(getActivity())).crossFade(1000).into(ivSix);
                break;
        }
    }

    @Override
    public void onGetEnterUser(List<String> enterUsers) {
        if (enterUsers == null || enterUsers.size() == 0){
            return;
        }
        if (getActivity() == null){
            return;
        }
        List<User> bindUsers = AvatarUserListManger.get().getBindUsers();
        if (bindUsers == null || bindUsers.size() == 0){
            return;
        }
        for (User user : bindUsers){
            if (user.getId().equalsIgnoreCase(enterUsers.get(0)) && !user.isEnterAlreadyShow()){
                showToastMessage(getString(R.string.robot_get_in_room,user.getName()));
                user.setEnterAlreadyShow(true);
                user.setExitAlreadyShow(false);
                continue;
            }
        }
    }

    @Override
    public void onGetLeaveUser(List<String> leaveUser) {
        if (leaveUser == null || leaveUser.size() == 0){
            return;
        }
        List<User> bindUsers = AvatarUserListManger.get().getBindUsers();
        if (bindUsers == null || bindUsers.size() == 0){
            return;
        }

        for (User user : bindUsers){
            if (user.getId().equalsIgnoreCase(leaveUser.get(0)) && !user.isExitAlreadyShow()){
                showToastMessage(getString(R.string.robot_left_room, user.getName()));
                user.setExitAlreadyShow(true);
                user.setEnterAlreadyShow(false);
                continue;
            }
        }
    }


    private static class MyHandler extends Handler{

        private final WeakReference<AvatarFragmentH> mFragment;

        public MyHandler(AvatarFragmentH fragment){
            mFragment = new WeakReference<AvatarFragmentH>(fragment);
        }


        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MIC_OPEN_HINT_EVENT:
                    AvatarFragmentH fragmentH = mFragment.get();
                    if (fragmentH != null && fragmentH.isResumed()){
                        fragmentH.showToastMessage(fragmentH.getString(R.string.echo_tips));
                    }
                    break;
            }
        }
    }


}
