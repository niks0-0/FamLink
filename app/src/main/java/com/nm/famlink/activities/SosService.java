package com.nm.famlink.activities;
import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.core.app.NotificationCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class SosService extends Service {
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private long lastSentTs = 0;
    private String uid;

    @Nullable @Override public IBinder onBind(Intent intent) { return null; }

    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    @Override public void onCreate() {
        super.onCreate();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        uid = FirebaseAuth.getInstance().getUid();
        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, "famlink_channel")
                .setContentTitle("FamLink tracking active")
                .setContentText("Sharing location with family")
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .build();
        startForeground(101, notification);

        locationCallback = new LocationCallback(){
            @Override public void onLocationResult(LocationResult result){
                for (Location loc: result.getLocations()){
                    sendLocation(loc.getLatitude(), loc.getLongitude(), getBatteryPercent());
                }
            }
        };

        LocationRequest req = LocationRequest.create();
        req.setInterval(30000);
        req.setFastestInterval(15000);
        req.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        fusedLocationClient.requestLocationUpdates(req, locationCallback, Looper.getMainLooper());
    }

    @Override public void onDestroy() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
        stopForeground(true);
        super.onDestroy();
    }

    private void sendLocation(double lat, double lng, int battery) {
        long now = System.currentTimeMillis();
        if (now - lastSentTs < 30000) return;
        lastSentTs = now;
        Map<String,Object> data = new HashMap<>();
        data.put("uid", uid);
        data.put("lat", lat);
        data.put("lng", lng);
        data.put("battery", battery);
        data.put("ts", ServerValue.TIMESTAMP);
        FirebaseDatabase.getInstance().getReference("live_locations").child(uid).setValue(data);
    }

    private int getBatteryPercent() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);
        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;
        if (level == -1 || scale == -1) return 0;
        return (int)((level / (float)scale) * 100);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel("famlink_channel", "FamLink", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager nm = getSystemService(NotificationManager.class);
            nm.createNotificationChannel(ch);
        }
    }
}
