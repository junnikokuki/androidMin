package com.ubtechinc.alpha.mini.event;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;

/**
 * @Date: 2018/1/16.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 机器人好友列表改变
 */

public class FriendListChangeEvent {

    public static final int BASE_NUMBER = 1000;

    public static final int FRIEND_LIST_ERROR = BASE_NUMBER - 1;
    public static final int FRIEND_INFO_CHANGED = BASE_NUMBER + 1;
    public static final int FRIEND_LIST_DELETE = BASE_NUMBER + 2;
    public static final int FRIEND_LIST_ADD = BASE_NUMBER + 3;

    @FriendFaceEvent
    private int event = BASE_NUMBER;


    @IntDef({BASE_NUMBER, FRIEND_LIST_ERROR, FRIEND_INFO_CHANGED, FRIEND_LIST_DELETE, FRIEND_LIST_ADD})
    @Target({PARAMETER, FIELD})//作用目标
    @Retention(RetentionPolicy.SOURCE)//作用域
    public @interface FriendFaceEvent {}


    public FriendListChangeEvent(@FriendFaceEvent int event) {
        this.event = event;
    }

    public int getEvent() {
        return event;
    }


}
