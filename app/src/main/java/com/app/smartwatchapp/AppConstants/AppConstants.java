package com.app.smartwatchapp.AppConstants;

import android.app.Notification;
import android.location.Location;

import com.app.smartwatchapp.Models.Watch;
import com.app.smartwatchapp.Models.WatchReadings;
import com.crrepa.ble.conn.CRPBleConnection;
import com.crrepa.ble.conn.CRPBleDevice;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;

import java.util.List;

public class AppConstants {
    public static CRPBleDevice mBleDevice;
    public static CRPBleConnection mBleConnection;
    public static final int SCAN_PERIOD = 10 * 1000;
    public static Watch connectedWatch;
    public static WatchReadings currentWatchReadings;
    public static List<Location> locationList;
    public static FusedLocationProviderClient client;
    public static boolean IS_JOURNEY_STARTED = false;
    public static final int PERMISSION_REQUEST_BACKGROUND_LOCATION = 2;
    public static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    public static final int BLUETOOTH_REQUEST_CODE = 1;
    public static final int LOCATION_REQUEST_CODE = 2;
    public static final String CHANNEL_ID = "serviceChannel";


    public static final long INTERVAL = 0;
    public static final long FASTEST_INTERVAL = 0;
    public static LocationRequest mLocationRequest;
    public static Notification notification;
}
