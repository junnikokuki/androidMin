package com.ubtechinc.alpha.mini.ui.msg;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ubtech.utilcode.utils.CollectionUtils;
import com.ubtechinc.alpha.im.IMCmdId;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseToolbarActivity;
import com.ubtechinc.alpha.mini.entity.Message;
import com.ubtechinc.alpha.mini.entity.RobotPermission;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.entity.observable.MessageLive;
import com.ubtechinc.alpha.mini.viewmodel.AccountApplyViewModel;
import com.ubtechinc.alpha.mini.viewmodel.MessageViewModel;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.ubtechinc.alpha.mini.widget.SwitchButton;
import com.ubtechinc.alpha.mini.widget.taglayout.FlowLayout;
import com.ubtechinc.alpha.mini.widget.taglayout.TagAdapter;
import com.ubtechinc.alpha.mini.widget.taglayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.ubtechinc.alpha.mini.constants.Constants.APPLY_HANDEL_ERROR;
import static com.ubtechinc.alpha.mini.constants.Constants.PERMISSIONCODE_AVATAR;
import static com.ubtechinc.alpha.mini.constants.Constants.PERMISSIONCODE_CALL;
import static com.ubtechinc.alpha.mini.constants.Constants.PERMISSIONCODE_PHOTO;
import static com.ubtechinc.alpha.mini.constants.Constants.PERMISSION_CODE_CODING;


public class MessageDetailActivity extends BaseToolbarActivity {

    public static final String NOTICE_ID = "noticeId";
    public static final String MESSAGE = "message";
    public static final String SCENCE = "sence";
    public static final int TYPE_DETAIL = 0;
    public static final int TYPE_APPLY = 1;

    MessageViewModel messageViewModel;

    AccountApplyViewModel accountApplyViewModel;

    private Message message;

    private View shareHandleContent;
    private View shareResultContent;
    private TextView msgTitleView;
    private TextView msgDateView;
    private TextView msgTitleRobotIdView;
    private View msgTitleLine;

    private String permission;

    private int sence;

    private MaterialDialog errorDialog;

    private View rejectBtn;
    private View acceptBtn;
    private SwitchButton videoSwitch;
    private SwitchButton gallerySwitch;
    private SwitchButton callSwitchbtn;
    private SwitchButton codeSwitch;
    private TagFlowLayout tagFlowLayout;

    private List<String> tagList;

    private String callTag;
    private String avatarTag;
    private String galleryTag;
    private String codeTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);
        initView();
        initData();
    }

    private void initView() {
        View toolbar = findViewById(R.id.toolbar);
        msgTitleView = findViewById(R.id.msg_title);
        msgDateView = findViewById(R.id.msg_date);
        msgTitleRobotIdView = findViewById(R.id.msg_title_robot_id);
        msgTitleLine = findViewById(R.id.msg_title_line);
        initToolbar(toolbar, "", View.GONE, false);
        initConnectFailedLayout(R.string.mobile_connected_error, R.string.robot_connected_error);
    }

    private void initData() {
        accountApplyViewModel = AccountApplyViewModel.getInstance();
        messageViewModel = MessageViewModel.get();
        Intent intent = getIntent();
        String noticeId = intent.getStringExtra(NOTICE_ID);
        sence = intent.getIntExtra(SCENCE, 0);
        message = (Message) intent.getSerializableExtra(MESSAGE);
        if (message == null) {
            messageViewModel.getMessageById(noticeId).observe(this, new Observer<LiveResult>() {
                @Override
                public void onChanged(@Nullable LiveResult liveResult) {
                    switch (liveResult.getState()) {
                        case LOADING:
                            showLoadingDialog();
                            break;
                        case SUCCESS:
                            message = (Message) liveResult.getData();
                            showData(message);
                            dismissDialog();
                            break;
                        case FAIL:
                            toastError(getString(R.string.op_fail));
                            dismissDialog();
                            break;
                        default:
                            break;
                    }
                }
            });
        } else {
            showData(message);
        }
    }

    private void showData(Message message) {
        if (message != null) {
            if (message.getIsRead() != 1) {
                messageViewModel.readMessage(message.getNoticeId());
                this.message.setIsRead(1);
                setResult();
                if (message.getNoticeType() == Message.TYPE_SHARE) {
                    MessageLive.get().readShareMsg();
                } else {
                    MessageLive.get().readSysMsg();
                }
            }
            msgTitleView.setText(message.getNoticeTitle());
            msgDateView.setText(message.getDate());
            if (!TextUtils.isEmpty(message.getRobotId())) {
                msgTitleRobotIdView.setText(getString(R.string.robot_id, message.getRobotId()));
                msgTitleLine.setVisibility(View.INVISIBLE);
                msgDateView.setPadding(msgDateView.getPaddingLeft(), 0, msgDateView.getPaddingRight(), 0);
            } else {
                msgTitleRobotIdView.setVisibility(View.GONE);
            }
            switch (message.getNoticeType()) {
                case Message.TYPE_SYS:
                    showMsgDetail();
                    break;
                case Message.TYPE_SHARE:
                    if (Integer.valueOf(message.getCommandId()) == IMCmdId.IM_ACCOUNT_APPLY_RESPONSE
                            || Integer.valueOf(message.getCommandId()) == IMCmdId.IM_ACCOUNT_PERMISSION_REQUEST_RESPONSE) {
                        showApplyDetail(message);
                    } else {
                        showMsgDetail();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void showApplyDetail(Message message) {
        String op = message.getOperation();
        int msgOp = 0;
        if (!TextUtils.isEmpty(op)) {
            msgOp = Integer.valueOf(op);
        }
        switch (msgOp) {
            case Message.OP_NORMAL:
                showApplyHandleDetail();
                break;
            case Message.OP_ACCEPT:
            case Message.OP_REJECT:
                showApplyResultDetail();
                break;
            case Message.OP_EXPIRE:
                showApplyHandleDetail();
                break;
        }
    }

    private void showApplyHandleDetail() {
        if (shareResultContent != null) {
            shareResultContent.setVisibility(View.GONE);
        }
        if (shareHandleContent == null) {
            ViewStub viewStub = findViewById(R.id.share_handle_content);
            shareHandleContent = viewStub.inflate();
        }
        acceptBtn = findViewById(R.id.accept_btn);
        rejectBtn = findViewById(R.id.reject_btn);
        videoSwitch = findViewById(R.id.switchbtn_video);
        callSwitchbtn = findViewById(R.id.switchbtn_call);
        gallerySwitch = findViewById(R.id.switchbtn_gallery);
        codeSwitch = findViewById(R.id.switchbtn_coding);
        tagFlowLayout = findViewById(R.id.id_flowlayout);
        if (String.valueOf(Message.OP_EXPIRE).equals(message.getOperation())) {//过期的消息将按钮不可点击
            showHandleExpired();
            return;
        }
        final List<String> tags = Arrays.asList(getResources().getStringArray(R.array.funtion));
        tagList = new ArrayList(tags);
        showTag();
        callTag = getString(R.string.contact);
        galleryTag = getString(R.string.gallery);
        avatarTag = getString(R.string.video_surveillance);
        codeTag = getString(R.string.coding);

        gallerySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    tagList.add(galleryTag);
                } else {
                    tagList.remove(galleryTag);
                }
                showTag();
            }
        });

        callSwitchbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    tagList.add(callTag);
                } else {
                    tagList.remove(callTag);
                }
                showTag();
            }
        });

        videoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    tagList.add(avatarTag);
                } else {
                    tagList.remove(avatarTag);
                }
                showTag();
            }
        });

        codeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tagList.add(codeTag);
                } else {
                    tagList.remove(codeTag);
                }
                showTag();
            }
        });

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permission = "";
                List<RobotPermission> robotPermissions = new ArrayList<RobotPermission>(2);

                RobotPermission robotPermission = new RobotPermission();
                robotPermission.setStatus(0);
                robotPermission.setPermissionCode(PERMISSIONCODE_AVATAR);
                robotPermission.setPermissionDesc(getString(R.string.video_surveillance));

                RobotPermission albumPermission = new RobotPermission();
                albumPermission.setStatus(0);
                albumPermission.setPermissionCode(PERMISSIONCODE_PHOTO);
                albumPermission.setPermissionDesc(getString(R.string.gallery));

                RobotPermission callPermission = new RobotPermission();
                callPermission.setStatus(0);
                callPermission.setPermissionCode(PERMISSIONCODE_CALL);
                callPermission.setPermissionDesc(getString(R.string.make_call));

                RobotPermission codePermission = new RobotPermission();
                codePermission.setStatus(0);
                codePermission.setPermissionCode(PERMISSION_CODE_CODING);
                codePermission.setPermissionDesc(getString(R.string.coding));

                robotPermissions.add(robotPermission);
                robotPermissions.add(albumPermission);
                robotPermissions.add(callPermission);
                robotPermissions.add(codePermission);


                if (videoSwitch.isChecked()) {
                    permission += PERMISSIONCODE_AVATAR;
                    robotPermission.setStatus(1);
                }
                if (gallerySwitch.isChecked()) {
                    if (!TextUtils.isEmpty(permission)) {
                        permission += ",";
                    }
                    albumPermission.setStatus(1);
                    permission += PERMISSIONCODE_PHOTO;
                }
                if (callSwitchbtn.isChecked()) {
                    if (!TextUtils.isEmpty(permission)) {
                        permission += ",";
                    }
                    callPermission.setStatus(1);
                    permission += PERMISSIONCODE_CALL;
                }
                if (codeSwitch.isChecked()) {
                    if (!TextUtils.isEmpty(permission)) {
                        permission += ",";
                    }
                    codePermission.setStatus(1);
                    permission += PERMISSION_CODE_CODING;
                }

                showLoadingDialog();
                message.setPermissionList(robotPermissions);
                accountApplyViewModel.applyApprove(message, permission).observe(MessageDetailActivity.this, new Observer<LiveResult>() {
                    @Override
                    public void onChanged(@Nullable LiveResult liveResult) {
                        switch (liveResult.getState()) {
                            case LOADING:
//                                showLoadingDialog();
                                break;
                            case SUCCESS:
                                if (sence == TYPE_DETAIL) {
                                    setResult();
                                    dismissDialog();
                                    showApplyResultDetail();
                                    liveResult.removeObservers(MessageDetailActivity.this);
                                    if (!TextUtils.isEmpty(message.getSenderName())) {
                                        toastSuccess(getString(R.string.robot_shared_to, message.getSenderName()));
                                    }else{
                                        toastSuccess(getString(R.string.robot_shared_tips));
                                    }
                                } else {
                                    if (!TextUtils.isEmpty(message.getSenderName())) {
                                        toastSuccess(getString(R.string.robot_shared_to, message.getSenderName()));
                                    }else{
                                        toastSuccess(getString(R.string.robot_shared_tips));
                                    }
                                    finish();
                                }
                                break;
                            case FAIL:
                                dismissDialog();
                                if (TextUtils.isEmpty(liveResult.getMsg())) {
                                    toastError(getString(R.string.op_fail));
                                } else {
                                    if (liveResult.getErrorCode() == APPLY_HANDEL_ERROR) {
                                        showErrorDialog();
                                    } else {
                                        toast(liveResult.getMsg());
                                    }
                                }
                                liveResult.removeObservers(MessageDetailActivity.this);
                                break;
                        }
                    }
                });
            }
        });

        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoadingDialog();
                accountApplyViewModel.applyReject(message).observe(MessageDetailActivity.this, new Observer<LiveResult>() {
                    @Override
                    public void onChanged(@Nullable LiveResult liveResult) {
                        switch (liveResult.getState()) {
                            case LOADING:
//                                showLoadingDialog();
                                break;
                            case SUCCESS:
                                if (sence == TYPE_DETAIL) {
                                    setResult();
                                    dismissDialog();
                                    showApplyResultDetail();
                                    liveResult.removeObservers(MessageDetailActivity.this);
                                    if (!TextUtils.isEmpty(message.getSenderName())) {
                                        toastError(getString(R.string.rejected_shared_to, message.getSenderName()));
                                    }else{
                                        toastError(getString(R.string.rejected_shared_tips));
                                    }
                                } else {
                                    if (!TextUtils.isEmpty(message.getSenderName())) {
                                        toastError(getString(R.string.rejected_shared_to, message.getSenderName()));
                                    }else{
                                        toastError(getString(R.string.rejected_shared_tips));
                                    }
                                    finish();
                                }
                                break;
                            case FAIL:
                                dismissDialog();
                                if (TextUtils.isEmpty(liveResult.getMsg())) {
                                    toast(R.string.op_fail);
                                } else {
                                    if (liveResult.getErrorCode() == APPLY_HANDEL_ERROR) {
                                        showErrorDialog();
                                    } else {
                                        toastError(liveResult.getMsg());
                                    }
                                }
                                liveResult.removeObservers(MessageDetailActivity.this);
                                break;
                        }
                    }
                });
            }
        });

    }

    private void showApplyResultDetail() {
        if (shareHandleContent != null) {
            shareHandleContent.setVisibility(View.GONE);
        }
        if (shareResultContent == null) {
            ViewStub viewStub = findViewById(R.id.share_result_content);
            shareResultContent = viewStub.inflate();
        }
        TextView resultText = findViewById(R.id.msg_apply_result_text);
        TextView resultTitle = findViewById(R.id.msg_apply_result);
        TextView resultDesc = findViewById(R.id.msg_apply_result_desc);
        if (String.valueOf(Message.OP_ACCEPT).equals(message.getOperation())) {
            resultText.setText(R.string.robot_shared);
            resultTitle.setText(getPermissionDesc(message.getPermissionList()));
            resultDesc.setTextColor(getResources().getColor(R.color.black));
        } else {
            resultText.setText(R.string.rejected);
            resultDesc.setVisibility(View.INVISIBLE);
            resultTitle.setText(R.string.can_not_use_robot);
            resultText.setTextColor(getResources().getColor(R.color.btn_red));
        }


    }

    private String getPermissionDesc(List<RobotPermission> permissions) {
        if (CollectionUtils.isEmpty(permissions)) {
            //return "对方无法使用视频监控、相册和电话功能";
            return getString(R.string.share_permissions, avatarTag, galleryTag, codeTag, callTag);
        } else {
            String allowPermissions = "";
            String rejectPermissions = "";
            for (RobotPermission robotPermission : permissions) {
                if (robotPermission.getStatus() == 1) {
                    allowPermissions += (robotPermission.getPermissionDesc() + "、");
                } else {
                    rejectPermissions += (robotPermission.getPermissionDesc() + "、");
                }
            }
            String tips = "";
            if (!TextUtils.isEmpty(allowPermissions)) {
                if (allowPermissions.endsWith("、")) {
                    allowPermissions = allowPermissions.substring(0, allowPermissions.length() - 1);
                }
                //tips += "对方可以使用" + allowPermissions + "功能";
                tips += getString(R.string.share_permissions_1, allowPermissions);
            }
            if (!TextUtils.isEmpty(rejectPermissions)) {
                if (rejectPermissions.endsWith("、")) {
                    rejectPermissions = rejectPermissions.substring(0, rejectPermissions.length() - 1);
                }
                if (TextUtils.isEmpty(tips)) {
                    //tips += "对方无法使用" + rejectPermissions + "功能";
                    tips += getString(R.string.share_permissions_2, rejectPermissions);
                } else {
                    //tips += ",但不可以使用" + rejectPermissions + "功能";
                    tips += getString(R.string.concat) + getString(R.string.share_permissions_3, rejectPermissions);
                }
            }
            if (TextUtils.isEmpty(tips)) {
                //tips = "对方无法使用视频监控、相册和电话功能";
                tips = getString(R.string.share_permissions, avatarTag, galleryTag, codeTag, callTag);
            }
            return tips;
        }
    }

    private void showMsgDetail() {
        if (shareHandleContent != null) {
            shareHandleContent.setVisibility(View.GONE);
        }
        if (shareResultContent == null) {
            ViewStub viewStub = findViewById(R.id.share_result_content);
            shareResultContent = viewStub.inflate();
        }
        TextView resultText = findViewById(R.id.msg_apply_result_text);
        TextView resultTitle = findViewById(R.id.msg_apply_result);
        TextView resultDesc = findViewById(R.id.msg_apply_result_desc);
        resultText.setVisibility(View.GONE);
        resultDesc.setVisibility(View.INVISIBLE);
        resultTitle.setText(message.getNoticeDes());

    }

    private void showHandleExpired() {
        if (rejectBtn != null) {
            rejectBtn.setEnabled(false);
        }
        if (acceptBtn != null) {
            acceptBtn.setEnabled(false);
        }
        if (videoSwitch != null) {
            videoSwitch.setEnabled(false);
        }
        if (gallerySwitch != null) {
            gallerySwitch.setEnabled(false);
        }
        if (callSwitchbtn != null) {
            callSwitchbtn.setEnabled(false);
        }
    }

    private void showErrorDialog() {
        if (errorDialog == null) {
            errorDialog = new MaterialDialog(this);
            errorDialog.setMessage(getString(R.string.msg_expired));
            message.setOperation(String.valueOf(Message.OP_EXPIRE));
            setResult();
            showHandleExpired();
            errorDialog.setPositiveButton(R.string.i_know, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    errorDialog.dismiss();
                }
            });
        }
        errorDialog.show();
    }

    private void setResult() {
        Intent intent = new Intent();
        intent.putExtra(MESSAGE, message);
        setResult(1, intent);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }

    private void showTag() {
        if (tagFlowLayout == null || tagList == null) {
            return;
        }
        tagFlowLayout.setAdapter(new TagAdapter<String>(tagList) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = new TextView(MessageDetailActivity.this);
                tv.setTextColor(ContextCompat.getColor(MessageDetailActivity.this, R.color.first_text));
                tv.setBackgroundResource(R.drawable.shape_grey_rectangle_tag_bg);
                tv.setText(s);
                return tv;
            }
        });
    }
}
