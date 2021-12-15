package com.example.smart_absensi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SplashAbsen extends AppCompatActivity {

    ConstraintLayout btn_dashboardHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_absen);
        btn_dashboardHome=findViewById(R.id.btn_SplashCheckOut);

        btn_dashboardHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashAbsen.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}