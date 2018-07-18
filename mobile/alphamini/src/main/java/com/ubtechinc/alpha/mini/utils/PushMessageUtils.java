package com.ubtechinc.alpha.mini.utils;

import com.ubtechinc.alpha.mini.entity.Message;
import com.ubtechinc.nets.utils.JsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Date: 2017/11/17.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] :
 */

public class PushMessageUtils {

    public static Message parserMsgBody(String content){
        Message msg = JsonUtil.getObject(content, Message.class);
        return msg;
    }


    public static String parserMsgHeader(String header){
        try {
            JSONObject object = new JSONObject(header);
            return object.getString("commandId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
