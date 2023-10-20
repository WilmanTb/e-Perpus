package com.perpustakaan.eperpus.User.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.perpustakaan.eperpus.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class Passcode extends AppCompatActivity {

    private Button btn_submit;
    private EditText et_passcode;
    private TextView tv_timer;
    private String Passcode, kodeBuku, judulBuku = "empty", pengarang = "empty", UID, gambar = "empty", npm = "empty";
    private int passcodeNumber;
    private int randomInt;
    private DatabaseReference dbPeminjaman;
    private FirebaseAuth userAuth;
    private String tanggalPinjam, tanggalKembali;
    private CountDownTimer countDownTimer;
    private int timerValue = 120;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);

        getID();
        generatePasscode();
        getCurrentDate();
        getTime();

        dbPeminjaman = FirebaseDatabase
                .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();

        userAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = userAuth.getCurrentUser();
        UID = currentUser.getUid();

        passcodeNumber = Desc_Buku.passcodeNumber;
        kodeBuku = Desc_Buku.kodeBuku;

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animationClick(view);
                checkEmpty();
            }
        });
    }
    private void getID(){
        btn_submit = findViewById(R.id.btn_submit);
        et_passcode = findViewById(R.id.et_passcode);
        tv_timer = findViewById(R.id.tv_timer);
    }

    private void checkEmpty(){
        Passcode = et_passcode.getText().toString();
        if (Passcode.isEmpty()){
            Toast.makeText(this, "Passcode tidak boleh kosong", Toast.LENGTH_SHORT).show();
        } else {
            checkPassCode();
        }
    }

    private void checkPassCode(){
        if (!Passcode.equals(String.valueOf(passcodeNumber))){
            Toast.makeText(this,"Passcode salah", Toast.LENGTH_SHORT).show();
        } else {
            sendPeminjaman();
        }
    }

    private void getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        tanggalPinjam = sdf.format(calendar.getTime());
        calendar.add(Calendar.DATE, 7);
        tanggalKembali = sdf.format(calendar.getTime());
    }

//    <------FUNGSI TIMER PASSCODE-------->
    private void getTime(){
        countDownTimer = new CountDownTimer(120000, 1000) {
            @Override
            public void onTick(long l) {
                tv_timer.setText("Waktu :" + l/1000);
            }

            @Override
            public void onFinish() {
                btn_submit.setEnabled(false);
                et_passcode.setEnabled(false);
            }
        }.start();
    }

    private void sendPeminjaman(){

        DatabaseReference dbPinjamBuku = FirebaseDatabase
                .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Peminjaman").child(String.valueOf(passcodeNumber));


        dbPeminjaman.child("Buku").orderByChild("kodeBuku").equalTo(kodeBuku).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DatabaseReference dbRef = FirebaseDatabase
                            .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                            .getReference("Buku");

                    dbRef.child(kodeBuku).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            getNpm();
                            judulBuku = snapshot.child("judulBuku").getValue().toString();
                            pengarang = snapshot.child("pengarang").getValue().toString();
                            gambar = snapshot.child("gambar").getValue().toString();
                            HashMap hashMap = new HashMap<>();
                            hashMap.put("peminjam", UID);
                            hashMap.put("kodeBuku", kodeBuku);
                            hashMap.put("tanggalPeminjaman", tanggalPinjam);
                            hashMap.put("tanggalKembali", tanggalKembali);
                            hashMap.put("passcode", String.valueOf(passcodeNumber));
                            hashMap.put("status", "Menunggu dikonfirmasi admin");
                            hashMap.put("denda", "Rp 0");
                            hashMap.put("judulBuku",judulBuku);
                            hashMap.put("pengarang",pengarang);
                            hashMap.put("gambar", gambar);
                            hashMap.put("npm", npm);
                            dbPinjamBuku.setValue(hashMap);
                            Toast.makeText(Passcode.this, "Buku berhasil di pinjam\nSilahkan cek menu Daftar tunggu", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Passcode.this, Dashboard_User_Activity.class));
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getNpm(){
        dbPeminjaman.child("Users").child(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                npm = snapshot.child("npm").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generatePasscode(){
        final int min = 100000;
        final int max = 1000000;
        final int random  = new Random().nextInt((max - min) +1 ) + min;
        randomInt = random;
    }

    private void animationClick(View view){
        Context context = this;
        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.animation_click));
    }



}