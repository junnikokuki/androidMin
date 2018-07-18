package com.ubtechinc.alpha.mini.ui.car;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.haohaohu.frameanimview.FrameAnimView;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseActivity;

public class CarDemoShowActivity extends BaseActivity implements View.OnClickListener{

    private FrameAnimView mAnimation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_car_demo_show);
        initView();
    }

    @Override
    protected void setStatuesBar() {

    }
    private void initView() {
        mAnimation = findViewById(R.id.iv_cardemo_ani);
        mAnimation.start();

        TextView knowText = findViewById(R.id.tv_cardemo_know);
        knowText.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cardemo_know:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
