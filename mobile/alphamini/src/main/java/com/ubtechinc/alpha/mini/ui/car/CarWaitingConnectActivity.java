package com.ubtechinc.alpha.mini.ui.car;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseActivity;
import com.ubtechinc.alpha.mini.databinding.ActivityCarWaitingConnectBinding;
import com.ubtechinc.alpha.mini.event.JimuCarDriverModeEvent;
import com.ubtechinc.alpha.mini.event.JimuFindCarListEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class CarWaitingConnectActivity extends BaseActivity{

    private static final String TAG = CarWaitingConnectActivity.class.getSimpleName();
    private ActivityCarWaitingConnectBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_car_waiting_connect);
        mBinding.setEvent(new EventResponse());

        CarMsgManager.driveModeCmd(CarMsgManager.ENTER_DRIVE_MODE);//发送进入开车模式
        Log.i("test", "register eventbus");

        initView();
    }

    @Override
    protected void setStatuesBar() {

    }

    private void initView() {
        ImageView waitingConnectImage = findViewById(R.id.iv_waitingconnect);
        Glide.with(this).load(R.drawable.robot_connect_car).into(new GlideDrawableImageViewTarget(waitingConnectImage,100));
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public class EventResponse{

        public void exit(View view) {
            finish();
        }

        public void noResponse(View view) {
            Intent intent = new Intent(CarWaitingConnectActivity.this, NoResponseActivity.class);
            startActivity(intent);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(JimuCarDriverModeEvent jimuCarDriverModeEvent) {
        Log.i("test","CarWaitingConnectActivity jimuCarDriverModeEvent " + jimuCarDriverModeEvent.getDriveMode());
        if (jimuCarDriverModeEvent.getDriveMode() == JimuCarDriverModeEvent.ENTER_DRIVE_MODE) {
            Intent intent = new Intent(CarWaitingConnectActivity.this, CarRemindActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(JimuFindCarListEvent jimuFindCarListEvent) {
        Log.i("test","CarWaitingConnectActivity jimuFindCarListEvent");
        Intent intent = new Intent(CarWaitingConnectActivity.this, CarListActivity.class);
        startActivity(intent);
    }
}
