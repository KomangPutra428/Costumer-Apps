package com.example.customerapps.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.customerapps.menu_utama.MainActivity;
import com.example.customerapps.R;
import com.example.customerapps.menu_login.login;

import java.util.Timer;

public class splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Timer t = new java.util.Timer();
        t.schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        SharedPreferences sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                        String nik_baru = sharedPreferences.getString("szId", null);

                        if (nik_baru == null) {
                            Intent i = new Intent(getApplicationContext(), login.class);
                            startActivity(i);
                        } else {
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                        }

                        t.cancel();
                    }
                },
                5000
        );

    }
}