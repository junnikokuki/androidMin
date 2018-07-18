package com.ubtechinc.alpha.mini.ui.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ubtechinc.alpha.mini.ui.bluetooth.bean.OpenBluetoothEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * @author：wululin
 * @date：2017/11/1 19:56
 * @modifier：ubt
 * @modify_date：2017/11/1 19:56
 * [A brief description]
 * version
 */

public class BluetoothReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i("wll","action========" + action);
        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
            Log.i("wll","blueState======" + blueState);
            OpenBluetoothEvent event = new OpenBluetoothEvent();
            event.mState = blueState;
            EventBus.getDefault().post(event);
        }
    }
}
