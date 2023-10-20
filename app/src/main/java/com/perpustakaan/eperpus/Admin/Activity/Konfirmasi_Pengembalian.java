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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.perpustakaan.eperpus.R;
import com.perpustakaan.eperpus.User.Class.Peminjaman_Model;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Konfirmasi_Pengembalian extends AppCompatActivity {

    private TextView penulis,peminjam,npm,passcode, penerbit, kategori, halaman, txt_ringkasan,judul_buku, txt_penulis,
            txt_penerbit, txt_kategori, txt_halaman, txt_peminjam, txt_npm, txt_passcode;
    private RelativeLayout rl_keterangan, rl_ringkasan, rl_informasi;
    private Button btn_konfirmasi;
    private ImageView gambar_buku;
    private int ringkasanClick = 0;
    private int informasiClick = 0;
    public static String kodeBuku;
    private String Peminjam, Kategori, Passcode, Halaman, NamaPeminjam,Key, Key1, tanggalKembali, tanggalPinjam;
    private DatabaseReference dbBuku;
    int STOK;
    Peminjaman_Model peminjamanModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfirmasi_pengembalian);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbBuku = FirebaseDatabase
                .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();

        getID();

        rl_ringkasan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkRingkasan();
            }
        });

        rl_informasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInformasi();
            }
        });

        btn_konfirmasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickAnimation(view);
                getCurrentDate();
                AlertDialog alertDialog = new AlertDialog.Builder(Konfirmasi_Pengembalian.this)
                        .setMessage("Yakin konfirmasi pengembalian buku ini ?")
                        .setPositiveButton("Ya", null)
                        .setNegativeButton("Tidak", null)
                        .show();

                Button positiveButton = alertDialog.getButton(alertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int TotalStok = STOK + 1;
                        getKeranjangPinjam(new CallbackKeyPinjam() {
                            @Override
                            public void onCallBack(String KEY) {
                                if (!KEY.equals("empty")){
                                    dbBuku.child("KeranjangPinjam").child(Key).child(Key1).child("status").setValue("Buku Telah dikembalikan");
                                    dbBuku.child("KeranjangPinjam").child(Key).child(Key1).child("tanggalKembali").setValue(tanggalPinjam);
                                } else{
                                    dbBuku.child("Peminjaman").child(Passcode).child("status").setValue("Buku Telah dikembalikan");
                                    dbBuku.child("Peminjaman").child(Passcode).child("tanggalKembali").setValue(tanggalPinjam);
                                }
                                dbBuku.child("Buku").child(kodeBuku).child("stokBuku").setValue(String.valueOf(TotalStok));
                                Toast.makeText(Konfirmasi_Pengembalian.this, "Pengembalian buku berhasil dikonfirmasi", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Konfirmasi_Pengembalian.this, Dashboard_Admin.class));
                                alertDialog.dismiss();
                            }
                        });

                    }
                });
            }
        });

        getData();
    }

    private void getID() {
        penulis = findViewById(R.id.penulis);
        peminjam = findViewById(R.id.peminjam);
        npm = findViewById(R.id.npm);
        penerbit = findViewById(R.id.penerbit);
        kategori = findViewById(R.id.kategori);
        halaman = findViewById(R.id.halaman);
        txt_ringkasan = findViewById(R.id.txt_ringkasan);
        judul_buku = findViewById(R.id.judul_buku);
        rl_keterangan = findViewById(R.id.rl_keterangan);
        rl_informasi = findViewById(R.id.rl_informasi);
        rl_ringkasan = findViewById(R.id.rl_ringkasan);
        gambar_buku = findViewById(R.id.gambar_buku);
        btn_konfirmasi = findViewById(R.id.btn_konfirmasi);
        txt_penulis = findViewById(R.id.txt_penulis);
        txt_penerbit = findViewById(R.id.txt_penerbit);
        txt_kategori = findViewById(R.id.txt_kategori);
        txt_halaman = findViewById(R.id.txt_halaman);
        txt_peminjam = findViewById(R.id.txt_peminjam);
        txt_npm = findViewById(R.id.txt_npm);
    }

    private void checkRingkasan() {
        if (ringkasanClick == 0) {
            txt_ringkasan.setVisibility(View.GONE);
            ringkasanClick = 1;
        } else if (ringkasanClick == 1) {
            txt_ringkasan.setVisibility(View.VISIBLE);
            ringkasanClick = 0;
        }
    }

    private void checkInformasi() {
        if (informasiClick == 0){
            penulis.setVisibility(View.GONE);
            penerbit.setVisibility(View.GONE);
            kategori.setVisibility(View.GONE);
            halaman.setVisibility(View.GONE);
            rl_keterangan.setVisibility(View.GONE);
            passcode.setVisibility(View.GONE);
            npm.setVisibility(View.GONE);
            peminjam.setVisibility(View.GONE);
            informasiClick = 1;
        } else if (informasiClick == 1){
            penulis.setVisibility(View.VISIBLE);
            penerbit.setVisibility(View.VISIBLE);
            kategori.setVisibility(View.VISIBLE);
            halaman.setVisibility(View.VISIBLE);
            rl_keterangan.setVisibility(View.VISIBLE);
            passcode.setVisibility(View.VISIBLE);
            npm.setVisibility(View.VISIBLE);
            peminjam.setVisibility(View.VISIBLE);
            informasiClick = 0;
        }
    }

    public void getData(){
        final Object object = getIntent().getSerializableExtra("deskripsi_tunggu2");
        if (object instanceof Peminjaman_Model){
            peminjamanModel = (Peminjaman_Model) object;
        }

        if (peminjamanModel != null){
            judul_buku.setText(peminjamanModel.getJudulBuku());
            txt_penulis.setText(peminjamanModel.getPengarang());
            txt_npm.setText(peminjamanModel.getNpm());
            Passcode =peminjamanModel.getPasscode();
            kodeBuku = peminjamanModel.getKodeBuku();
            txt_peminjam.setText(peminjamanModel.getPeminjam());
            getDataBuku();
            Glide.with(getApplicationContext()).load(peminjamanModel.getGambar()).into(gambar_buku);
        }
    }


    private void getDataBuku(){
        dbBuku.child("Buku").child(kodeBuku).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Kategori = snapshot.child("kategori").getValue().toString();
                Halaman = snapshot.child("jumlahHalaman").getValue().toString();
                txt_penerbit.setText(snapshot.child("namaPenerbit").getValue().toString());
                txt_halaman.setText(Halaman);
                txt_kategori.setText(Kategori);
                STOK = Integer.parseInt(snapshot.child("stokBuku").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getKeranjangPinjam(CallbackKeyPinjam callbackKeyPinjam){
        dbBuku.child("KeranjangPinjam").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Key = dataSnapshot.getKey();
                        dbBuku.child("KeranjangPinjam").child(Key).orderByChild("kodeBuku").equalTo(kodeBuku).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                        Key1 = dataSnapshot1.getKey();
                                        callbackKeyPinjam.onCallBack(Key1);
                                    }
                                }else {
                                    Key1 = "empty";
                                    callbackKeyPinjam.onCallBack(Key1);
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

    private void getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        tanggalPinjam = sdf.format(calendar.getTime());
        calendar.add(Calendar.DATE, 7);
        tanggalKembali =  sdf.format(calendar.getTime());
    }

    private void clickAnimation(View view) {
        Context context  = this;
        view.startAnimation(AnimationUtils.loadAnimation(context,R.anim.animation_click));
    }

    public interface CallbackKeyPinjam{
        void onCallBack(String KEY);
    }
}