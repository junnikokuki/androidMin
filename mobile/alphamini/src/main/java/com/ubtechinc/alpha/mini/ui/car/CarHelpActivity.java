package com.ubtechinc.alpha.mini.ui.car;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseActivity;
import com.ubtechinc.alpha.mini.constants.Constants;
import com.ubtechinc.alpha.mini.event.JimuCarDisconnectRobotEvent;
import com.ubtechinc.alpha.mini.event.JimuCarPowerEvent;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class CarHelpActivity extends BaseActivity implements View.OnClickListener{

    MaterialDialog mDisconnectedDialog;
    MaterialDialog mLowPowerDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_car_help);
        initView();
    }

    @Override
    protected void setStatuesBar() {

    }

    private void initView() {
        ImageView cancel = findViewById(R.id.iv_carhelp_cancel);
        cancel.setOnClickListener(this);
        TextView seeDemo = findViewById(R.id.tv_carhelp_see_demo);
        seeDemo.setOnClickListener(this);
        TextView voiceCmd = findViewById(R.id.tv_carhelp_voice_cmd);
        voiceCmd.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_carhelp_cancel:
                finish();
                break;
            case R.id.tv_carhelp_see_demo:
                Intent intent1 = new Intent(CarHelpActivity.this, CarDemoShowActivity.class);
                startActivity(intent1);
                break;
            case R.id.tv_carhelp_voice_cmd:
                Intent intent = new Intent(CarHelpActivity.this, CarAllVoiceCmdActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
