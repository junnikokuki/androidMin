package com.clj.fastble.server;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import java.util.UUID;

/**
 * Created by ubt on 2017/2/10.
 */

public class UBTGattService {

    private BluetoothGattService mBluetoothGattService;

    //三个特征
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private BluetoothGattCharacteristic mReadCharacteristic;
    private static final UUID HEART_RATE_SERVICE_UUID = UUID
            .fromString("10057e66-28bd-4c3e-a030-08b4982739bc");


    private static final UUID READ_UUID = UUID
            .fromString("d2f9ff26-ad08-4bd3-8436-9b8f33bdea76");

    private static final UUID NOTIFY_UUID = UUID
            .fromString("7e0dffa2-c44f-48ac-a39e-3fce46d9aabb");

    private static final UUID DESCRIPTION_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");


    public UBTGattService(){


        mNotifyCharacteristic =
                new BluetoothGattCharacteristic(NOTIFY_UUID,
                        BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                         BluetoothGattCharacteristic.PROPERTY_NOTIFY);
        mReadCharacteristic=
                new BluetoothGattCharacteristic(READ_UUID,
                        BluetoothGattCharacteristic.PROPERTY_READ| BluetoothGattCharacteristic.PROPERTY_WRITE,
                        BluetoothGattCharacteristic.PERMISSION_READ| BluetoothGattCharacteristic.PERMISSION_WRITE);
        mBluetoothGattService = new BluetoothGattService(HEART_RATE_SERVICE_UUID,
                BluetoothGattService.SERVICE_TYPE_PRIMARY);
        mNotifyCharacteristic.addDescriptor(new BluetoothGattDescriptor(DESCRIPTION_UUID, BluetoothGattCharacteristic.PERMISSION_WRITE));
        mBluetoothGattService.addCharacteristic(mReadCharacteristic);
        mBluetoothGattService.addCharacteristic(mNotifyCharacteristic);

    }

    public BluetoothGattService getGattService(){
        return mBluetoothGattService;
    }


    public BluetoothGattCharacteristic getHeartWriteCharacteristic(){
        return mNotifyCharacteristic;
    }

    /**
     * Function to communicate to the ServiceFragment that a device wants to write to a
     * characteristic.
     *
     * The ServiceFragment should check that the value being written is valid and
     * return a code appropriately. The ServiceFragment should update the UI to reflect the change.
     * @param characteristic Characteristic to write to
     * @param value Value to write to the characteristic
     * @return {@link android.bluetooth.BluetoothGatt#GATT_SUCCESS} if the read operation
     * was completed successfully. See {@link android.bluetooth.BluetoothGatt} for GATT return codes.
     */
    public int writeCharacteristic(BluetoothGattCharacteristic characteristic, int offset, byte[] value) {
        throw new UnsupportedOperationException("Method writeCharacteristic not overriden");
    };

}
