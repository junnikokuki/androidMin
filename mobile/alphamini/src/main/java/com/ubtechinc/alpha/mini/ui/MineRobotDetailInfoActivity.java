package com.ubtechinc.alpha.mini.ui;

import android.arch.lifecycle.Observer;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.View;

import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseToolbarActivity;
import com.ubtechinc.alpha.mini.databinding.ActivityMineRobotDetailBinding;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.entity.observable.RobotUnbindLive;
import com.ubtechinc.alpha.mini.ui.bind.UserList;
import com.ubtechinc.alpha.mini.viewmodel.RobotAllAccountViewModel;
import com.ubtechinc.alpha.mini.viewmodel.RobotsViewModel;
import com.ubtechinc.alpha.mini.viewmodel.unbind.UnbindDialog;
import com.ubtechinc.alpha.mini.widget.ActionSheetDialog;
import com.ubtechinc.alpha.mini.widget.LoadingDialog;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.ubtechinc.alpha.mini.widget.PopView;
import com.ubtechinc.alpha.mini.entity.RobotInfo;
import com.ubtechinc.alpha.mini.net.CheckBindRobotModule;

import java.util.ArrayList;
import java.util.List;

/**
 * @Date: 2018/2/27.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 我的机器人的详情页面
 */

public class MineRobotDetailInfoActivity extends BaseToolbarActivity implements PopView.IStartAnmaionListener,PopView.IDissmissAnmaionListener, PopView.IAnimationUpdataListener {

    private ActivityMineRobotDetailBinding binding;

    private RobotInfo mCurrentPageRobot;

    private RobotsViewModel mRobotModel;

    private RobotAllAccountViewModel mAllAccountViewModel;

    private ClickEvent mEvent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mine_robot_detail);
        mEvent = new ClickEvent();
        binding.setEvent(mEvent);
        mRobotModel = RobotsViewModel.get();
        mAllAccountViewModel = new RobotAllAccountViewModel();
        initView();
        initListener();
        checkSlaverListIfIsMaster();
    }

    private void checkSlaverListIfIsMaster() {
        if (mCurrentPageRobot.isMaster()){
            setSlaverListIfNotNull();
        }
    }

    private void setSlaverListIfNotNull() {
        String userId = mCurrentPageRobot.getRobotUserId();
        mAllAccountViewModel.getRobotUnBindWays(userId).observe(MineRobotDetailInfoActivity.this, new Observer<RobotUnbindLive>() {
            @Override
            public void onChanged(@Nullable RobotUnbindLive robotUnbindLive) {
                switch (robotUnbindLive.getUnbindCategory()){
                    case MASTER:
                        List<CheckBindRobotModule.User> slaverList = robotUnbindLive.getRobotOwners();
                        mEvent.setSlavers(slaverList);
                        break;
                    case SIMPLE:
                        mEvent.setNullSlavers(true);
                        break;
                }

            }
        });
    }

    private void initView() {
        mCurrentPageRobot = (RobotInfo) getIntent().getSerializableExtra(PageRouter.INTENT_KEY_ROBOT_INFO);
        if (mCurrentPageRobot == null){
            Log.e(TAG, "Get RobotInfo null!!!!!"  );
            return;
        }

        //初始化toolbar
        initToolbar(binding.toolbar, getString(R.string.robot_detail_toolbar_title), View.GONE , false);

        //初始化界面内容
        binding.textSerial.setText(getResources().getString(R.string.serial_number_1, mCurrentPageRobot.getUserId()));
        binding.textManager.setText(getResources().getString(R.string.manager_name, mCurrentPageRobot.getMasterUserName()));

        //初始化在线状态
        refreshOnlineState();
    }

    public void refreshOnlineState(){
        binding.robotStateIcon.setImageLevel(mCurrentPageRobot.getOnlineState() + 1);//icon
        if (mCurrentPageRobot.getOnlineState() == RobotInfo.ROBOT_STATE_ONLINE){
            binding.setStatus(getString(R.string.online));
        }else if(mCurrentPageRobot.getOnlineState() == RobotInfo.ROBOT_STATE_OFFLINE){
            binding.setStatus(getString(R.string.offline));
        }else{
            binding.setStatus(getString(R.string.unknown));
        }
    }

    private void initListener() {
        MyRobotsLive.getInstance().getRobots().observe(this, new Observer<LiveResult>() {
            @Override
            public void onChanged(@Nullable LiveResult liveResult) {
                List<RobotInfo> robots = MyRobotsLive.getInstance().getRobots().getData();
                if (robots != null){
                    for (RobotInfo robotInfo: robots){
                        //刷新当前在线状态
                        if (robotInfo.getUserId().equals(mCurrentPageRobot.getUserId())
                                && mCurrentPageRobot.getOnlineState() != robotInfo.getOnlineState()){
                            mCurrentPageRobot.setOnlineState(robotInfo.getOnlineState());
                            refreshOnlineState();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void animationEnd() {
        binding.setIsShowingUnbindView(false);

    }

    @Override
    public void animationStart() {
        binding.setIsShowingUnbindView(true);

    }

    @Override
    public void animationUpdate(float percent) {

    }


    private void getSlaverListCreateDialog() {
        String userId = mCurrentPageRobot.getRobotUserId();
        LoadingDialog.getInstance(this).show();

        mAllAccountViewModel.getRobotUnBindWays(userId).observe(MineRobotDetailInfoActivity.this, new Observer<RobotUnbindLive>() {
            @Override
            public void onChanged(@Nullable RobotUnbindLive robotUnbindLive) {

                LoadingDialog.getInstance(MineRobotDetailInfoActivity.this).dismiss();

                switch (robotUnbindLive.getUnbindCategory()){
                    case MASTER:
                        List<CheckBindRobotModule.User> slaverList = robotUnbindLive.getRobotOwners();
                        mEvent.setSlavers(slaverList);
                        mEvent.createMasterDialog();
                        break;
                    case SIMPLE:
                        mEvent.createSimpleDialog();
                        break;
                    case NETWORKERROR:
                        toastError(getString(R.string.can_not_bind_network_error));
                        break;
                }

            }
        });
    }


    public class ClickEvent{

        private List<CheckBindRobotModule.User> mSlavers = new ArrayList<>();

        private boolean mIsNullSlavers;

        /*************************响应界面点击事件start****************/

        public void showBottomUnbindView(View view){
            if (mSlavers.size() > 0){
                createMasterDialog();
                return;
            }
            if (mIsNullSlavers){
                createSimpleDialog();
                return;
            }
            if (mCurrentPageRobot.isMaster()){
                getSlaverListCreateDialog();
            }else{
                createSimpleDialog();
            }
        }

        public void createSimpleDialog() {
            new ActionSheetDialog(MineRobotDetailInfoActivity.this)
                    .builder()
                    .setCancelable(true)
                    .setCanceledOnTouchOutside(true)
                    .addSheetItem(getString(R.string.unbind),
                            ActionSheetDialog.SheetItemColor.Red,
                            new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    popUnbindDialog(R.string.robot_detail_slaver_unbind_title,
                                            R.string.robot_detail_master_unbind_mine_robot_message);
                                }
                            }).show();
        }

        public void createMasterDialog() {
            new ActionSheetDialog(MineRobotDetailInfoActivity.this)
                    .builder()
                    .setCancelable(true)
                    .setCanceledOnTouchOutside(true)
                    .setTitle(getString(R.string.pop_title_hint))
                    .addSheetItem(getString(R.string.robot_detail_master_unbind_mine_robot),
                            ActionSheetDialog.SheetItemColor.Blue,
                            new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    PageRouter.toDeliverMasterRight(MineRobotDetailInfoActivity.this,mCurrentPageRobot, new UserList(mSlavers));
                                }
                            })
                    .addSheetItem(getString(R.string.robot_detail_master_unbind_all_slaver),
                            ActionSheetDialog.SheetItemColor.Blue,
                            new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    popUnbindDialog(R.string.robot_detail_master_unbind_all_slaver_title, R.string.robot_detail_master_unbind_mine_robot_message);
                                }
                            }).show();
        }



        /******************************响应界面点击事件end******************************/

        void setNullSlavers(boolean isNullSlavers){
            mIsNullSlavers = isNullSlavers;
        }

        void setSlavers(List<CheckBindRobotModule.User> slavers) {
            mSlavers.clear();
            mSlavers.addAll(slavers);
        }

        private void popUnbindDialog(@StringRes int titleRes, @StringRes int messageRes){
            final MaterialDialog dialog = new MaterialDialog(MineRobotDetailInfoActivity.this);
            dialog.setTitle(titleRes)
                    .setMessage(messageRes)
                    .setMessageColor(R.color.color_unbind_red)
                    .setPositiveButton(R.string.unbind, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            unbindMySelf();
                        }
                    }).setNegativeButton(R.string.cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    }).setPostiveTextColor(R.color.color_unbind_red).show();
        }

        private void unbindMySelf(){
            LoadingDialog.getInstance(MineRobotDetailInfoActivity.this).show();
            mRobotModel.doThrowRobotPermission(new UnbindDialog.IOnUnbindListener() {
                @Override
                public void onUnbindSuccess() {
                    ToastUtils.showShortToast(R.string.cancle_permission_success);
                    LoadingDialog.getInstance(MineRobotDetailInfoActivity.this).dismiss();
                    MineRobotDetailInfoActivity.this.finish();
                }

                @Override
                public void onUnbindFailed() {
                    ToastUtils.showShortToast(R.string.cancle_permission_fail);
                    LoadingDialog.getInstance(MineRobotDetailInfoActivity.this).dismiss();
                    MineRobotDetailInfoActivity.this.finish();
                }
            }, mCurrentPageRobot.getRobotUserId());
        }

    }





}
