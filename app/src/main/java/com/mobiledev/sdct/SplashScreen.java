package com.mobiledev.sdct;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashScreen extends AppCompatActivity {
    Animation rotateAnimation;
    Animation alphaAnimation;
    Animation scaleAnimation;
    Animation translateAnimation;
    ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        //1. get animation from ressource
        rotateAnimation = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.rotate_animation);
        alphaAnimation = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.alpha_animation);
        scaleAnimation = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.scale_animation);
        translateAnimation = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.translate_animation);
        //2. get ImageView
        image = findViewById(R.id.logo);
        //3. apply animation in image
        image.startAnimation(alphaAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start LoginActivity
                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                // Finish splash screen activity
                finish();
            }
        }, 5000);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}