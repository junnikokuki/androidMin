package com.clj.fastble.data;

import java.util.List;

/**
 * @Deseription
 * @Author tanghongyu
 * @Time 2018/5/11 14:44
 */

public interface IDataEngine {

    public List<byte[]> spliteData(byte[] data);
    public byte[]  packetData(byte[] data);
}
