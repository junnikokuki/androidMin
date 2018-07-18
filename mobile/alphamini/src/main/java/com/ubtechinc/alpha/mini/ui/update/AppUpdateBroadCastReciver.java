package com.ubtechinc.alpha.mini.ui.update;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ubtechinc.alpha.mini.entity.AlbumItem;
import com.ubtechinc.alpha.mini.tvs.utils.SharedPreferencesUtils;
import com.ubtechinc.alpha.mini.viewmodel.AppUpdateViewModel;

/**
 * @作者：liudongyang
 * @日期: 18/6/25 16:44
 * @描述: 下载Apk完成的消息
 */
public class AppUpdateBroadCastReciver extends BroadcastReceiver {

    private String TAG = getClass().getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        //处理下载完成
        Log.i(TAG, "onReceive download finish" );
        long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        AppUpdateViewModel model = AppUpdateViewModel.getViewModel();

        if (downloadId == model.getDownloadId()){
            SharedPreferencesUtils.putLong(context, UpdateHelper.DOWNLOAD_ID, -1);
            UpdateHelper.installMiniAppsWithDownloadId(context, downloadId);
        }


    }




}
