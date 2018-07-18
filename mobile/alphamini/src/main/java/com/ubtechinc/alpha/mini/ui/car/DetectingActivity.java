package com.ubtechinc.alpha.mini.ui.car;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseActivity;

public class DetectingActivity extends BaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_detecteing);
        initView();
        CarMsgManager.checkJimuCarPartCmd();
    }

    @Override
    protected void setStatuesBar() {

    }

    private void initView() {
        ImageView closeImage = findViewById(R.id.iv_detacting_close);
        closeImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_detacting_close:
                Log.i("test", "detacting_close");
                setResult(2);
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("test", "DetectingActivity onDestroy");
    }
}
