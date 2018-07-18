package com.ubtechinc.alpha.mini.ui.friend;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.ubtechinc.alpha.CmFaceDetect;
import com.ubtechinc.alpha.CmFaceExit;
import com.ubtechinc.alpha.im.IMCmdId;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseToolbarActivity;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.event.FriendListChangeEvent;
import com.ubtechinc.alpha.mini.im.Phone2RobotMsgMgr;
import com.ubtechinc.alpha.mini.ui.PageRouter;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtechinc.nets.phonerobotcommunite.ICallback;

import org.greenrobot.eventbus.EventBus;

/**
 * @Date: 2017/12/19.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 检测人脸的页面
 */

public class DetectMiniFriendActivity extends BaseToolbarActivity {

    private ViewStub dectingViewStub;
    private View decetingView;

    private ViewStub succuessViewStub;
    private View successView;

    private String friendName;

    private FriendFaceInfo friendFaceInfo;

    private boolean isTimeout = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_friend);
        initView();
    }

    private void initView() {
        initToolbar(findViewById(R.id.toolbar), "", View.GONE, false);
        initConnectFailedLayout(R.string.face_detect_mobile_detect_network_error_hint, R.string.face_detect_mobile_detect_offline_hint);
        needDivideLine(false);
        friendName = getIntent().getStringExtra(PageRouter.FRIEND_NAME);
        dectingViewStub = findViewById(R.id.vb_detecting);
        decetingView = dectingViewStub.inflate();
        Glide.with(this).load(R.drawable.motion_face_recognition).into(new GlideDrawableImageViewTarget((ImageView) decetingView.findViewById(R.id.iv_hint_gif), 100));
        beginToDetectFace();
    }

    private void beginToDetectFace() {
        CmFaceDetect.CmFaceDetectRequest.Builder request = CmFaceDetect.CmFaceDetectRequest.newBuilder();
        request.setName(friendName);
        // TODO: 2017/12/21 这样写可能会有内存泄漏,导致DetectMiniFriend不能被回收
        timeoutHandler.sendEmptyMessageDelayed(0, 120000);
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_START_FACE_DETECT_REQUEST, IMCmdId.IM_VERSION, request.build(),
                MyRobotsLive.getInstance().getRobotUserId(), new ICallback<CmFaceDetect.CmFaceDetectResponse>() {

                    @Override
                    public void onSuccess(final CmFaceDetect.CmFaceDetectResponse data) {
                        if (!isDestroyed()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    timeoutHandler.removeMessages(0);
                                    Log.i(TAG, "run: " + data.toString());
                                    if (!isTimeout) {
                                        int resultCode = data.getResultCode();
                                        if (resultCode == FaceDetectErrorCode.SUCCESS_DETECT) {
                                            showSuccessView(data.getId());
                                            EventBus.getDefault().post(new FriendListChangeEvent(FriendListChangeEvent.FRIEND_LIST_ADD));
                                        } else {
                                            handleResult(data.getErrMsg());
                                        }
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(ThrowableWrapper e) {
                        if(!isDestroyed()){
                            e.printStackTrace();
                            timeoutHandler.removeMessages(0);
                        }
                    }
                });
    }

    @Override
    protected void setConnectFailedLayout(boolean robotConnectState, boolean networkConnectState) {
        if (!networkConnectState) {
            showDisconnectedWithRobotDialog(R.string.face_detect_mobile_detect_network_error_hint);
            return;
        }
        if (!robotConnectState) {
            showDisconnectedWithRobotDialog(R.string.face_detect_mobile_detect_offline_hint);
            return;
        }
    }

    private void showDisconnectedWithRobotDialog(@StringRes int stringRes) {
        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.setMessage(stringRes).setPositiveButton(R.string.i_know, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        }).show();
    }

    public void toFriendFaceInfo(View view) {
        if (friendFaceInfo != null) {
            PageRouter.toMiniFriendActivity(DetectMiniFriendActivity.this);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        onBack();
    }

    private void exitDetectFace() {
        CmFaceExit.CmFaceExitRequest.Builder request = CmFaceExit.CmFaceExitRequest.newBuilder();
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_FACE_EXIT_REQUEST, IMCmdId.IM_VERSION, request.build(),
                MyRobotsLive.getInstance().getRobotUserId(), new ICallback<CmFaceDetect.CmFaceDetectResponse>() {
                    @Override
                    public void onSuccess(final CmFaceDetect.CmFaceDetectResponse data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onError(ThrowableWrapper e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        });
                    }
                });
    }

    private void handleResult(String string) {
        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.setTitle(R.string.friend_info_detect_add_face_failure).setMessage(string).setPositiveButton(R.string.retry, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                beginToDetectFace();
            }
        }).setNegativeButton(R.string.face_detect_mobile_next_time, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                DetectMiniFriendActivity.this.finish();
            }
        }).show();
    }

    private void showSuccessView(String id) {
        friendFaceInfo = new FriendFaceInfo();
        friendFaceInfo.setId(id);
        friendFaceInfo.setName(friendName);
        succuessViewStub = findViewById(R.id.vb_success_detect);
        successView = succuessViewStub.inflate();
        decetingView.setVisibility(View.GONE);
        successView.setVisibility(View.VISIBLE);
        backBtn.setVisibility(View.GONE);
    }


    private void showExitDetectDialog() {
        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.setMessage(R.string.friend_info_detect_exit).setNegativeButton(R.string.simple_message_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        }).setPositiveButton(R.string.exit_ble, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                exitDetectFace();
            }
        }).show();
    }

    @Override
    protected void onBack() {
        showExitDetectDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeoutHandler.removeMessages(0);
        timeoutHandler.removeCallbacks(null);
        timeoutHandler = null;
    }

    private Handler timeoutHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isTimeout = true;
            if (!isDestroyed()) {
                showTimeoutDialog();
            }
        }

    };

    private void showTimeoutDialog() {
        final MaterialDialog msgDialog = new MaterialDialog(this);
        msgDialog.setMessage(getString(R.string.error_robot_timeout));
        msgDialog.setPositiveButton(R.string.i_know, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msgDialog.dismiss();
                finish();
            }
        });
        msgDialog.show();

    }
}
