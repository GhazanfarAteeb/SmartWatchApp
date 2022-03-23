package com.app.smartwatchapp.Activities.ui.maps;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.smartwatchapp.AppConstants.AppConstants;
import com.app.smartwatchapp.R;
import com.app.smartwatchapp.Services.LocationServices.LocalBinder;
import com.app.smartwatchapp.databinding.FragmentMapsBinding;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;

public class MapFragment extends Fragment {
    private FragmentMapsBinding binding;
    View root;
    public static GoogleMap mMap;
    Context context;
    SupportMapFragment mapFragment;
    TextView tvTimer;
    Intent serviceIntent;


    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
//        private LocationRequest locationRequest;

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            mMap.getUiSettings().setAllGesturesEnabled(false);

        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentMapsBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        context = getActivity();
        ((TextView) root.findViewById(R.id.tv_welcome)).setText("Journey");

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L;
    Handler handler;
    int Hours, Seconds, Minutes, Milliseconds;

    @Override

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }


        tvTimer = view.findViewById(R.id.tv_timer);
        ImageView ivJourneyStart = view.findViewById(R.id.iv_start);
        ImageView ivJourneyStop = view.findViewById(R.id.iv_stop);

        handler = new Handler();

        ivJourneyStart.setOnClickListener(v -> {
            AppConstants.locationList = new ArrayList<>();
            serviceIntent = new Intent(getActivity(), com.app.smartwatchapp.Services.LocationServices.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d("SERVICE", "STARTING SERVICE");
                getActivity().startForegroundService(new Intent(getActivity(), com.app.smartwatchapp.Services.LocationServices.class));
            } else {
                Log.d("SERVICE", "STARTING SERVICE");
                getActivity().startService(new Intent(getActivity(), com.app.smartwatchapp.Services.LocationServices.class));
            }
            getActivity().bindService(serviceIntent, GPSServiceConnection, Context.BIND_AUTO_CREATE | Context.BIND_IMPORTANT);
            AppConstants.IS_JOURNEY_STARTED = true;
            tvTimer.setVisibility(View.VISIBLE);
            ivJourneyStop.setVisibility(View.VISIBLE);
            ivJourneyStart.setVisibility(View.GONE);
            StartTime = SystemClock.uptimeMillis();
            handler.postDelayed(runnable, 0);
        });
        ivJourneyStop.setOnClickListener(v -> {
            AppConstants.IS_JOURNEY_STARTED = false;
            getActivity().unbindService(GPSServiceConnection);
            getActivity().stopService(serviceIntent);
            ivJourneyStop.setVisibility(View.GONE);
            tvTimer.setVisibility(View.GONE);
            ivJourneyStart.setVisibility(View.VISIBLE);
            handler.removeCallbacks(runnable);
        });
    }




    public Runnable runnable = new Runnable() {
        public void run() {
            MillisecondTime = SystemClock.uptimeMillis() - StartTime;
            UpdateTime = TimeBuff + MillisecondTime;
            Seconds = (int) (UpdateTime / 1000);
            Minutes = Seconds / 60;
            Seconds = Seconds % 60;
            Milliseconds = (int) (UpdateTime % 1000);
            tvTimer.setText(
                    convertDate(Hours) + ":"
                            + convertDate(Minutes) + ":"
                            + String.format("%02d", Seconds) + ":"
                            + String.format("%03d", Milliseconds)
            );
            handler.postDelayed(this, 0);
        }

    };
    private String convertDate(int input) {
        if (input >= 10) {
            return String.valueOf(input);
        } else {
            return "0" + input;
        }
    }

    com.app.smartwatchapp.Services.LocationServices locationServices;
    boolean isGPSServiceBound = false;
    private final ServiceConnection GPSServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LocalBinder binder = (LocalBinder) iBinder;
            locationServices = binder.getServiceInstance();
            isGPSServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
}