package com.nm.famlink.activities;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.nm.famlink.activities.MemberMapActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private ListView lvMembers;
    private Button btnMap, btnSos;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String familyId = "";

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(com.nm.famlink.R.layout.activity_home);
        lvMembers = findViewById(com.nm.famlink.R.id.lvMembers);
        btnMap = findViewById(com.nm.famlink.R.id.btnMap);
        btnSos = findViewById(com.nm.famlink.R.id.btnSos);

        // load user familyId
        db.collection("users").document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(doc -> {
            if (doc.exists() && doc.contains("familyId")) {
                familyId = doc.getString("familyId");
                loadMembers();
            } else {
                Toast.makeText(this, "No family set", Toast.LENGTH_SHORT).show();
            }
        });

        btnMap.setOnClickListener(v -> startActivity(new Intent(this, MemberMapActivity.class)));
        btnSos.setOnClickListener(v -> sendSos());
    }

    private void loadMembers() {
        db.collection("users").whereEqualTo("familyId", familyId).get().addOnSuccessListener((QuerySnapshot snaps) -> {
            List<String> names = new ArrayList<>();
            for (int i=0;i<snaps.size();i++){
                String n = snaps.getDocuments().get(i).getString("phone");
                names.add(n != null ? n : "Member");
            }
            lvMembers.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names));
        });
    }

    private void sendSos() {
        if (familyId.isEmpty()) { Toast.makeText(this, "No family", Toast.LENGTH_SHORT).show(); return; }
        String uid = mAuth.getCurrentUser().getUid();
        java.util.Map<String,Object> a = new java.util.HashMap<>();
        a.put("familyId", familyId);
        a.put("uid", uid);
        a.put("type", "sos");
        a.put("note", "Help needed");
        a.put("ts", com.google.firebase.firestore.FieldValue.serverTimestamp());
        a.put("resolved", false);
        db.collection("alerts").add(a).addOnSuccessListener(doc -> {
            Toast.makeText(this, "SOS sent", Toast.LENGTH_SHORT).show();
        });
    }
}
