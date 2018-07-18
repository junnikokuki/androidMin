package com.ubtechinc.alpha.mini.ui.friend;

import android.os.Parcel;
import android.os.Parcelable;

import com.ubtechinc.alpha.CmFaceList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Date: 2017/12/20.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 机器人好友的数据结构,因为CmFaceInfo为protoBuffer生成的数据结构，
 * 我们不能再这个对象的基础上派生子类, 所以定义了FriendFaceInfo这样一个数据结构,实现了Parcelable，用以在页面间传递。
 */
public class FriendFaceInfo implements Parcelable{

    private String name;

    private String id;

    private String avatar;

    /****本地字段用以标识是否被选中**/
    private boolean isChecked;

    public FriendFaceInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    protected FriendFaceInfo(Parcel in) {
        name = in.readString();
        id = in.readString();
        avatar = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
        dest.writeString(avatar);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FriendFaceInfo> CREATOR = new Creator<FriendFaceInfo>() {
        @Override
        public FriendFaceInfo createFromParcel(Parcel in) {
            return new FriendFaceInfo(in);
        }

        @Override
        public FriendFaceInfo[] newArray(int size) {
            return new FriendFaceInfo[size];
        }
    };

    public static List<FriendFaceInfo> transFormCmFaceListToFriendList(List<CmFaceList.CMFaceInfo> cmFaceInfoList){
        List<FriendFaceInfo> friendFaces = new ArrayList<>();
        if (cmFaceInfoList == null){
            return friendFaces;
        }
        Iterator<CmFaceList.CMFaceInfo> iterator = cmFaceInfoList.iterator();
        while (iterator.hasNext()){
            CmFaceList.CMFaceInfo info = iterator.next();
            FriendFaceInfo friendFaceInfo = new FriendFaceInfo();
            friendFaceInfo.setAvatar(info.getAvatar());
            friendFaceInfo.setId(info.getId());
            friendFaceInfo.setName(info.getName());
            friendFaces.add(friendFaceInfo);
        }
        return friendFaces;
    }
}
