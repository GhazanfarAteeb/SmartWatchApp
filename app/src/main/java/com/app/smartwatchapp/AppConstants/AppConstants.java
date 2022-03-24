package com.app.smartwatchapp.AppConstants;

import android.app.Notification;
import android.location.Location;
import android.util.Log;

import com.app.smartwatchapp.Models.Watch;
import com.app.smartwatchapp.Models.WatchReadings;
import com.crrepa.ble.conn.CRPBleConnection;
import com.crrepa.ble.conn.CRPBleDevice;
import com.crrepa.ble.conn.bean.CRPBloodOxygenInfo;
import com.crrepa.ble.conn.bean.CRPHeartRateInfo;
import com.crrepa.ble.conn.bean.CRPMovementHeartRateInfo;
import com.crrepa.ble.conn.listener.CRPBloodOxygenChangeListener;
import com.crrepa.ble.conn.listener.CRPBloodPressureChangeListener;
import com.crrepa.ble.conn.listener.CRPHeartRateChangeListener;
import com.crrepa.ble.conn.type.CRPHistoryDynamicRateType;
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
    public static List<WatchReadings> watchReadingsList;

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
    public static boolean HEART_RATE_MEASUREMENT_COMPLETED = false;
    public static boolean BLOOD_OXYGEN_MEASUREMENT_COMPLETED = false;
    public static boolean BLOOD_PRESSURE_MEASUREMENT_COMPLETED = false;

    //WATCH LISTENERS FOR CONTINUOUS READINGS
    public static final CRPHeartRateChangeListener heartRateChangeListener = new CRPHeartRateChangeListener() {
        @Override
        public void onMeasuring(int i) {

        }

        @Override
        public void onOnceMeasureComplete(int i) {
            if (i!=0) {
                currentWatchReadings.setHeartRate(i);
                Log.d("CONTINUOUS_HEART_RATE_READING", i + " BPM");
            }
            HEART_RATE_MEASUREMENT_COMPLETED = true;
            mBleConnection.startMeasureBloodOxygen();
        }

        @Override
        public void onMeasureComplete(CRPHistoryDynamicRateType crpHistoryDynamicRateType, CRPHeartRateInfo crpHeartRateInfo) {

        }

        @Override
        public void on24HourMeasureResult(CRPHeartRateInfo crpHeartRateInfo) {

        }

        @Override
        public void onMovementMeasureResult(List<CRPMovementHeartRateInfo> list) {

        }
    };

    public static final CRPBloodOxygenChangeListener bloodOxygenChangeListener = new CRPBloodOxygenChangeListener() {
        @Override
        public void onTimingMeasure(int i) {

        }

        @Override
        public void onBloodOxygenChange(int i) {
            if (i!=0) {
                currentWatchReadings.setBloodOxygenLevel(i);
                Log.d("CONTINUOUS_BLOOD_OXYGEN", i+"%");
            }
            BLOOD_OXYGEN_MEASUREMENT_COMPLETED = true;
            mBleConnection.startMeasureBloodPressure();
        }

        @Override
        public void onTimingMeasureResult(CRPBloodOxygenInfo crpBloodOxygenInfo) {

        }
    };

    public static final CRPBloodPressureChangeListener bloodPressureChangeListener = new CRPBloodPressureChangeListener() {
        @Override
        public void onBloodPressureChange(int i, int i1) {
            if (i!=255 && i1!=255) {
                currentWatchReadings.setSystolicBloodPressure(i);
                currentWatchReadings.setDiastolicBloodPressure(i1);
                Log.d("CONTINUOUS_BLOOD_PRESSURE", i+"/"+i1);

            }
            BLOOD_PRESSURE_MEASUREMENT_COMPLETED = true;
        }
    };
}
