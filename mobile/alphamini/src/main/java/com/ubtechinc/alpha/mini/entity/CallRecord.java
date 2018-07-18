package com.ubtechinc.alpha.mini.entity;

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.text.TextUtils;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.utils.ResourceUtils;
import com.ubtechinc.alpha.mini.utils.TimeUtils;
import com.ubtechinc.alpha.mini.database.EntityManagerHelper;
import com.ubtechinc.framework.db.annotation.Column;
import com.ubtechinc.framework.db.annotation.Id;
import com.ubtechinc.framework.db.annotation.Table;

import java.io.Serializable;
import java.util.Date;

import static com.ubtechinc.framework.db.annotation.GenerationType.ASSIGN;

/**
 * 通话记录实体类.
 */


@Table(version = EntityManagerHelper.DB_CALL_RECORD_VERSION)
public class CallRecord implements Serializable {

    /** Call log type for incoming calls. */
    public static final int INCOMING_TYPE = 1;
    /** Call log type for outgoing calls. */
    public static final int OUTGOING_TYPE = 2;
    /** Call log type for missed calls. */
    public static final int MISSED_TYPE = 3;
    /** Call log type for calls rejected by direct user action. */
    public static final int REJECTED_TYPE = 5;

    @Id(strategy = ASSIGN)
    private String id;

    @Column
    private String robotId;

    @Column
    private long callId;

    @Column
    private String callerName;

    @Column
    private int type;

    @Column
    private long duration;

    @Column
    private long callTime;

    private String dataString;

    private String timeString;

    private String subTitle;

    public String getRobotId() {
        return robotId;
    }

    public void setRobotId(String robotId) {
        this.robotId = robotId;
    }

    public long getCallId() {
        return callId;
    }

    public void setCallId(long callId) {
        this.callId = callId;
    }

    public String getCallerName() {
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getCallTime() {
        return callTime;
    }

    public void setCallTime(long callTime) {
        this.callTime = callTime;
    }

    public String getDate() {
        if (TextUtils.isEmpty(dataString)) {
            dataString = TimeUtils.getDateString(new Date(callTime));
        }
        return dataString;
    }

    public String getTime() {
        if (TextUtils.isEmpty(timeString)) {
            timeString = TimeUtils.getTimeString(new Date(callTime));
        }
        return timeString;
    }

    public String getSubTitle() {
        if (TextUtils.isEmpty(subTitle)) {
            switch (type) {
                case OUTGOING_TYPE:
                    subTitle = ResourceUtils.getString(R.string.type_call, TimeUtils.getDurationString(duration));
                    break;
                case INCOMING_TYPE:
                    if(duration == 0){
                        subTitle = ResourceUtils.getString(R.string.type_rejected);
                    }else{
                        subTitle = ResourceUtils.getString(R.string.type_incoming, TimeUtils.getDurationString(duration));
                    }
                    break;
                case MISSED_TYPE:
                    subTitle = ResourceUtils.getString(R.string.type_missed);
                    break;
                case REJECTED_TYPE:
                    subTitle = ResourceUtils.getString(R.string.type_rejected);
                    break;
                default:
                    subTitle = ResourceUtils.getString(R.string.type_call, TimeUtils.getDurationString(duration));
                    break;
            }
        }
        return subTitle;
    }

    public Drawable getSubTitleDrawable() {
        switch (type) {
            case OUTGOING_TYPE:
                return ResourceUtils.getDrawable(R.drawable.ic_call);
            case INCOMING_TYPE:
                if(duration == 0){
                    return ResourceUtils.getDrawable(R.drawable.ic_missed_calls);
                }else{
                    return ResourceUtils.getDrawable(R.drawable.ic_incoming);
                }
            case MISSED_TYPE:
                return ResourceUtils.getDrawable(R.drawable.ic_missed_calls);
            default:
                return ResourceUtils.getDrawable(R.drawable.ic_missed_calls);
        }
    }

    public
    @ColorRes
    int getTitleColor() {
        switch (type) {
            case OUTGOING_TYPE:
                return ResourceUtils.getColor(R.color.first_text);
            case INCOMING_TYPE:
                if(duration == 0){
                    return ResourceUtils.getColor(R.color.connect_error_bg);
                }else{
                    return ResourceUtils.getColor(R.color.first_text);
                }
            case MISSED_TYPE:
                return ResourceUtils.getColor(R.color.connect_error_bg);
            default:
                return ResourceUtils.getColor(R.color.connect_error_bg);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof CallRecord) {
            return ((CallRecord) obj).getId().equals(getId());
        } else {
            return false;
        }
    }
}
