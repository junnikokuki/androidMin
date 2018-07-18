package com.ubtechinc.alpha.mini.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ubtech.utilcode.utils.StringUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseToolbarActivity;
import com.ubtechinc.alpha.mini.entity.observable.AuthLive;
import com.ubtechinc.alpha.mini.ui.setting.MobileNumberChangeEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.ubtechinc.alpha.mini.ui.PageRouter.INTENT_KEY_ROBOT_BIND_PHONENUMBER;

/**
 * @作者：liudongyang
 * @日期: 18/4/17 22:16
 * @描述:
 */

public class BindPhoneDetailActivity extends BaseToolbarActivity {

    private TextView mNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone_detail);
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {
        initToolbar(findViewById(R.id.toolbar), "绑定手机", View.GONE, false);
        mNumber = findViewById(R.id.text_phone);
        String locationPhoneNumber =  getIntent().getStringExtra(INTENT_KEY_ROBOT_BIND_PHONENUMBER);
        if (!TextUtils.isEmpty(locationPhoneNumber)){
            String phoneNumber = locationPhoneNumber.substring(2, 13);
            mNumber.setText(phoneNumber);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MobileNumberChangeEvent event) {
        String phone =  event.getNewPhoneNumber();
        if (!TextUtils.isEmpty(phone)){
            mNumber.setText(phone);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void changeMobile(View view){
        PageRouter.toMobileNumber(this,mNumber.getText().toString());
    }
}
