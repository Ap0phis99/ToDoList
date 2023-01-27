package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Splash extends AppCompatActivity {

    private final int delay = 5000;//delay del splash
    private ImageView imageView;
    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();


        initializeView();
        animateLogo();
        changeActivity();
    }

    private void initializeView() {
        imageView = findViewById(R.id.circularImageView);
        textView = findViewById(R.id.loading);
    }
    private void animateLogo() {
        Animation fading = AnimationUtils.loadAnimation(this,R.anim.fade_in);
        fading.setDuration(delay);
        imageView.startAnimation(fading);
        textView.startAnimation(fading);
        imageView.animate().rotation(1000).setDuration(delay).start();
    }

    private void changeActivity() {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(Splash.this, Login.class));
            finish();
        }, delay);
    }
}