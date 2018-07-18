package com.ubtechinc.alpha.mini.ui.codemao;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.StringUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.codemaosdk.bean.UbtBleDevice;

import java.util.List;

/**
 * [LynxDeviceAdapter ]
 *
 * @author nxy
 * @version 1.0
 * @date 2017-2-7
 **/

public class CodingMiniDeviceAdapter extends BaseAdapter {
    private String TAG = "MiniDeviceAdapter";
    private List<UbtBleDevice> miniDeviceList;
    private LayoutInflater inflater;
    private String mConnectingSN;
    private String mConnectedSN;
    public CodingMiniDeviceAdapter(Context context, List<UbtBleDevice> miniDeviceList) {
        this.miniDeviceList = miniDeviceList;
        inflater = LayoutInflater.from(context);
    }


    public String getConnectSN() {
        return mConnectingSN;
    }



    public void setConnectedSN(String mConnectedSN) {
        this.mConnectedSN = mConnectedSN;
        LogUtils.d("setConnectedSN = " + mConnectedSN);
        notifyDataSetChanged();
    }

    /**
     * 正在连接时不可点击且置为灰色
     *
     */
    public void setConnecting(String  connectSN) {
        mConnectingSN = connectSN;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return miniDeviceList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return miniDeviceList.get(arg0);
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
            convertView = inflater.inflate(R.layout.bluetooth_listview_item_for_coding, null);
            viewHolder.wifi_name =  convertView.findViewById(R.id.wifi_name);
            viewHolder.mRelativeLayout =  convertView.findViewById(R.id.rl_connect);
            viewHolder.mItemLoadingDevice = convertView.findViewById(R.id.progressbar);
            viewHolder.iv_connected = convertView.findViewById(R.id.iv_connected);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String robotSN = miniDeviceList.get(position).getSn();
        if(!TextUtils.isEmpty(mConnectingSN) && mConnectingSN.equals(robotSN)){

                viewHolder.mItemLoadingDevice.setVisibility(View.VISIBLE);
                viewHolder.mRelativeLayout.setAlpha(0.5f);

        }else {
            viewHolder.mRelativeLayout.setAlpha(1.0f);
            viewHolder.mItemLoadingDevice.setVisibility(View.GONE);
        }
        if(StringUtils.isEquals(mConnectedSN, robotSN)) {
            viewHolder.iv_connected.setVisibility(View.VISIBLE);
        }else {
            viewHolder.iv_connected.setVisibility(View.GONE);
        }

//        String name = Constants.ROBOT_TAG + robotSN;
        String name = robotSN;
        viewHolder.wifi_name.setText(name);
        return convertView;
    }

    public class ViewHolder {
        TextView wifi_name;
        RelativeLayout mRelativeLayout;
        ProgressBar mItemLoadingDevice;
        ImageView iv_connected;
    }

    /**
     * @param miniDeviceList
     */
    public void onNotifyDataSetChanged(List<UbtBleDevice> miniDeviceList) {
        this.miniDeviceList = miniDeviceList;
        this.notifyDataSetChanged();
    }
}
