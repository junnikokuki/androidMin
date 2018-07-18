package com.ubtechinc.alpha.mini.avatar;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ubtech.utilcode.utils.ScreenUtils;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.alpha.mini.avatar.entity.User;
import com.ubtechinc.alpha.mini.avatar.viewutils.AvatarUserListManger;
import com.ubtechinc.alpha.mini.avatar.viewutils.ControlPanelUtil;
import com.ubtechinc.alpha.mini.avatar.viewutils.MaskLayUtil;
import com.ubtechinc.alpha.mini.avatar.viewutils.OnMaskClickListener;
import com.ubtechinc.alpha.mini.avatar.viewutils.VideoPanelUtil;
import com.ubtechinc.alpha.mini.avatar.widget.AvatarImageView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.ubtechinc.alpha.mini.avatar.AvatarStateManager.State.CONNECTFAILED;
import static com.ubtechinc.alpha.mini.avatar.AvatarStateManager.State.NETWORK_ERROR;
import static com.ubtechinc.alpha.mini.avatar.AvatarStateManager.State.NORMAL;


public class AvatarFragmentV extends AvatarBaseFragment implements AvatarControlListener, VideoPanelUtil.VideoPanelListener,
        AvatarStateManager.IAvatarStateListener, OnMaskClickListener, AvatarUserListManger.IAvatarUserChangeListener {


    private static final int MIC_OPEN_HINT_EVENT = 1000;
    ControlPanelUtil controlPanelUtil;
    VideoPanelUtil videoPanelUtil;

    AvatarActivity avatarActivity;

    private MaskLayUtil maskLayUtil;

    private AvatarImageView ivOne;
    private AvatarImageView ivTwo;
    private AvatarImageView ivThree;
    private AvatarImageView ivFour;
    private AvatarImageView ivFive;
    private AvatarImageView ivSix;
    private TextView  tvCount;

    private List<AvatarImageView> avatarCount = new ArrayList<>();
    private MyHandler myHandler;

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_avatar_v, null);
        maskLayUtil = new MaskLayUtil(view);
        maskLayUtil.setOnMaskClickListener(this);
        currentState(((AvatarActivity)getActivity()).getManager().getState());
        init(view);
        return view;
    }

    private void init(View view) {
        ivOne = view.findViewById(R.id.iv_user_one);
        ivTwo = view.findViewById(R.id.iv_user_two);
        ivThree = view.findViewById(R.id.iv_user_three);
        ivFour = view.findViewById(R.id.iv_user_four);
        ivFive = view.findViewById(R.id.iv_user_five);
        ivSix = view.findViewById(R.id.iv_user_six);
        avatarCount.add(ivOne);
        avatarCount.add(ivTwo);
        avatarCount.add(ivThree);
        avatarCount.add(ivFour);
        avatarCount.add(ivFive);
        avatarCount.add(ivSix);
        tvCount = view.findViewById(R.id.tv_user_count);
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
        if (controlPanelUtil == null) {
            controlPanelUtil = new ControlPanelUtil(avatarActivity, this, avatarActivity.favActionList);
        }
        if (videoPanelUtil == null) {
            videoPanelUtil = new VideoPanelUtil(avatarActivity, this);
        }
        if (videoView != null) {
            videoPanelUtil.setVideoView(videoView);
        }
        controlPanelUtil.enableMic(rtcManager.micEnable);
    }


    public void start(){
        if(getActivity() != null){
            videoContainer = (ViewGroup) getActivity().findViewById(R.id.surface_view_container);
            if (videoView == null) {
                videoView = rtcManager.createSurfaceView();
            }
            if (videoView != null) {
                if (videoView.getParent() == null) {
                    int wide = ScreenUtils.getScreenWidth();
                    int height = (int) (wide * 0.56);
                    videoContainer.addView(videoView, new FrameLayout.LayoutParams(wide, height));
                }
                int rotation = 0;
                switch (getActivity().getResources().getConfiguration().orientation) {
                    case Configuration.ORIENTATION_PORTRAIT:
                        rotation = 0;
                        break;
                    case Configuration.ORIENTATION_LANDSCAPE:
                        rotation = 270;
                        break;
                    default:
                        break;
                }
                rtcManager.start(videoView, rotation);
            }
        }
    }

    @Override
    public View getHeaderControl() {
        return controlPanelUtil.getHeadController();
    }

    @Override
    public View getWalkControl() {
        return controlPanelUtil.getWalkController();
    }



    @Override
    public void doStand() {
        ((AvatarActivity)getActivity()).robotStand();
        controlPanelUtil.disableAllControlPanel();
    }

    @Override
    public void onStandCommandReponse() {
        controlPanelUtil.enableAllControlPanel();
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
    public void onScreenshot() {
        if (getActivity() != null) {
            ((AvatarActivity)getActivity()).onScreenshot();
        }
    }




    @Override
    public void changeMicState(boolean enable) {
        super.changeMicState(enable);
        controlPanelUtil.enableMic(enable);
        if (enable){
            showToastMessage(getString(R.string.robot_mic_enable));
            myHandler.sendEmptyMessageDelayed(MIC_OPEN_HINT_EVENT, 3000);
        }else{
            showToastMessage(getString(R.string.robot_mic_disable));
        }
    }

    @Override
    public void onAudioVolumeIndication(int volume) {
        if(controlPanelUtil!= null){
            controlPanelUtil.onAudioVolumeIndication(volume);
        }
    }

    @Override
    public void currentState(AvatarStateManager.State state) {
        if (maskLayUtil == null){
            Log.i(TAG, "currentState: maskLayUtil is null");
            return;
        }
        Log.i(TAG, "currentState: " + state.name());
        switch (state){
            case NORMAL:
                maskLayUtil.close();
                break;
            case CONNECTING:
                maskLayUtil.showLoading();
                break;
            case CONNECTFAILED:
                maskLayUtil.connectFailed();
                break;
            case NETWORK_ERROR:
                maskLayUtil.networkError();
                break;
            case START_FAILED:
                maskLayUtil.startVideoFailed();
                break;
        }
        if (controlPanelUtil == null){
            return;
        }
        if (NORMAL == state){
            controlPanelUtil.enableAllControlPanel();
        }else{
            controlPanelUtil.disableAllControlPanel();
        }
    }



    @Override
    public void onStart() {
        AvatarUserListManger.get().setIAvatarUserChangeListener(this);
        if (avatarActivity != null){
            boolean enable = avatarActivity.getCurrentMicState();
            controlPanelUtil.enableMic(enable);
        }
        super.onStart();
    }

    @Override
    public void onStop() {
        AvatarUserListManger.get().removeAvatarUserChangeListener();
        super.onStop();
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

    private void showOnlineUserAvatar(List<String> onlineUsers, List<User> bindUsers) {
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

        if (onlineUsers.size() == 1 && onlineUsers.get(0).equalsIgnoreCase(AvatarUserListManger.get().getCurrentUserId())){
            tvCount.setVisibility(View.GONE);
            avatarCount.get(0).setVisibility(View.GONE);
        }
    }

    private void hideAllUserAvatar(){
        for (AvatarImageView imageView : avatarCount){
            imageView.setVisibility(View.GONE);
        }
        if (tvCount != null){
            tvCount.setVisibility(View.GONE);
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
        List<User> bindUsers = AvatarUserListManger.get().getBindUsers();
        if (bindUsers == null || bindUsers.size() == 0){
            return;
        }
        for (User user : bindUsers){
            if (user.getId().equalsIgnoreCase(enterUsers.get(0)) && !user.isEnterAlreadyShow()){
                showToastMessage(getString(R.string.robot_get_in_room, user.getName()));
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

        private final WeakReference<AvatarFragmentV> mFragment;

        public MyHandler(AvatarFragmentV fragment){
            mFragment = new WeakReference<AvatarFragmentV>(fragment);
        }


        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MIC_OPEN_HINT_EVENT:
                    AvatarFragmentV fragmentV = mFragment.get();
                    if (fragmentV != null && fragmentV.isResumed()){
                        fragmentV.showToastMessage(fragmentV.getString(R.string.echo_tips));
                    }
                    break;
            }
        }
    }
}
