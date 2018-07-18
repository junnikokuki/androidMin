package com.ubtechinc.alpha.mini.ui;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.ubtech.utilcode.utils.SPUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.ActivityManager;
import com.ubtechinc.alpha.mini.common.BaseActivity;
import com.ubtechinc.alpha.mini.common.BaseFragment;
import com.ubtechinc.alpha.mini.entity.observable.AuthLive;
import com.ubtechinc.alpha.mini.entity.observable.UpdateLive;
import com.ubtechinc.alpha.mini.tvs.utils.SharedPreferencesUtils;
import com.ubtechinc.alpha.mini.ui.update.UpdateHelper;
import com.ubtechinc.alpha.mini.viewmodel.AppUpdateViewModel;
import com.ubtechinc.alpha.mini.tvs.TVSManager;
import com.ubtechinc.alpha.mini.widget.ControlScrollableViewPager;
import com.ubtechinc.alpha.mini.widget.CustomRadioButton;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.ubtechinc.alpha.mini.widget.TipRadioButton;


import static com.ubtechinc.alpha.mini.ui.PageRouter.SERIAL;
import static com.ubtechinc.alpha.mini.ui.PageRouter.mainStarted;

public class MainActivity extends BaseActivity implements MeFragment.Callback{

    private ControlScrollableViewPager mainContainer;
    private RadioGroup menuBar;

    private RobotFragment robotFragment;
    private BaseFragment codingFragment;
    private BaseFragment meFragment;

    private CustomRadioButton mMeRadioBtn;

    public static final String SHOW_BINDING_GUIDE = "show_binding_guide";

    private ImageView ivBlur;

    private AppUpdateViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TVSManager.getInstance(this).init(this);
        setContentView(R.layout.activity_main);
        viewModel = AppUpdateViewModel.getViewModel();
        initView();
        handleIntent(getIntent());
        SPUtils.get().put(SHOW_BINDING_GUIDE + AuthLive.getInstance().getUserId(), true);
        checkVersionInfo();
    }

    private void checkVersionInfo() {
        viewModel.checkVersionInfo(this).observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                UpdateLive live = (UpdateLive) o;
                switch (live.getUpdateState()){
                    case FORCE_UPDATE:
                        showForceUpdateDialog();
                        break;
                    case SIMPLE_UPDATE:
                        Log.i(TAG, "onChanged:  needUpdate");
                        break;
                    case UNKOWN:
                        Log.i(TAG, "onChanged:  unknow");
                        break;
                    case UPDATED:
                        checkApkFileDelete();
                        break;
                }
            }
        });
    }

    private void checkApkFileDelete() {
        if (UpdateHelper.hasApkFile()){
            UpdateHelper.delete();
        }
    }


    public void showForceUpdateDialog(){
        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.setTitle(R.string.app_update_hint)
                .setMessage(R.string.app_update_message)
                .setCanceledOnTouchOutside(false);

        long downloadId = SharedPreferencesUtils.getLong(this, UpdateHelper.DOWNLOAD_ID, -1);

        if (!UpdateHelper.checkIsDownloading(this, downloadId) && UpdateHelper.hasApkFile()) {
            dialog.setPositiveButton(R.string.app_update_install_now_btn, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    UpdateHelper.installMiniAppsWithLocalUri(MainActivity.this);
                }
            }).show();
            return;
        } else {
            dialog.setPositiveButton(R.string.app_update_now_btn, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    AppUpdateViewModel.getViewModel().downloadMiniApps(MainActivity.this);
                    ActivityManager.getInstance().exit(true);
                }
            }).show();
            return;
        }
    }

    public void initView() {
        mainContainer = findViewById(R.id.main_container);
        menuBar = findViewById(R.id.menu_bar);
        mMeRadioBtn = findViewById(R.id.menu_me);

        ivBlur = findViewById(R.id.iv_blur);
        mainContainer.setOffscreenPageLimit(3);
        mainContainer.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment fragment;
                switch (position) {
                    case 0:
                        if (robotFragment == null) {
                            robotFragment = new RobotFragment();
                            handleIntent(getIntent());
                        }
                        fragment = robotFragment;
                        break;
                    case 1:
                        if (codingFragment == null) {
                            codingFragment = new CodingFragment();
                        }
                        fragment = codingFragment;
                        break;
                    case 2:
                        if (meFragment == null) {
                            meFragment = new MeFragment();
                        }
                        fragment = meFragment;
                        break;
                    default:
                        if (robotFragment != null) {
                            robotFragment = new RobotFragment();
                        }
                        fragment = robotFragment;
                        robotFragment.onShow();
                        break;
                }
                return fragment;
            }

            @Override
            public int getCount() {
                return 3;
            }
        });

        menuBar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                int index = 0;
                switch (i) {
                    case R.id.menu_robot:
                        index = 0;
                        break;
                    case R.id.menu_coding:
                        index = 1;
                        break;
                    case R.id.menu_me:
                        index = 2;
                        break;
                }
                mainContainer.setCurrentItem(index, false);
            }
        });


        menuBar.check(R.id.menu_robot);
        mainContainer.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        menuBar.check(R.id.menu_robot);
                        if(robotFragment != null){
                            robotFragment.onShow();
                        }
                        checkVersionInfo();
                        break;
                    case 1:
                        menuBar.check(R.id.menu_coding);
                        if(robotFragment != null){
                            robotFragment.onHide();
                        }
                        break;
                    case 2:
                        menuBar.check(R.id.menu_me);
                        if(robotFragment != null){
                            robotFragment.onHide();
                        }
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }


    public void dismissMenuBlurBackground(){
        ivBlur.setClickable(false);
        ivBlur.setVisibility(View.GONE);
    }

    public void onPageStepListener(float percent){
        if (ivBlur.getVisibility() == View.VISIBLE){
            ivBlur.setAlpha(percent);
        }
    }

    public void showMenuBlurBackground(){
        ivBlur.setVisibility(View.VISIBLE);
        ivBlur.setClickable(true);
        ivBlur.setImageResource(R.drawable.bg_tabbar);
    }

    public void dismissBackground(View view){
        if(mainContainer.getCurrentItem()  == 0){
            robotFragment.startBottomAnimation();
        }
    }

    private Bitmap screenShot(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        //创建一个DrawingCache的拷贝，因为DrawingCache得到的位图在禁用后会被回收
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);     //禁用DrawingCahce否则会影响性能
        return bitmap;
    }

    public void setForbidScoll(boolean isForbidViewPagerScroll){
        mainContainer.setForbidScoll(isForbidViewPagerScroll);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null) {
            String robotId = intent.getStringExtra(SERIAL);
            Log.d(TAG, "onNewIntent = " + robotId);
            if (mainContainer != null) {
                mainContainer.setCurrentItem(0, false);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainStarted = false;
        robotFragment = null;
        meFragment = null;
        codingFragment = null;
    }

    @Override
    public void onMsgChange(boolean hasNewMsg) {
        mMeRadioBtn.notifyUnRead(hasNewMsg);
    }
}
