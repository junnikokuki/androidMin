package com.ubtechinc.alpha.mini.im;

import com.ubtechinc.alpha.im.IMCmdId;

import java.util.ArrayList;
import java.util.List;

public class ImTimeoutWhileList {

    public final static List<Integer> whileList = new ArrayList<>();

    static {
        whileList.add(IMCmdId.IM_GET_AGORA_ROOM_INFO_REQUEST);
        whileList.add(IMCmdId.IM_SEND_WIFI_TO_ROBOT_REQUEST);
        whileList.add(IMCmdId.IM_START_FACE_DETECT_REQUEST);
        whileList.add(IMCmdId.IM_DISCONNECT_ROBOT_REQUEST);
    }

}
