package com.yourname.famlink.activities;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class OtpVerifyActivity extends AppCompatActivity {
    private EditText etOtp;
    private Button btnVerify;
    private String verificationId;
    private FirebaseAuth mAuth;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(com.nm.famlink.R.layout.activity_otp);
        etOtp = findViewById(com.nm.famlink.R.id.etOtp);
        btnVerify = findViewById(com.nm.famlink.R.id.btnVerify);
        mAuth = FirebaseAuth.getInstance();
        verificationId = getIntent().getStringExtra("verificationId");

        btnVerify.setOnClickListener(v -> {
            String code = etOtp.getText().toString().trim();
            if (code.isEmpty()) return;
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // create minimal user doc in Firestore
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String,Object> u = new HashMap<>();
                    u.put("uid", mAuth.getCurrentUser().getUid());
                    u.put("phone", mAuth.getCurrentUser().getPhoneNumber());
                    db.collection("users").document(mAuth.getCurrentUser().getUid()).set(u);
                    startActivity(new Intent(this, OnboardingActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
