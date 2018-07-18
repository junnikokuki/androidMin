package com.ubtechinc.alpha.mini.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.View;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.ui.PageRouter;
import com.ubtechinc.alpha.mini.ui.utils.Utils;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.yanzhenjie.permission.AndPermission;

import static com.ubtechinc.alpha.mini.constants.Constants.CUSTOMERS_CALL;

/**
 * Created by ubt on 2018/3/3.
 */

public class CustomerCallUtils {


    public static void call(Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (AndPermission.hasPermission(activity, Manifest.permission.CALL_PHONE)) {
                Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + CUSTOMERS_CALL));//直接拨打电话
                activity.startActivity(dialIntent);
            } else {
                requestPermission(activity);
            }
        } else {
            Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + CUSTOMERS_CALL));//直接拨打电话
            activity.startActivity(dialIntent);
        }

    }

    public static void requestPermission(final Activity activity) {
        CallPermission permission = new CallPermission(activity);
        permission.request(new CallPermission.PermissionContactsCallback() {
            @Override
            public void onSuccessful() {
                PageRouter.toMobileContact(activity);
            }

            @Override
            public void onFailure() {
                //空实现
            }

            @Override
            public void onRationSetting() {
                popPermissionDialog();
            }


            public void popPermissionDialog() {
                final MaterialDialog dialog = new MaterialDialog(activity);
                dialog.setTitle(R.string.call_permission_hint_title)
                        .setMessage(R.string.call_permission_hint_message)
                        .setNegativeButton(R.string.cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton(R.string.mobile_contacts_name_permission_position_button, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Utils.getAppDetailSettingIntent(activity);
                    }
                }).show();
            }
        });
    }
}
