package com.example.smart_absensi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.smart_absensi.API.APIClient;
import com.example.smart_absensi.API.ApiInterface;
import com.example.smart_absensi.Adapter.AdapterRiwayat;
import com.example.smart_absensi.Model.Absen;
import com.example.smart_absensi.Model.AbsenData;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Riwayat extends AppCompatActivity {

    ApiInterface apiInterface;
    private RecyclerView rv;
    private RecyclerView.Adapter adData;
    private RecyclerView.LayoutManager lmData;
    private List<AbsenData> listAbsen = new ArrayList<>();
    String ruangan,jabatan,nip,myjbt;
    int idRiwayat;

    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat);
        Bundle extras = getIntent().getExtras();

        btnBack = findViewById(R.id.btn_backRiwayat);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Riwayat.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ruangan = extras.getString("ruangan");
        jabatan = extras.getString("jabatan");
        nip = extras.getString("nip");


        rv = findViewById(R.id.rvDataRiwayat);
        lmData = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rv.setLayoutManager(lmData);
        myRiwayat(ruangan,nip,jabatan);

        Toast.makeText(this, jabatan, Toast.LENGTH_SHORT).show();
    }

    public void myRiwayat(String ruangan,String nip,String jabatan){
        apiInterface = APIClient.getClient().create(ApiInterface.class);
        Call<Absen> riwayatAbsen = apiInterface.getabsenRiwayat(ruangan,nip,jabatan);
        riwayatAbsen.enqueue(new Callback<Absen>() {
            @Override
            public void onResponse(Call<Absen> call, Response<Absen> response) {
                listAbsen = response.body().getAbsenDataku();
                adData = new AdapterRiwayat(Riwayat.this,listAbsen);
                rv.setAdapter(adData);
                adData.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Absen> call, Throwable t) {
                Toast.makeText(Riwayat.this, "Gagal Loading "+t.toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}