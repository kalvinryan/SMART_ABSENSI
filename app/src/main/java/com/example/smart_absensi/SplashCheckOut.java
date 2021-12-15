package com.example.smart_absensi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SplashCheckOut extends AppCompatActivity {

    ConstraintLayout btn_SplashCheckOut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_check_out);

        btn_SplashCheckOut=findViewById(R.id.btn_SplashCheckOut);

        btn_SplashCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashCheckOut.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}