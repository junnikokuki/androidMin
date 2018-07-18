package com.ubtechinc.alpha.mini.ui.codemao;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.clj.fastble.data.BleDevice;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.codemaosdk.bean.UbtBleDevice;

import java.util.List;

/**
 * @author：wululin
 * @date：2017/11/27 16:29
 * @modifier：ubt
 * @modify_date：2017/11/27 16:29
 * [A brief description]
 * version
 */

public class DevicesAdapter extends BaseAdapter {
    public static final String ROBOT_TAG = "Mini_";//机器人显示的TAG
    private final LayoutInflater inflater;
    private List<UbtBleDevice> mBluetoothDevices;
    private String mConnectSN;

    public DevicesAdapter(Context context, List<UbtBleDevice> devices){
        this.mBluetoothDevices = devices;
        inflater = LayoutInflater.from(context);
    }

    public String getConnectSN() {
        return mConnectSN;
    }

    /**
     * 正在连接时不可点击且置为灰色
     *
     */
    public void setConnecting(String  connectSN) {
        mConnectSN = connectSN;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mBluetoothDevices.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mBluetoothDevices.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.devices_item, null);
            viewHolder.wifi_name =  convertView.findViewById(R.id.wifi_name);
            viewHolder.mRelativeLayout =  convertView.findViewById(R.id.rl_connect);
            viewHolder.mItemLoadingDevice = convertView.findViewById(R.id.progressbar);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String robotSN = mBluetoothDevices.get(position).getSn();
        if(!TextUtils.isEmpty(mConnectSN)){
            if (mConnectSN.equals(robotSN)) {
                viewHolder.mItemLoadingDevice.setVisibility(View.VISIBLE);
                viewHolder.mRelativeLayout.setAlpha(0.5f);
            } else {
                viewHolder.mRelativeLayout.setAlpha(1.0f);
                viewHolder.mItemLoadingDevice.setVisibility(View.GONE);
            }
        }else {
            viewHolder.mRelativeLayout.setAlpha(1.0f);
            viewHolder.mItemLoadingDevice.setVisibility(View.GONE);
        }
        String name =  robotSN;
        viewHolder.wifi_name.setText(name);
        return convertView;
    }

    public class ViewHolder {
        TextView wifi_name;
        RelativeLayout mRelativeLayout;
        ProgressBar mItemLoadingDevice;
    }

    /**
     * @param miniDeviceList
     */
    public void onNotifyDataSetChanged(List<UbtBleDevice> miniDeviceList) {
        this.mBluetoothDevices = miniDeviceList;
        this.notifyDataSetChanged();
    }
}
