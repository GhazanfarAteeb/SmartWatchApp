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

import com.app.smartwatchapp.Activities.MainActivity;
import com.app.smartwatchapp.AppConstants.AppConstants;
import com.app.smartwatchapp.R;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

public class LocationServices extends Service {
    private static final int ID = 1;                        // The id of the notification

    private NotificationCompat.Builder builder;
    private NotificationManager mNotificationManager;
    private PowerManager.WakeLock wakeLock;                 // PARTIAL_WAKELOCK
    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 0;
    private static final long FASTEST_INTERVAL = 0;
    LocationRequest mLocationRequest;
    Notification notification;

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
//            mMap.clear();
//            Log.d(TAG, "Firing onLocationChanged..............................................");
//            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
//            MarkerOptions markerOptions = new MarkerOptions()
//                    .position(loc)
//                    .icon(
//                            BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(context, R.drawable.ic_maps_arrow
//                            ))
//                    );

            Log.d(null, "================ USER DETAILS ================");
            Log.d("CURRENT_LOCATION : ", location.getLatitude() + "," + location.getLongitude());
            Log.d("CURRENT_SPEED : ", String.valueOf(location.getSpeed()));
            Log.d("CURRENT_ALTITUDE : ", String.valueOf(location.getAltitude()));
            Log.d("CURRENT_ACCURACY : ", String.valueOf(location.getAccuracy()));
            Log.d(null, "==============================================");
//                        Date date = new Date(location.getTime());
//            mMap.addMarker(markerOptions);
//
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 20.f));
        }
    };

    /**
     * Returns the instance of the service
     */
    public class LocalBinder extends Binder {
        public LocationServices getServiceInstance() {
            return LocationServices.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();      // IBinder


    public static void setContext(Context context) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        AppConstants.client = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(getApplicationContext());
        createLocationRequest();
        startLocationUpdates();
        notification = getNotification();
        startForeground(ID, notification);
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
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
        }
        getApplicationContext().startActivity(intent);
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
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location currentLocation = locationResult.getLastLocation();
            Log.d("Location", currentLocation.toString());
//            Log.d(null, "================ USER DETAILS ================");
//            Log.d("CURRENT_LOCATION : ", currentLocation.getLatitude() + "," + currentLocation.getLongitude());
//            Log.d("CURRENT_SPEED : ", String.valueOf(currentLocation.getSpeed()));
//            Log.d("CURRENT_ALTITUDE : ", String.valueOf(currentLocation.getAltitude()));
//            Log.d("CURRENT_ACCURACY : ", String.valueOf(currentLocation.getAccuracy()));
//            Log.d(null, "==============================================");
            //Share/Publish Location
            builder.setContentText(currentLocation.getLatitude() + "," + currentLocation.getLongitude());
            notification = builder.build();
            startForeground(ID, notification);
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
        AppConstants.client.requestLocationUpdates(this.mLocationRequest,
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


}
