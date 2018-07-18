package com.ubtechinc.alpha.mini.ui.network;

import android.content.Context;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;

import com.ubtechinc.alpha.mini.R;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @desc : 网络加载视图View；包括加载，成功，失败等状态
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2017/11/2
 */

public class NetworkLoadStatusView extends FrameLayout implements INetworkLoadStatus{

    private static final int MAP_SIZE = 4;
    private INetworkLoadStatus.NetworkLoadStatas networkLoadStatas
            = NetworkLoadStatas.NETWORK_LOAD_STATAS_DISCONNECT;
    private View networkDisconnect;
    private View networkLoadding;
    private View networkLoadFail;
    private Map<NetworkLoadStatas, View> map = new HashMap<>(MAP_SIZE);
    private IReload reload;

    public NetworkLoadStatusView(Context context) {
        super(context);
        initView(context);
    }

    public NetworkLoadStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        toLoading();
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_network_load_status, this, true);
    }

    public NetworkLoadStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @Override
    public void toLoading() {
        if(networkLoadding == null) {
            networkLoadding = inflateByViewStubId(R.id.vs_network_loading);
            map.put(NetworkLoadStatas.NETWORK_LOAD_STATAS_LOADING, networkLoadding);
        }
        networkLoadStatas = NetworkLoadStatas.NETWORK_LOAD_STATAS_LOADING;
        switchStatus();
    }

    private View inflateByViewStubId(@IdRes int viewStubId) {
        ViewStub viewStub = findViewById(viewStubId);
        return viewStub.inflate();
    }

    private void switchStatus() {
        Set<NetworkLoadStatas> networkLoadStatases = map.keySet();
        Iterator<NetworkLoadStatas> iterator = networkLoadStatases.iterator();
        while (iterator.hasNext()) {
            NetworkLoadStatas networkLoadStatas = iterator.next();
            View view = map.get(networkLoadStatas);
            if(this.networkLoadStatas == networkLoadStatas) {
                view.setVisibility(VISIBLE);
            } else {
                view.setVisibility(GONE);
            }
        }
    }

    @Override
    public void toNetworkLoadFail() {
        if(networkLoadFail == null) {
            networkLoadFail = inflateByViewStubId(R.id.vs_network_load_fail);
            networkLoadFail.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(reload != null) {
                        reload.reload();
                    }
                }
            });
            map.put(NetworkLoadStatas.NETWORK_LOAD_STATAS_LOAD_FAIL, networkLoadFail);
        }
        networkLoadStatas = NetworkLoadStatas.NETWORK_LOAD_STATAS_LOAD_FAIL;
        switchStatus();
    }

    @Override
    public void toShowContent() {
        networkLoadStatas = NetworkLoadStatas.NETWORK_LOAD_STATAS_CONTENT;
        switchStatus();
    }

    public void setViewContent(View viewContent) {
        addView(viewContent);
        map.put(NetworkLoadStatas.NETWORK_LOAD_STATAS_CONTENT, viewContent);
    }

    @Override
    public void toNetworkDisconnect() {
        if(networkDisconnect == null) {
            networkDisconnect = inflateByViewStubId(R.id.vs_network_disconnect);
            networkDisconnect.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(reload != null) {
                        reload.reload();
                    }
                }
            });
            map.put(NetworkLoadStatas.NETWORK_LOAD_STATAS_DISCONNECT, networkDisconnect);
        }
        networkLoadStatas = NetworkLoadStatas.NETWORK_LOAD_STATAS_DISCONNECT;
        switchStatus();
    }

    public void setReload(IReload reload) {
        this.reload = reload;
    }

    public interface IReload{
        void reload();
    }
}
