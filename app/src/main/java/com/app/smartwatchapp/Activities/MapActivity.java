package com.app.smartwatchapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.renderscript.ScriptGroup;

import com.app.smartwatchapp.App;
import com.app.smartwatchapp.R;
import com.google.android.gms.maps.SupportMapFragment;


public class MapActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

    }
}