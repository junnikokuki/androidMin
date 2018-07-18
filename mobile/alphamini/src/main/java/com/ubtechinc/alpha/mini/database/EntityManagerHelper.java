/*
 *
 *  *
 *  * Copyright (c) 2008-2016 UBT Corporation.  All rights reserved.  Redistribution,
 *  *  modification, and use in source and binary forms are not permitted unless otherwise authorized by UBT.
 *  *
 *
 */
package com.ubtechinc.alpha.mini.database;

/**
 * @author liuhai
 * @date 2017/05/10
 * @Description 数据库管理基本数据定义
 * @modifier liuhai
 * @modify_time 2017/5/10
 */

public class EntityManagerHelper {

    public static final int DB_VERSION = 1;
    public static final String DB_ACCOUNT = "alpha_mini";


    public static final int DB_APP_VERSION = 4;
    public static final int DB_ACTION_VERSION = 2;
    public static final int DB_ROBOT_VERSION = 2;
    public static final int DB_NOTICE_MESSAGE_VERSION = 7;
    public static final int DB_CALL_RECORD_VERSION = 7;
    public static final int DB_CONTACT_TABLE_VERSION = 9;
    public static final int DB_ALBUM_TABLE_VERSION = 1;
    //编程作品
    public static final int DB_CODING_OPUS_VERSION = 4;

    public static final String DB_ALBUM_LIST_TABLE = "album_list_table";
    public static final String DB_CONTACT_LIST_TABLE = "contact_list_table";
    public static final String DB_CALLRECORD_LIST_TABLE = "callRecord_list_table";
    public static final String DB_ROBOT_TVS_INFO_TABLE = "robot_tvs_info_table";
    public static final String DB_NOTICE_MESSAGE_TABLE = "notice_message_table";
    public static final String DB_ALBUMS_LIST_TABLE = "albums_list_table";
    public static final String DB_CODING_OPUS_LIST_TABLE = "coding_ops_list_table";
}
