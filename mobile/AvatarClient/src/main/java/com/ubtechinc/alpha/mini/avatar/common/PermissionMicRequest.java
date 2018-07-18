package com.ubtechinc.alpha.mini.avatar.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.ubtechinc.alpha.mini.avatar.R;
import com.ubtechinc.alpha.mini.avatar.widget.MaterialDialog;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;
import com.yanzhenjie.permission.SettingService;

import java.util.Arrays;
import java.util.List;

/**
 * @author：liuhai
 * @date：2017/8/12 9:34
 * @modifier：ubt
 * @modify_date：2017/8/12 9:34
 * 定位权限设置
 * version
 */

public class PermissionMicRequest {

    private int REQUEST_CODE_PERMISSION_SINGLE = 1000;
    private Context mContext;
    private PermissionMicCallback mPermissionMicCallbackCallback;
    private boolean isShowRation;

    public PermissionMicRequest(Context context) {
        mContext = context;
    }


    /**
     * 申请麦克风权限
     *
     * @param showRation 是否在此页面显示设置对话框  false  SettingDialog可以毁掉onActivityResult方法
     * @param callback
     */
    public void request(boolean showRation, PermissionMicCallback callback) {
        this.isShowRation = showRation;
        this.mPermissionMicCallbackCallback = callback;
        boolean isFirstLocation = PreferencesManager.getInstance(mContext).get(Constants.PREF_PERMISSION_MIC, false);
        if (isFirstLocation && AndPermission.hasAlwaysDeniedPermission(mContext, Arrays.asList(Permission.MICROPHONE))) {
            if (showRation) {
                showRationDialog();
            } else {
                mPermissionMicCallbackCallback.onRationSetting();
            }
        } else {
            final MaterialDialog materialDialog = new MaterialDialog(mContext);
            materialDialog.setTitle(R.string.permissions_cellphone_microphone_title)
                    .setMessage(R.string.permissions_cellphone_microphone_dialogue)
                    .setPositiveButton(R.string.common_btn_allow_access, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PreferencesManager.getInstance(mContext).put(Constants.PREF_PERMISSION_MIC, true);
                            materialDialog.dismiss();
                            AndPermission.with(mContext)
                                    .requestCode(REQUEST_CODE_PERMISSION_SINGLE)
                                    .permission(Permission.MICROPHONE)
                                    .callback(permissionListener)
                                    // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
                                    // 这样避免用户勾选不再提示，导致以后无法申请权限。
                                    // 你也可以不设置。
                                    .rationale(new RationaleListener() {
                                        @Override
                                        public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                                            rationale.resume();
                                        }
                                    })
                                    .start();
                        }
                    }).setNegativeButton(R.string.common_btn_not_allow, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    materialDialog.dismiss();
                }
            }).show();
        }

    }

    /**
     * 回调监听。
     */
    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            mPermissionMicCallbackCallback.onSuccessful();

        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            mPermissionMicCallbackCallback.onFailure();

        }
    };


    private void showRationDialog() {
        final SettingService settingService = AndPermission.defineSettingDialog(mContext);
        final MaterialDialog settingDialog = new MaterialDialog(mContext);
        settingDialog.setTitle(R.string.permissions_cellphone_microphone_title)
                .setMessage(R.string.permissions_cellphone_microphone_dialogue)
                .setPositiveButton(R.string.common_button_settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        settingDialog.dismiss();
                        settingService.execute();
                    }
                }).setNegativeButton(R.string.common_btn_not_allow, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingDialog.dismiss();
                settingService.cancel();
            }
        }).show();
    }

    public interface PermissionMicCallback {
        /**
         * 授权成功
         */
        void onSuccessful();

        /**
         * 授权失败
         */
        void onFailure();

        /**
         * 已经勾选过不再提醒
         */
        void onRationSetting();
    }
}
