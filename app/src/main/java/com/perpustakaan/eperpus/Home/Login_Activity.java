package com.perpustakaan.eperpus.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.perpustakaan.eperpus.Admin.Activity.Dashboard_Admin;
import com.perpustakaan.eperpus.R;
import com.perpustakaan.eperpus.User.Activity.Dashboard_User_Activity;

public class Login_Activity extends AppCompatActivity {

    private Button btn_Masuk;
    private EditText etEmail, etPassword;
    private String Email, Password, Uid, mPassword;
    private TextView txt_register;
    private ImageView btn_back;
    private FirebaseAuth loginAuth;
    private DatabaseReference dbUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getId();

        loginAuth = FirebaseAuth.getInstance();
        dbUser = FirebaseDatabase
                .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users");

        btn_Masuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickAnimation(view);
                Email = etEmail.getText().toString();
                Password = etPassword.getText().toString();
                if (Email.isEmpty() || Password.isEmpty()) {
                    Toast.makeText(Login_Activity.this, "Form tidak boleh kosong", Toast.LENGTH_SHORT).show();
                } else if (Email.equals("admin") && Password.equals("admin")) {
                    startActivity(new Intent(Login_Activity.this, Dashboard_Admin.class));
                    finish();
                } else {
                    getUid(new FirebaseCallback() {
                        @Override
                        public void onCallback(String Uid) {
                            checkPassword(Uid);
                        }
                    });
                }
            }
        });

        txt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickAnimation(view);
                startActivity(new Intent(Login_Activity.this, Register_Activity.class));
                finish();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickAnimation(view);
                startActivity(new Intent(Login_Activity.this, LandingPage_Activity.class));
                finish();
            }
        });
    }

    private void getId() {
        btn_Masuk = findViewById(R.id.btn_Masuk);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        txt_register = findViewById(R.id.txt_register);
        btn_back = findViewById(R.id.btn_back);
    }

    private void loginUser() {
        loginAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(Login_Activity.this, Dashboard_User_Activity.class));
                    finish();
                } else {
                    Toast.makeText(Login_Activity.this, "Login gagal\nMohon cek koneksi internet anda", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getUid(FirebaseCallback firebaseCallback) {
        Email = etEmail.getText().toString();
        dbUser.orderByChild("email")
                .equalTo(Email)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                if (dataSnapshot.exists()) {
                                    Uid = dataSnapshot.getKey();
                                }
                                break;
                            }
                            firebaseCallback.onCallback(Uid);
                        } else {
                            Toast.makeText(Login_Activity.this, "Email tidak terdaftar", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void checkPassword(String UID) {
        Password = etPassword.getText().toString();
        dbUser.child(UID).child("password").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mPassword = snapshot.getValue().toString();
                if (mPassword.equals(Password)) {
                    loginUser();
                } else {
                    Toast.makeText(Login_Activity.this, "Password salah", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private interface FirebaseCallback {
        void onCallback(String Uid);
    }

    private void clickAnimation(View view) {
        Context context = this;
        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.animation_click));
    }
}