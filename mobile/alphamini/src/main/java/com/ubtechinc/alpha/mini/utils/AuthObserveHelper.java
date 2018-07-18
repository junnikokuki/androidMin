package com.ubtechinc.alpha.mini.utils;

import android.app.Activity;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.ubtechinc.alpha.mini.entity.observable.AuthLive;
import com.ubtechinc.alpha.mini.ui.PageRouter;
import com.ubtechinc.alpha.mini.viewmodel.AuthViewModel;

/**
 * Created by ubt on 2017/11/27.
 */

public class AuthObserveHelper {

    AuthViewModel authViewModel;

    public void authObserve(LifecycleOwner lifecycleOwner, final Activity activity) {
        AuthLive.getInstance().observe(lifecycleOwner, new Observer<AuthLive>() {
            @Override
            public void onChanged(@Nullable AuthLive authLive) {
                if (authLive.getState() == AuthLive.AuthState.FORCE_OFFLINE || authLive.getState() == AuthLive.AuthState.FORBIDDEN) {
                    if (authViewModel == null) {
                        authViewModel = new AuthViewModel();
                    }
                    authViewModel.logout(true);
                    PageRouter.toLogin(activity);
                }
            }
        });
    }
}
