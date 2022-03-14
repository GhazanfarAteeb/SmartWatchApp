package com.app.smartwatchapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.smartwatchapp.Adapters.AdapterWatch;
import com.app.smartwatchapp.App;
import com.app.smartwatchapp.Models.Watch;
import com.app.smartwatchapp.PermissionUtils.Permissions;
import com.app.smartwatchapp.R;
import com.crrepa.ble.conn.listener.CRPBleConnectionStateListener;
import com.crrepa.ble.scan.bean.CRPScanDevice;
import com.crrepa.ble.scan.callback.CRPScanCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HomeScreen extends AppCompatActivity {
    Context context;
    CardView cvWatch;
    private final List<CRPScanDevice> scanDeviceList = new ArrayList<>();
    private final List<Watch> watchList = new ArrayList<>();
    RecyclerView rvWatchList;
    private static final int SCAN_PERIOD = 10 * 1000;
    AdapterWatch adapterWatch;
    public static final Comparator<CRPScanDevice> SORTING_COMPARATOR = Comparator.comparing(crpScanDevice -> crpScanDevice.getDevice().getAddress());

    private static final int REQUEST_UPDATE_BAND_CONFIG = 4;
    private static final String[] PERMISSION_UPDATE_BAND_CONFIG = new String[]{
            "android.permission.ACCESS_FINE_LOCATION"
    };
    TextView tvWatchName;
    TextView tvWatchMACAddress;
    ImageView ivUnlink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        context = HomeScreen.this;
        adapterWatch = new AdapterWatch(context);
        initView();
        AdapterWatch.SendState sendState = new AdapterWatch.SendState() {
            @Override
            public void changeState(final int state, Watch watch) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (state == CRPBleConnectionStateListener.STATE_CONNECTED) {
                            cvWatch.setVisibility(View.VISIBLE);
                            rvWatchList.setVisibility(View.GONE);
                            tvWatchName.setText(watch.getWatchName());
                            tvWatchMACAddress.setText(watch.getWatchMACAddress());
                            ivUnlink.setOnClickListener(view -> {
                                AdapterWatch.mBleDevice.disconnect();
                                AdapterWatch.mBleConnection.close();
                                cvWatch.setVisibility(View.GONE);
                                rvWatchList.setVisibility(View.VISIBLE);
                                startScan();
                            });
                        }
                    }
                });
            }
        };
        adapterWatch.setChangeStateCallback(sendState);
        requestPermissions();
        startScan();
        rvWatchList.setLayoutManager(new LinearLayoutManager(context));
    }

    private void initView() {
        tvWatchName = findViewById(R.id.tv_watch_name);
        tvWatchMACAddress = findViewById(R.id.tv_watch_mac_address);
        rvWatchList = findViewById(R.id.rv_watch_connection);
        cvWatch = findViewById(R.id.cv_watch);
        cvWatch.setVisibility(View.GONE);
        rvWatchList.setVisibility(View.VISIBLE);
        ivUnlink = findViewById(R.id.iv_unlink);
        if (!App.getBleClient(context).isBluetoothEnable()) {
            startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
        }
    }

    void addScanResults(CRPScanDevice crpScanDevice) {
        Watch watch = new Watch();
        watch.setWatchName(crpScanDevice.getDevice().getName());
        watch.setWatchMACAddress(crpScanDevice.getDevice().getAddress());
        for (int i = 0; i < scanDeviceList.size(); i++) {
            if (scanDeviceList.get(i).getDevice().equals(crpScanDevice.getDevice())) {
                scanDeviceList.set(i, crpScanDevice);
                watchList.set(i, watch);
            }
        }
        if (!watchList.contains(watch)) {
            watchList.add(watch);
        }
        System.out.println(watch.getWatchName());
        scanDeviceList.add(crpScanDevice);
        Collections.sort(scanDeviceList, SORTING_COMPARATOR);
    }

    private void startScan() {
        if (scanDeviceList.size()>0 && watchList.size() >0) {
            scanDeviceList.clear();
            watchList.clear();
        }
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Scanning...");
        progressDialog.show();
        try {
            boolean success = App.getBleClient(context).scanDevice(new CRPScanCallback() {
                @Override
                public void onScanning(CRPScanDevice crpScanDevice) {
                    runOnUiThread(() -> addScanResults(crpScanDevice));
                }

                @Override
                public void onScanComplete(List<CRPScanDevice> list) {
                    progressDialog.dismiss();
                    adapterWatch.setData(watchList);
                    rvWatchList.setAdapter(adapterWatch);
                }
            }, SCAN_PERIOD);

        } catch(Exception ex) {
            ex.getLocalizedMessage();
        }
    }

    void requestPermissions() {
        if (!Permissions.hasSelfPermissions(this, PERMISSION_UPDATE_BAND_CONFIG)) {
            ActivityCompat.requestPermissions(
                    this, PERMISSION_UPDATE_BAND_CONFIG, REQUEST_UPDATE_BAND_CONFIG);

        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        try {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        } catch (Exception ex) {
            ex.getLocalizedMessage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        recreate();
    }
}