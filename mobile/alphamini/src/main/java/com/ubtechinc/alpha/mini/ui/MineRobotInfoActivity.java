package com.ubtechinc.alpha.mini.ui;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;

import com.ubtech.utilcode.utils.CollectionUtils;
import com.ubtech.utilcode.utils.network.NetworkHelper;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseToolbarActivity;
import com.ubtechinc.alpha.mini.entity.RobotInfo;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.ui.bluetooth.BleAutoConnectActivity;
import com.ubtechinc.alpha.mini.ui.bluetooth.StoreActivityNamesBeforeBinding;
import com.ubtechinc.alpha.mini.ui.minerobot.MineRobotAdapter;
import com.ubtechinc.alpha.mini.ui.minerobot.SwipeItemLayout;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.ubtechinc.alpha.mini.widget.MiniDividerDecoration;
import com.ubtechinc.bluetooth.UbtBluetoothManager;
import com.ubtechinc.bluetooth.utils.UbtBluetoothHelper;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.List;

/**
 * @Date: 2017/11/9.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 展示当前用户所有机器人，并且再该页面可以含有添加机器人入口
 */

public class MineRobotInfoActivity extends BaseToolbarActivity implements MiniDividerDecoration.IDividerCallBack {

    private RecyclerView recyclerView;
    private ViewStub emptyStub;
    private View emptyView;
    private View dataContnet;
    private MineRobotAdapter mineRobotAdapter;
    private List<RobotInfo> robots;
    private boolean mJumpBanding;

    private boolean forbidAddRobot;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_robotinfo);
        initView();
    }

    private void initView() {
        View toolbar = findViewById(R.id.toolbar);
        dataContnet = findViewById(R.id.data_content);
        emptyStub = findViewById(R.id.empty_stub);
        initToolbar(toolbar, getString(R.string.my_robot), View.GONE, false);
        initConnectFailedLayout(R.string.mobile_connected_error, R.string.robot_connected_error);
        setRobotNumbers();
        recyclerView = findViewById(R.id.rv_robot);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new MiniDividerDecoration(this, getResources().getDimensionPixelOffset(R.dimen.text_size_30px), this));
        recyclerView.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(this));
        mineRobotAdapter = new MineRobotAdapter(MyRobotsLive.getInstance().getRobots().getData(), this);
        recyclerView.setAdapter(mineRobotAdapter);
        MyRobotsLive.getInstance().getRobots().observe(this, new Observer<LiveResult>() {
            @Override
            public void onChanged(@Nullable LiveResult liveResult) {
                robots = MyRobotsLive.getInstance().getRobots().getData();
                if (robots != null && robots.size() >= 25) {
                    forbidAddRobot = true;
                } else {
                    forbidAddRobot = false;
                }
                mineRobotAdapter.updateList(robots);
                if (CollectionUtils.isEmpty(robots)) {
                    showEmptyLayout();
                } else {
                    showDataLayout();
                }
            }
        });
    }


    private void setRobotNumbers() {
        robots = MyRobotsLive.getInstance().getRobots().getData();
        if (robots != null && robots.size() >= 25) {
            forbidAddRobot = true;
        } else {
            forbidAddRobot = false;
        }
        if (CollectionUtils.isEmpty(robots)) {
            showEmptyLayout();
        } else {
            showDataLayout();
        }
    }

    @Override
    public void setConnectFailedLayout(boolean robotConnectState, boolean networkConnectState) {
        if (isColsedConnectLayout()) {
            return;
        }
        if (connectFailed == null) {
            return;
        }
        if (!networkConnectState) {
            connectFailed.setVisibility(View.VISIBLE);
            tvConnectedText.setText(R.string.mobile_connected_error);
            return;
        }
        if (CollectionUtils.isEmpty(robots)) {
            connectFailed.setVisibility(View.GONE);
            return;
        }
        if (!robotConnectState) {
            connectFailed.setVisibility(View.VISIBLE);
            tvConnectedText.setText(R.string.robot_connected_error);
            return;
        }
        connectFailed.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 开始搜索机器人
        if (UbtBluetoothManager.getInstance().isOpenBluetooth() &&
                AndPermission.hasPermission(this, Permission.LOCATION)) {
            UbtBluetoothHelper.getInstance().startScan(MyRobotsLive.getInstance().getRobotUserId());
            UbtBluetoothHelper.getInstance().setBluetoothScanListener(new UbtBluetoothHelper.BluetoothScanListener() {
                @Override
                public void scanEnd() {
                    if (mJumpBanding) {
                        dismissDialog();
                        jumpToBinding();
                    }
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        UbtBluetoothHelper.getInstance().stopScan();
    }

    public void showEmptyLayout() {
        if (emptyView == null) {
            emptyView = emptyStub.inflate();
        }
        dataContnet.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }

    public void showDataLayout() {
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
        dataContnet.setVisibility(View.VISIBLE);
        if (!CollectionUtils.isEmpty(robots)) {

        }
    }

    public void showErrorDialog(@StringRes int stringRes) {
        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.setMessage(stringRes).setPositiveButton(R.string.i_know, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        }).show();
    }

    public void addRobot(View view) {
        if (!NetworkHelper.sharedHelper().isNetworkAvailable()) {
            showMsgDialog(getString(R.string.network_invalid_tip));
            return;
        }
        if (forbidAddRobot) {
            showErrorDialog(R.string.mine_robot_add_too_much_robot);
        } else {
            UbtBluetoothManager.getInstance().setChangeWifi(false);
            UbtBluetoothManager.getInstance().setFromHome(false);
            StoreActivityNamesBeforeBinding.getInstance().addActivityName(getClass().getSimpleName());
            if (UbtBluetoothManager.getInstance().isOpenBluetooth() &&
                    AndPermission.hasPermission(this, Permission.LOCATION)) {
                if (UbtBluetoothHelper.getInstance().scanEnd()) {
                    jumpToBinding();
                } else {
                    showLoadingDialog();
                    mJumpBanding = true;
            }
            } else {
                PageRouter.toOpenBluetooth(this);
            }
        }
    }

    private void jumpToBinding() {
        mJumpBanding = false;
        Log.d(TAG, " jumpToBinding ");
         if (!NetworkHelper.sharedHelper().isNetworkAvailable()) {
            showMsgDialog(getString(R.string.network_invalid_tip));
            return;
        }
        if (UbtBluetoothHelper.getInstance().getPurposeDevice() != null) {
            Intent intent = new Intent(this, BleAutoConnectActivity.class);
            intent.putExtra(UbtBluetoothHelper.KEY_UBTBLUETOOTH, UbtBluetoothHelper.getInstance().getPurposeDevice());
            startActivity(intent);
        } else if (UbtBluetoothHelper.getInstance().hasDevices()) {
            PageRouter.toBleAutoConnectActivity(this);
        } else {
            PageRouter.toOpenPowerTip(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean needDivideLineAdjustToPosition(int position) {
        return true;
    }
}
