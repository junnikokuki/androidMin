package com.ubtechinc.alpha.mini;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreater;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.tencent.ai.tvs.LoginApplication;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.sdk.QbSdk;
import com.ubtech.utilcode.utils.CrashUtils;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.Utils;
import com.ubtech.utilcode.utils.network.NetworkHelper;
import com.ubtechinc.alpha.mini.component.http.AuthInterceptor;
import com.ubtechinc.alpha.mini.component.http.CookieInterceptor;
import com.ubtechinc.alpha.mini.push.AlphaMiniPushManager;
import com.ubtechinc.alpha.mini.qqmusic.model.QQMusicModel;
import com.ubtechinc.alpha.mini.ui.albums.CacheHelper;
import com.ubtechinc.alpha.mini.utils.LeakUtils;
import com.ubtechinc.bluetooth.event.BleScanResultEvent;
import com.ubtechinc.codemaosdk.MiniApi;
import com.ubtechinc.nets.HttpManager;
import com.ubtechinc.nets.im.business.ReceiveMessageBussinesss;
import com.ubtrobot.lib.communation.mobile.CommChannelManager;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.NoSubscriberEvent;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class AlphaMiniApplication extends LoginApplication {

    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.gray_bg_color, android.R.color.black);//全局设置主题颜色
                return new ClassicsHeader(context);//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreater(new DefaultRefreshFooterCreater() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context);
            }
        });
    }

    private static AlphaMiniApplication instance;

    @Override
    public void onCreate() {
        HttpManager.interceptors.add(CookieInterceptor.get());
        HttpManager.interceptors.add(AuthInterceptor.get());
        NetworkHelper.sharedHelper().registerNetworkSensor(getApplicationContext());
        super.onCreate();
        instance = this;
        Utils.init(this);
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(0)         // (Optional) How many method line to show. Default 2
                .methodOffset(5)        // (Optional) Hides internal method calls up to offset. Default 5
                .tag("AlphaMini")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
        CacheHelper.getInstance().init(getApplicationContext());
        ReceiveMessageBussinesss.getInstance().initStoragePath(CacheHelper.getThumbPath(), CacheHelper.getHDPath(), CacheHelper.getOrginPath());
        initBugly();
        AlphaMiniPushManager.getInstance().initPush(getApplicationContext());
        CommChannelManager.init(getApplicationContext());
        QQMusicModel.getInstance(this);
        EventBus.getDefault().register(this);
        CrashUtils.getInstance().init();
        MiniApi.get().init(this);
        initX5WebView();
        LeakUtils.init(this);
        LogUtils.init(true, true,"TAG");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void initBugly() {
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
        strategy.setCrashHandleCallback(new CrashReport.CrashHandleCallback() {


            @Override
            public byte[] onCrashHandleStart2GetExtraDatas(int crashType, String errorType,
                                                           String errorMessage, String errorStack) {
                try {
                    Logger.e("errorMessage: %s", errorMessage);
                    Logger.e("errorStack: %s", errorStack);
                    return "Extra data.".getBytes("UTF-8");
                } catch (Exception e) {
                    return null;
                }
            }

        });

        CrashReport.initCrashReport(getApplicationContext(), "d947af0b7e", BuildConfig.DEBUG, strategy);


    }
    private void initX5WebView() {
        //x5 内核初始化接口
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(),  cb);

    }

    public static AlphaMiniApplication getInstance() {
        return instance;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSubscribeBleScanResult(BleScanResultEvent event) {
        //ignore--FIXME logic.peng
    }

}
