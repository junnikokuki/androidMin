package com.ubtechinc.alpha.mini.ui;

import android.arch.lifecycle.Observer;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.ubtech.utilcode.utils.FileUtils;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseToolbarActivity;
import com.ubtechinc.alpha.mini.databinding.ActivitySettingBindingBinding;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.ui.albums.CacheHelper;
import com.ubtechinc.alpha.mini.ui.setting.MobileNumberChangeEvent;
import com.ubtechinc.alpha.mini.viewmodel.AuthViewModel;
import com.ubtechinc.alpha.mini.viewmodel.MobileViewModel;
import com.ubtechinc.alpha.mini.widget.ActionSheetDialog;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @作者：liudongyang
 * @日期: 18/4/17 21:30
 * @描述:
 */

public class SettingMobileActivity extends BaseToolbarActivity {

    private ActivitySettingBindingBinding bindingBinding;

    private AuthViewModel authViewModel;

    private MobileViewModel mobileViewModel;

    private String mBindPhone;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingBinding = DataBindingUtil.setContentView(this, R.layout.activity_setting_binding);
        authViewModel = new AuthViewModel();
        mobileViewModel = new MobileViewModel();
        EventBus.getDefault().register(this);
        initView();
        getBindPhone();
    }

    private void initView() {
        initToolbar(bindingBinding.toolbar, "设置", View.GONE, false);
        initConnectFailedLayout(R.string.net_work_error, R.string.connect_error);
        refreshCache();
    }

    private void getBindPhone(){
        mobileViewModel.getBindMobileInfo().observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                LiveResult liveResult = (LiveResult) o;
                switch (liveResult.getState()){
                    case LOADING:
                        break;
                    case FAIL:
                        liveResult.removeObserver(this);
                        ToastUtils.showShortToast("获取绑定手机失败");
                        break;
                    case SUCCESS:
                        liveResult.removeObserver(this);
                        setBindPhone((String) liveResult.getData());
                        break;
                }
            }

            private void setBindPhone(String userPhone) {
                mBindPhone = userPhone;
                if (!TextUtils.isEmpty(userPhone) && userPhone.length() == 13){//去掉地区号的86
                    String sub = userPhone.substring(2, 13);
                    Log.i(TAG, "initView: "+ sub);
                    bindingBinding.tvMobile.setText(sub);
                }
            }
        });
    }

    @Override
    protected void setConnectFailedLayout(boolean robotConnectState, boolean networkConnectState) {
        super.setConnectFailedLayout(robotConnectState, networkConnectState);
    }

    public void toMobileBind(View view) {
        PageRouter.toMobileDetail(this, mBindPhone);
    }

    public void clearCache(View view){
        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.setMessage(R.string.setting_clear_cacheMessage)
                .setNegativeButton(R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                }).setPositiveButton(R.string.confirm, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                boolean isDelete = FileUtils.deleteDir(CacheHelper.getCachePath());
                if (isDelete){
                    toastSuccess(getString(R.string.setting_clear_cache_success));
                }else{
                    toastError(getString(R.string.setting_clear_cache_failed));
                }
                refreshCache();
            }
        }).show();
    }

    public void refreshCache(){
        String size = FileUtils.getDirSize(CacheHelper.getCachePath());
        if (!TextUtils.isEmpty(size)){
            bindingBinding.tvCache.setText(size);
        }else{
            bindingBinding.tvCache.setText("0B");
        }
    }

    public void logout(View view){
        new ActionSheetDialog(this).builder()
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .setTitle(getString(R.string.logout_tips))
                .addSheetItem(getString(R.string.logout),
                        ActionSheetDialog.SheetItemColor.Red,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                logout();
                            }
                        }).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MobileNumberChangeEvent event) {
        String phone =  event.getNewPhoneNumber();
        if (!TextUtils.isEmpty(phone)){
            bindingBinding.tvMobile.setText(phone);
        }
    }

    public void logout(){
        authViewModel.logout(false);
        finish();
        PageRouter.toLogin(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
