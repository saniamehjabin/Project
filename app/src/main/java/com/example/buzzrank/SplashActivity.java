package com.example.buzzrank;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView splashImage = findViewById(R.id.splash);

        // Load fade-in animation
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in_scale);

        // Apply fade-in animation
        splashImage.startAnimation(fadeIn);

        // Delay before transitioning to the next screen
        new Handler().postDelayed(() -> {
            // Start SignInActivity after fade-in completes
            Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();  // Ensure SplashActivity is finished
        }, 4000);  // Adjust this delay based on your desired splash screen duration
    }
}
