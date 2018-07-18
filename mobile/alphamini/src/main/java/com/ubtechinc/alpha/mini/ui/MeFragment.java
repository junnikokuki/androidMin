package com.ubtechinc.alpha.mini.ui;

import android.arch.lifecycle.Observer;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ubtech.utilcode.utils.CollectionUtils;
import com.ubtechinc.alpha.mini.AlphaMiniApplication;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseFragment;
import com.ubtechinc.alpha.mini.constants.Constants;
import com.ubtechinc.alpha.mini.databinding.FragmentMeBinding;
import com.ubtechinc.alpha.mini.entity.RobotInfo;
import com.ubtechinc.alpha.mini.entity.observable.AuthLive;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.entity.observable.MessageLive;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.event.MsgEvent;
import com.ubtechinc.alpha.mini.qqmusic.QQMusicStatsImp;
import com.ubtechinc.alpha.mini.tvs.TVSManager;
import com.ubtechinc.alpha.mini.utils.PreferencesManager;
import com.ubtechinc.alpha.mini.entity.observable.UpdateLive;

import com.ubtechinc.alpha.mini.viewmodel.AuthViewModel;
import com.ubtechinc.alpha.mini.viewmodel.MessageViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class MeFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "MeFragment";
    AuthViewModel authViewModel;
    MessageViewModel messageViewModel;
    TextView userName;
    ImageView userAvatar;
    private TextView tvReceive;
    private TextView tvRedPoint;

    FragmentMeBinding fragmentMeBinding;

    private Observer<QQMusicStatsImp> qqMusicStatsImpObserver = new Observer<QQMusicStatsImp>() {
        @Override
        public void onChanged(@Nullable QQMusicStatsImp qqMusicStatsImp) {
            Log.d(TAG, " onChanged QQMusicStatsImp.getInstance().getReceiveStats() : " + QQMusicStatsImp.getInstance().getReceiveStats());
            fragmentMeBinding.setIsMainAccount(qqMusicStatsImp.isMainAccount());
            fragmentMeBinding.setGiftType(QQMusicStatsImp.getInstance().getReceiveStats());
            switch (QQMusicStatsImp.getInstance().getReceiveStats()) {
                case RECEIVE_CONTINUE:
                case RECEIVE_NOW:
                    tvReceive.setText(getResources().getString(R.string.wait_receive));
                    Drawable drawable = getResources().getDrawable(R.drawable.red_point);
                    drawable.setBounds( 0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());
                    tvReceive.setCompoundDrawables(null, null, drawable, null);
                    break;
                case ALREADY_OPEN:
                    tvReceive.setText(getResources().getString(R.string.already_open));
                    tvReceive.setCompoundDrawables(null, null, null, null);
                    break;
                case NOT_OPEN:
                    tvReceive.setText(getResources().getString(R.string.unopen));
                    tvReceive.setCompoundDrawables(null, null, null, null);
                    break;
                case ALREADY_OUTTIME:
                    tvReceive.setText(getResources().getString(R.string.already_outtime));
                    tvReceive.setCompoundDrawables(null, null, null, null);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentMeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_me, null, false);
        return fragmentMeBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, " onActivityCreated ");
        authViewModel = new AuthViewModel();
        messageViewModel = MessageViewModel.get();
        userName = getActivity().findViewById(R.id.user_name);
        userAvatar = getActivity().findViewById(R.id.me_avatar_bg);
        tvReceive = getActivity().findViewById(R.id.tv_receive);
        tvRedPoint = getActivity().findViewById(R.id.tv_red_point);
        AuthLive.getInstance().observe(this, new Observer<AuthLive>() {
            @Override
            public void onChanged(@Nullable AuthLive authLive) {
                if (authLive.getCurrentUser() != null) {
                    String name = authLive.getCurrentUser().getNickName();
                    userName.setText(name);
                    Glide.with(getActivity()).load(authLive.getCurrentUser().getUserImage())
                            .bitmapTransform(new CropCircleTransformation(getActivity())).crossFade(1000).into(userAvatar);
                    qqMusicUpdate();
                }
            }
        });

        UpdateLive.getInstance().observe(this, mUpdateObserver);
        initListeners();
        messageViewModel.getNoReadMsgCount();
        MessageLive.get().observeForever(msgObserver);
        MyRobotsLive.getInstance().getRobots().observeForever(robotsObserver);
        QQMusicStatsImp.getInstance().observeForever(qqMusicStatsImpObserver);
        qqMusicUpdate();

        EventBus.getDefault().register(this);
    }

    private void qqMusicUpdate() {
        Log.i(TAG, " getRobots().getData() : " + MyRobotsLive.getInstance().getRobots());
        String str = MyRobotsLive.getInstance().getCurrentRobotId().getValue();
        Log.i(TAG, " robotInfo : " + str + " isMainAccount : " + QQMusicStatsImp.getInstance().isMainAccount());
        fragmentMeBinding.setIsMainAccount(QQMusicStatsImp.getInstance().isMainAccount());
        fragmentMeBinding.setGiftType(QQMusicStatsImp.getInstance().getReceiveStats());
    }

    @Override
    public void onResume() {
        super.onResume();

        messageViewModel.getNoReadMsgCount();
        TVSManager.getInstance(AlphaMiniApplication.getInstance()).resetAuthListener(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MessageLive.get().removeObserver(msgObserver);
        UpdateLive.getInstance().removeObserver(mUpdateObserver);
        MyRobotsLive.getInstance().getRobots().removeObserver(robotsObserver);
        QQMusicStatsImp.getInstance().removeObserver(qqMusicStatsImpObserver);
    }

    private void initListeners() {
        fragmentMeBinding.myRobot.setOnClickListener(this);
        fragmentMeBinding.message.setOnClickListener(this);
        fragmentMeBinding.feedback.setOnClickListener(this);
        fragmentMeBinding.about.setOnClickListener(this);
        fragmentMeBinding.robotShow.setOnClickListener(this);
        fragmentMeBinding.setting.setOnClickListener(this);
        fragmentMeBinding.qqmusicmember.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (fragmentMeBinding.myRobot.getId() == view.getId()) {
            PageRouter.toMineRobotActivity(getActivity());
        } else if (fragmentMeBinding.message.getId() == view.getId()) {
            notifyMsgRead(false);
            PageRouter.toMessageActivity(getActivity());
        } else if (fragmentMeBinding.feedback.getId() == view.getId()) {
            PageRouter.toFeedBackActivity(getActivity());
        } else if (fragmentMeBinding.about.getId() == view.getId()) {
            PageRouter.toAboutActivity(getActivity());
        } else if (fragmentMeBinding.robotShow.getId() == view.getId()) {
            PageRouter.toRobotShowActivity(getActivity());
        } else if (fragmentMeBinding.setting.getId() == view.getId()){
            PageRouter.toMobileBindActivity(getActivity());
        } else if(fragmentMeBinding.qqmusicmember.getId() == view.getId()) {
            PageRouter.toQQMusicActivity(getActivity());
        }

    }


    private void logout() {
        authViewModel.logout(false);
        getActivity().finish();
        PageRouter.toLogin(getActivity());
    }

    private Observer<LiveResult> mUpdateObserver = new Observer<LiveResult>() {
        @Override
        public void onChanged(@Nullable LiveResult updateLive) {
            UpdateLive live = (UpdateLive) updateLive;
            switch (live.getUpdateState()){
                case SIMPLE_UPDATE:
                    int count = fragmentMeBinding.getMsgCount();
                    String countString = count + "";
                    count++;
                    if (count > 99) {
                        countString = "99+";
                    }
                    tvRedPoint.setVisibility(View.VISIBLE);
                    fragmentMeBinding.setMsgCountString(countString);
                    PreferencesManager pm = PreferencesManager.getInstance(AlphaMiniApplication.getInstance());
                    if (count > pm.get(Constants.UNREAD_COUNT, 0)) {
                        EventBus.getDefault().post(new MsgEvent(count, true));
                    }
                    break;
                case FORCE_UPDATE:
                case UNKOWN:
                case UPDATED:
                    tvRedPoint.setVisibility(View.GONE);
                    break;
            }
        }
    };

    private Observer<MessageLive> msgObserver = new Observer<MessageLive>() {
        @Override
        public void onChanged(@Nullable MessageLive messageLive) {
            int count = messageLive.getMsgCount();
            fragmentMeBinding.setMsgCount(count);
            String countString = count + "";
            if (count > 99) {
                countString = "99+";
            }
            fragmentMeBinding.setMsgCountString(countString);


            PreferencesManager pm = PreferencesManager.getInstance(AlphaMiniApplication.getInstance());
            if (count > pm.get(Constants.UNREAD_COUNT, 0)) {
                EventBus.getDefault().post(new MsgEvent(count, true));
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotifyMsgChange(MsgEvent msgEvent) {
        PreferencesManager pm = PreferencesManager.getInstance(AlphaMiniApplication.getInstance());
        pm.put(Constants.UNREAD_COUNT, msgEvent.msgCount);
        notifyMsgRead(msgEvent.hasNew);
    }

    private void notifyMsgRead(boolean hasNewMsg) {
        Callback callback = (Callback) getActivity();
        if (callback != null) {
            callback.onMsgChange(hasNewMsg);
        }
        fragmentMeBinding.msgCount.setVisibility(hasNewMsg ? View.VISIBLE : View.INVISIBLE);
    }

    private Observer<LiveResult> robotsObserver = new Observer<LiveResult>() {
        @Override
        public void onChanged(@Nullable LiveResult liveResult) {
            List<RobotInfo> infoList = (List<RobotInfo>) liveResult.getData();
            int robotCounts = CollectionUtils.isEmpty(infoList) ? 0 : infoList.size();
            fragmentMeBinding.setRobotCounts(robotCounts);
            fragmentMeBinding.setRobotCountString(getString(R.string.robot_count,robotCounts));
        }
    };

    public interface Callback {
        void onMsgChange(boolean hasNewMsg);
    }
}
