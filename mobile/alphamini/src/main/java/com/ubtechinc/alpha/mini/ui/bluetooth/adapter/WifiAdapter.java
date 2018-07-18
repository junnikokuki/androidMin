package com.ubtechinc.alpha.mini.ui.bluetooth.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.ui.bluetooth.bean.UbtScanResult;

import java.util.List;


/**
 * Created by liuhai on 2016/10/21.
 */

public class WifiAdapter extends BaseAdapter {
    private static final String TAG = WifiAdapter.class.getSimpleName();
    private LayoutInflater mInflater;
    private List<UbtScanResult> mList;

    public WifiAdapter(Context context, List<UbtScanResult> list) {
        this.mList = list;
        this.mInflater = LayoutInflater.from(context);
    }

    // 新加的一个函数，用来更新数据
    public void setData(List<UbtScanResult> list) {
        this.mList = list;
        Log.i("wifiap", "m_listWifi size = " + mList.size());
        notifyDataSetChanged();
    }

    public List<UbtScanResult> getList() {
        return mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        UbtScanResult scanResult = mList.get(position);
        String capabilities = scanResult.getC();
        int level = scanResult.getL();
        String ssid = scanResult.getS();
        final WifiAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new WifiAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.setting_wifi_listview_item, null);
            viewHolder.wifi_name = (TextView) convertView.findViewById(R.id.wifi_name);
            viewHolder.wifi_signal_level = (ImageView) convertView.findViewById(R.id.wifi_signal_level);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (WifiAdapter.ViewHolder) convertView.getTag();
        }

        if (!TextUtils.isEmpty(capabilities)) {

            if (capabilities.contains("WPA") || capabilities.contains("wpa") || capabilities.contains("WEP") || capabilities.contains("wep")) {
                viewHolder.wifi_signal_level.setImageResource(R.drawable.free_wifi_sel);
                Log.i("river", " ");
            } else {
                Log.i("river", "no");
                viewHolder.wifi_signal_level.setImageResource(R.drawable.wifi_nonewpa_selector);
            }
        }
        viewHolder.wifi_signal_level.setImageLevel(Math.abs(mList.get(position).getL()));
//		    (1).WPA-PSK/WPA2-PSK(目前最安全家用加密)
//		    (2).WPA/WPA2(较不安全)
//		    (3).WEP(安全较差)
//		    (4).EAP(迄今最安全的)
        viewHolder.wifi_name.setText(ssid);

        return convertView;
    }

    public final class ViewHolder {
        TextView wifi_name;
        ImageView wifi_signal_level;

        public ViewHolder() {
        }
    }
}
