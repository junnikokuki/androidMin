package com.ubtechinc.alpha.mini.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2017/10/30.
 */

public class PermissionStorageRequest {
    private int REQUEST_CODE_PERMISSION_SINGLE = 1000;
    private Context mContext;
    private PermissionStorageCallback mStorageCallback;


    public PermissionStorageRequest(Context context) {
        mContext = context;
    }

    /**
     * 请求授权
     *
     * @param callback 回调结果
     */
    public void request(PermissionStorageCallback callback) {
        this.mStorageCallback = callback;
        boolean isFirstLocation = PreferencesManager.getInstance(mContext).get("permission_storage", false);
        if (isFirstLocation && AndPermission.hasAlwaysDeniedPermission(mContext, Arrays.asList(Permission.STORAGE))) {
            mStorageCallback.onRationSetting();
        } else {
            final MaterialDialog materialDialog = new MaterialDialog(mContext);
            materialDialog.setTitle(R.string.permissions_cellphone_albums_title)
                    .setMessage(R.string.permissions_cellphone_albums_dialogue)
                    .setPositiveButton(R.string.common_btn_allow_access, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PreferencesManager.getInstance(mContext).put("permission_storage", true);
                            materialDialog.dismiss();
                            AndPermission.with(mContext)
                                    .requestCode(REQUEST_CODE_PERMISSION_SINGLE)
                                    .permission(Permission.STORAGE)
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
                    mStorageCallback.onFailure();
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
            mStorageCallback.onSuccessful();

        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            mStorageCallback.onFailure();
        }
    };

    public interface PermissionStorageCallback {
        /**
         * 授权成功
         */
        void onSuccessful();

        /**
         * 授权失败
         */
        void onFailure();

        /**
         * 已经勾选拒绝过
         */
        void onRationSetting();
    }
}


