package com.ubtechinc.alpha.mini.ui.car;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.entity.CarListInfo;

import java.util.ArrayList;

/**
 * Created by riley.zhang on 2018/7/6.
 */

public class CarListAdapter extends RecyclerView.Adapter<CarListAdapter.CarListViewHolder> {

    private ArrayList<CarListInfo> mCarList = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private OnItemClickListener mOnItemClickListener;

    public CarListAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<CarListInfo> cars) {
        mCarList = cars;
    }

    @Override
    public void onBindViewHolder(CarListViewHolder carListViewHolder, final int position) {
        carListViewHolder.mBleName.setText(mCarList.get(position).getDeviceBleName());
        carListViewHolder.mBleName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListener.onItemClick(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCarList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public CarListViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = mLayoutInflater.inflate(R.layout.carlist_item_layout, viewGroup, false);
        return new CarListViewHolder(view);
    }

    public class CarListViewHolder extends RecyclerView.ViewHolder{
        private TextView mBleName;
        public CarListViewHolder(View itemView) {
            super(itemView);
            mBleName = itemView.findViewById(R.id.tv_carlist_item_blename);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int positon);
    }

    public void setOnItemClick(OnItemClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }
}
