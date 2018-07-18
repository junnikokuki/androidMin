package com.ubtechinc.alpha.mini.ui.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.databinding.ItemPopupRobotBinding;
import com.ubtechinc.alpha.mini.widget.RobotPopupWindow;
import com.ubtechinc.alpha.mini.entity.RobotInfo;

import java.util.List;

public class PopupRobotAdapter extends RecyclerView.Adapter<PopupRobotAdapter.PopupRobotViewHolder> {

    private Context context;

    private List<RobotInfo> robots;
    private String currentRobotId;

    private RobotPopupWindow.SwitchRobotListener switchRobotListener;

    public PopupRobotAdapter(Context context, List<RobotInfo> robots, RobotPopupWindow.SwitchRobotListener switchRobotListener) {
        this.robots = robots;
        this.context = context;
        this.switchRobotListener = switchRobotListener;
    }

    @Override
    public PopupRobotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ItemPopupRobotBinding itemBinding = DataBindingUtil.inflate(inflater, R.layout.item_popup_robot, null, false);
        return new PopupRobotViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(PopupRobotViewHolder holder, final int position) {
        holder.bind(robots.get(position), currentRobotId);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switchRobotListener != null) {
                    RobotInfo robotInfo = robots.get(position);
                    if (!robotInfo.getRobotUserId().equals(currentRobotId)) {
                        switchRobotListener.onSwitch(robots.get(position));
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return robots == null ? 0 : robots.size();
    }

    public static class PopupRobotViewHolder extends RecyclerView.ViewHolder {

        ItemPopupRobotBinding itemPopupRobotBinding;

        View itemView;

        public PopupRobotViewHolder(ItemPopupRobotBinding binding) {
            super(binding.getRoot());
            itemPopupRobotBinding = binding;
            itemView = itemPopupRobotBinding.getRoot();
        }


        public void bind(RobotInfo robotInfo, String currentRobotId) {
            itemPopupRobotBinding.setCurrentRobotId(currentRobotId);
            itemPopupRobotBinding.setRobot(robotInfo);
        }

    }

    public void updateList(List<RobotInfo> robots, String currentRobotId) {
        this.robots = robots;
        this.currentRobotId = currentRobotId;
        notifyDataSetChanged();
    }


}
