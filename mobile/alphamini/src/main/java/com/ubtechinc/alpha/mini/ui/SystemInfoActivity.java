package com.ubtechinc.alpha.mini.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseToolbarActivity;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.tvs.utils.SharedPreferencesUtils;

/**
 * @author：wululin
 * @date：2017/11/21 14:03
 * @modifier：ubt
 * @modify_date：2017/11/21 14:03
 * [A brief description]
 * version
 */

public class SystemInfoActivity extends BaseToolbarActivity {
    private TextView mTotalspaceTV,
            mAvaiblespaceTv,
            mBatteryCapacityTv,
            mMainappVersionTv,
            mSysVersionTv,
            mSerailTv,
            mImeiTv,
            mMeidTv,
            mOperator,
            mFirstBindTimeTv,
            mMacTv,
            mHardwareTv;
    private RelativeLayout mSysVersionRl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_info_layout);
        initView();
        mTotalspaceTV.setText(getIntent().getStringExtra(PageRouter.TOTALSPACE));
        mAvaiblespaceTv.setText(getIntent().getStringExtra(PageRouter.AVAIBLESPACE));
        mMainappVersionTv.setText(getIntent().getStringExtra(PageRouter.MAINAPPVERSION));
        mSysVersionTv.setText(getIntent().getStringExtra(PageRouter.SYSTEMVERSION));
        mSerailTv.setText(getIntent().getStringExtra(PageRouter.SERIAL));
        mHardwareTv.setText(getIntent().getStringExtra(PageRouter.HANDWEARVERSION));
        mBatteryCapacityTv.setText(getIntent().getStringExtra(PageRouter.BATTERYCAPACITY));
        mImeiTv.setText(getIntent().getStringExtra(PageRouter.IMEI));
        mMeidTv.setText(getIntent().getStringExtra(PageRouter.MEID));
        mOperator.setText(getIntent().getStringExtra(PageRouter.OPERATOR));
        mFirstBindTimeTv.setText(getIntent().getStringExtra(PageRouter.FIRSTBIND));
        mMacTv.setText(getIntent().getStringExtra(PageRouter.MAC));
        mSysVersionRl.setOnClickListener(mDebugClickListener);
    }

    private String mRobotUserId;

    @Override
    protected void onResume() {
        super.onResume();
        mRobotUserId = MyRobotsLive.getInstance().getRobotUserId();
        //TODO 查询机器人端 adb 开启状态
//        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IM_GET_ROBOT_CONFIG_REQUEST, IM_VERSION, null, mRobotUserId, mICallback);
    }

//    private void syncDebugMode(boolean isAdbEnable){
//        mSwitchBn.setOnCheckedChangeListener(null);
//        if (PreferencesManager.getInstance(AlphaMiniApplication.getInstance()).get(Constants.DEBUG_MODE, 0) == 1) {
//            setDebugModeLayout(View.VISIBLE);
//            mSwitchBn.setChecked(isAdbEnable);
//        }
//        mSwitchBn.setOnCheckedChangeListener(mSwitcherChangedListener);
//    }


    private void initView() {
        View toolbar = findViewById(R.id.toolbar);
        initToolbar(toolbar, getString(R.string.system_update), View.GONE, false);
        initConnectFailedLayout(R.string.mobile_connected_error, R.string.robot_connected_error);
        mTotalspaceTV = findViewById(R.id.total_space_tv);
        mAvaiblespaceTv = findViewById(R.id.avaible_space_tv);
        mMainappVersionTv = findViewById(R.id.main_app_version_tv);
        mSysVersionTv = findViewById(R.id.system_version_tv);
        mSerailTv = findViewById(R.id.serail_rl_tv);
        mHardwareTv = findViewById(R.id.hardware_version_tv);
        mBatteryCapacityTv = findViewById(R.id.battery_capacity_tv);
        mImeiTv = findViewById(R.id.imei_tv);
        mMeidTv = findViewById(R.id.meid_tv);
        mOperator = findViewById(R.id.operator_tv);
        mFirstBindTimeTv = findViewById(R.id.activation_time_tv);
        mMacTv = findViewById(R.id.mac_tv);
        mSysVersionRl = findViewById(R.id.rl_sys_version);
    }

    private int mClickCount = 1;
    private long lastClickTime = System.currentTimeMillis();
    private View.OnClickListener mDebugClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (SharedPreferencesUtils.getBoolean(getApplicationContext(), "ADB-SWITCH" + MyRobotsLive.getInstance().getRobotUserId(), false)) {
                Toast.makeText(SystemInfoActivity.this, "您已处于调试模式！", Toast.LENGTH_SHORT).show();
                return;
            }
            if (mClickCount == 1) {
                lastClickTime = System.currentTimeMillis() - 50l;
                mClickCount++;
                return;
            }
            Log.d("SWITCH-ADB", "ClickPeriod=====" + (System.currentTimeMillis() - lastClickTime));
            if (System.currentTimeMillis() - lastClickTime < 1800l) {
                if (mClickCount < 10) {
                    Toast.makeText(SystemInfoActivity.this, "还差" + (10 - mClickCount) + "步进入调试模式！", Toast.LENGTH_SHORT).show();
                    mClickCount++;
                    lastClickTime = System.currentTimeMillis();
                } else if (mClickCount == 10) {
                    SharedPreferencesUtils.putBoolean(getApplicationContext(), "ADB-SWITCH" + MyRobotsLive.getInstance().getRobotUserId(), true);
                    Toast.makeText(SystemInfoActivity.this, "您已处于调试模式！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SystemInfoActivity.this, "您已处于调试模式！", Toast.LENGTH_SHORT).show();
                }
            } else {
                mClickCount = 1;
            }
        }
    };


}
