package com.ubt.lancommunicationhelper;

import com.google.protobuf.Message;
import com.ubtechinc.alpha.PushMessage;

/**
 * Created by bob.xu on 2018/1/15.
 */

public interface MessageListener {
    void onReceiveMessage(PushMessage.MessageWrapper messageWrapper);
}
