package com.yourname.famlink.activities;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OnboardingActivity extends AppCompatActivity {
    private EditText etFamilyName, etJoinCode;
    private Button btnCreate, btnJoin;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(com.nm.famlink.R.layout.activity_onboarding);
        etFamilyName = findViewById(com.nm.famlink.R.id.etFamilyName);
        etJoinCode = findViewById(com.nm.famlink.R.id.etJoinCode);
        btnCreate = findViewById(com.nm.famlink.R.id.btnCreate);
        btnJoin = findViewById(com.nm.famlink.R.id.btnJoin);

        btnCreate.setOnClickListener(v -> {
            String name = etFamilyName.getText().toString().trim();
            if (name.isEmpty()) { Toast.makeText(this, "Enter name", Toast.LENGTH_SHORT).show(); return; }
            String id = UUID.randomUUID().toString().substring(0,8);
            Map<String,Object> f = new HashMap<>();
            f.put("id", id);
            f.put("name", name);
            f.put("createdAt", FieldValue.serverTimestamp());
            f.put("members", new HashMap<String,Object>());
            db.collection("families").document(id).set(f).addOnSuccessListener(a -> {
                String uid = mAuth.getCurrentUser().getUid();
                db.collection("families").document(id).update("members."+uid, true);
                db.collection("users").document(uid).update("familyId", id);
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            });
        });

        btnJoin.setOnClickListener(v -> {
            String code = etJoinCode.getText().toString().trim();
            if (code.isEmpty()) return;
            db.collection("families").document(code).get().addOnSuccessListener(doc -> {
                if (!doc.exists()) { Toast.makeText(this, "Family not found", Toast.LENGTH_SHORT).show(); return; }
                String uid = mAuth.getCurrentUser().getUid();
                db.collection("families").document(code).update("members."+uid, true);
                db.collection("users").document(uid).update("familyId", code);
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            });
        });
    }
}
