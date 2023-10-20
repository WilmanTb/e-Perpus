package com.perpustakaan.eperpus.User.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContentInfo;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.perpustakaan.eperpus.R;
import com.perpustakaan.eperpus.User.Class.Buku_Model;

import java.util.HashMap;
import java.util.Random;

import papaya.in.sendmail.SendMail;

public class Desc_Buku extends AppCompatActivity {

    private TextView penulis, penerbit, kategori, halaman, txt_ringkasan, judul_buku, txt_penulis, txt_penerbit, txt_kategori, txt_halaman;
    private RelativeLayout rl_keterangan, rl_ringkasan, rl_informasi;
    private Button btn_pinjam, btn_keranjangPinjam;
    private ImageView gambar_buku;
    private int ringkasanClick = 0;
    private int informasiClick = 0;
    public static int passcodeNumber;
    public static String kodeBuku;
    private FirebaseAuth userAuth;
    String Email, Stok, Npm = "empty", UID;
    Buku_Model bukuModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desc_buku);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getID();

        userAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = userAuth.getCurrentUser();
        UID = firebaseUser.getUid();

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

        getEmail();

        btn_pinjam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickAnimation(view);
                if (!Stok.equals("0")) {
                    generatePasscode();
                    SendMail mail = new SendMail("rizki01tebe@gmail.com", "wlbvfhpnbyectbpw",
                            Email,
                            "Passcode Peminjaman Buku",
                            "BERIKUT PASSCODE PEMINJAMAN BUKU ANDA\n\n\n" + passcodeNumber);

                    AlertDialog alertDialog = new AlertDialog.Builder(Desc_Buku.this)
                            .setMessage("Yakin meminjam buku ini ?")
                            .setPositiveButton("Ya", null)
                            .setNegativeButton("Tidak", null)
                            .show();

                    Button positiveButton = alertDialog.getButton(alertDialog.BUTTON_POSITIVE);
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mail.execute();
                            Toast.makeText(Desc_Buku.this, "Passcode berhasil dikirim ke email anda", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Desc_Buku.this, Passcode.class));
                            alertDialog.dismiss();
                        }
                    });
                } else {
                    Toast.makeText(Desc_Buku.this, "Stok buku habis!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_keranjangPinjam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickAnimation(view);
                if (!Stok.equals("0")) {
                    AlertDialog alertDialog = new AlertDialog.Builder(Desc_Buku.this)
                            .setMessage("Yakin tambah buku ini ke keranjang peminjaman ?")
                            .setPositiveButton("Ya", null)
                            .setNegativeButton("Tidak", null)
                            .show();

                    Button positiveButton = alertDialog.getButton(alertDialog.BUTTON_POSITIVE);
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            keranjangPinjam();
                            alertDialog.dismiss();
                        }
                    });
                } else {
                    Toast.makeText(Desc_Buku.this, "Stok buku habis!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        getData();

    }

    private void getID() {
        penulis = findViewById(R.id.penulis);
        penerbit = findViewById(R.id.penerbit);
        kategori = findViewById(R.id.kategori);
        halaman = findViewById(R.id.halaman);
        txt_ringkasan = findViewById(R.id.txt_ringkasan);
        judul_buku = findViewById(R.id.judul_buku);
        rl_keterangan = findViewById(R.id.rl_keterangan);
        rl_informasi = findViewById(R.id.rl_informasi);
        rl_ringkasan = findViewById(R.id.rl_ringkasan);
        gambar_buku = findViewById(R.id.gambar_buku);
        btn_pinjam = findViewById(R.id.btn_pinjam);
        txt_penulis = findViewById(R.id.txt_penulis);
        txt_penerbit = findViewById(R.id.txt_penerbit);
        txt_kategori = findViewById(R.id.txt_kategori);
        txt_halaman = findViewById(R.id.txt_halaman);
        btn_keranjangPinjam = findViewById(R.id.btn_keranjangPinjam);
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
        if (informasiClick == 0) {
            penulis.setVisibility(View.GONE);
            penerbit.setVisibility(View.GONE);
            kategori.setVisibility(View.GONE);
            halaman.setVisibility(View.GONE);
            rl_keterangan.setVisibility(View.GONE);
            informasiClick = 1;
        } else if (informasiClick == 1) {
            penulis.setVisibility(View.VISIBLE);
            penerbit.setVisibility(View.VISIBLE);
            kategori.setVisibility(View.VISIBLE);
            halaman.setVisibility(View.VISIBLE);
            rl_keterangan.setVisibility(View.VISIBLE);
            informasiClick = 0;
        }
    }

    public void getData() {
        final Object object = getIntent().getSerializableExtra("deskripsi");
        if (object instanceof Buku_Model) {
            bukuModel = (Buku_Model) object;
        }

        if (bukuModel != null) {
            judul_buku.setText(bukuModel.getJudulBuku());
            txt_penulis.setText(bukuModel.getPengarang());
            txt_penerbit.setText(bukuModel.getNamaPenerbit());
            txt_kategori.setText(bukuModel.getKategori());
            txt_halaman.setText(bukuModel.getJumlahHalaman());
            Stok = bukuModel.getStokBuku();
            kodeBuku = bukuModel.getKodeBuku();
            Glide.with(getApplicationContext()).load(bukuModel.getGambar()).into(gambar_buku);
        }
    }

    private void clickAnimation(View view) {
        Context context = this;
        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.animation_click));
    }

    private void generatePasscode() {
        final int min = 100000;
        final int max = 1000000;
        final int random = new Random().nextInt((max - min) + 1) + min;
        passcodeNumber = random;
    }

    private void getEmail() {
        FirebaseUser currentUser = userAuth.getCurrentUser();
        String UID = currentUser.getUid();
        DatabaseReference dbUSer = FirebaseDatabase
                .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users");

        dbUSer.child(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Email = snapshot.child("email").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void keranjangPinjam() {
        DatabaseReference dbPinjamBuku = FirebaseDatabase
                .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("KeranjangPinjam");

        DatabaseReference dbBuku = FirebaseDatabase
                .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Buku");

        dbBuku.child(kodeBuku).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                getNpm(new Callback() {
                    @Override
                    public void onCallback(String NPM) {
                        String judulBuku = snapshot.child("judulBuku").getValue().toString();
                        String pengarang = snapshot.child("pengarang").getValue().toString();
                        String gambar = snapshot.child("gambar").getValue().toString();
                        HashMap hashMap = new HashMap<>();
                        hashMap.put("peminjam", UID);
                        hashMap.put("kodeBuku", kodeBuku);
                        hashMap.put("judulBuku", judulBuku);
                        hashMap.put("pengarang", pengarang);
                        hashMap.put("gambar", gambar);
                        hashMap.put("npm", Npm);
                        hashMap.put("tanggalPeminjaman", "empty");
                        hashMap.put("tanggalKembali", "empty");
                        hashMap.put("status", "Menunggu dikonfirmasi admin");
                        hashMap.put("denda", "Rp 0");
                        hashMap.put("passcode","empty");
                        hashMap.put("diminta", "false");
                        dbPinjamBuku.child(UID).push().setValue(hashMap);
                        startActivity(new Intent(Desc_Buku.this, Dashboard_User_Activity.class));
                        finish();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getNpm(Callback callback) {
        Toast.makeText(this, UID, Toast.LENGTH_SHORT).show();
        DatabaseReference dbUsers = FirebaseDatabase
                .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users");
        dbUsers.child(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Npm = snapshot.child("npm").getValue().toString();
                callback.onCallback(Npm);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface Callback{
        void onCallback(String NPM);
    }
}