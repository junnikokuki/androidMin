package com.ubtechinc.alpha.mini.avatar;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;


public abstract class BaseAlexaFragememt extends Fragment {
    protected Context mContext;
    protected View mContentView;
    protected Bundle bundle;
    protected Activity contactActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        contactActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(mContentView == null){
            mContentView = onCreateFragmentView(inflater, container, savedInstanceState);
        }
        return mContentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        initView();
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        // 清除所有跟视图相关的资源
        super.onDestroyView();
//        mContentView = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 得到参数
     *
     * @return
     */
    public Bundle getBundle() {
        return bundle;
    }

    /**
     * 设置参数
     *
     * @param bundle
     */
    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public abstract void initView();

    /**
     * [onCreateFragmentView abstract method, child class must implements the
     * method]
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public abstract View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);




}
