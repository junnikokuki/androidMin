package com.ubtechinc.alpha.mini.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.PopupWindow;

import com.ubtech.utilcode.utils.ScreenUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.ui.adapter.PopupRobotAdapter;
import com.ubtechinc.alpha.mini.entity.RobotInfo;

import java.util.List;

public class RobotPopupWindow extends PopupWindow implements View.OnClickListener {

    private Context context;
    private RecyclerView recyclerView;


    private PopupRobotAdapter popupRobotAdapter;

    public RobotPopupWindow(Context context, SwitchRobotListener switchRobotListener) {
        super(LayoutInflater.from(context).inflate(R.layout.popu_robots, null), ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.getScreenHeight() / 2);
        this.context = context;
        getContentView().setOnClickListener(this);
        recyclerView = getContentView().findViewById(R.id.popup_robot_list);
        getContentView().findViewById(R.id.iv_fill).setOnClickListener(this);
        popupRobotAdapter = new PopupRobotAdapter(context, null, switchRobotListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(popupRobotAdapter);
        recyclerView.addItemDecoration(new LinearLayoutColorDivider(context.getResources(), R.color.text_gray, R.dimen.one_px,DividerItemDecoration.VERTICAL));
    }


    public void show(List<RobotInfo> robots, String currentRobotId, View anchor, int xoff, int yoff) {
        popupRobotAdapter.updateList(robots, currentRobotId);
        showAsDropDown(anchor, xoff, yoff);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        int left = anchor.getLeft();
        setFocusable(true);
        setTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setOutsideTouchable(true);
        super.showAsDropDown(anchor, xoff, yoff);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    public interface SwitchRobotListener {

        public void onSwitch(RobotInfo robotInfo);

    }

    public void fullScreenImmersive(Window window) {
        if (window != null) {
            fullScreenImmersive(window.getDecorView());
        }
    }

    public void fullScreenImmersive(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            view.setSystemUiVisibility(uiOptions);
        }
    }
}
