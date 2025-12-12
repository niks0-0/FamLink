package com.yourname.famlink.activities;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends AppCompatActivity {
    private EditText etPhone;
    private Button btnSend;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(com.nm.famlink.R.layout.activity_phone_auth);
        etPhone = findViewById(com.nm.famlink.R.id.etPhone);
        btnSend = findViewById(com.nm.famlink.R.id.btnSend);
        mAuth = FirebaseAuth.getInstance();

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override public void onVerificationCompleted(PhoneAuthCredential credential) {
                mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(PhoneAuthActivity.this, OnboardingActivity.class));
                        finish();
                    }
                });
            }
            @Override public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(PhoneAuthActivity.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            @Override public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                Intent i = new Intent(PhoneAuthActivity.this, OtpVerifyActivity.class);
                i.putExtra("verificationId", verificationId);
                i.putExtra("phone", etPhone.getText().toString());
                startActivity(i);
            }
        };

        btnSend.setOnClickListener(v -> {
            String phone = etPhone.getText().toString().trim();
            if (phone.isEmpty()) { Toast.makeText(PhoneAuthActivity.this, "Enter phone", Toast.LENGTH_SHORT).show(); return; }
            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                    .setPhoneNumber(phone)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(PhoneAuthActivity.this)
                    .setCallbacks(callbacks)
                    .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
        });
    }
}
