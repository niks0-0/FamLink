package com.nm.famlink.activities;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;

public class MemberMapActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private DatabaseReference liveRef;
    private Map<String, Marker> markers = new HashMap<>();

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.nm.famlink.R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(com.nm.famlink.R.id.map);
        mapFragment.getMapAsync(this);
        liveRef = FirebaseDatabase.getInstance().getReference("live_locations");
    }

    @Override public void onMapReady(GoogleMap googleMap){
        mMap = googleMap;
        liveRef.addValueEventListener(new ValueEventListener(){
            @Override public void onDataChange(DataSnapshot snapshot){
                if (snapshot.exists()){
                    for (DataSnapshot child : snapshot.getChildren()){
                        Map d = (Map) child.getValue();
                        String uid = (String) d.get("uid");
                        double lat = Double.parseDouble(d.get("lat").toString());
                        double lng = Double.parseDouble(d.get("lng").toString());
                        LatLng pos = new LatLng(lat, lng);
                        if (markers.containsKey(uid)){
                            markers.get(uid).setPosition(pos);
                        } else {
                            Marker m = mMap.addMarker(new MarkerOptions().position(pos).title(uid));
                            markers.put(uid, m);
                        }
                    }
                    // move camera to first marker
                    if (!markers.isEmpty()) {
                        Marker any = markers.values().iterator().next();
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(any.getPosition(), 12));
                    }
                }
            }
            @Override public void onCancelled(DatabaseError error){}
        });
    }
}
