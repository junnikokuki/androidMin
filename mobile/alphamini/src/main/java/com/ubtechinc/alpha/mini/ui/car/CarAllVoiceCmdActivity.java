package com.ubtechinc.alpha.mini.ui.car;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseActivity;
import com.ubtechinc.alpha.mini.constants.Constants;
import com.ubtechinc.alpha.mini.event.JimuCarDisconnectRobotEvent;
import com.ubtechinc.alpha.mini.event.JimuCarPowerEvent;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class CarAllVoiceCmdActivity extends BaseActivity implements View.OnClickListener{

    private RecyclerView mCmdList;
    private CarCmdListAdapter mCarCmdAdapter;
    private MaterialDialog mLowPowerDialog;
    private MaterialDialog mDisconnectedDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_car_all_voice_cmd);
        initView();
    }

    @Override
    protected void setStatuesBar() {

    }

    private void initView() {
        mCmdList = findViewById(R.id.rv_voicecmd_all_cmd);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mCmdList.setLayoutManager(layoutManager);
        mCmdList.addItemDecoration(new SpacesItemDecoration(30));
        mCarCmdAdapter = new CarCmdListAdapter(CarAllVoiceCmdActivity.this);
        mCmdList.setAdapter(mCarCmdAdapter);
        ImageView backImage = findViewById(R.id.iv_voicecmd_back);
        backImage.setOnClickListener(this);
        ImageView cancelImage = findViewById(R.id.iv_voicecmd_cancel);
        cancelImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_voicecmd_back:
            case R.id.iv_voicecmd_cancel:
                finish();
                break;
        }
    }
}
