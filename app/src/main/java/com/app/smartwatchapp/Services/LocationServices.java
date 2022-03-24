package com.app.smartwatchapp.Services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.app.smartwatchapp.Activities.MainActivity;
import com.app.smartwatchapp.Activities.ui.maps.MapFragment;
import com.app.smartwatchapp.AppConstants.AppConstants;
import com.app.smartwatchapp.R;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class LocationServices extends Service {
    private static final int ID = 1;                        // The id of the notification

    private NotificationCompat.Builder builder;
    private NotificationManager mNotificationManager;
    private PowerManager.WakeLock wakeLock;                 // PARTIAL_WAKELOCK

    public static void createLocationRequest() {
        AppConstants.mLocationRequest = new LocationRequest();
        AppConstants.mLocationRequest.setInterval(AppConstants.INTERVAL);
        AppConstants.mLocationRequest.setFastestInterval(AppConstants.FASTEST_INTERVAL);
        AppConstants.mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Returns the instance of the service
     */
    public class LocalBinder extends Binder {
        public LocationServices getServiceInstance() {
            return LocationServices.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();      // IBinder

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        AppConstants.client = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(getApplicationContext());
        createLocationRequest();
        startLocationUpdates();
        AppConstants.notification = getNotification();
        startForeground(ID, AppConstants.notification);
        Log.d("SERVICE", "ON START COMMAND");

        return START_NOT_STICKY;
    }

    @SuppressLint("BatteryLife")
    @Override
    public void onCreate() {
        Log.d("SERVICE", "Service");
        super.onCreate();
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getResources().getString(R.string.app_name) + ":wakelock");
        Intent intent = new Intent();
        String packageName = LocationServices.this.getPackageName();
        PowerManager pm = (PowerManager) LocationServices.this.getSystemService(Context.POWER_SERVICE);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (pm.isIgnoringBatteryOptimizations(packageName))
            intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
        else {
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
            getApplicationContext().startActivity(intent);
        }
    }


    //Location Callback


    @SuppressLint("WakelockTimeout")
    @Override
    public IBinder onBind(Intent intent) {
        if (wakeLock != null && !wakeLock.isHeld()) {
            wakeLock.acquire();
        }
        return mBinder;
    }

    private Notification getNotification() {
        final String CHANNEL_ID = "serviceChannel";

        builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        //builder.setSmallIcon(R.drawable.ic_notification_24dp)
        builder.setSmallIcon(R.mipmap.ic_launcher_round)
                .setColor(getResources().getColor(R.color.teal_200))
                .setContentTitle(getString(R.string.app_name))
                .setShowWhen(false)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentText("ABC");

        final Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
        startIntent.setAction(Intent.ACTION_MAIN);
        startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 1, startIntent, 0);
        builder.setContentIntent(contentIntent);
        return builder.build();
    }

    //Location Callback
    private final LocationCallback locationCallback = new LocationCallback() {
        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            MapFragment.mMap.clear();
            Location currentLocation = locationResult.getLastLocation();
            float speedInKMPH = (float) (currentLocation.getSpeed()*3.6);
            Log.d("CURRENT_SPEED", String.valueOf(speedInKMPH));

            List<LatLng> latLngArrayList = new ArrayList<>();
            for (Location loc : AppConstants.locationList) {
                latLngArrayList.add(new LatLng(loc.getLatitude(), loc.getLongitude()));
            }
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(getApplicationContext(), R.drawable.ic_maps_arrow)));

            if (Math.round(speedInKMPH) != 0) {
                AppConstants.locationList.add(currentLocation);
            }
            MapFragment.mMap.addPolyline(new PolylineOptions().addAll(latLngArrayList).width(5).color(Color.BLUE).geodesic(true));

            Log.d(null, "================ USER DETAILS ================");
            Log.d("CURRENT_LOCATION : ", currentLocation.getLatitude() + "," + currentLocation.getLongitude());
            Log.d("CURRENT_SPEED : ", String.valueOf(currentLocation.getSpeed()));
            Log.d("CURRENT_ALTITUDE : ", String.valueOf(currentLocation.getAltitude()));
            Log.d("CURRENT_ACCURACY : ", String.valueOf(currentLocation.getAccuracy()));
            Log.d(null, "==============================================");


            MapFragment.tvAltitude.setText(String.format("%.2f",currentLocation.getAltitude()));
            MapFragment.tvSpeed.setText(String.format("%.2f", (currentLocation.getSpeed()*3.6)) +" KM/H");
            MapFragment.tvAccuracy.setText(String.format("%.2f", (currentLocation.getAccuracy())));
            MapFragment.mMap.addMarker(markerOptions);
            MapFragment.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 17.f));
            builder.setContentText(currentLocation.getLatitude() + "," + currentLocation.getLongitude());

            AppConstants.mBleConnection.setHeartRateChangeListener(AppConstants.heartRateChangeListener);
            AppConstants.mBleConnection.setBloodOxygenChangeListener(AppConstants.bloodOxygenChangeListener);
            AppConstants.mBleConnection.setBloodPressureChangeListener(AppConstants.bloodPressureChangeListener);
            if (AppConstants.mBleDevice != null) {
                if (!AppConstants.HEART_RATE_MEASUREMENT_COMPLETED &&
                        !AppConstants.BLOOD_PRESSURE_MEASUREMENT_COMPLETED &&
                        !AppConstants.BLOOD_OXYGEN_MEASUREMENT_COMPLETED) {
                    AppConstants.mBleConnection.startMeasureOnceHeartRate();
                }
                else if (AppConstants.HEART_RATE_MEASUREMENT_COMPLETED &&
                        AppConstants.BLOOD_PRESSURE_MEASUREMENT_COMPLETED &&
                        AppConstants.BLOOD_OXYGEN_MEASUREMENT_COMPLETED) {

                    AppConstants.BLOOD_OXYGEN_MEASUREMENT_COMPLETED = false;
                    AppConstants.HEART_RATE_MEASUREMENT_COMPLETED = false;
                    AppConstants.BLOOD_PRESSURE_MEASUREMENT_COMPLETED = false;
                }
            }

            if (AppConstants.currentWatchReadings!=null) {
                AppConstants.watchReadingsList.add(AppConstants.currentWatchReadings);
                MapFragment.tvHeartRate.setText(AppConstants.currentWatchReadings.getHeartRate() + " BPM");
                MapFragment.tvBloodOxygenLevel.setText(AppConstants.currentWatchReadings.getBloodOxygenLevel() + " %");
                MapFragment.tvBloodPressure.setText(AppConstants.currentWatchReadings.getSystolicBloodPressure() + "/" + AppConstants.currentWatchReadings.getDiastolicBloodPressure());
            }

            AppConstants.notification = builder.build();
            startForeground(ID, AppConstants.notification);
        }
    };

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        AppConstants.client.requestLocationUpdates(AppConstants.mLocationRequest,
                this.locationCallback, Looper.getMainLooper());
    }

    @Override
    public void onDestroy() {
        AppConstants.client.removeLocationUpdates(locationCallback);
        Log.d("STOP_SERVICE", "TRYING TO STOP SERVICE FROM ON DESTROY");
        stopSelf();
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        AppConstants.client.removeLocationUpdates(locationCallback);
        stopSelf();
        Log.d("STOP_SERVICE", "TRYING TO STOP SERVICE");
        super.onTaskRemoved(rootIntent);
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        assert drawable != null;
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
