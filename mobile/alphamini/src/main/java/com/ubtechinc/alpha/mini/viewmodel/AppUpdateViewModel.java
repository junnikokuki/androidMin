package com.ubtechinc.alpha.mini.viewmodel;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.entity.observable.UpdateLive;
import com.ubtechinc.alpha.mini.net.UpdateInfoModule;
import com.ubtechinc.alpha.mini.repository.UpdateRepository;
import com.ubtechinc.alpha.mini.tvs.utils.SharedPreferencesUtils;
import com.ubtechinc.alpha.mini.ui.update.UpdateHelper;

/**
 * @作者：liudongyang
 * @日期: 18/6/25 16:39
 * @描述: 与升级逻辑相关
 */
public class AppUpdateViewModel {

    private UpdateRepository repository;

    private String TAG = getClass().getSimpleName();

    private UpdateInfoModule.AppUpdateModule updateInfo;

    public  long downloadId = -1;

    private boolean needUpdateMiniApp;

    private AppUpdateViewModel() {
        repository = new UpdateRepository();
    }

    private static AppUpdateViewModel viewModel;

    public static AppUpdateViewModel getViewModel() {
        if (viewModel == null){
            synchronized (AppUpdateViewModel.class){
                if (viewModel == null){
                    viewModel = new AppUpdateViewModel();
                }
            }
        }
        return viewModel;
    }

    public LiveResult checkVersionInfo(Context context) {
        final UpdateLive live = UpdateLive.getInstance();
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            repository.checkVersionInfo("v"+info.versionName, new UpdateRepository.IVersionInfoCallback() {
                @Override
                public void onSuccess(UpdateInfoModule.AppUpdateModule response) {
                    AppUpdateViewModel.this.updateInfo = response;
                    if (response == null){
                        live.setState(UpdateLive.UpdateState.UPDATED);
                        needUpdateMiniApp = false;
                        LogUtils.i(TAG, "onSuccess no need update ");
                        return;
                    }

                    if (response.isIsForced()) {
                        needUpdateMiniApp = true;
                        live.setState(UpdateLive.UpdateState.FORCE_UPDATE);
                    } else {
                        needUpdateMiniApp = true;
                        live.setState(UpdateLive.UpdateState.SIMPLE_UPDATE);
                    }
                }

                @Override
                public void onError(int errorCode) {
                    LogUtils.i(TAG, "onError checkVersionInfo fail");
                    needUpdateMiniApp = false;
                    live.setState(UpdateLive.UpdateState.UNKOWN);
                }
            });
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return live;
    }


    public void downloadMiniApps(Context context) {
        if (updateInfo == null){
            Log.e(TAG, "update downloadMiniApps: requst failed");
        }
        if (UpdateHelper.checkIsDownloading(context, downloadId)){
            ToastUtils.showShortToast(R.string.app_update_downloading);
            return;
        }
        if (UpdateHelper.hasApkFile() && UpdateHelper.isApkValitle(updateInfo.getPackageMd5(), "mini.apk")) {
            UpdateHelper.installMiniAppsWithLocalUri(context);
            return;
        }
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(updateInfo.getPackageUrl()));
        request.setDestinationInExternalPublicDir("/mini/", "mini.apk");
        downloadId =  manager.enqueue(request);
        SharedPreferencesUtils.putLong(context, UpdateHelper.DOWNLOAD_ID, downloadId);
        Log.i(TAG, "enque download Id" +  downloadId);
    }

    public long getDownloadId(){
        return downloadId;
    }

    public boolean isNeedUpdateMiniApp() {
        return needUpdateMiniApp;
    }

    public UpdateInfoModule.AppUpdateModule getUpdateInfo() {
        return updateInfo;
    }

    public void release(){
        if (viewModel != null){
            viewModel = null;
        }
    }

}
