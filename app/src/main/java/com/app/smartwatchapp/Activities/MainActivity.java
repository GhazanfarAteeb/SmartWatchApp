package com.app.smartwatchapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.smartwatchapp.R;
import com.hbb20.CountryCodePicker;


public class MainActivity extends AppCompatActivity {
    CountryCodePicker countryCodePicker;
    EditText etPhoneNumber, etPassword;
    String phoneNumber;
    Button btnLogin;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);
        initView();
        context = MainActivity.this;

        countryCodePicker.registerCarrierNumberEditText(etPhoneNumber);
        btnLogin.setOnClickListener(view -> {
            if (countryCodePicker.isValidFullNumber()) {
                phoneNumber = countryCodePicker.getFullNumberWithPlus();
                Toast.makeText(MainActivity.this, "yes", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(MainActivity.this, "no", Toast.LENGTH_SHORT).show();
            }
            startActivity(new Intent(context, FragmentsMainActivity.class));
        });
    }

    private void initView() {
        countryCodePicker = findViewById(R.id.ccp_picker);
        etPhoneNumber = findViewById(R.id.et_phone_no);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
    }
}