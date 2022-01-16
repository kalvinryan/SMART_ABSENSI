package com.example.smart_absensi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smart_absensi.API.APIClient;
import com.example.smart_absensi.API.ApiInterface;
import com.example.smart_absensi.Model.Login;
import com.example.smart_absensi.Model.LoginData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginAbsen extends AppCompatActivity implements View.OnClickListener{

    EditText txtNip,txtPassword;
    Button btnMasuk,btnkeluar;
    String nip, password;
    ApiInterface apiInterface;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_absen);
        txtNip = findViewById(R.id.txt_nip);
        txtPassword = findViewById(R.id.txt_password);
        btnMasuk = findViewById(R.id.btn_login);
        btnkeluar = findViewById(R.id.btn_Close);
        btnMasuk.setOnClickListener(this);
        btnkeluar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                nip = txtNip.getText().toString();
                password = txtPassword.getText().toString();
                login(nip,password);
                break;
            case R.id.btn_Close:
                txtNip.setText("");
                txtPassword.setText("");
                finish();

        }
    }

    private void login(String nip, String password) {
        apiInterface = APIClient.getClient().create(ApiInterface.class);
        Call<Login> LoginCall = apiInterface.LoginResponse(nip,password);
        LoginCall.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.body() !=null && response.isSuccessful()){
                    sessionManager = new SessionManager(LoginAbsen.this);
                    LoginData loginData = response.body().getLoginData();
                    sessionManager.createLoginSession(loginData);
                    Toast.makeText(LoginAbsen.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginAbsen.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
//                    Toast.makeText(LoginAbsen.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(LoginAbsen.this, "NIP dan Password salah", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable throwable) {

            }
        });




    }
}