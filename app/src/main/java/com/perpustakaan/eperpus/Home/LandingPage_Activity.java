package com.perpustakaan.eperpus.Home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.perpustakaan.eperpus.R;
import com.perpustakaan.eperpus.User.Activity.Dashboard_User_Activity;

public class LandingPage_Activity extends AppCompatActivity {

    private Button btn_register, btn_login;
    private TextView txt_welcome, txt_welcome2;
    private FirebaseAuth mAuth;
    private ImageView imageView;
    Animation topAnimation, bottomAnimation;
    private static int SPLASH_SCREEN = 1500;
    DatabaseReference dbBuku;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landingpage);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getId();

        mAuth = FirebaseAuth.getInstance();

        topAnimation = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnimation = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        dbBuku = FirebaseDatabase
                .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                        .getReference();

        txt_welcome.setAnimation(topAnimation);
        imageView.setAnimation(topAnimation);
        txt_welcome2.setAnimation(topAnimation);
        btn_login.setAnimation(bottomAnimation);
        btn_register.setAnimation(bottomAnimation);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animationClick(view);
                startActivity(new Intent(LandingPage_Activity.this, Register_Activity.class));
                finish();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animationClick(view);
                startActivity(new Intent(LandingPage_Activity.this, Login_Activity.class));
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(LandingPage_Activity.this, Dashboard_User_Activity.class));
                    finish();
                }
            }, SPLASH_SCREEN);

        }
    }

    private void getId() {
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
        txt_welcome = findViewById(R.id.txt_welcome);
        txt_welcome2 = findViewById(R.id.txt_welcome2);
        imageView = findViewById(R.id.img_landingPage);
    }

    private void animationClick(View view){
        Context context = this;
        view.startAnimation(AnimationUtils.loadAnimation(context,R.anim.animation_click));
    }
}