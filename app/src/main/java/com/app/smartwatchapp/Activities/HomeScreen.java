package com.app.smartwatchapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.smartwatchapp.Adapters.AdapterWatch;
import com.app.smartwatchapp.App;
import com.app.smartwatchapp.Models.Watch;
import com.app.smartwatchapp.PermissionUtils.Permissions;
import com.app.smartwatchapp.R;
import com.crrepa.ble.conn.bean.CRPBloodOxygenInfo;
import com.crrepa.ble.conn.bean.CRPHeartRateInfo;
import com.crrepa.ble.conn.bean.CRPMovementHeartRateInfo;
import com.crrepa.ble.conn.listener.CRPBleConnectionStateListener;
import com.crrepa.ble.conn.listener.CRPBloodOxygenChangeListener;
import com.crrepa.ble.conn.listener.CRPBloodPressureChangeListener;
import com.crrepa.ble.conn.listener.CRPHeartRateChangeListener;
import com.crrepa.ble.conn.type.CRPHistoryDynamicRateType;
import com.crrepa.ble.scan.bean.CRPScanDevice;
import com.crrepa.ble.scan.callback.CRPScanCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    TextView tvWatchName, tvWatchMACAddress;
    public TextView tvHeartRate, tvBloodOxygen, tvBloodPressure;
    ImageView ivUnlink;
    ProgressDialog progressDialog;
    BottomNavigationView bottomNavigationView;
    public CRPHeartRateChangeListener heartRateChangeListener = new CRPHeartRateChangeListener() {
        @Override
        public void onMeasuring(int i) {

        }

        @Override
        public void onOnceMeasureComplete(int i) {
            tvHeartRate.post(new Runnable() {
                @Override
                public void run() {
                    if (i != 0) {
                        tvHeartRate.setText(i + " BPM");
                        AdapterWatch.mBleConnection.startMeasureBloodOxygen();
                        progressDialog.setMessage("Getting Blood Oxygen Reading...");
                    }
                }
            });
        }

        @Override
        public void onMeasureComplete(CRPHistoryDynamicRateType crpHistoryDynamicRateType, CRPHeartRateInfo crpHeartRateInfo) {

        }

        @Override
        public void on24HourMeasureResult(CRPHeartRateInfo crpHeartRateInfo) {
//            List<Integer> last24HoursHRList = crpHeartRateInfo.getHeartRateList();
//            List<Integer> removalList = new ArrayList<>();
//            removalList.add(0);
//            last24HoursHRList.removeAll(removalList);
//            averageHeartRate = 0;
//            for (Integer heartRate : last24HoursHRList) {
//                if (heartRate!=0) {
//                    averageHeartRate += heartRate;
//                }
//            }
//            Log.d("crp_size", String.valueOf(last24HoursHRList.size()));
//            Log.d("LAST_24_HOURS_HR_LIST", last24HoursHRList.toString());
//            averageHeartRate /= last24HoursHRList.size();
//            tvHeartRate.post(new Runnable() {
//                @Override
//                public void run() {
//                    tvHeartRate.setText(averageHeartRate +" BPM");
//                }
//            });
        }

        @Override
        public void onMovementMeasureResult(List<CRPMovementHeartRateInfo> list) {

        }
    };

    public CRPBloodOxygenChangeListener bloodOxygenChangeListener = new CRPBloodOxygenChangeListener() {
        @Override
        public void onTimingMeasure(int i) {

        }

        @Override
        public void onBloodOxygenChange(int i) {
            Log.d("BLOOD_OXYGEN : ", String.valueOf(i));
            tvBloodOxygen.post(new Runnable() {
                @Override
                public void run() {
                    if (i != 0) {
                        tvBloodOxygen.setText(i + " %");
                        AdapterWatch.mBleConnection.startMeasureBloodPressure();
                        progressDialog.setMessage("Getting Blood Pressure Reading...");
                    }
                }
            });
        }

        @Override
        public void onTimingMeasureResult(CRPBloodOxygenInfo crpBloodOxygenInfo) {
//            Log.d("Blood_OXYGEN_INFO_SIZE : ", crpBloodOxygenInfo.getList().toString());
//            Log.d("Blood_OXYGEN_INFO_LIST : ", crpBloodOxygenInfo.getList().toString());
        }
    };

    public CRPBloodPressureChangeListener bloodPressureChangeListener = new CRPBloodPressureChangeListener() {
        @Override
        public void onBloodPressureChange(int i, int i1) {
            tvBloodPressure.post(new Runnable() {
                @Override
                public void run() {
                    tvBloodPressure.setText(i + "/" + i1);
                }
            });
            progressDialog.dismiss();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        context = HomeScreen.this;
        adapterWatch = new AdapterWatch(context);
        initView();

        bottomNavigationView = findViewById(R.id.navigation_bar);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {


                    case R.id.home:
                        break;

                    case R.id.profile:
                        Intent intent = new Intent(HomeScreen.this, profileActivity.class);
                        startActivity(intent);
                    case R.id.journey:
                        Intent i = new Intent(HomeScreen.this, MapActivity.class);
                        startActivity(i);
                        return true;
                }
                return false;
            }
        });
        AdapterWatch.SendState sendState = new AdapterWatch.SendState() {
            @Override
            public void changeState(final int state, Watch watch) {
                AdapterWatch.mBleConnection.setHeartRateChangeListener(heartRateChangeListener);
                AdapterWatch.mBleConnection.setBloodOxygenChangeListener(bloodOxygenChangeListener);
                AdapterWatch.mBleConnection.setBloodPressureChangeListener(bloodPressureChangeListener);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (state == CRPBleConnectionStateListener.STATE_CONNECTED) {
                            progressDialog.setMessage("Getting Heart Rate Reading...");
                            progressDialog.show();
                            cvWatch.setVisibility(View.VISIBLE);
                            rvWatchList.setVisibility(View.GONE);
                            tvWatchName.setText(watch.getWatchName());
                            tvWatchMACAddress.setText(watch.getWatchMACAddress());
                            AdapterWatch.mBleConnection.startMeasureOnceHeartRate();

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
        tvHeartRate = findViewById(R.id.tv_heart_rate);
        tvBloodOxygen = findViewById(R.id.tv_blood_oxygen);
        tvBloodPressure = findViewById(R.id.tv_blood_pressure);

        if (!App.getBleClient(context).isBluetoothEnable()) {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 1);
        } else {
            startScan();
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
        if (scanDeviceList.size() > 0 && watchList.size() > 0) {
            scanDeviceList.clear();
            watchList.clear();
        }
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Scanning...");
        progressDialog.setCancelable(false);
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

        } catch (Exception ex) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            startScan();
        }
    }
}