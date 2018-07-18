package com.ubtechinc.alpha.mini.ui.contacts;

import android.Manifest;
import android.content.Context;
import android.support.annotation.NonNull;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.Arrays;
import java.util.List;

/**
 * @Date: 2018/2/9.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] :
 */

public class ContactsPermission {

    private Context ctx;

    private final static int REQUESTCODE = 1002;

    private PermissionContactsCallback callback;

    public ContactsPermission(Context ctx) {
        this.ctx = ctx;
    }

    public void request(PermissionContactsCallback callback){
        this.callback = callback;
        if (AndPermission.hasAlwaysDeniedPermission(ctx, Arrays.asList(Manifest.permission.READ_CONTACTS))) {
            callback.onRationSetting();
        }else{
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
            callback.onSuccessful();
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            callback.onFailure();
        }
    };


    public interface PermissionContactsCallback {
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
