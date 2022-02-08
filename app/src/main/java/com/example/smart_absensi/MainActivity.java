package com.example.smart_absensi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smart_absensi.API.APIClient;
import com.example.smart_absensi.API.ApiInterface;
import com.example.smart_absensi.Model.Absen;
import com.example.smart_absensi.Model.Expired;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,OnMapReadyCallback {
    TextClock jamdigital;
    ImageButton btn_checkIn, btn_checkOut;
    ConstraintLayout btn_LogOut, btn_izin;
    TextView txt_Nama, txt_Nip, txt_tanggal, txtHadir, txtSakit, txtIzin, txtAlfa, txt_updateStatus,txtTerlambat;
    SessionManager sessionManager;
    int myGeofTimer;
    String nip, nama, jabatan, ruangan, Tpp, jamMasuk, jamKeluar, tanggal, keterangan1, keterangan2,stsku;
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
    AlarmManager manager,manager2;
    PendingIntent pendingIntent ,pendingIntent2;
    long diffDays,diff;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        createNotificatioChanel();
        createNotificatioChanelku();
        startIn();


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
        Expired expired = new Expired();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        sessionManager = new SessionManager(MainActivity.this);
        if (!sessionManager.isLoggedIn()) {
            moveToLogin();
        }
        myGeofTimer = Integer.parseInt(sessionManager.getExpired());
        if (myGeofTimer>=0){
            Toast.makeText(this, expired.getPesan(), Toast.LENGTH_SHORT).show();
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
        txtTerlambat = findViewById(R.id.txtTerlambat);

//        txt_updateStatus.setText("fzczxczx");
        txt_tanggal.setText(formattedDate + ", ");

        btn_LogOut = findViewById(R.id.btn_LogOut);
        btn_checkIn = findViewById(R.id.btn_Check_in);
        btn_checkOut = findViewById(R.id.btn_CheckOut);
        btn_izin= findViewById(R.id.btn_myIzin);
        btn_checkOut.setOnClickListener(this);
        btn_LogOut.setOnClickListener(this);
        btn_checkIn.setOnClickListener(this);
        btn_izin.setOnClickListener(this);


        nip = sessionManager.getUserDetail().get(SessionManager.NIP);
        nama = sessionManager.getUserDetail().get(SessionManager.Nama);
        ruangan = sessionManager.getUserDetail().get(SessionManager.Ruangan);
        Tpp = sessionManager.getUserDetail().get(SessionManager.Tpp);
        jabatan = sessionManager.getUserDetail().get(SessionManager.Jabatan);

        Locale LocaleID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(LocaleID);
        numberAbsen(ruangan,jabatan);


        txt_Nip.setText(nip);
        txt_Nama.setText("Selamat Beraktivitas, " + nama);
        stsku="Libur";

    }

    private void createNotificatioChanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            CharSequence name ="smartAbsensi";
            String description = "Alarm Kerja";
            int importan = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("smart",name,importan);
            channel.setDescription(description);

            NotificationManager notificationManager= getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }
    private void createNotificatioChanelku() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            CharSequence name ="smartAbsensi";
            String description = "Alarm Kerja";
            int importan = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("smartku",name,importan);
            channel.setDescription(description);

            NotificationManager notificationManager= getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
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
                StopAlarmIn();
                startOut();
//                Toast.makeText(MainActivity.this, "Berhasil", Toast.LENGTH_SHORT).show();
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
                                nMap.addMarker(new MarkerOptions().position(sydney).title(""));
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


                                if(value <= 20 ){
                                    Calendar c = Calendar.getInstance();
                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                    String formattedDate = df.format(c.getTime());
                                    String currentTime = new SimpleDateFormat("HHmmss", Locale.getDefault()).format(new Date());
                                    String currentTime2 = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
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
                                    String time2 = "160000";
                                    String time3 = currentTime;
                                    String time4 = "060000";

                                    SimpleDateFormat format = new SimpleDateFormat("HHmmss");

                                    String tpp = sessionManager.getUserDetail().get(SessionManager.Tpp);
                                    Intent intent = new Intent(MainActivity.this, CheckInNow.class);
                                    intent.putExtra("nip", nip);
                                    intent.putExtra("nama", nama);
                                    intent.putExtra("jabatan", jabatan);
                                    intent.putExtra("ruangan", ruangan);
                                    intent.putExtra("latitude", latitude);
                                    intent.putExtra("longitude", longitude);
                                    intent.putExtra("jamMasuk", jamMasuk);
                                    intent.putExtra("tanggal", tanggal);
                                    intent.putExtra("keterangan", keterangan1);
                                    intent.putExtra("tpp", tpp);
                                    startActivity(intent);
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
                StopAlarmOut();
                startIn();
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


                                if(value <= 20 ){
                                    Calendar T = Calendar.getInstance();
                                    SimpleDateFormat dT = new SimpleDateFormat("yyyy-MM-dd");
                                    String formattedT = dT.format(T.getTime());
                                    String mytime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                                    nip = sessionManager.getUserDetail().get(SessionManager.NIP);
                                    jamKeluar = mytime;
                                    tanggal = formattedT;
                                    keterangan1 = "Hadir";

                                    String chck="Hadir";
                                    Intent intent = new Intent(MainActivity.this,ChckOutNow.class);
                                    intent.putExtra("nip",nip);
                                    intent.putExtra("tanggal",tanggal);
                                    intent.putExtra("keterangan",keterangan1);
                                    intent.putExtra("jamKeluar",jamKeluar);
                                    intent.putExtra("chekOut",chck);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(MainActivity.this, "Lokasi anda tidak masuk kedalam radius yang di tentukan." +
                                            "Silahakan Untuk melakukan Chek Out di Lobi sekda", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Lokasi Tidak Ditemukan, Mohon Untuk " +
                                        "mengaktifkan Lokasi anda..", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                });




                break;
            case R.id.btn_myIzin:
                int dataku = Integer.parseInt(sessionManager.getExpired());
                Log.d(TAG, "onClick: "+dataku);
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
        }

    }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        nMap = googleMap;
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

    public void startIn() {
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Date dat = new Date();
        Calendar cal_alarm = Calendar.getInstance();
        Calendar cal_now = Calendar.getInstance();
        cal_now.setTime(dat);
        cal_alarm.setTime(dat);
        cal_alarm.set(Calendar.HOUR_OF_DAY,6);
        cal_alarm.set(Calendar.MINUTE, 00);
        cal_alarm.set(Calendar.SECOND, 0);
        Intent myIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, 0);
        String absenj="Waktunya Untuk Cek In";
        myIntent.putExtra("absenku",absenj);
//        manager.setRepeating(AlarmManager.RTC_WAKEUP,cal_alarm.getTimeInMillis(),1000 * 60 * 1, pendingIntent);
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP,cal_alarm.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);

    }
    public void startOut() {
        manager2 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Date dat = new Date();
        Calendar cal_alarm = Calendar.getInstance();
        Calendar cal_now = Calendar.getInstance();
        cal_now.setTime(dat);
        cal_alarm.setTime(dat);
        cal_alarm.set(Calendar.HOUR_OF_DAY,16);
        cal_alarm.set(Calendar.MINUTE,00);
        cal_alarm.set(Calendar.SECOND,0);

        Intent myIntent = new Intent(MainActivity.this, AlarmReceiverOut.class);
        String absenj="Waktunya Untuk Cek Out";
        myIntent.putExtra("absenku",absenj);
        pendingIntent2 = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, 0);

        manager2.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent2);

    }

    public void StopAlarmOut(){
        Intent myIntent = new Intent(MainActivity.this, AlarmReceiverOut.class);
        pendingIntent2 = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, 0);
        if (manager2!= null) {
            manager2.cancel(pendingIntent2);
        }
//        Toast.makeText(this, "Alarm berhenti", Toast.LENGTH_SHORT).show();

    }
    public void StopAlarmIn(){
        Intent myIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, 0);
        if (manager!= null) {
            manager.cancel(pendingIntent);
        }
//        Toast.makeText(this, "Alarm berhenti", Toast.LENGTH_SHORT).show();

    }

    public String getTimer() {
        Date currentDate = Calendar.getInstance().getTime();
        String birthDateString="2022/02/06";
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");

        try {
            Date birthDate;
            birthDate = format.parse(birthDateString);
            diff =  birthDate.getTime()-currentDate.getTime();
            diffDays = diff / (24 * 60 * 60 * 1000);
//            Toast.makeText(this, "Hari = " +diffDays, Toast.LENGTH_SHORT).show();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return String.valueOf(diffDays);

    }




}