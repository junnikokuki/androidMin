package com.ubtechinc.alpha.mini.event;

/**
 * Created by riley.zhang on 2018/7/4.
 */

public class JimuCarDriverModeEvent {

    public final static int ENTER_DRIVE_MODE = 0;
    public final static int EXIT_DRIVE_MODE = 1;
    int mDriveMode;

    public JimuCarDriverModeEvent(int driveMode) {
        mDriveMode = driveMode;
    }

    public int getDriveMode() {
        return mDriveMode;
    }
}
