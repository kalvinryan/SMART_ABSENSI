package com.example.smart_absensi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class SplashAbsen extends AppCompatActivity implements OnMapReadyCallback {

    ConstraintLayout btn_dashboardHome;
    private GoogleMap nMap;
    private MapView nMapView;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    int GEOFENCE_RADIUS = 20;
    int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
//    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_absen);
        btn_dashboardHome = findViewById(R.id.btn_SplashCheckOut);

        btn_dashboardHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashAbsen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        nMapView = (MapView) findViewById(R.id.mapGeofenc);
        nMapView.onCreate(mapViewBundle);
        nMapView.getMapAsync(this);


    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        nMap = googleMap;
        LatLng MyLocation = new LatLng(-2.957500,  119.923593);

        nMap.addMarker(new MarkerOptions().position(MyLocation).title("Marker"));
        nMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MyLocation,20));

        myGeofence();
        enabeledUserLocation();

    }

    private void myGeofence(){
        LatLng latLng = new LatLng(-2.957500,119.923593);
        addMarker(latLng);
        addCircle(latLng,GEOFENCE_RADIUS);

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