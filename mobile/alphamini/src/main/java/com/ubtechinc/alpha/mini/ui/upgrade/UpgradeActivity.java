package com.ubtechinc.alpha.mini.ui.upgrade;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.ubtech.utilcode.utils.CollectionUtils;
import com.ubtechinc.alpha.DetectUpgradeProto;
import com.ubtechinc.alpha.FirmwareUpgradeProto;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseToolbarActivity;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.viewmodel.UpgradeViewModel;

import java.util.List;

import static com.ubtechinc.alpha.mini.ui.upgrade.UpgradeStateFragment.STATE_FAIL;

/**
 * 固件升级
 * Created by ubt on 2018/4/8.
 */

public class UpgradeActivity extends BaseToolbarActivity {


    private UpgradeStateFragment upgradeStateFragment;

    private UpgradeNewestFragment upgradeNewestFragment;

    private UpgradeUpdateFragment upgradeUpdateFragment;

    private UpgradeViewModel upgradeViewModel;

    private boolean lastRobotConnectState;
    private boolean lastMobileConnectState;

    private boolean isSaveInstanceStateCall;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isSaveInstanceStateCall = false;
        setContentView(R.layout.activity_upgrade);
        initView();
        upgradeViewModel = new UpgradeViewModel();
        showState(UpgradeStateFragment.STATE_LOADING);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        isSaveInstanceStateCall = true;
    }

    private void initView() {
        View toolBar = findViewById(R.id.toolbar);
        initToolbar(toolBar, getString(R.string.headware_update), View.GONE, false);
    }

    private void showState(int state) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (upgradeUpdateFragment != null) {
            fragmentTransaction.hide(upgradeUpdateFragment);
        }
        if (upgradeNewestFragment != null) {
            fragmentTransaction.hide(upgradeNewestFragment);
        }
        if (upgradeStateFragment == null) {
            upgradeStateFragment = new UpgradeStateFragment();
            fragmentTransaction.add(R.id.upgrade_content_view, upgradeStateFragment, UpgradeStateFragment.class.getSimpleName());
            fragmentTransaction.commit();
        } else {
            fragmentTransaction.show(upgradeStateFragment);
            fragmentTransaction.commit();
        }
        upgradeStateFragment.setState(state);
    }

    private void showUpdate(String version, String updateNote, DetectUpgradeProto.DetectState state) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (upgradeStateFragment != null) {
            fragmentTransaction.hide(upgradeStateFragment);
        }
        if (upgradeNewestFragment != null) {
            fragmentTransaction.hide(upgradeNewestFragment);
        }
        if (upgradeUpdateFragment == null) {
            upgradeUpdateFragment = new UpgradeUpdateFragment();
            upgradeUpdateFragment.setUpgradeViewModel(upgradeViewModel);
            fragmentTransaction.add(R.id.upgrade_content_view, upgradeUpdateFragment, UpgradeUpdateFragment.class.getSimpleName());
            fragmentTransaction.commit();
        } else {
            fragmentTransaction.show(upgradeUpdateFragment);
            fragmentTransaction.commit();
        }
        upgradeUpdateFragment.setUpdateInfo(version,updateNote,state);
    }

    private void showNewestVersion(String version) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (upgradeStateFragment != null) {
            fragmentTransaction.hide(upgradeStateFragment);
        }
        if (upgradeUpdateFragment != null) {
            fragmentTransaction.hide(upgradeUpdateFragment);
        }
        if (upgradeNewestFragment == null) {
            upgradeNewestFragment = new UpgradeNewestFragment();
            fragmentTransaction.add(R.id.upgrade_content_view, upgradeNewestFragment, UpgradeNewestFragment.class.getSimpleName());
            fragmentTransaction.commit();
        } else {
            fragmentTransaction.show(upgradeNewestFragment);
            fragmentTransaction.commit();
        }
        upgradeNewestFragment.setVersion(version);
    }

    @Override
    protected void setConnectFailedLayout(boolean robotConnectState, boolean networkConnectState) {
        if(isSaveInstanceStateCall){
            return;
        }
        if (!networkConnectState) {
            showState(UpgradeStateFragment.STATE_MOBILE_OFFLINE);
        } else if (!robotConnectState) {
            showState(UpgradeStateFragment.STATE_ROBOT_OFFLINE);
        } else {
            if(!lastMobileConnectState || !lastRobotConnectState){
                showState(UpgradeStateFragment.STATE_LOADING);
                checkVersion();
            }
        }
        lastMobileConnectState = networkConnectState;
        lastRobotConnectState = robotConnectState;
    }

    private void checkVersion() {
        upgradeViewModel.checkUpgrade().observe(this, new Observer<LiveResult>() {
            @Override
            public void onChanged(@Nullable LiveResult o) {
                switch (o.getState()) {
                    case SUCCESS:
                        o.removeObserver(this);
                        DetectUpgradeProto.DetectUpgrade response = (DetectUpgradeProto.DetectUpgrade) o.getData();
                        switch (response.getState()) {
                            case HAS_UPDATE:
                                toUpdate(response);
                                break;
                            case NO_UPDATE:
                                toNewestVersion(response);
                                break;
                            case IS_DOWNLOADING:
                                toUpdate(response);
                                break;
                            case UNRECOGNIZED:
                                showState(STATE_FAIL);
                                break;
                            case HAS_DOWNLOADED:
                                toUpdate(response);
                                break;
                        }
                        break;
                    case FAIL:
                        o.removeObserver(this);
                        showState(STATE_FAIL);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void toNewestVersion(DetectUpgradeProto.DetectUpgrade response) {
        DetectUpgradeProto.FirmwareInfo androidInfo = getAndroidInfo(response);
        if (androidInfo != null) {
            String version = androidInfo.getVersion();
            showNewestVersion(version);
        } else {
            showState(STATE_FAIL);
        }
    }

    private void toUpdate(DetectUpgradeProto.DetectUpgrade response) {
        DetectUpgradeProto.FirmwareInfo androidInfo = getAndroidInfo(response);
        if (androidInfo != null) {
            String version = androidInfo.getVersion();
            String updateNote = androidInfo.getReleaseNote();
            DetectUpgradeProto.DetectState state = response.getState();
            showUpdate(version, updateNote,state);
        } else {
            showState(STATE_FAIL);
        }
    }

    private DetectUpgradeProto.FirmwareInfo getAndroidInfo(DetectUpgradeProto.DetectUpgrade response) {
        List<DetectUpgradeProto.FirmwareInfo> infos = response.getFirmwareInfoList();
        DetectUpgradeProto.FirmwareInfo androidInfo = null;
        if (!CollectionUtils.isEmpty(infos)) {
            for (DetectUpgradeProto.FirmwareInfo info : infos) {
                if (info.getName().equals("android")) {
                    androidInfo = info;
                    break;
                }
            }
            if (androidInfo == null) {
                androidInfo = infos.get(0);
            }
        }
        return androidInfo;
    }
}
