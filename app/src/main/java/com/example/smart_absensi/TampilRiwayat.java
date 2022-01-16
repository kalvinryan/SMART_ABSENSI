package com.example.smart_absensi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.smart_absensi.API.APIClient;
import com.example.smart_absensi.API.ApiInterface;
import com.example.smart_absensi.Model.Absen;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.smart_absensi.Model.Tpp;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TampilRiwayat extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap nMap;
    private MapView nMapView;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    int GEOFENCE_RADIUS = 20;
    SessionManager sessionManager;
    int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    String id,nip,nama,latitude,longitude,keterangan,tpp,myjbt;
    EditText txt_nip,txt_nama;
    private FusedLocationProviderClient fusedLocationProviderClient;
    SwipeRefreshLayout swipeRefreshLayout;
    ApiInterface apiInterface;
    ImageView btnBack;

    Button btn_Izin,btn_alfa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tampil_riwayat);
        swipeRefreshLayout=findViewById(R.id.Swipe_ref);
        btn_alfa=findViewById(R.id.btn_alfaTmp);
        btn_Izin=findViewById(R.id.btn_iziTmp);
        btnBack=findViewById(R.id.btn_backDtlRiwayat);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TampilRiwayat.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },5000);
            }
        });

        Intent data = getIntent();

        id = data.getStringExtra("xId");
        nip = data.getStringExtra("xNIP");
        nama = data.getStringExtra("xNama");
        latitude = data.getStringExtra("xLatitude");
        longitude = data.getStringExtra("xLongitude");
        keterangan = data.getStringExtra("xketerangan");
        tpp= data.getStringExtra("xtpp");

//        Toast.makeText(this, "Tpp : "+tpp, Toast.LENGTH_SHORT).show();

        sessionManager = new SessionManager(TampilRiwayat.this);
        if (!sessionManager.isLoggedIn()) {
            moveToLogin();
        }
        myjbt = sessionManager.getUserDetail().get(SessionManager.Jabatan);
        if (myjbt.equalsIgnoreCase("SEKDA")||myjbt.equalsIgnoreCase("KABAG")){
            btn_Izin.setVisibility(View.VISIBLE);
            btn_alfa.setVisibility(View.VISIBLE);
        }else{
            btn_Izin.setVisibility(View.GONE);
            btn_alfa.setVisibility(View.GONE);
        }
        Toast.makeText(this, myjbt, Toast.LENGTH_SHORT).show();

        float mmm= Float.valueOf(latitude);
        float mmi= Float.valueOf(longitude);

        txt_nama= findViewById(R.id.txt_namaTmp);
        txt_nip=findViewById(R.id.txt_nipTemp);

        txt_nama.setText(nama);
        txt_nip.setText(nip);
        txt_nip.setEnabled(false);
        txt_nama.setEnabled(false);

        Toast.makeText(this, mmm+" "+mmi, Toast.LENGTH_SHORT).show();

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        nMapView = (MapView) findViewById(R.id.mapTmp);
        nMapView.onCreate(mapViewBundle);
        nMapView.getMapAsync(this);

        btn_Izin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keteranganku = "Izin";
                String flag = "izin";
                izin(id,keteranganku,flag);
            }
        });

        btn_alfa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keteranganku = "Alfa";
                String flag = "alfa";
                alfa(id,keteranganku,flag);
                int hasilttp = (int) (0.5 * Integer.parseInt(tpp));
                tppPegawai(nip,String.valueOf(hasilttp));
            }
        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        nMap = googleMap;
        try{
            float myLat = Float.valueOf(latitude);
            float myLong = Float.valueOf(longitude);
            LatLng MyLocation = new LatLng(myLat,myLong);
            nMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            nMap.addMarker(new MarkerOptions().position(MyLocation).title(nama));
            nMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MyLocation,50));
            myGeofence();
        }catch (NumberFormatException e){
            Log.e(String.valueOf(this), "onMapReady: ",e );       }

    }

    private void myGeofence(){
        LatLng latLng = new LatLng(-2.957500,119.923593);
        addMarker(latLng);
        addCircle(latLng,GEOFENCE_RADIUS);


    }

    private void addMarker(LatLng latLng){
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        nMap.addMarker(markerOptions);
//        nMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,50));

    }

    private void addCircle(LatLng latLng,float radius){
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(225,0,0,225));
        circleOptions.fillColor(Color.argb(64,0,0,225));
        circleOptions.strokeWidth(4);
        nMap.addCircle(circleOptions);
    }

    private void tppPegawai(String nip,String tpp){
        apiInterface =APIClient.getClient().create(ApiInterface.class);
        Call<Tpp>UpdateTpp = apiInterface.UpdateTpp(nip,tpp);
        UpdateTpp.enqueue(new Callback<Tpp>() {
            @Override
            public void onResponse(Call<Tpp> call, Response<Tpp> response) {

            }

            @Override
            public void onFailure(Call<Tpp> call, Throwable t) {

            }
        });
    }

    private void alfa(String id,String keterangan,String flag){
        apiInterface =APIClient.getClient().create(ApiInterface.class);
        Call<Absen>UpdateTpp = apiInterface.getRiwayatAbsen(id,keterangan,flag);
        UpdateTpp.enqueue(new Callback<Absen>() {
            @Override
            public void onResponse(Call<Absen> call, Response<Absen> response) {
//                Toast.makeText(getApplicationContext(), "Data berhasil Di Update", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(TampilRiwayat.this,Riwayat.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<Absen> call, Throwable t) {
                Log.d("data", "onFailure: " + t);
            }
        });
    }
    private void izin(String id,String keterangan,String flag){
        apiInterface =APIClient.getClient().create(ApiInterface.class);
        Call<Absen>UpdateTpp = apiInterface.getRiwayatAbsen(id,keterangan,flag);
        UpdateTpp.enqueue(new Callback<Absen>() {
            @Override
            public void onResponse(Call<Absen> call, Response<Absen> response) {
//                Toast.makeText(getApplicationContext(), "Data berhasil Di Update", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(TampilRiwayat.this,Riwayat.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<Absen> call, Throwable t) {
                Log.d("data", "onFailure: " + t);
            }
        });
    }


    private void moveToLogin() {
        Intent intent = new Intent(TampilRiwayat.this, LoginAbsen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

}