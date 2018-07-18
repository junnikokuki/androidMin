package com.ubtechinc.alpha.mini.qqmusic.model;

import android.arch.lifecycle.Observer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tencent.ai.tvs.BindingListener;
import com.tencent.ai.tvs.LoginProxy;
import com.tencent.ai.tvs.comm.CommOpInfo;
import com.tencent.ai.tvs.info.CPMemberManager;
import com.tencent.ai.tvs.info.DeviceCPMemberManager;
import com.tencent.ai.tvs.info.DeviceManager;
import com.ubtech.utilcode.utils.CollectionUtils;
import com.ubtech.utilcode.utils.NetworkUtils;
import com.ubtechinc.alpha.mini.entity.RobotInfo;
import com.ubtechinc.alpha.mini.entity.observable.AuthLive;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.net.AddQQMusicRecord;
import com.ubtechinc.alpha.mini.net.ChangeRobotEquipmentIdModule;
import com.ubtechinc.alpha.mini.net.QueryQQMusicRecord;
import com.ubtechinc.alpha.mini.qqmusic.IQQMusicStats;
import com.ubtechinc.alpha.mini.qqmusic.QQMusicStatsImp;
import com.ubtechinc.alpha.mini.qqmusic.ReceiveStats;
import com.ubtechinc.alpha.mini.qqmusic.RobotPresentStats;
import com.ubtechinc.alpha.mini.repository.RobotAuthorizeReponsitory;
import com.ubtechinc.alpha.mini.repository.datasource.IRobotAuthorizeDataSource;
import com.ubtechinc.alpha.mini.tvs.BuildConfig;
import com.ubtechinc.alpha.mini.tvs.TVSManager;
import com.ubtechinc.bluetooth.IQueryPresent;
import com.ubtechinc.bluetooth.UbtBluetoothManager;
import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.ThrowableWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author htoall
 * @Description: QQ音乐数据
 * @date 2018/4/18 下午5:42
 * @copyright TCL-MIE
 */
public class QQMusicModel {

    // 默认dsn，用于查询会员状态
    private static final String DATA_CHANGE = "android.intent.action.DATE_CHANGED";
    public static final String DEFAULT_DSN = "060100KFK180120003Q";
    private static final String TAG = "QQMusicModel";
    private static final Object NOT_OPNE_STRING = "0000-00-00";
    private List<RobotInfo> robotInfoList;
    private static QQMusicModel instance;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private ExecutorService executorServiceSingle = Executors.newSingleThreadExecutor();
    private IQQMusicStats iqqMusicStats = QQMusicStatsImp.getInstance();
    private LoginProxy proxy;
    private Context context;
    private volatile String currentQueryDsn;
    private volatile boolean deviceStatusRequest;
    private String ExpireTime;
    // 记录目标数目
    private volatile int purposeNumber;
    private QQMusicStatsImp qqMusicStatsImp;
    private volatile boolean isVip;
    private DataChangeReceiver dataChangeReceiver;
    private List<RobotPresentStats> robotPresentStatsList = new ArrayList<>(3);
    // 判断是否更新完成会员状态
    private boolean hasUpdateMemberStatus;
    // 等待QQSDK查询结果完成和后台结果查询完成
    private CountDownLatch countDownLatch = new CountDownLatch(2);
    // 用于等待结果完成
    private Object lock = new Object();
    private volatile boolean memberStatusRequest;
    private String updateDsn;
    // 记录之前的礼物领取，上传服务器
    private List<RobotPresentStats> beforerobotPresentStatsList = new ArrayList<>(3);
    private IDeviceStats deviceStats;
    private volatile boolean queryDevice;


    private Map<String,String> robotNumMap = new HashMap<>();


    private QQMusicModel(Context context) {
        Log.i(TAG, " QQMusicModel ");
        init(context);
    }

    public void init(final Context context) {
        Log.i(TAG, " QQMusicModel init");

        proxy = LoginProxy.getInstance(TVSManager.WX_APPId, TVSManager.QQ_OPEN_APPId, context);

        qqMusicStatsImp = QQMusicStatsImp.getInstance();
        if (context != null) {
            this.context = context.getApplicationContext();
        } else if (this.context == null) {
            throw new IllegalArgumentException("QQMusicModel no init!");
        }

        // 日期改变更新
        dataChangeReceiver = new DataChangeReceiver();
        context.registerReceiver(dataChangeReceiver, new IntentFilter(DATA_CHANGE));

        // 更新会员状态，每天都要更新，避免过期
        AuthLive.getInstance().observeForever(new Observer<AuthLive>() {
            @Override
            public void onChanged(@Nullable AuthLive authLive) {
                if (AuthLive.getInstance().getState().equals(AuthLive.AuthState.LOGINED)) {
                    executorServiceSingle.submit(new Runnable() {
                        @Override
                        public void run() {
                            queryMemberStatus();
                        }
                    });
                }
            }
        });
        // 监听机器人列表变化
        MyRobotsLive.getInstance().getRobots().observeForever(new Observer<LiveResult>() {
            public void onChanged(@Nullable final LiveResult robotInfoList) {
                Log.i(TAG, " QQMusicModel onChanged -- robotInfoList :" + robotInfoList);
                updateMemberStatus();
            }
        });

        // 监听当前用户变化
        MyRobotsLive.getInstance().getCurrentRobot().observeForever(new Observer<RobotInfo>() {
            @Override
            public void onChanged(@Nullable RobotInfo robotInfo) {
                if (robotInfo == null) {
                    QQMusicStatsImp.getInstance().isHasPresent(false);
                    QQMusicStatsImp.getInstance().reset();
                    return;
                }
                Log.i(TAG, "getCurrentRobot onChanged robotInfo : " + robotInfo + " robotInfo.isMaster() : " + robotInfo.isMaster());
                qqMusicStatsImp.setIsMainAccount(robotInfo.isMaster());
                boolean hasPresent = false;
                Log.i(TAG, "onChanged : " + robotPresentStatsList);
                for (RobotPresentStats robotPresentStats : robotPresentStatsList) {
                    if (robotPresentStats.getRobotSn().equals(robotInfo.getUserId())) {
                        if (!robotPresentStats.isReceive()) {
                            hasPresent = true;
                        }
                        break;
                    }
                }
                Log.i(TAG, "onChanged hasPresent : " + hasPresent);
                qqMusicStatsImp.isHasPresent(hasPresent);
            }
        });
        bind();
    }

    private void copyList(List<RobotPresentStats> dst, List<RobotPresentStats> source,List<RobotInfo> robotInfosMaster) {
        Log.i(TAG, " copyList 1 : dst : " + dst.size() + "  " + source.size());
        if (source != null && !source.isEmpty()) {
            dst.clear();
            for (RobotPresentStats robotPresentStats : source) {
                for (RobotInfo robotInfo : robotInfosMaster) {
                    if(robotInfo.getRobotUserId().equals(robotPresentStats.getRobotSn())){
                        dst.add(robotPresentStats);
                        break;
                    }
                }
            }

        }else{

            dst.clear();
        }
        Log.i(TAG, " copyList 2 : dst : " + dst.size());
    }

    public void bind() {
        proxy.setBindingListener(new BindingListener() {
            @Override
            public void onSuccess(int i, CommOpInfo var2) {
                Log.i(TAG, " BindingListener -- onChanged onSuccess -- id :" + i + Thread.currentThread().getId() + " updateDsn : " + updateDsn + " queryDevice : " + queryDevice);
                if (i == BIND_GET_DEVICE_STATUS_TYPE) {
                    synchronized (currentQueryDsn) {
                        Log.i(TAG, " BindingListener -- deviceStatus :" + DeviceCPMemberManager.getInstance().deviceStatus);
                        if (queryDevice) {
                            queryDevice = DeviceCPMemberManager.getInstance().deviceStatus == 1;
                            currentQueryDsn.notifyAll();
                            return;
                        }
                        if (updateDsn != null) {
                            Log.i(TAG, " BindingListener -- updateDsn -- deviceStatus :" + DeviceCPMemberManager.getInstance().deviceStatus);
                            if (updateDsn.equals(currentQueryDsn) && DeviceCPMemberManager.getInstance().deviceStatus == 0) {
                                updateDsn = null;
                                // 表示已领取
                                RobotAuthorizeReponsitory.getInstance().addReceiveSuc(context, updateDsn, "", "", new ResponseListener<AddQQMusicRecord.Response>() {
                                    @Override
                                    public void onError(ThrowableWrapper e) {
                                        // TODO
                                        Log.i(TAG, " onError ");
                                    }

                                    @Override
                                    public void onSuccess(AddQQMusicRecord.Response response) {
                                        updateMemberStatus();
                                        Log.i(TAG, " onSuccess response : " + response);
                                    }
                                });
                            }
                        }
                        deviceStatusRequest = false;
                        if (DeviceCPMemberManager.getInstance().deviceStatus == 1) {
                            Log.i(TAG, " currentQueryDsn : " + currentQueryDsn + " MyRobotsLive.getInstance().getCurrentRobotId() : " + MyRobotsLive.getInstance().getCurrentRobotId().getValue());
                            if (currentQueryDsn.equals(MyRobotsLive.getInstance().getRobotUserId())) {
                                qqMusicStatsImp.isHasPresent(true);
                            }
                            RobotInfo info = MyRobotsLive.getInstance().getRobotById(currentQueryDsn);
                            if(info != null){
                                String robotEquipmentId = info.getEquipmentSeq();

                                robotPresentStatsList.add(new RobotPresentStats(currentQueryDsn, robotEquipmentId,false));

                                Log.d(TAG, " qqMusicStatsImp.getReceiveStats() :" + qqMusicStatsImp.getReceiveStats());
                                Log.d(TAG, " qqMusicStatsImp.isHasPresent() :" + qqMusicStatsImp.hasPresent());
                                if (!qqMusicStatsImp.getReceiveStats().equals(ReceiveStats.RECEIVE_CONTINUE)) {
                                    qqMusicStatsImp.setReceiveStats(ReceiveStats.RECEIVE_NOW);
                                }
                            }
                        }
                        currentQueryDsn.notifyAll();
                        checkPurpose();
                    }
                } else if (i == BIND_GET_MEMBER_STATUS_TYPE) {
                    synchronized (lock) {
                        memberStatusRequest = false;
                        hasUpdateMemberStatus = true;
                        Log.i(TAG, " onChanged onSuccess -- CPMemberManager : " + CPMemberManager.getInstance());
                        Log.i(TAG, " onChanged onSuccess -- CPMemberManager.getInstance().vip : " + CPMemberManager.getInstance().vip);
                        Log.i(TAG, " onChanged onSuccess -- CPMemberManager : " + CPMemberManager.getInstance().expireTime);
                        if (CPMemberManager.getInstance().vip == 1) {
                            isVip = true;
                            qqMusicStatsImp.setReceiveStats(ReceiveStats.ALREADY_OPEN);
                            qqMusicStatsImp.setTime(CPMemberManager.getInstance().expireTime);
                        } else {
                            if (NOT_OPNE_STRING.equals(CPMemberManager.getInstance().expireTime)) {
                                qqMusicStatsImp.setReceiveStats(ReceiveStats.NOT_OPEN);
                            } else {
                                qqMusicStatsImp.setReceiveStats(ReceiveStats.ALREADY_OUTTIME);
                                qqMusicStatsImp.setTime(CPMemberManager.getInstance().expireTime);
                            }
                        }
                        lock.notifyAll();
                    }
                }
            }

            @Override
            public void onError(int i, CommOpInfo var2) {
                Log.i(TAG, " BindingListener --  onChanged1 onError onError i : " + i + " queryDevice : " + queryDevice);
                if (i == BIND_GET_DEVICE_STATUS_TYPE) {
                    synchronized (currentQueryDsn) {
                        if (queryDevice) {
                            queryDevice = DeviceCPMemberManager.getInstance().deviceStatus == 1;
                            currentQueryDsn.notifyAll();
                            return;
                        }
                        if (updateDsn != null) {
                            if (DeviceCPMemberManager.getInstance().deviceStatus == 0) {
                                RobotAuthorizeReponsitory.getInstance().addReceiveSuc(context, updateDsn, "", "", new ResponseListener<AddQQMusicRecord.Response>() {
                                    @Override
                                    public void onError(ThrowableWrapper e) {
                                        // TODO
                                    }

                                    @Override
                                    public void onSuccess(AddQQMusicRecord.Response response) {
                                        updateMemberStatus();
                                    }
                                });
                            }
                            deviceStatusRequest = false;
                            currentQueryDsn.notifyAll();
                            return;
                        }
                        deviceStatusRequest = false;
                        currentQueryDsn.notifyAll();
                        checkPurpose();
                    }
                } else if (i == BIND_GET_MEMBER_STATUS_TYPE) {
                    synchronized (lock) {
                        memberStatusRequest = false;
                        hasUpdateMemberStatus = true;
                        if (currentQueryDsn.equals(MyRobotsLive.getInstance().getRobotUserId())) {
                            qqMusicStatsImp.isHasPresent(false);
                        }
                        lock.notifyAll();
                    }
                }
            }
        });
    }

    public void updateMemberStatus() {
        updateMemberStatus(false);
    }

    /**
     * 更新会员状态
     */
    public void updateMemberStatus(boolean bindagain) {
        bind();
        List<RobotInfo> robotInfos = MyRobotsLive.getInstance().getRobots().getData();
        if (CollectionUtils.isEmpty(robotInfos)) {
            robotPresentStatsList.clear();
            qqMusicStatsImp.setNeedNotify(true);
            qqMusicStatsImp.isHasPresent(false);
            qqMusicStatsImp.setIsMainAccount(false);
            return;
        }
        final List<RobotInfo> robotInfosMaster = new ArrayList<>(robotInfos.size());
        for (RobotInfo robotInfo : robotInfos) {
            if (robotInfo.isMaster()) {
                robotInfosMaster.add(robotInfo);
            }
        }
        executorServiceSingle.submit(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, " submit Thread.id : " + Thread.currentThread().getId());
                copyList(beforerobotPresentStatsList, robotPresentStatsList,robotInfosMaster);
                robotPresentStatsList.clear();

//                qqMusicStatsImp.isHasPresent(false);
//                qqMusicStatsImp.setNeedNotify(false);
                Log.i(TAG, " before requestMember ");

                requestMember();
                // 加一次对应网络请求结果
                purposeNumber = robotInfosMaster.size() + 1;
                for (RobotInfo robotInfo : robotInfosMaster) {
                    Log.i(TAG, " robotInfo.getUserId() : " + robotInfo.getUserId());
                    executorService.submit(new MemberRunnable(robotInfo.getUserId()));
                }
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, " getReceiveStatus ===============================: ");
                        RobotAuthorizeReponsitory.getInstance().getReceiveStatus(new ResponseListener<QueryQQMusicRecord.Response>() {
                            @Override
                            public void onError(ThrowableWrapper e) {
                                Log.i(TAG, " onError Thread.id : " + Thread.currentThread().getId());
                                qqMusicStatsImp.setNeedNotify(true);
                                checkPurpose();
                            }

                            @Override
                            public void onSuccess(QueryQQMusicRecord.Response response) {
                                Log.i(TAG, " onSuccess Thread.id : " + Thread.currentThread().getId() + " resultBean : " + response.getData().getResult());
                                qqMusicStatsImp.setNeedNotify(true);
                                for (QueryQQMusicRecord.Response.DataBean.ResultBean resultBean : response.getData().getResult()) {
                                    if (qqMusicStatsImp.getReceiveStats().equals(ReceiveStats.RECEIVE_NOW)) {
                                        qqMusicStatsImp.setReceiveStats(ReceiveStats.RECEIVE_CONTINUE);
                                    } else if (qqMusicStatsImp.getReceiveStats().equals(ReceiveStats.NOT_OPEN)) {
                                        qqMusicStatsImp.setReceiveStats(ReceiveStats.ALREADY_OUTTIME);
                                    }

                                    robotPresentStatsList.add(new RobotPresentStats(resultBean.getEquipmentId(),null, true));

                                }
                                checkPurpose();
                            }
                        });
                    }
                });
            }
        });
    }

    private void checkPurpose() {
        Log.i(TAG, " checkPurpose -- purposeNumber : " + purposeNumber + " robotPresentStatsList : " + robotPresentStatsList);
        if (--purposeNumber == 0) {
            compareList();
            QQMusicStatsImp.getInstance().setRobotPresentStatss(robotPresentStatsList);
        }
    }

    private void compareList() {
        Log.i(TAG, " compareList beforerobotPresentStatsList.size : " + beforerobotPresentStatsList.size() + " robotPresentStatsList : " + robotPresentStatsList.size());
        for (RobotPresentStats robotPresentStats : beforerobotPresentStatsList) {
            if (!robotPresentStats.isReceive()) {
                boolean find = false;
                for (RobotPresentStats robotPresentStats1 : robotPresentStatsList) {
                    if (robotPresentStats.getRobotSn().equals(robotPresentStats1.getRobotSn())) {
                        find = true;
                    }
                }
                Log.i(TAG, " compareList -- find : " + find);
                if (!find) {
                    RobotAuthorizeReponsitory.getInstance().addReceiveSuc(context, robotPresentStats.getRobotSn(), "", "", new ResponseListener<AddQQMusicRecord.Response>() {
                        @Override
                        public void onError(ThrowableWrapper e) {
                            Log.i(TAG, " compareList -- onError : " + Log.getStackTraceString(e));
                        }

                        @Override
                        public void onSuccess(AddQQMusicRecord.Response response) {
                            Log.i(TAG, " compareList -- onSuccess response : " + response);

                        }
                    });
                }
            }
        }
    }

    public void updateDevice(final String dsn) {
        bind();
        Log.i(TAG, " updateDevice -- dsn :" + dsn);
        if (dsn == null) {
            return;
        }
        updateDsn = dsn;
//        executorService.submit(new MemberRunnable(dsn));
        updateMemberStatus();
    }

    public static QQMusicModel getInstance() {
        return getInstance(null);
    }

    public static QQMusicModel getInstance(Context context) {
        if (instance == null) {
            synchronized (QQMusicModel.class) {
                if (instance == null) {
                    instance = new QQMusicModel(context);
                }
            }
        }
        return instance;
    }

    private void queryDeviceStatus(String dsn) {
        Log.i(TAG, " queryDeviceStatus -- dsn : " + dsn + " platform : " + TVSManager.getInstance(context).getELoginPlatform());
        final RobotInfo info = MyRobotsLive.getInstance().getRobotById(dsn);
        Log.i(TAG, " queryDeviceStatus -- info : " + info);
        if (info != null) {
            if (info.getEquipmentSeq() != null) {
                DeviceManager deviceManager = new DeviceManager();

                deviceManager.deviceOEM = TVSManager.DEVICE_OEM;
                deviceManager.deviceType = TVSManager.DEVICE_TYPE;
                deviceManager.dsn = info.getEquipmentSeq();
                deviceManager.productId = TVSManager.PRODUCT_ID;

                proxy.getDeviceStatus(TVSManager.getInstance(context).getELoginPlatform(), deviceManager);
                Log.i(TAG, " queryDeviceStatus -- dsn : " + dsn + " thread id : " + Thread.currentThread().getId());
                Log.i(TAG, " queryDeviceStatus -- equipment : " + info.getEquipmentSeq() + " thread id : " + Thread.currentThread().getId());
            } else {
                RobotAuthorizeReponsitory.getInstance().changeRobotEquipmentId(Arrays.asList(info.getRobotUserId()), new IRobotAuthorizeDataSource.GetRobotEquipmentIdCallback() {
                    @Override
                    public void onRobotEquipmentId(List<ChangeRobotEquipmentIdModule.Model> models) {
                        if (!CollectionUtils.isEmpty(models)) {
                            String equipmentId = models.get(0).getSeqNum();
                            DeviceManager deviceManager = new DeviceManager();

                            deviceManager.deviceOEM = TVSManager.DEVICE_OEM;
                            deviceManager.deviceType = TVSManager.DEVICE_TYPE;
                            info.setEquipmentSeq(equipmentId);
                            deviceManager.dsn = info.getEquipmentSeq();
                            deviceManager.productId = TVSManager.PRODUCT_ID;
                            proxy.getDeviceStatus(TVSManager.getInstance(context).getELoginPlatform(), deviceManager);
                            Log.i(TAG, " queryDeviceStatus -- equipment : " + info.getEquipmentSeq() + " thread id : " + Thread.currentThread().getId());
                        }else{

                            Log.i(TAG, " onDataNotAvailable -- equipment : " + info.getEquipmentSeq() + " deviceStatusRequest : " + deviceStatusRequest);
                            // 等待操作完成
                            synchronized (currentQueryDsn) {
                                if (deviceStatusRequest) {
                                    deviceStatusRequest = false;
                                    currentQueryDsn.notifyAll();
                                    checkPurpose();
                                }
                            }
                        }
                    }

                    @Override
                    public void onDataNotAvailable(ThrowableWrapper e) {
                        Log.i(TAG, " onDataNotAvailable -- equipment : " + info.getEquipmentSeq() + " deviceStatusRequest : " + deviceStatusRequest);
                        // 等待操作完成
                        synchronized (currentQueryDsn) {
                            if (deviceStatusRequest) {
                                deviceStatusRequest = false;
                                currentQueryDsn.notifyAll();
                                checkPurpose();
                            }
                        }
                    }
                });
            }

        }else{

            Log.i(TAG, " onDataNotAvailable -- info null ");
            // 等待操作完成
            synchronized (currentQueryDsn) {
                if (deviceStatusRequest) {
                    deviceStatusRequest = false;
                    currentQueryDsn.notifyAll();
                    checkPurpose();
                }
            }
        }
/*        DeviceManager deviceManager = new DeviceManager();
        deviceManager.deviceOEM = DEVICE_OEM;
        deviceManager.deviceType = DEVICE_TYPE;
        deviceManager.dsn = dsn;
        deviceManager.productId = PRODUCT_ID;
        proxy.getDeviceStatus(TVSManager.getInstance(context).getELoginPlatform(), deviceManager);*/
        Log.i(TAG, " queryDeviceStatus -- dsn : " + dsn + " thread id : " + Thread.currentThread().getId());
    }

    public void destroy() {
        context.unregisterReceiver(dataChangeReceiver);
    }

    private void queryMemberStatus() {
        Log.i(TAG, " queryMemberStatus ");
        DeviceManager deviceManager = new DeviceManager();
        deviceManager.deviceOEM = TVSManager.DEVICE_OEM;
        deviceManager.deviceType = TVSManager.DEVICE_TYPE;
        deviceManager.dsn = DEFAULT_DSN;
        currentQueryDsn = DEFAULT_DSN;
        deviceManager.productId = TVSManager.PRODUCT_ID;
        memberStatusRequest = true;
        proxy.getMemberStatus(TVSManager.getInstance(context).getELoginPlatform(), deviceManager);
        Log.i(TAG, " queryMemberStatus finish");
        synchronized (lock) {
            try {
                if (memberStatusRequest) {
                    lock.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG, " queryMemberStatus finish");
    }

    private class MemberRunnable implements Runnable {

        private String dsn;

        public MemberRunnable(String dsn) {
            this.dsn = dsn;
        }

        @Override
        public void run() {
            Log.i(TAG, " MemberRunnable -- run ");
            currentQueryDsn = dsn;
            deviceStatusRequest = true;
            queryDeviceStatus(dsn);
            // 等待操作完成
            synchronized (currentQueryDsn) {
                try {
                    if (deviceStatusRequest) {
                        currentQueryDsn.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.i(TAG, " MemberRunnable -- run -- 2");
        }
    }

    private class DataChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            hasUpdateMemberStatus = false;
            if (action.equals(DATA_CHANGE)) {
                if (NetworkUtils.isConnected()) {
                    requestMember();
                }
            }
        }
    }

    private void requestMember() {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                queryMemberStatus();
            }
        });
    }

    public interface IDeviceStats {
        void hasPresent(boolean hasPresent);
    }

}
