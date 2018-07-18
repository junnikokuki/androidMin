package com.ubtechinc.alpha.mini.avatar.viewutils;

import android.util.Log;

import com.ubtechinc.alpha.mini.avatar.entity.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @作者：liudongyang
 * @日期: 18/5/24 16:53
 * @描述:
 */

public class AvatarUserListManger {

    private String TAG = getClass().getSimpleName();

    private List<String> mAllUsers;

    private List<String> mEnterUsers;

    private List<String> mLeaveUsers;

    private List<User> mUsers;

    private IAvatarUserChangeListener mlistener;

    private String currentUserId;

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    private static AvatarUserListManger manger;

    private AvatarUserListManger() {
        mAllUsers = new ArrayList<>();
        mEnterUsers = new ArrayList<>();
        mLeaveUsers = new ArrayList<>();
        mUsers = new ArrayList<>();
    }

    public static AvatarUserListManger get(){
        if (manger == null){
            synchronized (AvatarUserListManger.class){
                if (manger == null){
                    manger = new AvatarUserListManger();
                }
            }
        }
        return manger;
    }

    public void setIAvatarUserChangeListener(IAvatarUserChangeListener listener) {
        this.mlistener = listener;
        if (listener != null) {
            mlistener.onGetAllUser(mAllUsers);
            mlistener.onGetEnterUser(mEnterUsers);
            mlistener.onGetLeaveUser(mLeaveUsers);
        }
    }

    public void removeAvatarUserChangeListener(){
        mlistener = null;
    }

    public void avatarUserChange(List<String> allUsers, List<String> enterUsers, List<String> leaveUsers) {
        mAllUsers.clear();
        mEnterUsers.clear();
        mLeaveUsers.clear();
        mAllUsers.addAll(allUsers);
        mEnterUsers.addAll(enterUsers);
        mLeaveUsers.addAll(leaveUsers);
        if (mlistener != null) {
            mlistener.onGetAllUser(mAllUsers);
            mlistener.onGetEnterUser(mEnterUsers);
            mlistener.onGetLeaveUser(mLeaveUsers);
        }
    }

    public void setUserAvatars(List<User> avatarts) {
        Log.i(TAG, "setUserAvatars: ");
        mUsers.addAll(avatarts);
        if (mlistener != null) {
            mlistener.onGetAllUser(mAllUsers);
            mlistener.onGetEnterUser(mEnterUsers);
            mlistener.onGetLeaveUser(mLeaveUsers);
        }
    }

    public List<String> getAllUsers() {
        return mAllUsers;
    }

    public List<String> getEnterUsers() {
        return mEnterUsers;
    }

    public List<String> getLeaveUsers() {
        return mLeaveUsers;
    }

    public List<User> getBindUsers() {
        return mUsers;
    }

    public void release(){
        mAllUsers.clear();
        mEnterUsers.clear();
        mLeaveUsers.clear();
        manger = null;
    }

    public interface IAvatarUserChangeListener {

        public void onGetAllUser(List<String> allUsers);

        public void onGetEnterUser(List<String> enterUsers);

        public void onGetLeaveUser(List<String> leaveUsers);

    }


}
