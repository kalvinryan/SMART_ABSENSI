package com.example.smart_absensi;

import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;

public class GeofenceHelper extends ContextWrapper {
    public static final String TAG = "GeofenceHelper";
    PendingIntent pendingIntent;

    public GeofenceHelper(Context base) {
        super(base);
    }

    public GeofencingRequest getGeofencingRequest(Geofence geofence){
        return null;
    }

    public Geofence getGeofrnce(String ID, LatLng latLng, float radius, int transitionTypes){
        return null;
    }

    public PendingIntent getPendingIntent() {
        return pendingIntent;
    }
}
