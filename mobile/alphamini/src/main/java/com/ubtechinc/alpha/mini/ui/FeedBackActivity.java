package com.ubtechinc.alpha.mini.ui;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qiniu.android.http.ResponseInfo;
import com.ubtech.utilcode.utils.KeyboardUtils;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.PhoneUtils;
import com.ubtech.utilcode.utils.SPUtils;
import com.ubtechinc.alpha.im.IMCmdId;
import com.ubtechinc.alpha.mini.BuildConfig;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseActivity;
import com.ubtechinc.alpha.mini.entity.RobotInfo;
import com.ubtechinc.alpha.mini.entity.observable.AuthLive;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.im.Phone2RobotMsgMgr;
import com.ubtechinc.alpha.mini.repository.CommitFeedbackRepository;
import com.ubtechinc.alpha.mini.repository.QiNiuRepository;
import com.ubtechinc.alpha.mini.utils.CustomerCallUtils;
import com.ubtechinc.alpha.mini.widget.MiniNetwrokProgressDailog;
import com.ubtechinc.alpha.mini.widget.MultiEditInputView;
import com.ubtechinc.alpha.mini.net.FeedbackModule;
import com.ubtechinc.alpha.mini.widget.WheelDialog.WheelDialog;
import com.ubtechinc.nets.http.ThrowableWrapper;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okio.BufferedSource;
import okio.Okio;
import okio.Source;

/**
 * Created by hongjie.xiang on 2017/11/29.
 */

public class FeedBackActivity extends BaseActivity {

    private static final String TAG = FeedBackActivity.class.getSimpleName();
    public static final String FEED_BACK_DETAIL_KEY = "feed_back_detail_key";
    public static final String FEED_BACK_PHONE_KEY = "feed_back_phone_key";

    private TextView title;
    private ImageView imageView;
    private TextView commit;
    private MultiEditInputView feedBackDetail;
    private EditText phoneInfo;
    private ImageView back;
    private View call;
    private TextView mSerialNumText;
    private String mSelectedSerialNum;
    private long lastCommitTime;
    private ArrayList<String> mRobotSerialNumList = new ArrayList<>();
    private MiniNetwrokProgressDailog loadingDialog;
    private boolean isUploadLog = false;
    private int mUploadSuccess = 0; //  =0 数据没有返回  =1 上传文件成功  =2 上传文件失败
    private int mFeedbackSuccess = 0; //=0 数据没有返回  =1 意见提交成功  =2 意见提交失败

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_feedback);
        title = findViewById(R.id.toolbar_title);
        title.setText(R.string.feedback_title);
        imageView = findViewById(R.id.toolbar_action);
        imageView.setVisibility(View.GONE);
        commit = findViewById(R.id.commit);
        feedBackDetail = findViewById(R.id.edittext);
        feedBackDetail.setHintText(getString(R.string.feedback_detail));
        phoneInfo = findViewById(R.id.edittext_connect);
        initRobotSerialLayout();

        back = findViewById(R.id.toolbar_back);
        call = findViewById(R.id.call_phone_rl);
        commit.setEnabled(false);
        feedBackDetail.setOnTextChangeListener(new MultiEditInputView.OnTextChangeListener() {
            @Override
            public void textChange(long textSize) {
                setCommitViewEnable();
            }
        });
        String userId = AuthLive.getInstance().getUserId();
        String feedDetail = SPUtils.get().getString(FEED_BACK_DETAIL_KEY + userId);
        String phone = SPUtils.get().getString(FEED_BACK_PHONE_KEY + userId);
        feedBackDetail.setContentText(feedDetail);
        phoneInfo.setText(phone);
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastCommitTime < 3 * 1000) {
                    return;
                }
                lastCommitTime = currentTime;
                showLoading();
                String userId = AuthLive.getInstance().getUserId();
                CommitFeedbackRepository commitFeedbackRepository = new CommitFeedbackRepository();
                int updateLog = 0;
                if (isUploadLog) {
                    updateLog = 1;
                }

                commitFeedbackRepository.commitFeedback(BuildConfig.VERSION_NAME, feedBackDetail.getContentText(), userId + "#" + phoneInfo.getText().toString(), updateLog, mSelectedSerialNum, new CommitFeedbackRepository.CommitFeedBackCallback() {
                    @Override
                    public void onError(ThrowableWrapper e) {
                        LogUtils.d(TAG, e.getMessage());
                        LogUtils.i("test", "setOnClickListener commitFeedback onSuccess isUploadLog = " + isUploadLog + " mUploadSuccess = " + mUploadSuccess);
                        mFeedbackSuccess = 2;
                        if (isUploadLog) {
                            if (mUploadSuccess > 0) {
                                commitFail();
                            }
                        }else {
                            commitFail();
                        }

                    }

                    @Override
                    public void onSuccess(FeedbackModule.Response response) {
                        LogUtils.d("xxxxx", "commit onSuccess" + response.getMsg());
                        mFeedbackSuccess = 1;
                        LogUtils.i("test", "setOnClickListener commitFeedback onSuccess isUploadLog = " + isUploadLog + " mUploadSuccess = " + mUploadSuccess);
                        if (isUploadLog) {
                            if (mUploadSuccess == 1) {
                                commitSuccess();
                            }else if (mUploadSuccess == 2) {
                                commitFail();
                            }
                        }else {
                            commitSuccess();
                        }
                    }
                });
                if (isUploadLog) {
                    LogUtils.i(TAG, "mSelectedSerialNum = " + mSelectedSerialNum);
                    Phone2RobotMsgMgr.getInstance().sendData(IMCmdId.IM_UPLOAD_LOG_REQUEST, IMCmdId.IM_VERSION, null, mSelectedSerialNum, null);
                    requestReadWrite();

                    byte[] fileBytes = getLogFileBytes();//此处不用判断fileBytes为空，如果数据为空的话七牛直接返回失败
                    LogUtils.i(TAG, "fileBytes = " + fileBytes);
                    String uploadFileName = getUploadFileName();
                    QiNiuRepository.getInstance().uploadData(fileBytes, uploadFileName, new QiNiuRepository.UploadFileListener() {
                        @Override
                        public void onSuccess(String key, ResponseInfo info, JSONObject response) {
                            LogUtils.i(TAG, "setOnClickListener uploadFilePath onSuccess mFeedbackSuccess = " + mFeedbackSuccess);
                            mUploadSuccess = 1;
                            if (mFeedbackSuccess == 1) {
                                commitSuccess();
                            }else if (mFeedbackSuccess == 2) {
                                commitFail();
                            }
                        }

                        @Override
                        public void onFail() {
                            LogUtils.i(TAG, "setOnClickListener uploadFilePath onFail mFeedbackSuccess = " + mFeedbackSuccess);
                            mUploadSuccess = 2;
                            if (mFeedbackSuccess > 0) {
                                commitFail();
                            }
                        }

                        @Override
                        public void onProgress(String key, double percent) {

                        }
                    });
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exit();
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomerCallUtils.call(FeedBackActivity.this);
            }
        });
    }

    private String getUploadFileName() {
        //七牛文件格式为  (日期/序列号_UID_IMEI_android)
        Date now = new Date();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(now);
        String userId = AuthLive.getInstance().getCurrentUser().getUserId();
        String imei = PhoneUtils.getIMEI();
        String fileName = date + "/" + mSelectedSerialNum +"_" + userId + "_" +  imei + "_android";
        return fileName;
    };

    private byte[] getLogFileBytes() {
        byte[] fileBytes = null;
        Source source = null;
        BufferedSource bSource = null;
        try{
            String filePath = LogUtils.logFilePath();
            LogUtils.i(TAG, "filePath = " + filePath);
            File file = new File(filePath);
            //读文件
            source = Okio.source(file);
            //通过source拿到 bufferedSource
            bSource = Okio.buffer(source);
            fileBytes = bSource.readByteArray();
            LogUtils.i(TAG, "fileBytes = " + fileBytes + " bSource = " + bSource);
        } catch (IOException e){
            e.printStackTrace();
            LogUtils.i(TAG, "read file fail !!!!!");
        } finally {
            try{
                if (null != bSource) {
                    bSource.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return fileBytes;
    }

    private void commitFail() {
        LogUtils.i(TAG, "setOnClickListener commitFail ");
        dismissLoading();
        toastError(getString(R.string.commit_failed));
        lastCommitTime = 0;
        mFeedbackSuccess = 0;
        mUploadSuccess = 0;
    }

    private void commitSuccess() {
        LogUtils.i(TAG, "setOnClickListener commitSuccess ");
        dismissLoading();
        toastSuccess(getString(R.string.commit_sucess));
        String userId = AuthLive.getInstance().getUserId();
//        SPUtils.get().put(FEED_BACK_DETAIL_KEY + userId, "");
//        SPUtils.get().put(FEED_BACK_PHONE_KEY + userId, "");
        finish();
        lastCommitTime = 0;
    }

    private void initRobotSerialLayout() {
        List<RobotInfo> robotInfos = MyRobotsLive.getInstance().getRobots().getData();
        RelativeLayout relativeLayout = findViewById(R.id.rlayout_serial_num);

        if (robotInfos == null || robotInfos.size() == 0) {
            relativeLayout.setVisibility(View.GONE);
        } else {
            mRobotSerialNumList.clear();
            for(RobotInfo robotInfo:robotInfos) {
                LogUtils.i(TAG, "serial num = " + robotInfo.getRobotUserId());
                mRobotSerialNumList.add(robotInfo.getRobotUserId());
            }
            relativeLayout.setVisibility(View.VISIBLE);
            mSerialNumText = findViewById(R.id.text_serial);
            RobotInfo robotInfo = MyRobotsLive.getInstance().getCurrentRobot().getValue();
            mSelectedSerialNum = robotInfo.getRobotUserId();
            mSerialNumText.setText(mSelectedSerialNum);
            if (mRobotSerialNumList.size() > 1) {
                Drawable rightDrawable = getResources().getDrawable(R.drawable.ic_row_arrow);
                rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
                mSerialNumText.setCompoundDrawables(null, null, rightDrawable, null);
            } else {
                mSerialNumText.setCompoundDrawables(null, null, null, null);
            }

            mSerialNumText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mRobotSerialNumList != null && mRobotSerialNumList.size() > 1) {
                        final WheelDialog wheelDialog = new WheelDialog.Builder(FeedBackActivity.this)
                                .setData(mRobotSerialNumList)
                                .setSelectedSerialNum(mSelectedSerialNum)
                                .setOnCancelListener(new WheelDialog.OnItemClickListener() {
                                    @Override
                                    public void onClick(WheelDialog dialog, int position) {
                                        LogUtils.i(TAG, "setOnCancelListener position = " + position);
                                        dialog.dismiss();
                                    }
                                })
                                .setOnSureListener(new WheelDialog.OnItemClickListener() {
                                    @Override
                                    public void onClick(WheelDialog dialog, int position) {
                                        LogUtils.i(TAG, "setOnSureListener position = " + position);
                                        if (0 <= position && position < mRobotSerialNumList.size()) {
                                            mSelectedSerialNum = mRobotSerialNumList.get(position);
                                            mSerialNumText.setText(mSelectedSerialNum);
                                        }
                                        dialog.dismiss();
                                    }
                                })
                                .create();
                        wheelDialog.show();
                    }
                }
            });

            CheckBox checkbox_upload = findViewById(R.id.checkbox_upload);
            checkbox_upload.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    isUploadLog = checked;
                }
            });
        }
    }

    private void setCommitViewEnable() {
        if (feedBackDetail.getContentText().length() > 1 && feedBackDetail.getContentText().length() < 201) {
            commit.setEnabled(true);
        } else {
            commit.setEnabled(false);
        }
    }

    private void showLoading() {
        if (loadingDialog == null) {
            loadingDialog = new MiniNetwrokProgressDailog(this, getResources().getString(R.string.submitting), R.style.Dialog_Fullscreen);
        }
        loadingDialog.show(this);
    }

    private void dismissLoading(){
        if(loadingDialog != null){
            loadingDialog.dismiss();
        }
    }

    private void requestReadWrite() {
        int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE" };
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(this,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                View view = getCurrentFocus();
                hideKeyBoard(ev, view);
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);

    }

    private void hideKeyBoard(MotionEvent event, View view) {
        int[] location = { 0, 0 };
        view.getLocationInWindow(location);
        int left = location[0], top = location[1], right = left
                + view.getWidth(), bootom = top + view.getHeight();
        // 判断焦点位置坐标是否在空间内，如果位置在控件外，则隐藏键盘
        if (event.getRawX() < left || event.getRawX() > right
                || event.getY() < top || event.getRawY() > bootom) {
            // 隐藏键盘
            IBinder token = view.getWindowToken();
            InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exit();
    }

    private void exit() {
        String feedDetail = feedBackDetail.getContentText();
        String phone = phoneInfo.getText().toString();
        String userId = AuthLive.getInstance().getUserId();
//        if (!TextUtils.isEmpty(feedDetail) || !TextUtils.isEmpty(phone)) {
//            SPUtils.get().put(FEED_BACK_DETAIL_KEY + userId, feedDetail);
//            SPUtils.get().put(FEED_BACK_PHONE_KEY + userId, phone);
//        } else {
//            SPUtils.get().put(FEED_BACK_DETAIL_KEY + userId, "");
//            SPUtils.get().put(FEED_BACK_PHONE_KEY + userId, "");
//        }
        finish();
    }

    @Override
    public void finish() {
        KeyboardUtils.hideSoftInput(this);
        super.finish();
    }
}
