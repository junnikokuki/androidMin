package com.ubtechinc.alpha.mini.ui.car;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.gyf.barlibrary.BarHide;
import com.gyf.barlibrary.ImmersionBar;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseActivity;

public class DetectedResultActivity extends BaseActivity implements View.OnClickListener{

    private int mErrorPart;
    private ImageView mMotorImage;
    private ImageView mIrDamage;
    private TextView mTitleText;
    private TextView mLitteleTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_detected_result);

        initView();
    }

    @Override
    protected void setStatuesBar() {

    }

    private void initView() {
        TextView backText = findViewById(R.id.tv_detectedresult_back);
        backText.setOnClickListener(this);
        ImageView closeImage = findViewById(R.id.tv_detectedresult_close);
        closeImage.setOnClickListener(this);
        TextView redetect = findViewById(R.id.tv_detectedresult_redetect);
        redetect.setOnClickListener(this);
        TextView jimuVideoText = findViewById(R.id.tv_detectedresult_jimuvideo);
        jimuVideoText.setOnClickListener(this);

        mMotorImage = findViewById(R.id.iv_motor_damage);
        mIrDamage = findViewById(R.id.iv_ir_damage);
        mTitleText = findViewById(R.id.tv_detectresult_title);
        mLitteleTitle = findViewById(R.id.tv_detectresult_little_title);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mErrorPart = getIntent().getIntExtra("data",0);
        if (mErrorPart == 1) {
            mMotorImage.setVisibility(View.VISIBLE);
            mIrDamage.setVisibility(View.GONE);
            mTitleText.setText(R.string.dont_find_motor);
            mLitteleTitle.setText(R.string.install_motor);
        } else if (mErrorPart == 2) {
            mMotorImage.setVisibility(View.GONE);
            mIrDamage.setVisibility(View.VISIBLE);
            mTitleText.setText(R.string.dont_find_IR_sensor);
            mLitteleTitle.setText(R.string.install_IR_sensor);
        } else if (mErrorPart == 3) {
            mMotorImage.setVisibility(View.VISIBLE);
            mIrDamage.setVisibility(View.VISIBLE);
            mTitleText.setText(R.string.dont_IR_sensor_and_motor);
            mLitteleTitle.setText(R.string.install_IR_sensor_and_motor);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_detectedresult_back:
                break;
            case R.id.tv_detectedresult_close:
                finish();
                break;
            case R.id.tv_detectedresult_redetect:
                Intent intent = new Intent(DetectedResultActivity.this, DetectingActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.tv_detectedresult_jimuvideo:
                JimuAppUtil.startJimuAPP();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("test", "DetectedResultActivity requestCode = " + requestCode + " resultCode = " + resultCode);
        finish();
    }

    @Override
    protected void onDestroy() {
        Log.i("test", "DetectedResultActivity onDestroy");
        super.onDestroy();
    }
}
