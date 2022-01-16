package com.example.smart_absensi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.smart_absensi.API.APIClient;
import com.example.smart_absensi.API.ApiInterface;
import com.example.smart_absensi.Adapter.AdapterSekda;
import com.example.smart_absensi.Model.Absen;
import com.example.smart_absensi.Model.AbsenData;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RiwayatSekda extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1;
    ImageView imgGMB;
    Button btn_ambilGambar,btn_chckInNow;
    ApiInterface apiInterface;

    private RecyclerView rv;
    private RecyclerView.Adapter adData;
    private RecyclerView.LayoutManager lmData;
    private List<AbsenData> listAbsen = new ArrayList<>();
    String ruangan,jabatan,nip,myjbt;
    int idRiwayat;
    SessionManager sessionManager;
    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_sekda);

        btnBack=findViewById(R.id.btn_backRiwayatSekda);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RiwayatSekda.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        sessionManager = new SessionManager(RiwayatSekda.this);
        if (!sessionManager.isLoggedIn()) {
            moveToLogin();
        }
        jabatan =sessionManager.getUserDetail().get(SessionManager.Jabatan);

        rv = findViewById(R.id.rvDataRiwayatSekda);
        lmData = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rv.setLayoutManager(lmData);
        myRiwayat(jabatan);

    }
    public void myRiwayat(String jabatan){
        apiInterface = APIClient.getClient().create(ApiInterface.class);
        Call<Absen> riwayatAbsen = apiInterface.getabsenSekda(jabatan);
        riwayatAbsen.enqueue(new Callback<Absen>() {
            @Override
            public void onResponse(Call<Absen> call, Response<Absen> response) {
                listAbsen = response.body().getAbsenDataku();
                adData = new AdapterSekda(RiwayatSekda.this,listAbsen);
                rv.setAdapter(adData);
                adData.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Absen> call, Throwable t) {
                Toast.makeText(RiwayatSekda.this, "Gagal Loading "+t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void moveToLogin() {
        Intent intent = new Intent(RiwayatSekda.this, LoginAbsen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }
}