package com.ubtechinc.alpha.mini.entity;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ubtechinc.alpha.im.IMCmdId;
import com.ubtechinc.alpha.mini.database.EntityManagerHelper;
import com.ubtechinc.framework.db.annotation.Column;
import com.ubtechinc.framework.db.annotation.Id;
import com.ubtechinc.framework.db.annotation.Table;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Table(version = EntityManagerHelper.DB_NOTICE_MESSAGE_VERSION)
public class Message implements Serializable {

    private static Gson gson;

    public static final String SHARE_PERMISSION_CHANGE = "共享权限变更";
    public static final String SHARE_REQUIRE_HANDLE = "共享申请处理";
    public static final String USE_PERMISSION_CHANGE = "使用权限变更";
    public static final String SHARE_REQUIRE = "共享申请通知";
    public static final String INVITATION_ACCEPTED = "共享邀请通知";
    public static final String DEFAULT = "通知";

    public static final int TYPE_SYS = 0;
    public static final int TYPE_SHARE = 3;

    public static final int OP_NORMAL = 0;
    public static final int OP_REJECT = 1;
    public static final int OP_ACCEPT = 2;
    public static final int OP_EXPIRE = 3;


    @Id
    private String noticeId;

    @Column
    private int noticeType;

    @Column
    private String fromId;

    @Column
    private String operation;

    @Column
    private String senderName;

    @Column
    private int isRead;

    @Column
    private String noticeDes;

    @Column
    private String toId;

    @Column
    private Long noticeTime;

    @Column
    private Long time;

    @Column
    private String robotId;

    private String showDate;

    @Column
    private String userId;

    @Column
    private String commandId;

    @Column
    private int agree;

    @Column
    private String noticeTitle;

    @Column
    private String permission;

    private String typeTitle;

    @Column
    private String noticeExtend;

    private List<RobotPermission> permissionList;

    public List<RobotPermission> getPermissionList() {
        if (permissionList == null) {
            if (!TextUtils.isEmpty(permission)) {
                if (gson == null) {
                    gson = new Gson();
                }
                permissionList = gson.fromJson(permission, new TypeToken<List<RobotPermission>>() {
                }.getType());
            } else if (!TextUtils.isEmpty(noticeExtend)) {
                if (gson == null) {
                    gson = new Gson();
                }
                permissionList = gson.fromJson(noticeExtend, new TypeToken<List<RobotPermission>>() {
                }.getType());
            }
        }
        return permissionList;
    }

    public void setPermissionList(List<RobotPermission> permissionList) {
        this.permissionList = permissionList;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }

    public int getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(int noticeType) {
        this.noticeType = noticeType;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getOperation() {
        if (operation == null) {
            operation = String.valueOf(OP_NORMAL);
        }
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public String getNoticeDes() {
        return noticeDes;
    }

    public void setNoticeDes(String noticeDes) {
        this.noticeDes = noticeDes;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public Long getNoticeTime() {
        return noticeTime;
    }

    public void setNoticeTime(Long noticeTime) {
        this.noticeTime = noticeTime;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getRobotId() {
        return robotId;
    }

    public void setRobotId(String robotId) {
        this.robotId = robotId;
    }

    public String getShowDate() {
        return showDate;
    }

    public void setShowDate(String showDate) {
        this.showDate = showDate;
    }

    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    public String getDate() {
        if (showDate == null) {
            Date date = new Date(noticeTime);
            showDate = new SimpleDateFormat("yyyy年MM月dd日").format(date);
        }
        return showDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getAgree() {
        return agree;
    }

    public void setAgree(int agree) {
        this.agree = agree;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public void setNoticeTitle(String noticeTitle) {
        this.noticeTitle = noticeTitle;
    }

    public String getTypeTitle() {
        if (TextUtils.isEmpty(typeTitle)) {
            if (commandId != null) {
                switch (Integer.valueOf(commandId)) {
                    case IMCmdId.IM_ACCOUNT_APPLY_RESPONSE:
                        typeTitle = SHARE_REQUIRE;
                        break;
                    case IMCmdId.IM_ACCOUNT_BEEN_UNBIND_RESPONSE:
                        typeTitle = SHARE_PERMISSION_CHANGE;
                        break;
                    case IMCmdId.IM_ACCOUNT_HANDLE_APPLY_RESPONSE:
                        typeTitle = SHARE_REQUIRE_HANDLE;
                        break;
                    case IMCmdId.IM_ACCOUNT_INVITATION_ACCEPTED_RESPONSE:
                        typeTitle = INVITATION_ACCEPTED;
                        break;
                    case IMCmdId.IM_ACCOUNT_MASTER_UNBINDED_RESPONSE:
                        typeTitle = SHARE_PERMISSION_CHANGE;
                        break;
                    case IMCmdId.IM_ACCOUNT_PERMISSION_CHANGE_RESPONSE:
                        typeTitle = USE_PERMISSION_CHANGE;
                        break;
                    case IMCmdId.IM_ACCOUNT_SLAVER_UNBIND_RESPONSE:
                        typeTitle = SHARE_PERMISSION_CHANGE;
                        break;
                    case IMCmdId.IM_ACCOUNT_PERMISSION_REQUEST_RESPONSE:
                        typeTitle = SHARE_PERMISSION_CHANGE;
                        break;
                    default:
                        typeTitle = DEFAULT;
                        break;
                }
            } else {
                typeTitle = DEFAULT;
            }
        }
        return typeTitle;
    }

    public void setTypeTitle(String typeTitle) {
        this.typeTitle = typeTitle;
    }

    public String getNoticeExtend() {
        if (TextUtils.isEmpty(permission) && TextUtils.isEmpty(noticeExtend)) {
            if (gson == null) {
                gson = new Gson();
            }
            noticeExtend = gson.toJson(getPermissionList());
        }
        return noticeExtend;
    }

    public void setNoticeExtend(String noticeExtend) {
        this.noticeExtend = noticeExtend;
    }

    @Override
    public String toString() {
        return "Message{" +
                "noticeId='" + noticeId + '\'' +
                ", noticeType=" + noticeType +
                ", fromId='" + fromId + '\'' +
                ", operation='" + operation + '\'' +
                ", senderName='" + senderName + '\'' +
                ", isRead=" + isRead +
                ", noticeDes='" + noticeDes + '\'' +
                ", toId='" + toId + '\'' +
                ", noticeTime=" + noticeTime +
                ", time=" + time +
                ", robotId='" + robotId + '\'' +
                ", showDate='" + showDate + '\'' +
                ", userId='" + userId + '\'' +
                ", commandId='" + commandId + '\'' +
                ", noticeTitle='" + noticeTitle + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Message) {
            Message msg = (Message) obj;
            return msg.getNoticeId().equals(getNoticeId());
        }
        return super.equals(obj);
    }
}
