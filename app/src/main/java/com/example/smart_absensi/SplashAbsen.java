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
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;


public class SplashAbsen extends AppCompatActivity implements OnMapReadyCallback {

    ConstraintLayout btn_dashboardHome;
    private GoogleMap nMap;
    private MapView nMapView;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    int GEOFENCE_RADIUS = 20;
    int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private FusedLocationProviderClient fusedLocationProviderClient;
//    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_absen);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        btn_dashboardHome = findViewById(R.id.btn_BackAbsen);
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
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
                        nMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,20));

                        String Latlang = "Latitude " + geoPoint.getLatitude() + "| Longitude " + geoPoint.getLatitude();

                        Toast.makeText(SplashAbsen.this, Latlang, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SplashAbsen.this, "Location Is Null", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });

        myGeofence();
        enabeledUserLocation();

    }

    private void myGeofence(){
        LatLng latLng = new LatLng(-2.957739046132328, 119.92355423779769);
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