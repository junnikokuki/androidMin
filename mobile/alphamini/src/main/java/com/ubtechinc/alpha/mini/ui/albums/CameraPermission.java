package com.ubtechinc.alpha.mini.ui.albums;

import android.Manifest;
import android.content.Context;
import android.support.annotation.NonNull;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.Arrays;
import java.util.List;

/**
 * @作者：liudongyang
 * @日期: 18/4/19 21:51
 * @描述:
 */

public class CameraPermission {


    private Context ctx;

    private final static int REQUESTCODE = 1002;

    private PermissionCamerasCallback mCallback;

    public CameraPermission(Context ctx) {
        this.ctx = ctx;
    }


    public void request(PermissionCamerasCallback callback) {
        this.mCallback = callback;
        if (AndPermission.hasAlwaysDeniedPermission(ctx, Arrays.asList(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE))) {
            callback.onRationSetting();
        } else {
            AndPermission.with(ctx)
                    .requestCode(REQUESTCODE)
                    .permission(Permission.CONTACTS)
                    .callback(permissionListener).start();
        }
    }


    /**
     * 回调监听。
     */
    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            mCallback.onSuccessful();
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            mCallback.onFailure();
        }
    };


    public interface PermissionCamerasCallback {
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
