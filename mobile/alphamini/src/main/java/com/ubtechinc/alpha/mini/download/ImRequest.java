package com.ubtechinc.alpha.mini.download;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ubtechinc.alpha.AlDwonloadfile;
import com.ubtechinc.alpha.AlphaMessageOuterClass;
import com.ubtechinc.alpha.im.IMCmdId;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.entity.RobotInfo;
import com.ubtechinc.alpha.mini.im.Phone2RobotMsgMgr;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtechinc.nets.phonerobotcommunite.ICallback;
import com.ubtrobot.common.utils.XLog;

public class ImRequest implements Runnable, Parcelable, Comparable<ImRequest> {

    public static final int HIGH_PRIORITY   = 1001;

    public static final int NORMAL_PRIORITY = 1000;

    public static final int THUMB_LEVEL  = 1;//用于IM协议标示缩图片，不可修改

    public static final int ORIGIN_LEVEL = 0;//用于IM协议标示原图片，不可修改

    public static final int HD_LEVE      = 2;//用于IM协议标示高清图片，不可修改

    private int mPriority = NORMAL_PRIORITY;//优先级，默认为普通

    private int mLevel    = THUMB_LEVEL;    //缩略图，级别

    private long size;

    private String fileName = "";

    public ImRequest() {
        super();
    }

    public ImRequest(int priority) {
        super();
        mPriority = priority;
    }

    public void setPriority(int priority) {
        this.mPriority = priority;
    }

    public int getPriority() {
        return mPriority;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName1) {
        fileName = fileName1;
    }

    public int getLevel() {
        return mLevel;
    }

    public void setLevel(int mLevel) {
        this.mLevel = mLevel;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public void run() {
        String peer = "";
        RobotInfo robotInfo = MyRobotsLive.getInstance().getCurrentRobot().getValue();
        if (robotInfo != null) {
            peer = robotInfo.getRobotUserId();
        }
        if(getFileName()==null||getFileName().equals("")){
            return;
        }
        AlDwonloadfile.AlDwonloadfileRequest.Builder builder = AlDwonloadfile.AlDwonloadfileRequest.newBuilder();
        builder.setName(getFileName());
        builder.setLevel(getLevel());
        Log.d("xxxxxxx","sendData"+getFileName()+getLevel());

        Phone2RobotMsgMgr.getInstance().sendData(IMCmdId.IM_GET_MOBILE_ALBUM_DOWNLOAD_REQUEST,
                "1",
                builder.build(),
                peer, new ICallback<AlphaMessageOuterClass.AlphaMessage>() {
                    @Override
                    public void onSuccess(AlphaMessageOuterClass.AlphaMessage data) {
                        XLog.d("xxxxxxx", "download success");

                    }

                    @Override
                    public void onError(ThrowableWrapper e) {
                        XLog.d("xxxxxxx", "download onError"+e.getMessage());

                    }
                });

    }


    //序列化，为了传递给Service，如果是使用Thread处理本例，则无需序列化 
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public ImRequest createFromParcel(Parcel source) {
            ImRequest data = null;
            Bundle bundle = source.readBundle();
            if(bundle != null) {
                data = new ImRequest(bundle.getInt("PRIORITY"));
                data.fileName = bundle.getString("FILENAME");
                data.mLevel = bundle.getInt("LEVEL");
                data.size = bundle.getInt("size");
            }
            return data;
        }

        @Override
        public ImRequest[] newArray(int size) {
            return new ImRequest[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putInt("PRIORITY", mPriority);
        bundle.putString("FILENAME", fileName);
        bundle.putInt("LEVEL", mLevel);
        bundle.putLong("SIZE",size);
        dest.writeBundle(bundle);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImRequest)) return false;

        ImRequest request = (ImRequest) o;

        return getFileName().equals(request.getFileName());
    }

    @Override
    public int hashCode() {
        return getFileName().hashCode();
    }

    @Override
    public int compareTo(@NonNull ImRequest args) {
        return mPriority < args.getPriority() ? 1 : ( mPriority > args.getPriority() ? -1 : 0 );
    }
}