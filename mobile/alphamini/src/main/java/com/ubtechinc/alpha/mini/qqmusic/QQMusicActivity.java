package com.ubtechinc.alpha.mini.qqmusic;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.Observer;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gyf.barlibrary.ImmersionBar;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.entity.RobotInfo;
import com.ubtechinc.alpha.mini.entity.observable.AuthLive;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.qqmusic.model.QQMusicModel;
import com.ubtechinc.alpha.mini.tvs.TVSManager;
import com.ubtechinc.alpha.mini.ui.title.TitleBackItemView;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class QQMusicActivity extends LifecycleActivity {

    private MaterialDialog msgDialog;
    private static final String TAG = "QQMusicActivity";
    private IQQMusicStats iqqMusicStats;
    private LinearLayout llQQMemberView;
    private ImageView imageViewCard;
    private ImageView imageUser;
    private TextView tvReceiveDes;
    private TextView tvReceiveStats;
    private TextView tvOpenMember;
    private TextView tvName;
    private TextView tvTip;
    private ImageView ivQQMusic;
    private Space space;
    private boolean toUserCenter;
    private String dsn;
    protected TitleBackItemView titleBackItemView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, " onCreate -- this : " + this);
        setContentView(R.layout.activity_qqmusic);
        titleBackItemView = findViewById(R.id.title_back_item_view);
        titleBackItemView.setBackgroundColor(Color.TRANSPARENT);
        titleBackItemView.setTextColor(Color.WHITE);
        titleBackItemView.hideUnderLine();
        space = (Space)findViewById(R.id.space_status_bar);
        space.setMinimumHeight(getStatusBarHeight());
        Log.e(TAG, " height : " + space.getMinimumHeight());
        ImmersionBar.with(this)
                .transparentBar()
                .init();
        titleBackItemView.setOnBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(toUserCenter) {
            toUserCenter = false;
            if(dsn == null) {
                QQMusicModel.getInstance(QQMusicActivity.this).updateDevice(dsn);
            } else {
                dsn = null;
                QQMusicModel.getInstance(QQMusicActivity.this).updateMemberStatus(true);
            }

        }
    }

    private int getStatusBarHeight() {
        Class<?> clazz = null;
        try {
            clazz = Class.forName("com.android.internal.R$dimen");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Object object = null;
        try {
            object = clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        int height = 0;
        try {
            height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return getResources().getDimensionPixelSize(height);
    }

    protected void initView() {
        llQQMemberView = findViewById(R.id.qqmusicmember);
        imageViewCard = findViewById(R.id.iv_qqmusic_card);
        imageUser = findViewById(R.id.me_avatar_bg);
        tvReceiveDes = findViewById(R.id.receive_des);
        tvReceiveStats = findViewById(R.id.tv_receive_stats);
        tvTip = findViewById(R.id.tv_tip);
        tvOpenMember = findViewById(R.id.tv_open_member);
        tvName = findViewById(R.id.tv_name);
        ivQQMusic = findViewById(R.id.iv_qqmusic_bg);
        tvName.setText(AuthLive.getInstance().getCurrentUser().getNickName());
        tvReceiveStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(QQMusicStatsImp.getInstance().getRobotPresentStatss() != null &&
                        !QQMusicStatsImp.getInstance().getRobotPresentStatss().isEmpty()) {
                    for(RobotPresentStats robotPresentStats : QQMusicStatsImp.getInstance().getRobotPresentStatss()) {
                        if(!robotPresentStats.isReceive()) {
                            toUserCenter(robotPresentStats.getRobotEquipmentId());
                            return ;
                        }
                    }
                }
                StringBuilder stringBuilder = new StringBuilder();
                int count = 0;
                for(RobotInfo robotInfo : MyRobotsLive.getInstance().getRobots().getData()) {
                    if(robotInfo.isMaster()) {
                        if(count ++ != 0) {
                            stringBuilder.append("\n");
                        }
                        stringBuilder.append(getResources().getString(R.string.serial_number_1, robotInfo.getRobotUserId()));
                    }
                }
                showMsgDialog(getResources().getString(R.string.member_des), stringBuilder.toString());
            }
        });
        tvOpenMember.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                StringBuilder stringBuilder = new StringBuilder();
                int count = 0;
                for(RobotInfo robotInfo : MyRobotsLive.getInstance().getRobots().getData()) {
                    if(robotInfo.isMaster()) {
                        if(count ++ != 0) {
                            stringBuilder.append("\n");
                        }
                        stringBuilder.append(getResources().getString(R.string.serial_number_1, robotInfo.getRobotUserId()));
                    }
                }
                showMsgDialog(getResources().getString(R.string.member_des), stringBuilder.toString());
            }
        });
        QQMusicStatsImp.getInstance().observe(this, new Observer<QQMusicStatsImp>() {

            @Override
            public void onChanged(@Nullable QQMusicStatsImp qqMusicStatsImp) {
                updateStatus(qqMusicStatsImp);
            }
        });
        updateStatus(QQMusicStatsImp.getInstance());
    }

    public void showMsgDialog(String title, String message) {
        msgDialog = new MaterialDialog(this);
        msgDialog.setTitle(title);
        msgDialog.setMessage(message);
        msgDialog.setPositiveButton(R.string.simple_message_comfire, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msgDialog.dismiss();
                toUserCenterRecharge(MyRobotsLive.getInstance().getRobotUserId());
            }
        });
        msgDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                msgDialog.dismiss();

            }
        });
        msgDialog.show();
    }

    public void showMsgDialogForButton(String message, String btnName) {
        msgDialog = new MaterialDialog(this);
        msgDialog.setMessage(message);
        msgDialog.setPositiveButton(btnName, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msgDialog.dismiss();
            }
        });
        msgDialog.show();
    }

    private void updateStatus(IQQMusicStats iqqMusicStats) {
        Log.e(TAG, " updateStatus -- iqqMusicStats : " + iqqMusicStats + " iqqMusicStats.getReceiveStats() : " + iqqMusicStats.getReceiveStats());
        Log.e(TAG, " updateStatus -- iqqMusicStats.getRobotPresentStatss() : " + iqqMusicStats.getRobotPresentStatss());
        updateList(iqqMusicStats.getRobotPresentStatss());
        switch (iqqMusicStats.getReceiveStats()) {
            case ALREADY_OUTTIME:
                tvReceiveDes.setText(R.string.qqmusic_status_des_renew);
                imageViewCard.setBackgroundResource(R.drawable.ic_qqmusic_card_dark);
                ivQQMusic.setBackgroundResource(R.drawable.bg_qqmusic_dark);
                tvReceiveStats.setText(R.string.qqmusic_status_renew);
                tvTip.setText(R.string.qqmusic_status_tip_present);
                tvOpenMember.setVisibility(View.GONE);
                break;
            case NOT_OPEN:
                tvReceiveDes.setText(R.string.qqmusic_status_des_opennow);
                imageViewCard.setBackgroundResource(R.drawable.ic_qqmusic_card_dark);
                ivQQMusic.setBackgroundResource(R.drawable.bg_qqmusic_dark);
                tvReceiveStats.setText(R.string.qqmusic_status_opennow);
                tvTip.setText(R.string.qqmusic_status_tip_present);
                tvOpenMember.setVisibility(View.GONE);
                break;
            case RECEIVE_NOW:
                tvReceiveDes.setText(R.string.qqmusic_status_des_receive_now);
                imageViewCard.setBackgroundResource(R.drawable.ic_qqmusic_card_green);
                ivQQMusic.setBackgroundResource(R.drawable.bg_qqmusic_green);
                tvReceiveStats.setText(R.string.qqmusic_status_receive_now);
                tvTip.setText(R.string.qqmusic_status_tip_present);
                tvOpenMember.setVisibility(View.VISIBLE);
                break;
            case RECEIVE_CONTINUE:
                tvReceiveDes.setText(getResources().getString(R.string.qqmusic_status_des_receive_continue_now, QQMusicStatsImp.getInstance().getTime()));
                imageViewCard.setBackgroundResource(R.drawable.ic_qqmusic_card_blue);
                ivQQMusic.setBackgroundResource(R.drawable.bg_qqmusic_blue);
                tvReceiveStats.setText(R.string.qqmusic_status_receive_continue_now);
                tvTip.setText(R.string.qqmusic_status_tip_opennow);
                tvOpenMember.setVisibility(View.VISIBLE);
                break;
            case ALREADY_OPEN:
                tvReceiveDes.setText(getResources().getString(R.string.qqmusic_status_des_receive_continue_now, QQMusicStatsImp.getInstance().getTime()));
                imageViewCard.setBackgroundResource(R.drawable.ic_qqmusic_card_blue);
                ivQQMusic.setBackgroundResource(R.drawable.bg_qqmusic_blue);
                tvReceiveStats.setText(R.string.qqmusic_status_renew);
                tvTip.setText(R.string.qqmusic_status_tip_opennow);
                tvOpenMember.setVisibility(View.GONE);
                break;
        }
        Glide.with(this).load(AuthLive.getInstance().getCurrentUser().getUserImage())
                .bitmapTransform(new CropCircleTransformation(this)).crossFade(1000).into(imageUser);

    }

    private void updateList(List<RobotPresentStats> robotPresentStatsList) {
        if(robotPresentStatsList == null) {
            return ;
        }
        llQQMemberView.removeAllViews();
        int size = robotPresentStatsList.size();
        for(int i = 0; i < size; i ++) {
            RobotPresentStats robotPresentStats = robotPresentStatsList.get(i);
            QQMemberView qqMemberView = new QQMemberView(this);
            qqMemberView.updateStatus(robotPresentStats);
            llQQMemberView.addView(qqMemberView);
            qqMemberView.setReceivePresent(new QQMemberView.IReceivePresent() {
                @Override
                public void onReceivePresent(String todsn) {
                    dsn = todsn;
                    toUserCenter = true;
                }

                @Override
                public void alreadyReceive() {
                    showMsgDialogForButton(getString(R.string.member_already_receive), getString(R.string.i_know));
                }
            });
            if(i != size - 1) {
                View view = new View(this);
                view.setBackgroundColor(Color.argb(0,241, 241, 241));
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
                llQQMemberView.addView(view, layoutParams);
            }
        }
    }

    private void toUserCenter(String dsn) {
        toUserCenter = true;
        this.dsn = dsn;
        TVSManager.getInstance(this).toUserCenter(dsn);
    }

    private void toUserCenterRecharge(String dsn) {
        toUserCenter = true;
        this.dsn = dsn;
        TVSManager.getInstance(this).toUserCenter(dsn, true);
    }

    @Override
    protected void onDestroy() {
        ImmersionBar.with(this).destroy();
        super.onDestroy();
    }
}
