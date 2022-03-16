package com.app.smartwatchapp.Activities.ui.maps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.app.smartwatchapp.R;
import com.app.smartwatchapp.databinding.FragmentMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment {

    private FragmentMapsBinding binding;
    View root;
    private GoogleMap mMap;
    Context context;
    SupportMapFragment mapFragment;
    LocationManager locationManager;
    FusedLocationProviderClient mFusedLocationClient;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        private LocationRequest locationRequest;
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
            locationRequest = LocationRequest.create();
            locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setNumUpdates(Integer.MAX_VALUE);
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                LocationCallback locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(@NonNull LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        mMap.clear();
                        Location currentLocation = locationResult.getLastLocation();
                        LatLng location = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        Log.d(null, "================ USER DETAILS ================");
                        Log.d("CURRENT_LOCATION : ", currentLocation.getLatitude() + "," + currentLocation.getLongitude());
                        Log.d("CURRENT_SPEED : ", String.valueOf(currentLocation.getSpeed()));
                        Log.d("CURRENT_ALTITUDE : ", String.valueOf(currentLocation.getAltitude()));
                        Log.d("CURRENT_ACCURACY : ", String.valueOf(currentLocation.getAccuracy()));
                        Log.d(null, "==============================================");
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(location)
                                .icon(
                                        BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(context, R.drawable.ic_maps_arrow
                                        ))
                                );
                        mMap.addMarker(markerOptions);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 20.f));

                    }
                };
                mFusedLocationClient.requestLocationUpdates(this.locationRequest,
                        locationCallback, Looper.getMainLooper());
            }
        }
    };
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentMapsBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        context = getActivity();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
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