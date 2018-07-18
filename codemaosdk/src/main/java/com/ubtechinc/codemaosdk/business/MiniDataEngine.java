package com.ubtechinc.codemaosdk.business;

import com.clj.fastble.data.IDataEngine;
import com.ubtechinc.protocollibrary.protocol.MiniBleProto;

import java.util.List;

/**
 * @Deseription
 * @Author tanghongyu
 * @Time 2018/5/11 17:32
 */

public class MiniDataEngine implements IDataEngine {
    @Override
    public List<byte[]> spliteData(byte[] data) {
        return MiniBleProto.INSTANCE.devide(data);
    }

    @Override
    public byte[] packetData(byte[] data) {
        return MiniBleProto.INSTANCE.devide(data).get(0);
    }
}
