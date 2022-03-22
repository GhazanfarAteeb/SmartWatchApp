package com.app.smartwatchapp.Application;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.app.smartwatchapp.AppConstants.AppConstants;
import com.app.smartwatchapp.SharedPref.SharedPref;
import com.crrepa.ble.CRPBleClient;

public class App extends Application {
    private CRPBleClient mBleClient;
    public static NotificationManager manager;
    public static CRPBleClient getBleClient(Context context) {
        App application = (App) context.getApplicationContext();
        return application.mBleClient;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBleClient = CRPBleClient.create(this);
        SharedPref.init(this);
        createNotificationChannel();
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    AppConstants.CHANNEL_ID,
                    "Notification service",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
            manager.cancelAll();
        }
    }
}
