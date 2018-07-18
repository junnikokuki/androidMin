package com.ubtechinc.alpha.mini.ui.car;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ubtech.utilcode.utils.NetworkUtils;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.alpha.mini.AlphaMiniApplication;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.ActivityManager;
import com.ubtechinc.alpha.mini.common.BaseActivity;
import com.ubtechinc.alpha.mini.common.BaseFragment;
import com.ubtechinc.alpha.mini.common.NetworkChangeManager;
import com.ubtechinc.alpha.mini.constants.Constants;
import com.ubtechinc.alpha.mini.databinding.ActivityCarRemindBinding;
import com.ubtechinc.alpha.mini.entity.RobotInfo;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.event.JimuCarDisconnectRobotEvent;
import com.ubtechinc.alpha.mini.event.JimuCarPowerEvent;
import com.ubtechinc.alpha.mini.im.msghandler.JiMuCarPowerMsgHandler;
import com.ubtechinc.alpha.mini.ui.PageRouter;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 2.configuration不能回调导致页面不正确
 * 3.播放按钮
 * 开车去浪界面
 * */
public class CarRemindActivity extends BaseActivity implements SensorEventListener {

    private String TAG = getClass().getSimpleName();
    private ActivityCarRemindBinding binding;

    private BaseFragment mAuthWordsFragment;

    private BaseFragment mDefineOwnWordsFragment;

    private SensorManager mSensorManager;
    private Sensor mZhongliSensor;

    private boolean isPoiliceMode;
    private int mRunDirection = 0;//1:left  2: right
    private MaterialDialog mLowPowerDialog;
    private MaterialDialog mDisconnectedDialog;
    private MaterialDialog mNoInternetDialog;
    private NetworkChangeManager.NetworkChangedListener mNetworkChangedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_car_remind);
        binding.setEvent(new EventReponse());
        binding.setIsPoliceMode(isPoiliceMode);
        initData();

    }

    private void initData() {
        setWordsContent();
        sensorManagerinit();
        setEditListener();
        addNetworkChangeListener();
        EventBus.getDefault().register(this);
        if (!MyRobotsLive.getInstance().getCurrentRobot().getValue().isOnline()) {
            final MaterialDialog offLineDialog = new MaterialDialog(ActivityManager.getInstance().currentActivity());
            offLineDialog.setTitle(R.string.robot_off_line)
                    .setMessage(R.string.connect_wifi_for_robot)
                    .setPositiveButton(R.string.switch_wifi, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            PageRouter.toChooseWifiActivity(CarRemindActivity.this);
                            offLineDialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(CarRemindActivity.this, CarActivity.class);
                            startActivity(intent);
                            offLineDialog.dismiss();
                        }
                    }).show();
        }

        CarMsgManager.changeDriveMode(CarMsgManager.NOMAL_MODE);
    }

    private void addNetworkChangeListener() {
        if (NetworkChangeManager.sApplication == null) {
            NetworkChangeManager.install(AlphaMiniApplication.getInstance());
        }
        mNetworkChangedListener = new NetworkChangeManager.NetworkChangedListener() {
            @Override
            public void onNetworkChanged(int type) {
                Log.i("test", "addNetworkChangeListener type = " + type);
                if (mNoInternetDialog == null) {
                    mNoInternetDialog = new MaterialDialog(ActivityManager.getInstance().currentActivity());
                }
                if (type == com.ubtechinc.alpha.mini.utils.Constants.NetWork.NO_CONNECTION) {
                    if (!mNoInternetDialog.isShowing()) {
                        mNoInternetDialog.setTitle(R.string.phone_without_internet)
                                .setMessage(R.string.connect_internet)
                                .setPositiveButton(R.string.go_to_set, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        NetworkUtils.openWirelessSettings();
                                        mNoInternetDialog.dismiss();
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(CarRemindActivity.this, CarActivity.class);
                                        startActivity(intent);
                                        mNoInternetDialog.dismiss();
                                    }
                                }).show();
                    }
                }else {
                    Log.i("test","" + mNoInternetDialog.isShowing());
                    if (mNoInternetDialog.isShowing()) {
                        mNoInternetDialog.dismiss();
                    }
                }
            }
        };

        NetworkChangeManager.getInstance().addListener(mNetworkChangedListener);
    }

    @Override
    protected void setStatuesBar() {
        //空实现
    }

    private void setEditListener() {
        binding.etDefineOwn.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEND:
                        String words = binding.etDefineOwn.getText().toString();
                        if (TextUtils.isEmpty(words)){
                            ToastUtils.showShortToast(R.string.car_null_words_hint);
                        }else{
                            ((CarDefineOwnWordFragment)mDefineOwnWordsFragment).addDefineWords(words);
                            CarMsgManager.robotControlCmd(CarMsgManager.CUSTOM_CHAT_MODE, words);
                        }
                        binding.etDefineOwn.setText("");
                        return true;
                }
                return false;
            }
        });
    }

    private void sensorManagerinit() {
        mSensorManager = (SensorManager)getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null) {
            mZhongliSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        if (mSensorManager != null) {
            mSensorManager.registerListener(this, mZhongliSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void setWordsContent() {
        binding.vpWords.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                BaseFragment fragment;
                switch (position) {
                    case 0:
                        if (mAuthWordsFragment == null) {
                            mAuthWordsFragment = new CarAuthWordFragment();
                        }
                        fragment = mAuthWordsFragment;
                        break;
                    case 1:
                        if (mDefineOwnWordsFragment == null) {
                            mDefineOwnWordsFragment = new CarDefineOwnWordFragment();
                        }
                        fragment = mDefineOwnWordsFragment;
                        break;
                    default:
                        if (mAuthWordsFragment == null) {
                            mAuthWordsFragment = new CarAuthWordFragment();
                        }
                        fragment = mAuthWordsFragment;
                        break;
                }
                return fragment;
            }

            @Override
            public int getCount() {
                return 2;
            }
        });

        binding.rgWordsBar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int index = 0;
                switch (checkedId) {
                    case R.id.rb_authroizes_words:
                        index = 0;
                        break;
                    case R.id.rb_own_words:
                        index = 1;
                        break;
                }
                binding.vpWords.setCurrentItem(index, false);
            }
        });
        binding.rgWordsBar.check(R.id.rb_authroizes_words);
        binding.setIsAuthWordsFragment(true);
        binding.vpWords.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        binding.rgWordsBar.check(R.id.rb_authroizes_words);
                        binding.setIsAuthWordsFragment(true);
                        break;
                    case 1:
                        binding.rgWordsBar.check(R.id.rb_own_words);
                        binding.setIsAuthWordsFragment(false);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // 获得x,y,z坐标
        float x = sensorEvent.values[0];//表示手机顶部朝向与正北方的夹角
        float y = sensorEvent.values[1];//表示手机顶部或尾部翘起的角度(横屏的情况下 左晃为正右晃为负)
        float z = sensorEvent.values[2];//表示手机左侧或右侧翘起的角度(横屏情况下 前为负后位正)
//        Log.i("test", "onSensorChanged y = " + y);
        if (y > 1) {
            if (mRunDirection != 2) {
                mRunDirection = 2;
                Log.i("test", "turn left");
                CarMsgManager.jimuCarControlCmd(CarMsgManager.JIMU_CAR_LEFT);
            }
        } else if (y < -1) {
            if (mRunDirection != 1) {
                mRunDirection = 1;
                Log.i("test", "turn right");
                CarMsgManager.jimuCarControlCmd(CarMsgManager.JIMU_CAR_RIGHT);
            }
        } else if (y < 1 && y > -1) {
            if (mRunDirection != 0) {
                mRunDirection = 0;
                Log.i("test", "turn reset direction");
                CarMsgManager.jimuCarControlCmd(CarMsgManager.JIMU_CAR_RESET_DIRECTION);
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public class EventReponse {

        public void exit(View view) {
            Intent intent = new Intent(CarRemindActivity.this, CarActivity.class);
            startActivity(intent);
            CarRemindActivity.this.finish();
        }

        public void askForHelp(View view) {
            Intent intent = new Intent(CarRemindActivity.this, CarHelpActivity.class);
            startActivity(intent);
        }

        public void forward(View view) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        CarMsgManager.jimuCarControlCmd(CarMsgManager.JIMU_CAR_FORWARD);
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                        CarMsgManager.jimuCarControlCmd(CarMsgManager.JIMU_CAR_STOP);
                    }
                    return false;
                }
            });
        }

        public void backward(View view) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        CarMsgManager.jimuCarControlCmd(CarMsgManager.JIMU_CAR_BACK);
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                        CarMsgManager.jimuCarControlCmd(CarMsgManager.JIMU_CAR_STOP);
                    }
                    return false;
                }
            });
        }

        public void modeChange(View view) {
            isPoiliceMode = !isPoiliceMode;
            binding.setIsPoliceMode(isPoiliceMode);
            if (isPoiliceMode) {
                ColorStateList csl = getResources().getColorStateList(R.color.color_car_title_police_mode_selector);
                binding.rbAuthroizesWords.setTextColor(csl);
                binding.rbOwnWords.setTextColor(csl);
                binding.etDefineOwn.setTextColor(ContextCompat.getColor(CarRemindActivity.this, R.color.white));
                CarMsgManager.changeDriveMode(CarMsgManager.POLICE_MODE);
            } else {
                ColorStateList csl = getResources().getColorStateList(R.color.color_car_title_selector);
                binding.rbAuthroizesWords.setTextColor(csl);
                binding.rbOwnWords.setTextColor(csl);
                binding.etDefineOwn.setTextColor(ContextCompat.getColor(CarRemindActivity.this, R.color.first_text));
                CarMsgManager.changeDriveMode(CarMsgManager.NOMAL_MODE);
            }
            if (mAuthWordsFragment != null){
                ((CarAuthWordFragment)mAuthWordsFragment).setMode(isPoiliceMode);
            }
            if (mDefineOwnWordsFragment != null){
                ((CarDefineOwnWordFragment)mDefineOwnWordsFragment).setMode(isPoiliceMode);
            }
        }

        public void simpleModeRing(View view){
            // TODO: 18/7/7 正常模式下按下响铃
            CarMsgManager.robotControlCmd(CarMsgManager.RESET_CHAT_MODE, Constants.JIMU_CAR_BELL);
        }

        public void policeModeRing(View view){
            // TODO: 18/7/7 警车模式下按下响铃
            CarMsgManager.robotControlCmd(CarMsgManager.RESET_CHAT_MODE, Constants.JIMU_CAR_BELL);
        }

        public void policeRinging(View view){
            // TODO: 18/7/7 按下警铃
			CarMsgManager.robotControlCmd(CarMsgManager.RESET_CHAT_MODE, Constants.JIMU_CAR_POLICE_BELL);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManagerinit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CarMsgManager.driveModeCmd(CarMsgManager.EXIT_DRIVE_MODE);
        EventBus.getDefault().unregister(this);
        if (mNetworkChangedListener != null) {
            NetworkChangeManager.getInstance().removeListener(mNetworkChangedListener);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(JimuCarPowerEvent jimuCarPowerEvent) {
        int lowPowerType = jimuCarPowerEvent.getLowPowerType();
        if (mLowPowerDialog == null) {
            mLowPowerDialog = new MaterialDialog(ActivityManager.getInstance().currentActivity());
            mLowPowerDialog.setPositiveButton(R.string.confirm, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(CarRemindActivity.this, CarActivity.class);
                    startActivity(intent);
                }
            });
        }

        switch (lowPowerType) {
            case Constants.CAR_LOW_POWER:
                mLowPowerDialog.setTitle(R.string.car_low_power);
                mLowPowerDialog.setMessage(R.string.car_low_power_message);
                break;
            case Constants.ROBOT_LOW_POWER:
                mLowPowerDialog.setTitle(R.string.robot_low_power);
                mLowPowerDialog.setMessage(R.string.robot_low_power_message);
                break;
            case Constants.ALL_LOW_POWER:
                mLowPowerDialog.setTitle(R.string.low_power);
                mLowPowerDialog.setMessage(R.string.low_power_message);
                break;
            default:
                break;
        }
        if (!mLowPowerDialog.isShowing()) {
            mLowPowerDialog.show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(JimuCarDisconnectRobotEvent jimuCarDisconnectEvent) {

        if (mDisconnectedDialog == null) {
            mDisconnectedDialog = new MaterialDialog(ActivityManager.getInstance().currentActivity());
            mDisconnectedDialog.setTitle(R.string.disconnected_car)
                    .setMessage(R.string.disconnected_message)
                    .setPositiveButton(R.string.sure, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(CarRemindActivity.this, CarActivity.class);
                            startActivity(intent);
                        }
                    });
        }
        if (!mDisconnectedDialog.isShowing()) {
            mDisconnectedDialog.show();
        }
    }
}
