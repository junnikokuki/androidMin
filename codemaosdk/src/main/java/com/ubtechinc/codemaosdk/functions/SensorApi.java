package com.ubtechinc.codemaosdk.functions;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.notification.NotificationCenter;
import com.ubtech.utilcode.utils.notification.Subscriber;
import com.ubtechinc.codemao.CodeMaoGetInfraredDistance;
import com.ubtechinc.codemao.CodeMaoObserveButteryStatus;
import com.ubtechinc.codemao.CodeMaoObserveFallClimb;
import com.ubtechinc.codemao.CodeMaoObserveHeadRacket;
import com.ubtechinc.codemao.CodeMaoObserveInfraredDistance;
import com.ubtechinc.codemao.CodeMaoObserveVolumeKeyPress;
import com.ubtechinc.codemaosdk.HandlerUtils;
import com.ubtechinc.codemaosdk.Phone2RobotMsgMgr;
import com.ubtechinc.codemaosdk.Statics;
import com.ubtechinc.codemaosdk.event.ButteryEvent;
import com.ubtechinc.codemaosdk.event.FallDownEvent;
import com.ubtechinc.codemaosdk.event.HeadRacketEvent;
import com.ubtechinc.codemaosdk.event.InfraredDistanceEvent;
import com.ubtechinc.codemaosdk.event.RobotPostureEvent;
import com.ubtechinc.codemaosdk.event.VolumeKeyEvent;
import com.ubtechinc.codemaosdk.interfaces.ISensor;
import com.ubtechinc.protocollibrary.communite.ICallback;
import com.ubtechinc.protocollibrary.communite.ThrowableWrapper;
import com.ubtechinc.protocollibrary.protocol.CmdId;

/**
 * @Deseription
 * @Author tanghongyu
 * @Time 2018/4/3 9:10
 */

public class SensorApi {

    ISensor.ObserveFallDownState observeFallDownState;
    ISensor.ObserveBattery observeBattery;
    ISensor.ObserveHeadRacket observeHeadRacket;
    ISensor.ObserveVolumeKeyPress observeVolumeKeyPress;
    ISensor.ObserveRobotPosture observeRobotPosture;
    ISensor.ObserveInfraredDistance observeInfraredDistance;
    private SensorApi() {}
    private static SensorApi sensorApi = new SensorApi();
    public static SensorApi get() {
        if(sensorApi == null) {
            synchronized (SensorApi.class) {
                sensorApi = new SensorApi();
            }

        }
        return sensorApi;
    }

    public void getInfraredDistance(final ISensor.GetInfraredDistance getInfraredDistance) {
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_GET_DISTANCE_REQUEST, Statics.PROTO_VERSION, null, "", new ICallback< CodeMaoGetInfraredDistance.GetInfraredDistanceResponse>() {
            @Override
            public void onSuccess(final  CodeMaoGetInfraredDistance.GetInfraredDistanceResponse data) {
                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        getInfraredDistance.onGetDistance(data.getDistance());
                    }
                });

            }

            @Override
            public void onError(final ThrowableWrapper e) {
                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        getInfraredDistance.onFail(e.getErrorCode());

                    }
                });
            }
        });
    }

    public void registerFalldownEvent(final ISensor.ObserveFallDownState observeFallDownState ) {
        this.observeFallDownState = observeFallDownState;
        CodeMaoObserveFallClimb.ObserveFallClimbRequest.Builder builder =   CodeMaoObserveFallClimb.ObserveFallClimbRequest.newBuilder();
        builder.setIsSubscribe(true);
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_OBSERVE_FALL_DOWN_REQUEST, Statics.PROTO_VERSION, builder.build(), "",null);
        NotificationCenter.defaultCenter().subscriber(FallDownEvent.class, fallDownEventSubscriber);
    }

    public void unregisterFalldownEvent() {
        CodeMaoObserveFallClimb.ObserveFallClimbRequest.Builder builder =  CodeMaoObserveFallClimb.ObserveFallClimbRequest.newBuilder();
        builder.setIsSubscribe(false);
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_OBSERVE_FALL_DOWN_REQUEST, Statics.PROTO_VERSION, builder.build(), "",null);
        NotificationCenter.defaultCenter().unsubscribe(FallDownEvent.class, fallDownEventSubscriber);
        this.observeFallDownState = null;
    }



    public void registerBatteryEvent(final ISensor.ObserveBattery observeBattery) {
        this.observeBattery = observeBattery;
        CodeMaoObserveButteryStatus.ObserveButteryStatusRequest.Builder builder = CodeMaoObserveButteryStatus.ObserveButteryStatusRequest.newBuilder();
        builder.setIsSubscribe(true);
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_OBSERVE_BUTTERY_STATUS_REQUEST, Statics.PROTO_VERSION, builder.build(), "",null);
        NotificationCenter.defaultCenter().subscriber(ButteryEvent.class, butteryEventSubscriber);
    }

    public void unregisterBatteryEvent() {
        CodeMaoObserveButteryStatus.ObserveButteryStatusRequest.Builder builder =  CodeMaoObserveButteryStatus.ObserveButteryStatusRequest.newBuilder();
        builder.setIsSubscribe(false);
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_OBSERVE_BUTTERY_STATUS_REQUEST, Statics.PROTO_VERSION, builder.build(), "",null);
        NotificationCenter.defaultCenter().unsubscribe(ButteryEvent.class, butteryEventSubscriber);
        this.observeBattery = null;
    }

    public void observeHeadRacketEvent(final ISensor.ObserveHeadRacket observeHeadRacket) {
        this.observeHeadRacket = observeHeadRacket;
        CodeMaoObserveHeadRacket.ObserveHeadRacketRequest.Builder builder =  CodeMaoObserveHeadRacket.ObserveHeadRacketRequest.newBuilder();
        builder.setIsSubscribe(true);
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_OBSERVE_RACKET_HEAD_REQUEST, Statics.PROTO_VERSION, builder.build(), "",null);
        NotificationCenter.defaultCenter().subscriber(HeadRacketEvent.class, headRacketEventSubscriber);
    }
    public void unregisterHeadRacketEvent() {
        CodeMaoObserveHeadRacket.ObserveHeadRacketRequest.Builder builder =  CodeMaoObserveHeadRacket.ObserveHeadRacketRequest.newBuilder();
        builder.setIsSubscribe(false);
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_OBSERVE_RACKET_HEAD_REQUEST, Statics.PROTO_VERSION, builder.build(), "",null);
        NotificationCenter.defaultCenter().unsubscribe(HeadRacketEvent.class, headRacketEventSubscriber);
        this.observeHeadRacket = null;

    }
    public void observeVolumeKeyEvent(final ISensor.ObserveVolumeKeyPress observeVolumeKeyPress) {
        this.observeVolumeKeyPress = observeVolumeKeyPress;
        CodeMaoObserveVolumeKeyPress.ObserveVolumeKeyPressRequest.Builder builder =  CodeMaoObserveVolumeKeyPress.ObserveVolumeKeyPressRequest.newBuilder();
        builder.setIsSubscribe(true);
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_OBSERVE_VOLUME_KEY_PRESS_REQUEST, Statics.PROTO_VERSION, builder.build(), "",null);
        NotificationCenter.defaultCenter().subscriber(VolumeKeyEvent.class, volumeKeyEventSubscriber);
    }
    public void unregisterVolumeKeyEvent() {

        CodeMaoObserveVolumeKeyPress.ObserveVolumeKeyPressRequest.Builder builder =  CodeMaoObserveVolumeKeyPress.ObserveVolumeKeyPressRequest.newBuilder();
        builder.setIsSubscribe(false);
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_OBSERVE_VOLUME_KEY_PRESS_REQUEST, Statics.PROTO_VERSION, builder.build(), "",null);
        NotificationCenter.defaultCenter().unsubscribe(VolumeKeyEvent.class, volumeKeyEventSubscriber);
        this.observeVolumeKeyPress = null;
    }

    public void registerRobotPostureEvent(final ISensor.ObserveRobotPosture observeRobotPostureEvent) {
        this.observeRobotPosture = observeRobotPostureEvent;
        CodeMaoObserveFallClimb.ObserveFallClimbRequest.Builder builder = CodeMaoObserveFallClimb.ObserveFallClimbRequest.newBuilder();
        builder.setIsSubscribe(true);
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_OBSERVE_ROBOT_POSTURE_REQUEST, Statics.PROTO_VERSION, builder.build(), "",null);
        NotificationCenter.defaultCenter().subscriber(RobotPostureEvent.class, robotPostureEventSubscriber);
    }
    public void unregisterRobotPostureEvent() {

        CodeMaoObserveFallClimb.ObserveFallClimbRequest.Builder builder = CodeMaoObserveFallClimb.ObserveFallClimbRequest.newBuilder();
        builder.setIsSubscribe(false);
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_OBSERVE_ROBOT_POSTURE_REQUEST, Statics.PROTO_VERSION, builder.build(), "",null);
        NotificationCenter.defaultCenter().unsubscribe(RobotPostureEvent.class, robotPostureEventSubscriber);
        this.observeRobotPosture = null;
    }

    public void registerInfraredDistanceEvent(int samplingPeriod, final ISensor.ObserveInfraredDistance observeInfraredDistance) {
        this.observeInfraredDistance = observeInfraredDistance;
        CodeMaoObserveInfraredDistance.ObserveInfraredDistanceRequest.Builder builder = CodeMaoObserveInfraredDistance.ObserveInfraredDistanceRequest.newBuilder();
        builder.setIsSubscribe(true);
        builder.setSamplingPeriod(samplingPeriod);
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_OBSERVE_DISTANCE_REQUEST, Statics.PROTO_VERSION, builder.build(), "",null);
        NotificationCenter.defaultCenter().subscriber(InfraredDistanceEvent.class, robotInfraredDistanceEventSubscriber);
    }
    public void unregisterInfraredDistanceEvent() {

        CodeMaoObserveInfraredDistance.ObserveInfraredDistanceRequest.Builder builder = CodeMaoObserveInfraredDistance.ObserveInfraredDistanceRequest.newBuilder();
        builder.setIsSubscribe(false);
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_OBSERVE_DISTANCE_REQUEST, Statics.PROTO_VERSION, builder.build(), "",null);
        NotificationCenter.defaultCenter().unsubscribe(InfraredDistanceEvent.class, robotInfraredDistanceEventSubscriber);
        this.observeInfraredDistance = null;
    }

    Subscriber<FallDownEvent> fallDownEventSubscriber = new Subscriber<FallDownEvent>() {
        @Override
        public void onEvent(FallDownEvent fallDownEvent) {
            if(observeFallDownState != null) {
                observeFallDownState.onFallDownState(fallDownEvent.type);
            }

        }
    };

    Subscriber<ButteryEvent> butteryEventSubscriber =new Subscriber<ButteryEvent>() {
        @Override
        public void onEvent(ButteryEvent butteryEvent) {
            if(observeBattery != null) {
                observeBattery.onGetBattery(butteryEvent.level, butteryEvent.status);
            }

        }
    };

    Subscriber<HeadRacketEvent> headRacketEventSubscriber =new Subscriber<HeadRacketEvent>() {
        @Override
        public void onEvent(HeadRacketEvent headRacketEvent) {
            if(observeHeadRacket != null) {
                observeHeadRacket.onHeadRacket(headRacketEvent.type);
            }

        }
    };

    Subscriber<VolumeKeyEvent> volumeKeyEventSubscriber = new Subscriber<VolumeKeyEvent>() {
        @Override
        public void onEvent(VolumeKeyEvent volumeKeyEvent) {
            LogUtils.d("onEvent VolumeKeyEvent = " + volumeKeyEvent.type);
            if(observeVolumeKeyPress != null) {
                observeVolumeKeyPress.onKeyPress(volumeKeyEvent.type);
            }

        }
    };
    Subscriber<RobotPostureEvent> robotPostureEventSubscriber = new Subscriber<RobotPostureEvent>() {
        @Override
        public void onEvent(RobotPostureEvent robotPostureEvent) {
            if(observeRobotPosture != null) {
                observeRobotPosture.onRobotPosture(robotPostureEvent.type);
            }

        }
    };

    Subscriber<InfraredDistanceEvent> robotInfraredDistanceEventSubscriber = new Subscriber<InfraredDistanceEvent>() {
        @Override
        public void onEvent(InfraredDistanceEvent infraredDistanceEvent) {
            if(observeInfraredDistance != null) {
                observeInfraredDistance.onInfraredDistance(infraredDistanceEvent.distance);
            }

        }
    };
}
