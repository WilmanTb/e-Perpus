package com.perpustakaan.eperpus.User.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import com.perpustakaan.eperpus.R;
import com.perpustakaan.eperpus.User.Class.Buku_Model;
import com.perpustakaan.eperpus.User.Class.Peminjaman_Model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Pengembalian extends AppCompatActivity {

    private ImageView gambar_buku;
    private TextView judul_buku, pengarang_buku, denda;
    private Button btn_kembalikan;
    private DatabaseReference dbReturn;
    private Peminjaman_Model peminjamanModel;
    private String Status = "Menunggu konfirmasi pengembalian", passCode;
    private String tanggalPinjam, tanggalKembali, x, kodeBuku, Key, Key1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengembalian);

        getID();

        dbReturn = FirebaseDatabase
                .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();

        getData();
        getCurrentDate();
        dendaPinjam(new CallbackDenda() {
            @Override
            public void onCallback(String DENDA) {
                denda.setText(DENDA);
            }
        });

        btn_kembalikan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animationClick(view);
                AlertDialog alertDialog = new AlertDialog.Builder(Pengembalian.this)
                        .setMessage("Yakin mengembalikan buku ini ?")
                        .setPositiveButton("Ya", null)
                        .setNegativeButton("Tidak", null)
                        .show();

                Button positiveButton = alertDialog.getButton(alertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getKeranjangPinjam(new CallbackKeranjang() {
                            @Override
                            public void onCallBack(String KEY) {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                try {
                                    Date x = sdf.parse(peminjamanModel.getTanggalKembali());
                                    Date y = sdf.parse(tanggalPinjam);
                                    if (y.after(x)){
                                        long diff = (y.getTime() - x.getTime())/86400000;
                                        long Denda = diff * 500;
                                        if (KEY.equals("empty")) {
                                            dbReturn.child("Peminjaman").child(passCode).child("denda").setValue("Rp" + Denda);
                                        } else {
                                            dbReturn.child("KeranjangPinjam").child(Key).child(Key1).child("denda").setValue("Rp" + Denda);
                                        }
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if (KEY.equals("empty")) {
                                    dbReturn.child("Peminjaman").child(passCode).child("status").setValue(Status);
                                }else{
                                    dbReturn.child("KeranjangPinjam").child(Key).child(Key1).child("status").setValue("Menunggu konfirmasi pengembalian");
                                }
                                Toast.makeText(Pengembalian.this, "Permintaan pengembalian buku sudah dikirim\nMohon mengembalikan buku ke perpustakaan", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Pengembalian.this, Dashboard_User_Activity.class));
                                finish();
                            }
                        });
                    }
                });
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        dendaPinjam(new CallbackDenda() {
            @Override
            public void onCallback(String DENDA) {
                denda.setText("Rp"+DENDA);
            }
        });
    }

    private void getID() {
        gambar_buku = findViewById(R.id.gambar_buku);
        judul_buku = findViewById(R.id.judul_buku);
        pengarang_buku = findViewById(R.id.pengarang_buku);
        btn_kembalikan = findViewById(R.id.btn_kembalikan);
        denda = findViewById(R.id.denda);
    }

    public void getData() {
        final Object object = getIntent().getSerializableExtra("kembali");
        if (object instanceof Peminjaman_Model) {
            peminjamanModel = (Peminjaman_Model) object;
        }

        if (peminjamanModel != null) {
            judul_buku.setText(peminjamanModel.getJudulBuku());
            pengarang_buku.setText(peminjamanModel.getPengarang());
            passCode = peminjamanModel.getPasscode();
            denda.setText(peminjamanModel.getDenda());
            kodeBuku = peminjamanModel.getKodeBuku();
            Glide.with(getApplicationContext()).load(peminjamanModel.getGambar()).into(gambar_buku);
        }
    }

    private void animationClick(View view) {
        Context context = this;
        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.animation_click));
    }

    private void getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        tanggalPinjam = sdf.format(calendar.getTime());
        calendar.add(Calendar.DATE, 7);
        tanggalKembali =  sdf.format(calendar.getTime());
    }


    private void dendaPinjam(CallbackDenda callbackDenda){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date x = sdf.parse(peminjamanModel.getTanggalKembali());
            Date y = sdf.parse(tanggalPinjam);
            if (y.after(x)){
                long diff = (y.getTime() - x.getTime())/86400000;
                long Denda = diff * 500;
                dbReturn.child("Peminjaman").child(passCode).child("denda").setValue("Rp"+Denda);
                callbackDenda.onCallback(String.valueOf(Denda));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void getKeranjangPinjam(CallbackKeranjang callbackKeranjang){
        dbReturn.child("KeranjangPinjam").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Key = dataSnapshot.getKey();
                        dbReturn.child("KeranjangPinjam").child(Key).orderByChild("kodeBuku").equalTo(kodeBuku).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                        Key1 = dataSnapshot1.getKey();
                                        callbackKeranjang.onCallBack(Key1);

                                    }
                                }else{
                                    Key1 = "empty";
                                    callbackKeranjang.onCallBack(Key1);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private interface CallbackDenda{
        void onCallback(String DENDA);
    }

    public interface CallbackKeranjang{
        void onCallBack(String KEY);
    }

}