package com.ubtechinc.alpha.mini.ui.minerobot;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.ui.PageRouter;
import com.ubtechinc.alpha.mini.entity.RobotInfo;

import java.util.List;

/**
 * @desc : 我的机器人界面的Adapter
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2017/11/13
 */
public class MineRobotAdapter extends RecyclerView.Adapter<MineRobotAdapter.ViewHolder> {

    private List<RobotInfo> robotInfoList;
    private Context context;

    public MineRobotAdapter(List<RobotInfo> robotInfoList, Context context) {
        this.robotInfoList = robotInfoList;
        this.context = context;
    }

    public List<RobotInfo> getRobotInfoList() {
        return robotInfoList;
    }

    public void updateList(List<RobotInfo> robotInfoList){
        this.robotInfoList = robotInfoList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_item_my_robot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final RobotInfo robotInfo = robotInfoList.get(position);
        String serialNumber = context.getResources().getString(R.string.serial_number_1, robotInfo.getUserId());
        holder.robotSerialNumber.setText(serialNumber);
        String shareUser = context.getResources().getString(R.string.share_user, robotInfo.getMasterUserName());
        holder.shareUser.setText(shareUser);
        if(robotInfo.isMaster()) {
            holder.manager.setVisibility(View.VISIBLE);
            holder.shareUser.setVisibility(View.GONE);
            holder.manager.setText(context.getString(R.string.manager));
        } else {
            holder.manager.setVisibility(View.GONE);
            holder.shareUser.setVisibility(View.VISIBLE);
            holder.manager.setText(context.getString(R.string.setting_biaoqian, robotInfo.getMasterUserName()));
        }
        if(robotInfo.isOnline()) {
            holder.online.setVisibility(View.VISIBLE);
        } else {
            holder.online.setVisibility(View.GONE);
        }

        holder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PageRouter.toMineRobotDetail((Activity) context, robotInfo);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(robotInfoList == null){
            return 0;
        }else {
            return robotInfoList.size();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView robotSerialNumber;
        private TextView shareUser;
        private TextView manager;
        private View online;

        private View mItemView;

        public ViewHolder(View view){
            super(view);
            mItemView = view;
            robotSerialNumber = view.findViewById(R.id.serial_number);
            shareUser = view.findViewById(R.id.share_user);
            manager = view.findViewById(R.id.tv_manager);
            online = view.findViewById(R.id.view_online);
        }
    }
}
