package com.ubtechinc.alpha.mini.avatar;

import android.content.Context;
import android.view.View;

import com.ubtechinc.alpha.mini.avatar.common.PermissionMicRequest;
import com.ubtechinc.alpha.mini.avatar.widget.MaterialDialog;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.SettingService;

public class PermissionManager {


    public static void requestMicPermission(Context context, PermissionMicRequest.PermissionMicCallback callback) {
        if (callback == null) {
            return;
        }
        if (AndPermission.hasPermission(context, Permission.MICROPHONE)) {
            callback.onSuccessful();
        } else {
            PermissionMicRequest permissionMicRequest = new PermissionMicRequest(context);
            permissionMicRequest.request(true, callback);
        }

    }

    public static void showRationDialog(Context context) {
        final SettingService settingService = AndPermission.defineSettingDialog(context);
        final MaterialDialog settingDialog = new MaterialDialog(context);
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
}
