package com.ubtechinc.alpha.mini.ui.upgrade;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.alpha.DetectUpgradeProto;
import com.ubtechinc.alpha.FirmwareUpgradeProto;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseFragment;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.viewmodel.UpgradeViewModel;
import com.ubtechinc.alpha.mini.widget.LoadingDialog;
import com.ubtechinc.alpha.mini.widget.ProgressBtn;

/**
 * Created by ubt on 2018/4/8.
 */

public class UpgradeUpdateFragment extends BaseFragment implements View.OnClickListener {

    private UpgradeViewModel upgradeViewModel;

    private TextView upgradeMsg;

    private TextView startDownloadBtn;

    private TextView startUpgradeBtn;

    private ProgressBtn downloadProgressBtn;

    private TextView versionView;

    private Handler handler;

    private String version = "V1.0";

    private String updateNote = "";

    private DetectUpgradeProto.DetectState state;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        initData();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                getDownloadProgress();
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upgrade_update, null, false);
    }

    private void init() {
        upgradeMsg = getActivity().findViewById(R.id.upgrade_msg);
        startDownloadBtn = getActivity().findViewById(R.id.download_btn);
        startUpgradeBtn = getActivity().findViewById(R.id.upgrade_btn);
        downloadProgressBtn = getActivity().findViewById(R.id.download_progress);
        versionView = getActivity().findViewById(R.id.upgrade_version);

        startDownloadBtn.setOnClickListener(this);
        startUpgradeBtn.setOnClickListener(this);
    }

    private void initData() {
        upgradeMsg.setText(updateNote);
        versionView.setText(getString(R.string.upgrade_new_version, version));
        switchBtn();
    }

    private void switchBtn() {
        if (state == null) {
            showDownloadBtn();
        } else {
            switch (state) {
                case HAS_DOWNLOADED:
                    showUpgradeBtn();
                    break;
                case IS_DOWNLOADING:
                    showDownloadProgress(getString(R.string.downloading_progress, 0), 0f);
                    getDownloadProgress();
                    break;
                case HAS_UPDATE:
                    showDownloadBtn();
                    break;
            }
        }
    }

    public void setUpgradeViewModel(UpgradeViewModel upgradeViewModel) {
        this.upgradeViewModel = upgradeViewModel;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.download_btn:
                startDownload(false);
                break;
            case R.id.upgrade_btn:
                startUpgrade();
                break;
        }
    }

    private void startDownload(final boolean silence) {
        if (!silence) {
            LoadingDialog.getInstance(getActivity()).show();
        }
        upgradeViewModel.startDownload().observe(this, new Observer<LiveResult>() {
            @Override
            public void onChanged(@Nullable LiveResult o) {
                switch (o.getState()) {
                    case SUCCESS:
                        o.removeObserver(this);
                        if (!silence) {
                            showDownloadProgress(getString(R.string.downloading_progress, "0%"), 0f);
                            LoadingDialog.dissMiss();
                        }
                        startGetDownloadProgress();

                        break;
                    case FAIL:
                        o.removeObserver(this);
                        LoadingDialog.dissMiss();
                        toastError(getString(R.string.download_fail));
                        break;
                }
            }
        });
    }

    private void startUpgrade() {
        LoadingDialog.getInstance(getActivity()).show();
        upgradeViewModel.startUpgrade().observe(UpgradeUpdateFragment.this, new Observer<LiveResult>() {
            @Override
            public void onChanged(@Nullable LiveResult o) {
                switch (o.getState()) {
                    case SUCCESS:
                        FirmwareUpgradeProto.UpgradeState state = (FirmwareUpgradeProto.UpgradeState) o.getData();
                        o.removeObserver(this);
                        LoadingDialog.dissMiss();
                        if (state != null && state == FirmwareUpgradeProto.UpgradeState.OK) {
                            showStartUpgradeDialog();
                        } else {
                            showStartUpgradeFailDialog(state);
                        }
                        break;
                    case FAIL:
                        o.removeObserver(this);
                        LoadingDialog.dissMiss();
                        showStartUpgradeFailDialog(null);
                        break;
                }
            }
        });
    }

    private void getDownloadProgress() {
        upgradeViewModel.getDownloadProgress().observe(this, new Observer<LiveResult>() {
            @Override
            public void onChanged(@Nullable LiveResult o) {
                switch (o.getState()) {
                    case SUCCESS:
                        int progress = (int) o.getData();
                        if (progress >= 100) {
                            showUpgradeBtn();
                        } else {
                            String percent = progress + "%";
                            showDownloadProgress(getString(R.string.downloading_progress, percent), progress);
                            startGetDownloadProgress();
                        }
                        break;
                    case FAIL:
                        startDownload(true);
                        break;
                }
            }
        });
    }

    private void showDownloadBtn() {
        startDownloadBtn.setVisibility(View.VISIBLE);
        startUpgradeBtn.setVisibility(View.GONE);
        downloadProgressBtn.setVisibility(View.GONE);
    }

    private void showUpgradeBtn() {
        startDownloadBtn.setVisibility(View.GONE);
        startUpgradeBtn.setVisibility(View.VISIBLE);
        downloadProgressBtn.setVisibility(View.GONE);
    }

    private void showDownloadProgress(String text, float percent) {
        startDownloadBtn.setVisibility(View.GONE);
        startUpgradeBtn.setVisibility(View.GONE);
        downloadProgressBtn.setVisibility(View.VISIBLE);
        downloadProgressBtn.setProgressText(text, percent);
    }

    private void showStartUpgradeDialog() {
        showMsgDialog(getString(R.string.upgrade_start), getString(R.string.upgrade_start_tips), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });
    }

    private void showStartUpgradeFailDialog(FirmwareUpgradeProto.UpgradeState state) {
        String message = getString(R.string.upgrade_fail);
        if (state != null) {
            switch (state) {
                case LOW_POWER:
                    message = getString(R.string.upgrade_fail_low_power);
                    break;
                case NO_CHARGING:
                    message = getString(R.string.upgrade_fail_no_charging);
                    break;
                default:
                    break;
            }
        }
        showMsgDialog(null, message);
    }

    private void startGetDownloadProgress() {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(0, 3000);
        }
    }

    public void setUpdateInfo(String version, String updateNote, DetectUpgradeProto.DetectState state) {
        this.version = version;
        this.updateNote = updateNote;
        this.state = state;
        if (versionView != null) {
            versionView.setText(getString(R.string.upgrade_new_version, version));
        }
        if (upgradeMsg != null) {
            upgradeMsg.setText(updateNote);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeMessages(0);
        handler = null;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            handler.removeMessages(0);
        }else{
            switchBtn();
        }
    }
}

