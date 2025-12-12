package com.nm.famlink.activities;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.nm.famlink.R.layout.activity_splash);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        new android.os.Handler().postDelayed(() -> {
            if (auth.getCurrentUser() != null) {
                startActivity(new Intent(this, com.yourname.famlink.activities.HomeActivity.class));
            } else {
                startActivity(new Intent(this, com.yourname.famlink.activities.PhoneAuthActivity.class));
            }
            finish();
        }, 700);
    }
}
