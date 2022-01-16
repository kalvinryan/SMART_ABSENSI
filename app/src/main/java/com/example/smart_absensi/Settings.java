package com.example.smart_absensi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.smart_absensi.API.APIClient;
import com.example.smart_absensi.API.ApiInterface;
import com.example.smart_absensi.Model.Login;
import com.example.smart_absensi.Model.LoginData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Settings extends AppCompatActivity {

    SessionManager sessionManager;
    EditText txtNip,txtNama,txtPassLama,txtPassBaru;
    ApiInterface apiInterface;
    String Nip,Nama,Passlama,PassBaru;
    Button btn_update;
    ImageView btn_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        txtNip = findViewById(R.id.txtNipPtn);
        txtNama = findViewById(R.id.txtNamaPtn);
        txtPassLama = findViewById(R.id.txtPasswordPtn);
        txtPassBaru = findViewById(R.id.txt_passwordBaruPtn);
        btn_update = findViewById(R.id.btn_kirimPtn);
        btn_back = findViewById(R.id.btn_backPtn);

        sessionManager = new SessionManager(Settings.this);
        if (!sessionManager.isLoggedIn()) {
            moveToLogin();
        }

        Nip=sessionManager.getUserDetail().get(SessionManager.NIP);
        Nama=sessionManager.getUserDetail().get(SessionManager.Nama);
        Passlama=sessionManager.getUserDetail().get(SessionManager.Password);



        txtNip.setText(Nip);
        txtNama.setText(Nama);
        txtPassLama.setText(Passlama);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.this,MainActivity.class);
                startActivity(intent);
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PassBaru = String.valueOf(txtPassBaru.getText());
                UpdatePassword(Nip,PassBaru);
            }
        });

    }
    private void moveToLogin() {
        Intent intent = new Intent(Settings.this, LoginAbsen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    private void UpdatePassword(String nip,String password){
        apiInterface =APIClient.getClient().create(ApiInterface.class);
        Call<Login>UpdatePass = apiInterface.UpdatePassword(nip,password);
        UpdatePass.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.body() != null && response.isSuccessful()) {
                    Intent intent = new Intent(Settings.this, LoginAbsen.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(Settings.this, "Gagal Update Data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {

            }
        });

    }
}