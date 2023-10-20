package com.perpustakaan.eperpus.Admin.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.perpustakaan.eperpus.Admin.Class.Anggota_Model;
import com.perpustakaan.eperpus.R;
import com.perpustakaan.eperpus.User.Class.Buku_Model;

public class Desc_Anggota extends AppCompatActivity {

    ImageView foto_user;
    TextView nama_user, npm_user, txt_alamat, txt_email, txt_jenisKelamin, txt_noHp, txt_tanggalLahir;
    Button btn_hapus;
    DatabaseReference dbUsers;
    Anggota_Model anggotaModel;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desc_anggota);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbUsers = FirebaseDatabase
                .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users");

        getID();
        getData();
        deleteUser();

        btn_hapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickAnimation(view);
                AlertDialog alertDialog = new AlertDialog.Builder(Desc_Anggota.this)
                        .setMessage("Yakin menghapus user ini ?")
                        .setPositiveButton("Ya", null)
                        .setNegativeButton("Tidak", null)
                        .show();

                Button positiveButton = alertDialog.getButton(alertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dbUsers.child(key).setValue(null);
                        Toast.makeText(Desc_Anggota.this, "User berhasil dihapus", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Desc_Anggota.this, Dashboard_Admin.class));
                        alertDialog.dismiss();
                    }
                });
            }
        });
    }

    private void getID() {
        nama_user = findViewById(R.id.nama_user);
        npm_user = findViewById(R.id.npm_user);
        txt_alamat = findViewById(R.id.txt_alamat);
        txt_email = findViewById(R.id.txt_email);
        txt_jenisKelamin = findViewById(R.id.txt_jenisKelamin);
        txt_noHp = findViewById(R.id.txt_noHp);
        txt_tanggalLahir = findViewById(R.id.txt_tanggalLahir);
        btn_hapus = findViewById(R.id.btn_hapus);
        foto_user = findViewById(R.id.foto_user);
    }

    public void getData(){
        final Object object =getIntent().getSerializableExtra("desc_anggota");
        if (object instanceof Anggota_Model){
            anggotaModel = (Anggota_Model) object;
        }

        if (anggotaModel != null){
           nama_user.setText(anggotaModel.getNama());
           npm_user.setText(anggotaModel.getNpm());
           txt_alamat.setText(anggotaModel.getAlamat());
           txt_email.setText(anggotaModel.getEmail());
           txt_jenisKelamin.setText(anggotaModel.getJenisKelamin());
           txt_noHp.setText(anggotaModel.getNoHp());
           txt_tanggalLahir.setText(anggotaModel.getTanggalLahir());
           if (!anggotaModel.getFoto().equals("empty")){
               Glide.with(getApplicationContext()).load(anggotaModel.getFoto()).into(foto_user);
           }
        }
    }

    private void clickAnimation(View view) {
        Context context  = this;
        view.startAnimation(AnimationUtils.loadAnimation(context,R.anim.animation_click));
    }

    private void deleteUser(){
        dbUsers.orderByChild("npm").equalTo(anggotaModel.getNpm()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        key = dataSnapshot.getKey();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}