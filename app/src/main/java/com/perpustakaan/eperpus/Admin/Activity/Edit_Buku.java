package com.perpustakaan.eperpus.Admin.Activity;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.perpustakaan.eperpus.R;
import com.perpustakaan.eperpus.User.Activity.Desc_Buku;
import com.perpustakaan.eperpus.User.Activity.Passcode;
import com.perpustakaan.eperpus.User.Class.Buku_Model;

public class Edit_Buku extends AppCompatActivity {

    private TextView penulis, penerbit, kategori, halaman, txt_ringkasan,judul_buku, txt_penulis, txt_penerbit, txt_kategori, txt_halaman;
    private RelativeLayout rl_keterangan, rl_ringkasan, rl_informasi;
    private Button btn_edit, btn_hapus;
    private ImageView gambar_buku;
    private int ringkasanClick = 0;
    private int informasiClick = 0;
    private DatabaseReference dbBuku;
    public static String kodeBuku;
    Buku_Model bukuModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_buku);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getID();

        getData();

        dbBuku = FirebaseDatabase
                .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();

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

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickAnimation(view);
                AlertDialog alertDialog = new AlertDialog.Builder(Edit_Buku.this)
                        .setMessage("Yakin edit buku ini ?")
                        .setPositiveButton("Ya", null)
                        .setNegativeButton("Tidak", null)
                        .show();

                Button positiveButton = alertDialog.getButton(alertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Edit_Buku.this, Edit_Data_Buku.class));
                        alertDialog.dismiss();
                    }
                });
            }
        });

        btn_hapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickAnimation(view);
                AlertDialog alertDialog = new AlertDialog.Builder(Edit_Buku.this)
                        .setMessage("Yakin menghapus buku ini ?")
                        .setPositiveButton("Ya", null)
                        .setNegativeButton("Tidak", null)
                        .show();

                Button positiveButton = alertDialog.getButton(alertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dbBuku.child("Buku").child(kodeBuku).setValue(null);
                        Toast.makeText(Edit_Buku.this, "Buku berhasil dihapus", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Edit_Buku.this, Dashboard_Admin.class));
                        alertDialog.dismiss();
                    }
                });
            }
        });


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
        btn_edit = findViewById(R.id.btn_edit);
        btn_hapus = findViewById(R.id.btn_hapus);
        txt_penulis = findViewById(R.id.txt_penulis);
        txt_penerbit = findViewById(R.id.txt_penerbit);
        txt_kategori = findViewById(R.id.txt_kategori);
        txt_halaman = findViewById(R.id.txt_halaman);
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
            informasiClick = 1;
        } else if (informasiClick == 1){
            penulis.setVisibility(View.VISIBLE);
            penerbit.setVisibility(View.VISIBLE);
            kategori.setVisibility(View.VISIBLE);
            halaman.setVisibility(View.VISIBLE);
            rl_keterangan.setVisibility(View.VISIBLE);
            informasiClick = 0;
        }
    }

    public void getData(){
        final Object object =getIntent().getSerializableExtra("deskripsi");
        if (object instanceof Buku_Model){
            bukuModel = (Buku_Model) object;
        }

        if (bukuModel != null){
            judul_buku.setText(bukuModel.getJudulBuku());
            txt_penulis.setText(bukuModel.getPengarang());
            txt_penerbit.setText(bukuModel.getNamaPenerbit());
            txt_kategori.setText(bukuModel.getKategori());
            txt_halaman.setText(bukuModel.getJumlahHalaman());
            kodeBuku = bukuModel.getKodeBuku();
            Glide.with(getApplicationContext()).load(bukuModel.getGambar()).into(gambar_buku);
        }
    }

    private void clickAnimation(View view) {
        Context context  = this;
        view.startAnimation(AnimationUtils.loadAnimation(context,R.anim.animation_click));
    }
}