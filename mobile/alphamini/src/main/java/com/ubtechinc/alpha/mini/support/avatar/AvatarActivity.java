package com.ubtechinc.alpha.mini.support.avatar;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.gyf.barlibrary.ImmersionBar;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.avatar.entity.User;
import com.ubtechinc.alpha.mini.avatar.viewutils.AvatarUserListManger;
import com.ubtechinc.alpha.mini.entity.PowerValue;
import com.ubtechinc.alpha.mini.entity.observable.AuthLive;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.entity.observable.PowerLive;
import com.ubtechinc.alpha.mini.net.CheckBindRobotModule;
import com.ubtechinc.alpha.mini.utils.AuthObserveHelper;
import com.ubtechinc.alpha.mini.utils.CleanLeakUtils;
import com.ubtechinc.alpha.mini.utils.MsgObserveHelper;
import com.ubtechinc.alpha.mini.utils.RomUtil;
import com.ubtechinc.alpha.mini.viewmodel.RobotAllAccountViewModel;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.ubtechinc.alpha.mini.widget.NotifactionView;

import java.util.ArrayList;
import java.util.List;

public class AvatarActivity extends com.ubtechinc.alpha.mini.avatar.AvatarActivity implements LifecycleOwner {


    private final LifecycleRegistry mRegistry = new LifecycleRegistry(this);

    private MaterialDialog busyDialog;

    private RobotAllAccountViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new RobotAllAccountViewModel();
        AvatarUserListManger.get().setCurrentUserId(AuthLive.getInstance().getUserId());
        new AuthObserveHelper().authObserve(this, this);
        new MsgObserveHelper().msgObserve(this, this, null);
        PowerLive.get().observe(this, new Observer<PowerLive>() {
            @Override
            public void onChanged(@Nullable PowerLive powerLive) {
                PowerValue powerValue = powerLive.getCurrentValue();
                if (powerValue != null) {
                    onPowerChange(powerValue.value, powerValue.isLowPower, powerValue.isCharging);
                }
            }
        });

        init();
    }

    private void init() {
        viewModel.getBindUsers(MyRobotsLive.getInstance().getRobotUserId()).observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                LiveResult result = (LiveResult) o;
                switch (result.getState()){
                    case SUCCESS:
                        List<CheckBindRobotModule.User> users = (List<CheckBindRobotModule.User>) ((LiveResult) o).getData();
                        handleAvatarUrl(users);
                        break;
                    case FAIL:
                        break;
                }
            }

            private void handleAvatarUrl(List<CheckBindRobotModule.User> users) {
                if (users == null || users.size() == 0){
                    Log.i("0524", "handleAvatarUrl: no bind users" );
                    return;
                }
                List<User> avatarUsers = new ArrayList<>();
                for (int i = 0 ; i < users.size() ; i++){
                    User user = new User();
                    user.setAvtar(users.get(i).getUserImage());
                    user.setId(String.valueOf(users.get(i).getUserId()));
                    user.setName(users.get(i).getNickName());
                    avatarUsers.add(user);
                }
                userListManger.setUserAvatars(avatarUsers);
            }
        });


    }

    @Override
    public Lifecycle getLifecycle() {
        return mRegistry;
    }


    @Override
    public void showBusyDialog(String msg) {
        busyDialog = new MaterialDialog(this);
        busyDialog.setMessage(msg);
        busyDialog.setPositiveButton(R.string.i_know, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                busyDialog.dismiss();
                finish();
            }
        });
        busyDialog.show();
    }

    @Override
    public void showTopMessage(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean popNotifyView = checkCanPopNotifyView();
                if (popNotifyView){
                    ToastUtils.showShortToast(msg);
                    return;
                }
                NotifactionView notifactionView = new NotifactionView(AvatarActivity.this);
                notifactionView.setControlImmeration(false);
                notifactionView.setNotifactionText(msg, R.drawable.ic_toast_succeed);
            }
        });
    }


    private boolean checkCanPopNotifyView() {
        int version = RomUtil.getMiuiVersion();
        if (version == -1){//小米系统
            return false;
        }

        if (RomUtil.checkFloatWindowPermission(this)){
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        CleanLeakUtils.fixInputMethodManagerLeak(this);
        super.onDestroy();
    }
}
