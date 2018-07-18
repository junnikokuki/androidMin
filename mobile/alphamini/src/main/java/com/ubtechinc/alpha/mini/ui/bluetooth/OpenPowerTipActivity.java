package com.ubtechinc.alpha.mini.ui.bluetooth;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.AnalysisClickListener;
import com.ubtechinc.alpha.mini.common.BleNetworkBaseActivity;
import com.ubtechinc.alpha.mini.ui.PageRouter;
import com.ubtechinc.bluetooth.UbtBluetoothDevice;
import com.ubtechinc.bluetooth.UbtBluetoothManager;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

/**
 * @author：wululin
 * @date：2017/10/14 10:09
 * @modifier：ubt
 * @modify_date：2017/10/14 10:09
 * [A brief description]
 * 开启电源提示界面
 */

public class OpenPowerTipActivity extends BleNetworkBaseActivity {
    private ImageButton mBackBtn;
    private CheckBox mCheckBox;
    private TextView mNextStepBtn;
    private ImageView mOpenpowerIv;
    private LinearLayout mCheckLl;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_power_tips);
        initView();
        initListener();

    }

    @Override
    public void onReconnectDevice(UbtBluetoothDevice device) {

    }

    private void initView(){
        mBackBtn = findViewById(R.id.back_btn);
        mCheckBox = findViewById(R.id.is_stand_up_cb);
        mNextStepBtn = findViewById(R.id.next_step_btn);
        mOpenpowerIv = findViewById(R.id.open_power_iv);
        mCheckLl = findViewById(R.id.open_power_ll);
        Glide.with(this).load(R.drawable.turn_on_robot).into(new GlideDrawableImageViewTarget(mOpenpowerIv,100));
    }

    private void initListener(){
        mBackBtn.setOnClickListener(mOnClickListener);
        mNextStepBtn.setOnClickListener(mOnClickListener);
        mCheckLl.setOnClickListener(mOnClickListener);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mNextStepBtn.setEnabled(b);
            }
        });
    }

    AnalysisClickListener mOnClickListener = new AnalysisClickListener(){
        @Override
        public void onClick(View view,boolean reported) {
            switch (view.getId()){
                case R.id.back_btn:
                    onBackPressed();
                    break;
                case R.id.next_step_btn:
                    if(UbtBluetoothManager.getInstance().isOpenBluetooth()) {
                        if(AndPermission.hasPermission(OpenPowerTipActivity.this, Permission.LOCATION)){
                            PageRouter.toBleAutoConnectActivity(OpenPowerTipActivity.this);
                            finish();
                        }else {
                            toast(R.string.open_location_tips);
                        }
                    }
                    break;
                case R.id.open_power_ll:
                    /*if(mCheckBox.isChecked()){
                        mCheckBox.setChecked(false);
                    }else {
                        mCheckBox.setChecked(true);
                    }*/
                    break;
            }
        }
    };
}
