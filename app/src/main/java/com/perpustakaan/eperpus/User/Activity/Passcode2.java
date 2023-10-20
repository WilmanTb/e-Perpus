package com.perpustakaan.eperpus.User.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
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
import com.perpustakaan.eperpus.User.Class.Peminjaman_Model;
import com.perpustakaan.eperpus.User.Fragment.Keranjang_Pinjam;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Passcode2 extends AppCompatActivity {

    ArrayList<Peminjaman_Model> arrayPinjam;
    private Button btn_submit;
    private TextView tv_timer;
    private EditText et_passcode;
    private int passcodeNumber;
    private int randomInt;
    private DatabaseReference dbPeminjaman;
    private CountDownTimer countDownTimer;
    private FirebaseAuth userAuth;
    private String tanggalPinjam, tanggalKembali;
    private String Passcode, kodeBuku, judulBuku = "empty", pengarang = "empty", UID, gambar = "empty", npm = "empty", Key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode2);

        getID();
        getCurrentDate();
        getTIme();

        arrayPinjam = new ArrayList<>();

        dbPeminjaman = FirebaseDatabase
                .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();

        userAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = userAuth.getCurrentUser();
        UID = currentUser.getUid();

        passcodeNumber = Keranjang_Pinjam.passcodeNumber;

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkEmpty();
            }
        });
    }

    private void getID(){
        btn_submit = findViewById(R.id.btn_submit);
        et_passcode = findViewById(R.id.et_passcode);
        tv_timer = findViewById(R.id.tv_timer);
    }

    private void getTIme(){
        countDownTimer = new CountDownTimer(120000, 1000) {
            @Override
            public void onTick(long l) {
                tv_timer.setText("Waktu : "+l/1000);
            }

            @Override
            public void onFinish() {
                btn_submit.setEnabled(false);
                et_passcode.setEnabled(false);
            }
        }.start();
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

    private void sendPeminjaman() {
        DatabaseReference dbPinjamBuku = FirebaseDatabase
                .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("KeranjangPinjam").child(UID);

        dbPinjamBuku.orderByChild("peminjam").equalTo(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Key = dataSnapshot.getKey();
                        dbPeminjaman.child("KeranjangPinjam").child(UID).child(Key).child("passcode").setValue(String.valueOf(passcodeNumber));
                        dbPeminjaman.child("KeranjangPinjam").child(UID).child(Key).child("tanggalPeminjaman").setValue(tanggalPinjam);
                        dbPeminjaman.child("KeranjangPinjam").child(UID).child(Key).child("tanggalKembali").setValue(tanggalKembali);
                        dbPeminjaman.child("KeranjangPinjam").child(UID).child(Key).child("diminta").setValue("true");
                    }
                    startActivity(new Intent(Passcode2.this, Dashboard_User_Activity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        tanggalPinjam = sdf.format(calendar.getTime());
        calendar.add(Calendar.DATE, 7);
        tanggalKembali = sdf.format(calendar.getTime());
    }
}