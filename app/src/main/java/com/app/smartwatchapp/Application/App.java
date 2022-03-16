package com.app.smartwatchapp.Application;

import android.app.Application;
import android.content.Context;

import com.app.smartwatchapp.SharedPref.SharedPref;
import com.crrepa.ble.CRPBleClient;

public class App extends Application {
    private CRPBleClient mBleClient;

    public static CRPBleClient getBleClient(Context context) {
        App application = (App) context.getApplicationContext();
        return application.mBleClient;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBleClient = CRPBleClient.create(this);
        SharedPref.init(this);
    }
}