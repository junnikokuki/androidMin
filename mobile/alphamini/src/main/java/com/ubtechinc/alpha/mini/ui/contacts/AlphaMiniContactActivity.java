package com.ubtechinc.alpha.mini.ui.contacts;

import android.Manifest;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioGroup;

import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseToolbarActivity;
import com.ubtechinc.alpha.mini.databinding.ActivityContactsBinding;
import com.ubtechinc.alpha.mini.ui.PageRouter;
import com.ubtechinc.alpha.mini.ui.utils.Utils;
import com.ubtechinc.alpha.mini.widget.ContactsPopupWindow;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.yanzhenjie.permission.AndPermission;

/**
 * @Date: 2018/1/30.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 机器人通讯录页面
 */

public class AlphaMiniContactActivity extends BaseToolbarActivity implements ContactsPopupWindow.IContactsWindowCallBack {

    public static final String PHONE_KEY = "phone_num";

    private AlphaMiniContactFragment2 mContactFragment;

    private AlphaMiniCallRecordFragment mPhoneHistoryFragment;

    private ContactsPopupWindow popupWindow;

    private ActivityContactsBinding binding;

    private String phoneNum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contacts);
        binding.setReponse(new EventResponse());
        Intent intent = getIntent();
        if (intent != null) {
            phoneNum = intent.getStringExtra(PHONE_KEY);
        }
        initView();
    }

    private void initView() {
        initConnectFailedLayout(R.string.mini_contacts_network_error, R.string.mini_contacts_network_robot_connect_error);

        binding.vpContainer.setOffscreenPageLimit(1);
        binding.vpContainer.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment fragment;
                switch (position) {
                    case 0:
                        if (mContactFragment == null) {
                            mContactFragment = new AlphaMiniContactFragment2();
                            Bundle bundle = new Bundle();
                            bundle.putString("phoneNum", phoneNum);
                            mContactFragment.setArguments(bundle);
                        }
                        fragment = mContactFragment;
                        break;
                    case 1:
                        if (mPhoneHistoryFragment == null) {
                            mPhoneHistoryFragment = new AlphaMiniCallRecordFragment();
                        }
                        fragment = mPhoneHistoryFragment;
                        break;
                    default:
                        if (mContactFragment != null) {
                            mContactFragment = new AlphaMiniContactFragment2();
                            Bundle bundle = new Bundle();
                            bundle.putString("phoneNum", phoneNum);
                            mContactFragment.setArguments(bundle);
                        }
                        fragment = mContactFragment;
                        break;
                }
                return fragment;
            }

            @Override
            public int getCount() {
                return 2;
            }
        });
        binding.vpContainer.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        binding.rgGroup.check(R.id.text_contacts);
                        binding.setIsContactFragment(true);
                        break;
                    case 1:
                        binding.rgGroup.check(R.id.text_phone_history);
                        binding.setIsContactFragment(false);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        binding.rgGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                int index = 0;
                switch (checkedId) {
                    case R.id.text_contacts:
                        index = 0;
                        break;
                    case R.id.text_phone_history:
                        index = 1;
                        break;
                }
                binding.vpContainer.setCurrentItem(index, false);
            }
        });
        binding.rgGroup.check(R.id.text_contacts);
        binding.setIsContactFragment(true);
    }

    @Override
    public void newContacts() {
        PageRouter.toCreateContact(this);
    }




    @Override
    public void importContacts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (AndPermission.hasPermission(this, Manifest.permission.READ_CONTACTS)) {
                PageRouter.toMobileContact(this);
            } else {
                requestPermission();
            }
        } else {
            PageRouter.toMobileContact(this);
        }
    }

    private ContactsPermission permission;
    public void requestPermission() {
        if (permission == null) {
            permission = new ContactsPermission(this);
        }
        permission.request(new ContactsPermission.PermissionContactsCallback() {
            @Override
            public void onSuccessful() {
                ToastUtils.showShortToast("获取权限成功");
                PageRouter.toMobileContact(AlphaMiniContactActivity.this);
            }

            @Override
            public void onFailure() {
                //空实现
            }

            @Override
            public void onRationSetting() {
                popPermissionDialog();
            }


            public void popPermissionDialog(){
                final MaterialDialog dialog = new MaterialDialog(AlphaMiniContactActivity.this);
                dialog.setTitle(R.string.mobile_contacts_name_permission_hint_title)
                        .setMessage(R.string.mobile_contacts_name_permission_hint_message)
                        .setNegativeButton(R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                }).setPositiveButton(R.string.mobile_contacts_name_permission_position_button, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Utils.getAppDetailSettingIntent(AlphaMiniContactActivity.this);
                    }
                }).show();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10000) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == 1) {
                    return;
                }
            }
            PageRouter.toMobileContact(this);
        }
    }

    public class EventResponse {
        public void exitContacts(View view) {
            onBack();
        }

        public void showContactsDialog(View view) {
            if (popupWindow == null) {
                popupWindow = new ContactsPopupWindow(AlphaMiniContactActivity.this);
            }
            popupWindow.show(view);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mContactFragment != null) {
            mContactFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void setConnectFailedLayout(boolean robotConnectState, boolean networkConnectState) {
        super.setConnectFailedLayout(robotConnectState, networkConnectState);
        if (mContactFragment != null) {
            mContactFragment.setConnectFail(!robotConnectState || !networkConnectState);
        }
        if (mPhoneHistoryFragment != null) {
            mPhoneHistoryFragment.setConnectFail(!robotConnectState || !networkConnectState);
        }
    }
}
