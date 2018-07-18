package com.ubtechinc.codemaosdk.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.clj.fastble.data.BleDevice;

/**
 * @author：wululin
 * @date：2017/10/18 17:53
 * @modifier：ubt
 * @modify_date：2017/10/18 17:53
 * [A brief description]
 * version
 *
 */

public class UbtBleDevice implements Parcelable {
    BleDevice device;
    String sn;
    public UbtBleDevice() {

    }

    protected UbtBleDevice(Parcel in) {
        device = in.readParcelable(BleDevice.class.getClassLoader());
        sn = in.readString();
    }

    public static final Creator<UbtBleDevice> CREATOR = new Creator<UbtBleDevice>() {
        @Override
        public UbtBleDevice createFromParcel(Parcel in) {
            return new UbtBleDevice(in);
        }

        @Override
        public UbtBleDevice[] newArray(int size) {
            return new UbtBleDevice[size];
        }
    };

    public BleDevice getDevice() {
        return device;
    }

    public void setDevice(BleDevice device) {
        this.device = device;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    @Override
    public String toString() {
        return "UbtBluetoothDevice{" +
                "device=" + device +
                ", sn='" + sn + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(device, flags);
        dest.writeString(sn);
    }
}
