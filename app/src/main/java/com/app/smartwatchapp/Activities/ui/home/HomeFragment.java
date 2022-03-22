package com.app.smartwatchapp.Activities.ui.home;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.smartwatchapp.Adapters.AdapterWatch;
import com.app.smartwatchapp.AppConstants.AppConstants;
import com.app.smartwatchapp.Application.App;
import com.app.smartwatchapp.Models.Watch;
import com.app.smartwatchapp.Models.WatchReadings;
import com.app.smartwatchapp.R;
import com.app.smartwatchapp.databinding.FragmentHomeBinding;
import com.crrepa.ble.conn.bean.CRPBloodOxygenInfo;
import com.crrepa.ble.conn.bean.CRPHeartRateInfo;
import com.crrepa.ble.conn.bean.CRPMovementHeartRateInfo;
import com.crrepa.ble.conn.listener.CRPBloodOxygenChangeListener;
import com.crrepa.ble.conn.listener.CRPBloodPressureChangeListener;
import com.crrepa.ble.conn.listener.CRPHeartRateChangeListener;
import com.crrepa.ble.conn.type.CRPHistoryDynamicRateType;
import com.crrepa.ble.scan.bean.CRPScanDevice;
import com.crrepa.ble.scan.callback.CRPScanCallback;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HomeFragment extends Fragment {
    View root;
    Context context;
    CardView cvWatch;
    private final List<CRPScanDevice> scanDeviceList = new ArrayList<>();
    private final List<Watch> watchList = new ArrayList<>();
    RecyclerView rvWatchList;

    AdapterWatch adapterWatch;
    public static final Comparator<CRPScanDevice> SORTING_COMPARATOR = Comparator.comparing(crpScanDevice -> crpScanDevice.getDevice().getAddress());


    TextView tvWatchName, tvWatchMACAddress;
    public TextView tvHeartRate, tvBloodOxygen, tvBloodPressure;
    ImageView ivUnlink;
    ProgressDialog progressDialog;


    public CRPHeartRateChangeListener heartRateChangeListener = new CRPHeartRateChangeListener() {
        @Override
        public void onMeasuring(int i) {

        }

        @Override
        public void onOnceMeasureComplete(int i) {
            tvHeartRate.post(() -> {
                if (i != 0) {
                    AppConstants.currentWatchReadings.setHeartRate(i);
                    tvHeartRate.setText(AppConstants.currentWatchReadings.getHeartRate() + " BPM");
                    AppConstants.mBleConnection.startMeasureBloodOxygen();
                    progressDialog.setMessage("Getting Blood Oxygen Reading...");
                }
            });
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

    public CRPBloodOxygenChangeListener bloodOxygenChangeListener = new CRPBloodOxygenChangeListener() {
        @Override
        public void onTimingMeasure(int i) {

        }

        @Override
        public void onBloodOxygenChange(int i) {
            Log.d("BLOOD_OXYGEN : ", String.valueOf(i));
            tvBloodOxygen.post(() -> {
                if (i != 0) {
                    AppConstants.currentWatchReadings.setBloodOxygenLevel(i);
                    tvBloodOxygen.setText(AppConstants.currentWatchReadings.getBloodOxygenLevel() + " %");
                    AppConstants.mBleConnection.startMeasureBloodPressure();
                    progressDialog.setMessage("Getting Blood Pressure Reading...");
                }
            });
        }

        @Override
        public void onTimingMeasureResult(CRPBloodOxygenInfo crpBloodOxygenInfo) {
        }
    };

    public CRPBloodPressureChangeListener bloodPressureChangeListener = new CRPBloodPressureChangeListener() {
        @Override
        public void onBloodPressureChange(int i, int i1) {
            if (i != 255 && i1 != 255) {
                AppConstants.currentWatchReadings.setSystolicBloodPressure(i);
                AppConstants.currentWatchReadings.setDiastolicBloodPressure(i1);
                tvBloodPressure.post(() -> tvBloodPressure.setText(AppConstants.currentWatchReadings.getSystolicBloodPressure() + "/" + AppConstants.currentWatchReadings.getDiastolicBloodPressure()));
            }
            progressDialog.dismiss();
        }
    };
    private FragmentHomeBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        context = getActivity();
        adapterWatch = new AdapterWatch(context);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, AppConstants.PERMISSION_REQUEST_FINE_LOCATION);
        }
        initView();

        AdapterWatch.SendState sendState = () -> {
            AppConstants.mBleConnection.setHeartRateChangeListener(heartRateChangeListener);
            AppConstants.mBleConnection.setBloodOxygenChangeListener(bloodOxygenChangeListener);
            AppConstants.mBleConnection.setBloodPressureChangeListener(bloodPressureChangeListener);
            requireActivity().runOnUiThread(() -> {
                if (AppConstants.mBleDevice.isConnected()) {
                    setWatchData();
                    AppConstants.currentWatchReadings = new WatchReadings();
                    AppConstants.mBleConnection.startMeasureOnceHeartRate();
                    progressDialog.setMessage("Getting Heart Rate Reading...");
                    progressDialog.show();
                }
            });
        };
        adapterWatch.setChangeStateCallback(sendState);
        if (AppConstants.connectedWatch != null) {
            setWatchData();
            tvHeartRate.setText(AppConstants.currentWatchReadings.getHeartRate() + " BPM");
            tvBloodOxygen.setText(AppConstants.currentWatchReadings.getBloodOxygenLevel() + " %");
            tvBloodPressure.setText(AppConstants.currentWatchReadings.getSystolicBloodPressure() + "/" + AppConstants.currentWatchReadings.getDiastolicBloodPressure());
        }
        if (!App.getBleClient(context).isBluetoothEnable()) {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 1);
        } else {
            if (AppConstants.connectedWatch == null) {
                startScan();
            }
        }
        rvWatchList.setLayoutManager(new LinearLayoutManager(context));
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void initView() {
        ((TextView) root.findViewById(R.id.tv_welcome)).setText("Home");
        tvWatchName = root.findViewById(R.id.tv_watch_name);
        tvWatchMACAddress = root.findViewById(R.id.tv_watch_mac_address);
        rvWatchList = root.findViewById(R.id.rv_watch_connection);
        cvWatch = root.findViewById(R.id.cv_watch);
        cvWatch.setVisibility(View.GONE);
        rvWatchList.setVisibility(View.VISIBLE);
        ivUnlink = root.findViewById(R.id.iv_unlink);
        tvHeartRate = root.findViewById(R.id.tv_heart_rate);
        tvBloodOxygen = root.findViewById(R.id.tv_blood_oxygen);
        tvBloodPressure = root.findViewById(R.id.tv_blood_pressure);

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
        scanDeviceList.sort(SORTING_COMPARATOR);
    }

    private void startScan() {
        if (scanDeviceList.size() > 0 && watchList.size() > 0) {
            scanDeviceList.clear();
            watchList.clear();
        }
        tvHeartRate.setText(getResources().getText(R.string.heart_rate));
        tvBloodOxygen.setText(getResources().getText(R.string.blood_oxygen));
        tvBloodPressure.setText(getResources().getText(R.string.blood_pressure));
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Scanning...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        try {
            boolean success = App.getBleClient(context).scanDevice(new CRPScanCallback() {
                @Override
                public void onScanning(CRPScanDevice crpScanDevice) {
                    getActivity().runOnUiThread(() -> addScanResults(crpScanDevice));
                }

                @Override
                public void onScanComplete(List<CRPScanDevice> list) {
                    progressDialog.dismiss();
                    adapterWatch.setData(watchList);
                    rvWatchList.setAdapter(adapterWatch);
                }
            }, AppConstants.SCAN_PERIOD);

        } catch (Exception ex) {
            ex.getLocalizedMessage();
        }
    }

//    void requestPermissions() {
//        Log.d("HOME_FRAGMENT", "IN Request permissions");
//        if (!Permissions.hasSelfPermissions(context, AppConstants.PERMISSION_UPDATE_BAND_CONFIG)) {
//            ActivityCompat.requestPermissions(requireActivity(), AppConstants.PERMISSION_UPDATE_BAND_CONFIG, AppConstants.PERMISSION_REQUEST_FINE_LOCATION);
//        }
//        if (this.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
//                                Manifest.permission.ACCESS_BACKGROUND_LOCATION},
//                        AppConstants.PERMISSION_REQUEST_FINE_LOCATION);
//
//            } else {
//                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                builder.setTitle("Functionality limited");
//                builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons.  Please go to Settings -> Applications -> Permissions and grant location access to this app.");
//                builder.setPositiveButton(android.R.string.ok, null);
//                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
//
//                    @Override
//                    public void onDismiss(DialogInterface dialog) {
//                    }
//
//                });
//                builder.show();
//            }
//        }
//        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//
//        try {
//            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//            }
//        } catch (Exception ex) {
//            ex.getLocalizedMessage();
//        }
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        Log.d("HOME_FRAGMEnt", "In permission fine location");
//        switch (requestCode) {
//            case AppConstants.PERMISSION_REQUEST_FINE_LOCATION:
//                if (grantResults.length > 0 && PackageManager.PERMISSION_GRANTED == grantResults[0]) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && Permissions.hasSelfPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                            builder.setTitle("Functionality limited");
//                            builder.setMessage("Please go to Settings -> Applications -> Permissions and set to Allow all time");
//                            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    getActivity().requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, AppConstants.PERMISSION_REQUEST_BACKGROUND_LOCATION);
//
//                                }
//                            });
//                            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
//
//                                @Override
//                                public void onDismiss(DialogInterface dialog) {
//                                }
//
//                            });
//                            builder.show();
//                        }
//                    }
//                }
//                break;
//            case AppConstants.PERMISSION_REQUEST_BACKGROUND_LOCATION:
//                if (grantResults.length > 0 && PackageManager.PERMISSION_GRANTED == grantResults[0]) {
//                    Toast.makeText(getActivity(), "Background Permission Granted", Toast.LENGTH_SHORT).show();
//                }
//                break;
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        getActivity().recreate();
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getActivity();
        if (resultCode == Activity.RESULT_OK) {
            startScan();
        }

    }

    public void setWatchData() {
        cvWatch.setVisibility(View.VISIBLE);
        rvWatchList.setVisibility(View.GONE);
        tvWatchName.setText(AppConstants.connectedWatch.getWatchName());
        tvWatchMACAddress.setText(AppConstants.connectedWatch.getWatchMACAddress());
        ivUnlink.setOnClickListener(view -> {
            AppConstants.mBleDevice.disconnect();
            AppConstants.mBleConnection.close();
            cvWatch.setVisibility(View.GONE);
            rvWatchList.setVisibility(View.VISIBLE);
            AppConstants.connectedWatch = null;
            startScan();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case AppConstants.PERMISSION_REQUEST_FINE_LOCATION:
                if (grantResults.length > 0 && PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Functionality limited");
                        builder.setMessage("Since background location access has not been granted, this app will not be able to discover beacons in the background.  Please go to Settings -> Applications -> Permissions and grant background location access to this app.");
                        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, AppConstants.PERMISSION_REQUEST_BACKGROUND_LOCATION);

                            }
                        });
                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                            @Override
                            public void onDismiss(DialogInterface dialog) {
                            }

                        });
                        builder.show();
                    }
                }
                break;
            case AppConstants.PERMISSION_REQUEST_BACKGROUND_LOCATION:
                if (grantResults.length > 0 && PackageManager.PERMISSION_GRANTED == grantResults[0]) {
//                    Toast.makeText(getActivity(), "Background Permission Granted", Toast.LENGTH_SHORT).show();
                    getActivity().recreate();
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}