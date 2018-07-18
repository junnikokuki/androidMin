package com.ubtechinc.alpha.mini.ui.car;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.alpha.JimuCarPower;
import com.ubtechinc.alpha.JimuErrorCodeOuterClass;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseActivity;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.ubtechinc.nets.http.ThrowableWrapper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class CarActivity extends BaseActivity implements View.OnClickListener{

    private static final int MSG_DELAY = 3000;
    private static final int[] mImageResId = {R.drawable.car_slide_img, R.drawable.car_slide_img, R.drawable.car_slide_img, R.drawable.car_slide_img};
	private static final String JIMU_PACKAGE_NAME="com.ubt.jimu";
    private TextView mActionTitle;
    private ImageView mActionImage;
    private ImageView mActionBack;
    private TextView mStartJimu;
    private TextView mStartCar;
    private ViewPager mViewPager;
    private ArrayList<View> mViews = new ArrayList<>();
    private LinearLayout mPointLayout;
    private CarWheelAdapter mWheelAdapter;
    private int newPosition;
    private int oldPosition;
    private MyHandler mMyHandler;
    private Context mConetxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConetxt = this;
        setContentView(R.layout.activity_car);
        mActionTitle = findViewById(R.id.toolbar_title);
        mActionTitle.setText(R.string.car_title);
        mActionImage = findViewById(R.id.toolbar_action);
        mActionImage.setVisibility(View.GONE);
        mActionBack = findViewById(R.id.toolbar_back);
        mActionBack.setOnClickListener(this);
        mViewPager = findViewById(R.id.car_viewpage);
        mPointLayout = findViewById(R.id.ll_point);
        mStartJimu = findViewById(R.id.tv_setup_jimu);
        mStartJimu.setOnClickListener(this);
        mStartCar = findViewById(R.id.tv_start_car);
        mStartCar.setOnClickListener(this);

        for (int i = 0; i < mImageResId.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(mImageResId[i]);
            mViews.add(imageView);

            View pointView = new View(this);
            pointView.setBackgroundResource(R.drawable.car_point_wheel_selector);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(8, 8);
            if (i != 0) {
                params.leftMargin = 10;
            }
            pointView.setEnabled(false);
            mPointLayout.addView(pointView, params);
        }
        mWheelAdapter = new CarWheelAdapter();
        mWheelAdapter.setData(mViews);
        mViewPager.setAdapter(mWheelAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                newPosition = position;
                mPointLayout.getChildAt(oldPosition).setEnabled(false);
                mPointLayout.getChildAt(newPosition).setEnabled(true);
                oldPosition = position;

                mMyHandler.sendMessage(Message.obtain(mMyHandler, MyHandler.UPDATE_PAGE, position, 0));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        mMyHandler.sendEmptyMessage(MyHandler.KEEP_SILENT);
                        break;
                    case ViewPager.SCROLL_STATE_IDLE:
                        mMyHandler.sendEmptyMessageDelayed(MyHandler.UPDATE_MESSAGE, MSG_DELAY);
                        break;
                }
            }
        });

        mViewPager.setCurrentItem(0);
        mMyHandler = new MyHandler(new WeakReference<CarActivity>(this));
        mMyHandler.sendEmptyMessageDelayed(MyHandler.UPDATE_MESSAGE, MSG_DELAY);
        getdensity();

    }

    private void getdensity() {
        DisplayMetrics dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();
        Log.i("test", " dm.density = " + dm.density + " dm.densityDpi = " + dm.densityDpi + " dm.widthPixels = " + dm.widthPixels + " dm.heightPixels = " + dm.heightPixels);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_back:
                finish();
                break;
            case R.id.tv_setup_jimu:
                if (isJimuInstall()) {
                    Log.i("test", "5555555555555");

                    JimuAppUtil.startJimuAPP();
                } else {
                    Log.i("test", "66666666666666");
                    startAppStore();
                }
                break;
            case R.id.tv_start_car:
                showLoadingDialog();
                CarMsgManager.getJimuCarPower(new CarModeInterface.ICarPowerListener() {
                    @Override
                    public void onSuccess(JimuCarPower.GetJimuCarPowerResponse response) {
                        dismissDialog();
                        if (response.getErrorCode() == JimuErrorCodeOuterClass.JimuErrorCode.REQUEST_SUCCESS) {
                            if (response.getRobotPower().getPowerPercentage() < 20) {
                                mMyHandler.sendEmptyMessage(MyHandler.SHOW_LOW_POWER);
                            }else {
                                Intent intent = new Intent(CarActivity.this, CarWaitingConnectActivity.class);
                                startActivity(intent);
                            }
                        }
                    }

                    @Override
                    public void onFail(ThrowableWrapper e) {
                        dismissDialog();
                    }
                });
                break;
        }
    }

    private boolean isJimuInstall() {
        Log.i("test", "isJimuInstall()");
        PackageManager packageManager = getApplicationContext().getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                if (pinfo.get(i).packageName.equalsIgnoreCase(JIMU_PACKAGE_NAME)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void startAppStore() {
        Uri uri = Uri.parse("market://details?id=" + JIMU_PACKAGE_NAME);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private static class MyHandler extends Handler {

        private static final int UPDATE_MESSAGE = 100;
        private static final int UPDATE_PAGE = 101;
        private static final int KEEP_SILENT = 102;
        private static final int SHOW_LOW_POWER = 103;

        private int mCurrentItem = 0;
        WeakReference<CarActivity> mWeakReference;

        public MyHandler(WeakReference<CarActivity> wk) {
            mWeakReference = wk;
        }

        @Override
        public void dispatchMessage(Message msg) {
            CarActivity viewPageActivity = mWeakReference.get();
            if (viewPageActivity == null) {
                return;
            }
            if (hasMessages(UPDATE_MESSAGE)) {
                removeMessages(UPDATE_MESSAGE);
            }
            switch (msg.what) {
                case UPDATE_MESSAGE:
                    mCurrentItem++;
                    LogUtils.i("test", "dispatchMessage mCurrentItem = " + mCurrentItem);
                    if (mCurrentItem == 3) {
                        mCurrentItem = 0;
                    }
                    viewPageActivity.mViewPager.setCurrentItem(mCurrentItem);
                    sendEmptyMessageDelayed(UPDATE_MESSAGE, CarActivity.MSG_DELAY);
                    break;
                case UPDATE_PAGE:
                    mCurrentItem = msg.arg1;
                    break;
                case SHOW_LOW_POWER:
                    final MaterialDialog lowPowerDialog = new MaterialDialog(viewPageActivity.mConetxt);
                    lowPowerDialog.setTitle(R.string.robot_low_power)
                            .setMessage(R.string.robot_low_power_message)
                            .setPositiveButton(R.string.sure, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    lowPowerDialog.dismiss();
                                }
                            })
                            .show();
                    break;
            }
            super.dispatchMessage(msg);
        }

    }
}
