package com.ubtechinc.alpha.mini.qqmusic;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.tvs.TVSManager;

/**
 * @author htoall
 * @Description: QQ会员列表项
 * @date 2018/4/17 下午3:20
 * @copyright TCL-MIE
 */
public class QQMemberView extends LinearLayout{

    private static final String TAG = "QQMemberView";
    private TextView tvStatus;
    private TextView tvNumber;
    private View symbol;
    private ImageView mTips;
    private RobotPresentStats robotPresentStats;
    private IReceivePresent receivePresent;

    public QQMemberView(Context context) {
        super(context);
        initView();
    }

    public QQMemberView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public QQMemberView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_qq_member, this, true);
        tvStatus = findViewById(R.id.tv_status);
        tvNumber = findViewById(R.id.tv_number);
        symbol = findViewById(R.id.tv_symbol);
        mTips  = findViewById(R.id.tv_share_user_tip);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!robotPresentStats.isReceive()) {
                    TVSManager.getInstance(getContext()).toUserCenter(robotPresentStats.getRobotEquipmentId());
                    if (receivePresent != null) {
                        receivePresent.onReceivePresent(robotPresentStats.getRobotSn());
                    }
                } else {
                    if(receivePresent != null) {
                        receivePresent.alreadyReceive();
                    }
                }
            }
        });
    }

    public void updateStatus(RobotPresentStats robotPresentStats) {
        this.robotPresentStats = robotPresentStats;
        Log.i(TAG, " updateStatus : robotPresentStats :" + robotPresentStats);
        if(robotPresentStats.isReceive()) {
            tvStatus.setText(R.string.already_receive);
            mTips.setImageResource(R.drawable.ic_qqmusic_disable);
            symbol.setVisibility(GONE);
        } else {
            tvStatus.setText(R.string.wait_receive);
            mTips.setImageResource(R.drawable.ic_qqmusic_normal);
            symbol.setVisibility(VISIBLE);
        }
        tvNumber.setText(getResources().getString(R.string.robot_number, robotPresentStats.getRobotSn()));
        Log.i(TAG, " updateStatus : robotPresentStats");
    }

    public void setReceivePresent(IReceivePresent receivePresent) {
        this.receivePresent = receivePresent;
    }

    public interface IReceivePresent {
        void onReceivePresent(String dsn);
        void alreadyReceive();
    }
}
