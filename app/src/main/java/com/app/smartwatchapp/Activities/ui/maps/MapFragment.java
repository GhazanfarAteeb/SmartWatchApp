package com.app.smartwatchapp.Activities.ui.maps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.app.smartwatchapp.AppConstants.AppConstants;
import com.app.smartwatchapp.R;
import com.app.smartwatchapp.databinding.FragmentMapsBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment {
    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 200;
    private static final long FASTEST_INTERVAL = 100;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    private FragmentMapsBinding binding;
    View root;
    private GoogleMap mMap;
    Context context;
    SupportMapFragment mapFragment;
    TextView textViewTimer;

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override

        public void onLocationChanged(@NonNull Location location) {
            mMap.clear();
            Log.d(TAG, "Firing onLocationChanged..............................................");
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(loc)
                    .icon(
                            BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(context, R.drawable.ic_maps_arrow
                            ))
                    );
            Log.d(null, "================ USER DETAILS ================");
            Log.d("CURRENT_LOCATION : ", location.getLatitude() + "," + location.getLongitude());
            Log.d("CURRENT_SPEED : ", String.valueOf(location.getSpeed()));
            Log.d("CURRENT_ALTITUDE : ", String.valueOf(location.getAltitude()));
            Log.d("CURRENT_ACCURACY : ", String.valueOf(location.getAccuracy()));
            Log.d(null, "==============================================");
//                        Date date = new Date(location.getTime());
            mMap.addMarker(markerOptions);

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 20.f));
        }
    };

    private final GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, locationListener, Looper.getMainLooper()
            );
            Log.d(TAG, "Location update started ..............: ");
        }

        @Override
        public void onConnectionSuspended(int i) {

        }
    };

    private final GoogleApiClient.OnConnectionFailedListener connectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.d(TAG, "Connection failed: " + connectionResult.toString());
        }
    };

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
            //TODO - UNCOMMENT LINES
//            if (AppConstants.mBleDevice == null) {
//                NavHostFragment.findNavController(MapFragment.this).navigate(R.id.action_navigation_dashboard_to_navigation_home);
//            }
            //TODO - UNCOMMENT ABOVE LINES
//            locationRequest = LocationRequest.create();
//            locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
//            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//            locationRequest.setNumUpdates(Integer.MAX_VALUE);
//            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
//            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                        ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    return;
//                }
//                LocationCallback locationCallback = new LocationCallback() {
//                    @Override
//                    public void onLocationResult(@NonNull LocationResult locationResult) {
//                        super.onLocationResult(locationResult);
//                        mMap.clear();
//                        Location currentLocation = locationResult.getLastLocation();
//                        LatLng location = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
//                        Log.d(null, "================ USER DETAILS ================");
//                        Log.d("CURRENT_LOCATION : ", currentLocation.getLatitude() + "," + currentLocation.getLongitude());
//                        Log.d("CURRENT_SPEED : ", String.valueOf(currentLocation.getSpeed()));
//                        Log.d("CURRENT_ALTITUDE : ", String.valueOf(currentLocation.getAltitude()));
//                        Log.d("CURRENT_ACCURACY : ", String.valueOf(currentLocation.getAccuracy()));
//                        Log.d(null, "==============================================");
//                        MarkerOptions markerOptions = new MarkerOptions()
//                                .position(location)
//                                .icon(
//                                        BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(context, R.drawable.ic_maps_arrow
//                                        ))
//                                );
//                        mMap.addMarker(markerOptions);
//                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 20.f));
//
//                    }
//                };
//                mFusedLocationClient.requestLocationUpdates(this.locationRequest,
//                        locationCallback, Looper.getMainLooper());
//            }
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentMapsBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        context = getActivity();
        ((TextView) root.findViewById(R.id.tv_welcome)).setText("Journey");
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(connectionFailedListener)
                .build();
        mGoogleApiClient.connect();
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
        if (ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(context)) {


        }
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }


        textViewTimer = view.findViewById(R.id.tv_timer);
        ImageView imageViewStart = view.findViewById(R.id.iv_start);
        ImageView imageViewStop = view.findViewById(R.id.iv_stop);

        handler = new Handler();

        imageViewStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppConstants.IS_JOURNEY_STARTED) {

                    imageViewStart.setVisibility(View.GONE);
                    textViewTimer.setVisibility(View.VISIBLE);
                    imageViewStop.setVisibility(View.VISIBLE);

//                    AppConstants.IS_JOURNEY_STARTED = true;
                    StartTime = SystemClock.uptimeMillis();
                    handler.postDelayed(runnable, 0);
//                    RelativeLayout.LayoutParams layoutParamsImageView = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                    layoutParamsImageView.setMargins(0, 0, 270, 0);
//                    imageViewStart.setLayoutParams(layoutParamsImageView);

                }
//                else {
//                    imageViewStart.setImageResource(R.drawable.ic_start);
//                    AppConstants.IS_JOURNEY_STARTED = false;
//                    handler.removeCallbacks(runnable);
//                }
            }
        });
        imageViewStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewStop.setVisibility(View.GONE);
                imageViewStart.setVisibility(View.VISIBLE);
                handler.removeCallbacks(runnable);
            }
        });
        Runnable runnable = new Runnable() {

            public void run() {

                MillisecondTime = SystemClock.uptimeMillis() - StartTime;

                UpdateTime = TimeBuff + MillisecondTime;

                Seconds = (int) (UpdateTime / 1000);

                Hours = Minutes / 60;

                Minutes = Seconds / 60;

                Seconds = Seconds % 60;

                Milliseconds = (int) (UpdateTime % 1000);

                textViewTimer.setText(Hours + ":" + Minutes + ":"
                        + String.format("%02d", Seconds) + ":"
                        + String.format("%03d", Milliseconds));

                handler.postDelayed(this, 0);
            }

        };


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

    public Runnable runnable = new Runnable() {

        public void run() {

            MillisecondTime = SystemClock.uptimeMillis() - StartTime;

            UpdateTime = TimeBuff + MillisecondTime;

            Seconds = (int) (UpdateTime / 1000);

            Minutes = Seconds / 60;

            Seconds = Seconds % 60;

            Milliseconds = (int) (UpdateTime % 1000);

            textViewTimer.setText(Hours + ":" + Minutes + ":"
                    + String.format("%02d", Seconds) + ":"
                    + String.format("%03d", Milliseconds));

            handler.postDelayed(this, 0);
        }

    };

}