package com.ubtechinc.alpha.mini.ui.update;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.ubtech.utilcode.utils.FileUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseToolbarActivity;
import com.ubtechinc.alpha.mini.net.UpdateInfoModule;
import com.ubtechinc.alpha.mini.viewmodel.AppUpdateViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @作者：liudongyang
 * @日期: 18/6/27 16:25
 * @描述:
 */
public class MiniAppUpdateActivity extends BaseToolbarActivity {

    private TextView tvTime, tvContent, tvSize;

    private TextView tv_UpdateMiniApp;

    private AppUpdateViewModel viewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updating);
        viewModel = AppUpdateViewModel.getViewModel();
        initView();

        checkDownloadEnable();
    }

    private boolean isDownloading;
    private void checkDownloadEnable() {
        isDownloading = UpdateHelper.checkIsDownloading(this, AppUpdateViewModel.getViewModel().getDownloadId());

        if (isDownloading){
            tv_UpdateMiniApp.setEnabled(false);
        }else{
            tv_UpdateMiniApp.setEnabled(true);
        }
    }

    private void initView() {
        initToolbar(findViewById(R.id.titlebar), getString(R.string.app_update_title), View.GONE, false);
        tvTime = findViewById(R.id.text_time);
        tvContent = findViewById(R.id.text_updata_content);
        tvSize = findViewById(R.id.text_size);
        tv_UpdateMiniApp = findViewById(R.id.tv_update_now);
        UpdateInfoModule.AppUpdateModule module = AppUpdateViewModel.getViewModel().getUpdateInfo();
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        tvTime.setText(sdr.format(new Date(module.getReleaseTime() * 1000)));
        tvSize.setText(FileUtils.byte2FitMemorySize(module.getPackageSize()));
        tvContent.setText(module.getReleaseNote());
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    public void update(View view){
        viewModel.downloadMiniApps(this);
        tv_UpdateMiniApp.setEnabled(false);
    }


}
