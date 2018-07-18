package com.clj.fastble.bluetooth;


import android.util.Log;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.IDataEngine;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.exception.OtherException;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SplitWriter {

    private BleBluetooth mBleBluetooth;
    private String mUuid_service;
    private String mUuid_write;
    private byte[] mData;
    private int mCount;
    private BleWriteCallback mCallback;
    private Queue<byte[]> mDataQueue;
    private int mTotalNum;
    private IDataEngine mDataEngine;
    private Object writObject = new Object();
    public SplitWriter() {


    }


    public void splitWrite(BleBluetooth bleBluetooth,
                           String uuid_service,
                           String uuid_write,
                           byte[]data, IDataEngine iDataEngine,
                           BleWriteCallback callback) {
        mBleBluetooth = bleBluetooth;
        mUuid_service = uuid_service;
        mUuid_write = uuid_write;
        mData = data;
        mCount = BleManager.getInstance().getSplitWriteNum();
        mCallback = callback;
        mDataEngine = iDataEngine;
        splitWrite();
    }

    private void splitWrite() {
        if (mData == null) {
            throw new IllegalArgumentException("data is Null!");
        }
        if (mCount < 1) {
            throw new IllegalArgumentException("split count should higher than 0!");
        }
        mDataQueue = splitByte(mData, mCount);
        mTotalNum = mDataQueue.size();
        writeData();
    }

    private void writeData() {

        while (mDataQueue.peek() != null) {
            Log.d(TAG, "Split start Writer thread = " + Thread.currentThread().getName());
            byte[] data = mDataQueue.poll();
            write(data);

            Log.d(TAG, "write end thread = " + Thread.currentThread().getName());
        }

    }

    private static final String TAG = "SplitWriter";
    private int tryCount ;
    private void write(final byte[] data) {
        synchronized (writObject) {
            tryCount = 3;
            while (tryCount > 0) {
                tryCount --;
                write(data, new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        synchronized (writObject) {
                            int position = mTotalNum - mDataQueue.size();
                            tryCount = 0;
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            writObject.notify();

                            if (mCallback != null && current == total) {
                                mCallback.onWriteSuccess(position, mTotalNum, justWrite);

                            }
                        }

                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        synchronized (writObject) {
                            Log.d(TAG, "write fail");
                            if (mCallback != null && tryCount == 0) {
                                mCallback.onWriteFailure(new OtherException("exception occur while writing: " + exception.getDescription()));
                            }
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            writObject.notify();
                        }


                    }
                });
                try {
                    writObject.wait(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


    }
    private void write(byte[] data, BleWriteCallback bleWriteCallback) {
        mBleBluetooth.newBleConnector()
                .withUUIDString(mUuid_service, mUuid_write)
                .writeCharacteristic(data, bleWriteCallback,
                        mUuid_write);
    }

    private  Queue<byte[]> splitByte(byte[] data, int count) {
        List<byte[]> dataList = mDataEngine.spliteData(data);
        Queue<byte[]> byteQueue = new LinkedList<>();
        byteQueue.addAll(dataList);
        return byteQueue;
    }


}
