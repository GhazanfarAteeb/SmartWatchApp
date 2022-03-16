package com.app.smartwatchapp.AppConstants;

import com.app.smartwatchapp.Models.Watch;
import com.app.smartwatchapp.Models.WatchReadings;
import com.crrepa.ble.conn.CRPBleConnection;
import com.crrepa.ble.conn.CRPBleDevice;

public class AppConstants {
    public static CRPBleDevice mBleDevice;
    public static CRPBleConnection mBleConnection;
    public static final int SCAN_PERIOD = 10 * 1000;
    public static Watch connectedWatch;
    public static WatchReadings currentWatchReadings;
}
