package com.ubtechinc.alpha.mini.ui.car;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ubtechinc.alpha.mini.R;

/**
 * Created by riley.zhang on 2018/7/12.
 */

public class CarCmdListAdapter extends RecyclerView.Adapter <CarCmdListAdapter.CarCmdViewHolder>{

    private LayoutInflater mLayoutInflater;
    private String[] mCmdList = new String[]{"“启动”","“前进”“后退”“左转”“右转”","“熄火”","“喇叭”", "“开启警笛”", "“关闭警笛”", "“别开车了”"};
    private String[] mCmdTextList = new String[]{",坐骑就启动.",",坐骑就能往前走、往后走、左转弯、右转弯.","坐骑就会停下.",",坐骑就会播一声喇叭的声音",
            ",坐骑就会播出警笛的声音.",",警笛就会关闭.",",就会推出开车模式."};
    public CarCmdListAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public void onBindViewHolder(CarCmdViewHolder holder, int position) {
        holder.mCmdText.setText(mCmdTextList[position]);
        holder.mCmd.setText(mCmdList[position]);
    }

    @Override
    public CarCmdViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.cmdlist_item_layout, parent, false);

        return new CarCmdViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mCmdList.length;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class CarCmdViewHolder extends RecyclerView.ViewHolder{
        private TextView mCmdText;
        private TextView mCmd;
        public CarCmdViewHolder(View itemView) {
            super(itemView);
            mCmd = itemView.findViewById(R.id.tv_cmditem_cmd);
            mCmdText = itemView.findViewById(R.id.tv_cmditem_text);
        }
    }
}
