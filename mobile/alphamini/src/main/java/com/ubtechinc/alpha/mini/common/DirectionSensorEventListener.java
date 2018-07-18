package com.ubtechinc.alpha.mini.common;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * @className DirectionSensorEventListener
 *
 * @author wmma
 * @description 继承手机传感器SensorEventListener，实现对手机加速度传感器的监听，按照目前jimu的实现。
 * @date 2017/3/6
 * @update
 */


public class DirectionSensorEventListener implements SensorEventListener {

    private final String TAG = DirectionSensorEventListener.this.getClass().getSimpleName();
    private float xLastValue;
    private float yLastValue;
    private float zLastValue;
    private ActionListener listener;
    private static final int INTERVAL_TIME = 100;//检测时间间隔
    private static final int SPEED_SHRESHOLD = 12;//搖一搖加速度临界值
    private long currentUpdateTime;
    private long lastUpdateTime;


    private boolean isShaking;

    private static final float CHANGE_RADIO = 2.5f;
    private static final float SHAKE_RADIO = 1.5f;

    public DirectionSensorEventListener(ActionListener listener) {
        this.listener = listener;
    }

    public boolean isShaking() {
        return isShaking;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        currentUpdateTime = System.currentTimeMillis();
        long timeInterval = currentUpdateTime - lastUpdateTime;
        if (timeInterval < INTERVAL_TIME)
            return;
        lastUpdateTime   = currentUpdateTime;
        float xValue = event.values[0];// Acceleration minus Gx on the x-axis
        float yValue = event.values[1];//Acceleration minus Gy on the y-axis
        float zValue = event.values[2];//Acceleration minus Gz on the z-axis
        // Log.i(TAG, "value[" + 0 + "]=" + xValue + "value[" + 1 + "]=" + yValue + "value[" + 2 + "]=" + zValue);
        if (Math.abs(xValue) < Math.abs(yValue)) {
            if (Math.abs(xValue) >= SPEED_SHRESHOLD
                    || Math.abs(yValue) >= SPEED_SHRESHOLD
                    || Math.abs(zValue) >= SPEED_SHRESHOLD) {
                dd = DeviceDirectionEnum.SWING;
                listener.onShake(currentUpdateTime, event.values);
                return;
            }
        }
        if (listener == null) return;
        if (Math.abs(xValue) > Math.abs(yValue)) {//左右抬起
            if (xValue > CHANGE_RADIO) {//右抬起
                dd = DeviceDirectionEnum.DOWN;
                listener.onBottomUp();
            } else if (-xValue > CHANGE_RADIO) {
                dd = DeviceDirectionEnum.UP;
                listener.onTopUp();
            }
        } else if (Math.abs(xValue) < Math.abs(yValue)) {//前后抬起
            if (yValue > CHANGE_RADIO) {//前抬起
                dd = DeviceDirectionEnum.RIGHT;
                listener.onRightUp();
            } else if (-yValue > CHANGE_RADIO) {
                dd = DeviceDirectionEnum.LEFT;
                listener.onLeftUp();
            }
        }
        if (Math.abs(yValue) < CHANGE_RADIO && Math.abs(xValue) < CHANGE_RADIO) {
            dd = DeviceDirectionEnum.NONE;
            listener.onDirectionNone();
        }
        xLastValue = xValue;
        yLastValue = yValue;
        zLastValue = zValue;
    }

    public boolean directionNotChange() {
        if (dd == DeviceDirectionEnum.NONE) return true;
        return false;
    }

    private DeviceDirectionEnum dd = DeviceDirectionEnum.NONE;

    public DeviceDirectionEnum getDeviceDirection() {
        return dd;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public interface ActionListener {

        void onShake(long timeStamp, float[] value);

        void onLeftUp();

        void onRightUp();

        void onTopUp();

        void onBottomUp();

        void onDirectionNone();
    }





}



