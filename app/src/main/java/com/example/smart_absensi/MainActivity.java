package com.example.smart_absensi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smart_absensi.API.APIClient;
import com.example.smart_absensi.API.ApiInterface;
import com.example.smart_absensi.Model.Absen;
import com.example.smart_absensi.Model.Tpp;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,OnMapReadyCallback {
    TextClock jamdigital;
    ImageButton btn_checkIn, btn_checkOut,btn_riwayat,btn_izin;
    ConstraintLayout btn_LogOut,btnRiwayatSekda,btn_setingan;
    TextView txt_Nama, txt_Nip, txt_tanggal, txtHadir, txtSakit, txtIzin, txtAlfa, txtTpp,txtTerlambat;
    SessionManager sessionManager;
    String nip, nama, jabatan, ruangan, Tpp, jamMasuk, jamKeluar, tanggal, keterangan1, keterangan2;
    private GoogleMap nMap,googleMap;
    private MapView nMapViewActivity;
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;
    String GEOFENCE_ID = "SOME_GEOFENCE_ID";
    int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    ApiInterface apiInterface;
    int GEOFENCE_RADIUS = 20;
    private static final String TAG = "MainActivity";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CAMERA = 1888;
    private static final int REQUEST_WRITE_PERMISION = 786;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        nMapViewActivity = (MapView) findViewById(R.id.nMapViewActivity);
        nMapViewActivity.onCreate(mapViewBundle);
        nMapViewActivity.getMapAsync(this);
//        Geofencing
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);



        sessionManager = new SessionManager(MainActivity.this);
        if (!sessionManager.isLoggedIn()) {
            moveToLogin();
        }

        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());
        String currentTime = new SimpleDateFormat("HH.mm", Locale.getDefault()).format(new Date());

        jamdigital = findViewById(R.id.txt_clock);
        txt_tanggal = findViewById(R.id.txtTanggal);
        txt_Nip = findViewById(R.id.txtNip);
        txt_Nama = findViewById(R.id.txtNama);
        txtHadir = findViewById(R.id.txtHadir);
        txtSakit = findViewById(R.id.txtSakit);
        txtIzin = findViewById(R.id.txtIzin);
        txtAlfa = findViewById(R.id.txtAlfa);
        txtTpp = findViewById(R.id.txtTpp);
        txtTerlambat = findViewById(R.id.txtTerlambat);


        txt_tanggal.setText(formattedDate + ", ");

        btn_LogOut = findViewById(R.id.btn_LogOut);
        btn_checkIn = findViewById(R.id.btn_Check_in);
        btn_checkOut = findViewById(R.id.btn_CheckOut);
        btn_riwayat =  findViewById(R.id.btn_riwayat);
        btn_izin = findViewById(R.id.btn_izin);
        btnRiwayatSekda = findViewById(R.id.btn_myRiwayatku);
        btn_setingan = findViewById(R.id.btnSettingan);
        btn_riwayat.setOnClickListener(this);
        btn_checkOut.setOnClickListener(this);
        btn_LogOut.setOnClickListener(this);
        btn_checkIn.setOnClickListener(this);
        btn_izin.setOnClickListener(this);
        btnRiwayatSekda.setOnClickListener(this);
        btn_setingan.setOnClickListener(this);



        nip = sessionManager.getUserDetail().get(SessionManager.NIP);
        nama = sessionManager.getUserDetail().get(SessionManager.Nama);
        ruangan = sessionManager.getUserDetail().get(SessionManager.Ruangan);
        Tpp = sessionManager.getUserDetail().get(SessionManager.Tpp);
        jabatan = sessionManager.getUserDetail().get(SessionManager.Jabatan);

        Locale LocaleID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(LocaleID);
        numberAbsen(ruangan,jabatan);
//        DataTpp(nip);
        int myTPP = Integer.parseInt(Tpp);

        txt_Nip.setText(nip);
        txt_Nama.setText("Selamat Beraktivitas, " + nama);
        txtTpp.setText(formatRupiah.format(myTPP));

        if(jabatan.equalsIgnoreCase("SEKDA")){
            btnRiwayatSekda.setVisibility(View.VISIBLE);
        }else {
            btnRiwayatSekda.setVisibility(View.GONE);
        }

    }


    private void numberAbsen(String ruangan,String jabatan) {
        apiInterface = APIClient.getClient().create(ApiInterface.class);
        Call<Absen> numberHadir = apiInterface.getabsenHadir(ruangan,jabatan);
        numberHadir.enqueue(new Callback<Absen>() {
            @Override
            public void onResponse(Call<Absen> call, Response<Absen> response) {
                txtHadir.setText(response.body().getHadir());
                txtSakit.setText(response.body().getSakit());
                txtIzin.setText(response.body().getIzin());
                txtAlfa.setText(response.body().getAlfa());
                txtTerlambat.setText(response.body().getTerlambat());
            }

            @Override
            public void onFailure(Call<Absen> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT);
            }
        });
    }

    private void moveToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginAbsen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_LogOut:
                sessionManager.logoutSession();
                moveToLogin();
                break;
            case R.id.btn_Check_in:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            Location location = task.getResult();
                            if (location != null) {
                                GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                                LatLng sydney = new LatLng(geoPoint.getLatitude(),geoPoint.getLongitude());
                                nMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                nMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                                nMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                                String latitude = String.valueOf(geoPoint.getLatitude());
                                String longitude = String.valueOf(geoPoint.getLongitude());

//                                myGeofence();

                                double lat2 = -2.957745;
                                double lng2 = 119.923543;
                                double lat1 = geoPoint.getLatitude();
                                double lng1 = geoPoint.getLongitude();
                                Double pi = 3.14159265358979;
                                Double R = 6371e3;

                                Double latRad1 = lat1 *(pi /180);
                                Double latRad2 = lat2 * (pi / 180);
                                Double deltaLatRad = (lat2 - lat1)*(pi/180);
                                Double deltaLonRad = (lng2 - lng1)*(pi/180);

                                Double a = Math.sin(deltaLatRad / 2) * Math.sin(deltaLatRad / 2) + Math.cos(latRad1) * Math.cos(latRad2) * Math.sin(deltaLonRad / 2) * Math.sin(deltaLonRad / 2);
                                Double ci = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                                Double s = R * ci; // hasil jarak dalam meter

                                Double newdata = new Double(s);
                                int value = newdata.intValue();
//                                Toast.makeText(MainActivity.this, "Meters : "+value, Toast.LENGTH_SHORT).show();


                                if(value < 20 ){
                                    Calendar c = Calendar.getInstance();
                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                    String formattedDate = df.format(c.getTime());
                                    String currentTime = new SimpleDateFormat("kkmm", Locale.getDefault()).format(new Date());
                                    String currentTime2 = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                                    nip = sessionManager.getUserDetail().get(SessionManager.NIP);
                                    nama = sessionManager.getUserDetail().get(SessionManager.Nama);
                                    jabatan = sessionManager.getUserDetail().get(SessionManager.Jabatan);
                                    ruangan = sessionManager.getUserDetail().get(SessionManager.Ruangan);
                                    Tpp = sessionManager.getUserDetail().get(SessionManager.Tpp);
                                    int MyTpp = Integer.parseInt(Tpp);
                                    jamMasuk = currentTime2;
                                    tanggal = formattedDate;
                                    keterangan1 = "On Working";
                                    keterangan2 = "Terlambat";

                                    String time1 = currentTime;
                                    String time2 = "1600";
                                    String time3 = currentTime;
                                    String time4 = "0730";
                                    SimpleDateFormat format = new SimpleDateFormat("HHmm");

                                    try {
                                        Date date1 = format.parse(time1);
                                        Date date2 = format.parse(time2);
                                        Date date3 = format.parse(time3);
                                        Date date4 = format.parse(time4);
                                        long difference = date2.getTime() - date1.getTime();
                    //                    long diffMinutes = difference / (60 * 1000) % 60;
                                        long diffMinutes = difference / (60 * 60 * 1000) % 24;
                                        long diff = date4.getTime() - date3.getTime();
                                        long dif = diff / (60 * 60 * 1000) % 24;
                                        int hours = (int) Math.abs(dif);
                                        int minuts = (int) Math.abs(diffMinutes);

                                        if (minuts >= 0) {
                                            if (minuts >= 8) {
                                                Intent intent = new Intent(MainActivity.this,CheckInNow.class);
                                                intent.putExtra("nip",nip);
                                                intent.putExtra("nama",nama);
                                                intent.putExtra("jabatan",jabatan);
                                                intent.putExtra("ruangan",ruangan);
                                                intent.putExtra("latitude",latitude);
                                                intent.putExtra("longitude",longitude);
                                                intent.putExtra("jamMasuk",jamMasuk);
                                                intent.putExtra("tanggal",tanggal);
                                                intent.putExtra("keterangan",keterangan1);
                                                intent.putExtra("tpp",Tpp);
                                                startActivity(intent);

                                            } else {
                                                double updtateTPP;
                                                updtateTPP = MyTpp - 0.5;
                                                int TppPns = (int) updtateTPP;
                                                String tppPns = String.valueOf(TppPns);
                                                Intent intent = new Intent(MainActivity.this,CheckInNow.class);
                                                intent.putExtra("nip",nip);
                                                intent.putExtra("nama",nama);
                                                intent.putExtra("jabatan",jabatan);
                                                intent.putExtra("ruangan",ruangan);
                                                intent.putExtra("latitude",latitude);
                                                intent.putExtra("longitude",longitude);
                                                intent.putExtra("jamMasuk",jamMasuk);
                                                intent.putExtra("tanggal",tanggal);
                                                intent.putExtra("keterangan",keterangan2);
                                                intent.putExtra("tpp",Tpp);
                                                startActivity(intent);
                                            }
                                        } else {
                                            Toast.makeText(MainActivity.this, "Silahkan Melakukan Absen Di jam 7.00 pagi", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    Toast.makeText(MainActivity.this, "Lokasi anda tidak masuk kedalam radius yang di tentukan." +
                                            "Silahakan Untuk melakukan absensi di Lobi sekda", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Lokasi Tidak Ditemukan, Mohon Untuk " +
                                        "mengaktifkan Lokasi anda..", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                });




                break;
            case R.id.btn_CheckOut:
                Calendar T = Calendar.getInstance();
                SimpleDateFormat dT = new SimpleDateFormat("yyyy-MM-dd");
                String formattedT = dT.format(T.getTime());
                String mytime = new SimpleDateFormat("HHmm", Locale.getDefault()).format(new Date());
                nip = sessionManager.getUserDetail().get(SessionManager.NIP);
                jamKeluar = mytime;
                tanggal = formattedT;
                keterangan1 = "Hadir";

                String time12 = mytime;
                String time13 = "1600";
                String time14 = mytime;
                String time15 = "0730";
                SimpleDateFormat formatout = new SimpleDateFormat("HHmm");

                try {
                    Date date1 = formatout.parse(time12);
                    Date date2 = formatout.parse(time13);
                    Date date3 = formatout.parse(time14);
                    Date date4 = formatout.parse(time15);
                    long difference = date2.getTime() - date1.getTime();
//                    long diffMinutes = difference / (60 * 1000) % 60;
                    long diffMinutes = difference / (60 * 60 * 1000) % 24;
                    long diff = date4.getTime() - date3.getTime();
                    long dif = diff / (60 * 60 * 1000) % 24;
                    int hours = (int) Math.abs(dif);
                    int minuts = (int) Math.abs(diffMinutes);
//                    String coba=String.valueOf(minuts);
//                    Toast.makeText(MainActivity.this,"jam : "+coba , Toast.LENGTH_SHORT).show();
                    if (minuts < 0) {
                        ChecK_Out(nip, jamKeluar, tanggal, keterangan1);
                    } else {
                        Toast.makeText(MainActivity.this, "Silahkan Check-Out di jam 16.00", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_riwayat:
                try {
                    String ruanganku = sessionManager.getUserDetail().get(SessionManager.Ruangan);
                    String jabatanku = sessionManager.getUserDetail().get(SessionManager.Jabatan);
                    String nipku = sessionManager.getUserDetail().get(SessionManager.NIP);
                    if (ruanganku != null && ruanganku != ""){
                        Intent i = new Intent(MainActivity.this, Riwayat.class);
                        i.putExtra("ruangan", ruanganku);
                        i.putExtra("jabatan", jabatanku);
                        i.putExtra("nip", nipku);
                        startActivity(i);

                    } else {
                        Toast.makeText(getApplication(), "YOU NEED TO FILL YOUR NAME",Toast.LENGTH_SHORT);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getApplication(), "ERROR, TRY AGAIN !",Toast.LENGTH_SHORT);
                }


                break;

            case R.id.btn_izin:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            Location location = task.getResult();
                            if (location != null) {
                                GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                                LatLng sydney = new LatLng(geoPoint.getLatitude(),geoPoint.getLongitude());
                                nMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                nMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                                nMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                                String latitude = String.valueOf(geoPoint.getLatitude());
                                String longitude = String.valueOf(geoPoint.getLongitude());

//                                String Latlang = "Latitude " + geoPoint.getLatitude() + "| Longitude " + geoPoint.getLatitude();
                                Intent intent = new Intent(MainActivity.this,Izin.class);
                                intent.putExtra("nip",nip);
                                intent.putExtra("nama",nama);
                                intent.putExtra("jabatan",jabatan);
                                intent.putExtra("ruangan",ruangan);
                                intent.putExtra("latitude",latitude);
                                intent.putExtra("longitude",longitude);
                                intent.putExtra("jamMasuk",jamMasuk);
                                intent.putExtra("tpp",Tpp);
                                startActivity(intent);
//                                Toast.makeText(MainActivity.this, Latlang, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Lokasi Tidak Ditemukan, Mohon Untuk " +
                                "mengaktifkan Lokasi anda..", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                });

            break;
            case  R.id.btn_myRiwayatku:
                Intent myIntent = new Intent(MainActivity.this,RiwayatSekda.class);
                startActivity(myIntent);
                break;
            case  R.id.btnSettingan:
                Intent intentS = new Intent(MainActivity.this, Settings.class);
                startActivity(intentS);
                break;
        }

    }



    private void ChecK_Out(String nip, String jamKeluar, String tanggal, String keterangan) {
        apiInterface = APIClient.getClient().create(ApiInterface.class);
        Call<Absen> CheckOut = apiInterface.absenUpdate(nip, jamKeluar, tanggal, keterangan);
        CheckOut.enqueue(new Callback<Absen>() {
            @Override
            public void onResponse(Call<Absen> call, Response<Absen> response) {
                if (response.body() != null && response.isSuccessful()) {
                    Intent intent = new Intent(MainActivity.this, SplashCheckOut.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Anda Sudah Check-Out", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Absen> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT);
            }
        });
    }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        nMap = googleMap;
//        LatLng latLng = new LatLng(-2.9575381015932436, 119.92326534659807);
//        nMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//        nMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
//        nMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,25));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    if (location != null) {
                        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                        LatLng sydney = new LatLng(geoPoint.getLatitude(),geoPoint.getLongitude());
                        nMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        nMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                        nMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

//                        String Latlang = "Latitude " + geoPoint.getLatitude() + "| Longitude " + geoPoint.getLatitude();
//
//                        Toast.makeText(MainActivity.this, Latlang, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Lokasi Tidak Ditemukan, Mohon Untuk " +
                                "mengaktifkan Lokasi anda..", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });

        myGeofence();
        enabeledUserLocation();
//        myMarker();
    }

    private void myMarker(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    if (location != null) {
                        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                        LatLng sydney = new LatLng(geoPoint.getLatitude(),geoPoint.getLongitude());
                        nMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        nMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                        nMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,16));

                        String Latlang = "Latitude " + geoPoint.getLatitude() + "| Longitude " + geoPoint.getLatitude();

                        Toast.makeText(MainActivity.this, Latlang, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Lokasi Tidak Ditemukan, Mohon Untuk " +
                                "mengaktifkan Lokasi anda..", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
    }

    private void myGeofence(){
        LatLng latLng = new LatLng(-2.957639, 119.923510);
//        Toast.makeText(this, "Lat : "+latLng, Toast.LENGTH_SHORT).show();
        addMarker(latLng);
        addCircle(latLng,GEOFENCE_RADIUS);
        addGeofence(latLng,GEOFENCE_RADIUS);


    }

    private void addGeofence(LatLng latLng,float radius){
        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID,latLng,radius,Geofence.
                GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL
                | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest =geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {
                        Log.d(TAG, "onSuccess: GeoFence Add...");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErroString(e);
                        Log.d(TAG, "onFailure: "+errorMessage);
                    }
                });
    }

    private void addMarker(LatLng latLng){
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        nMap.addMarker(markerOptions);
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

    private void enabeledUserLocation() {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==
        PackageManager.PERMISSION_GRANTED){
            nMap.setMyLocationEnabled(true);
        }else{
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},FINE_LOCATION_ACCESS_REQUEST_CODE);
            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                nMap.setMyLocationEnabled(true);
            }else{

            }
        }
    }

}