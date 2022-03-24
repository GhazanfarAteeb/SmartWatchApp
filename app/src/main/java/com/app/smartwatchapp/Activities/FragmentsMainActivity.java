package com.app.smartwatchapp.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.app.smartwatchapp.R;
import com.app.smartwatchapp.databinding.ActivityFragmentsMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FragmentsMainActivity extends AppCompatActivity {
    private ActivityFragmentsMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFragmentsMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
//                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_fragments_main);
//        binding.container.setAnimation();
        NavigationUI.setupWithNavController(binding.navView, navController);

    }

}