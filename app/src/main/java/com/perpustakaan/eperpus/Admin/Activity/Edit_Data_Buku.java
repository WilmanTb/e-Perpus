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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.perpustakaan.eperpus.R;
import com.perpustakaan.eperpus.User.Activity.Desc_Buku;
import com.perpustakaan.eperpus.User.Activity.Passcode;
import com.perpustakaan.eperpus.User.Class.Buku_Model;

public class Edit_Data_Buku extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText et_bahasa, et_stok, et_halaman, et_judul, et_pengarang, et_terbit;
    private Spinner spin_kategori;
    private Button btn_submit;
    private DatabaseReference dbBuku;
    private String kodeBuku, Bahasa, Stok, Halaman, Judul, Pengarang, Terbit,Kategori,Gambar, penerbitBuku;
    ArrayAdapter<CharSequence> adapterKategori;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data_buku);

        dbBuku = FirebaseDatabase
                .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getID();
        spinKategori();
        getDataBuku();


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickAnimation(view);
                AlertDialog alertDialog = new AlertDialog.Builder(Edit_Data_Buku.this)
                        .setMessage("Yakin menyimpan data buku ini ?")
                        .setPositiveButton("Ya", null)
                        .setNegativeButton("Tidak", null)
                        .show();

                Button positiveButton = alertDialog.getButton(alertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        inputData();
                        Toast.makeText(Edit_Data_Buku.this, "Data buku berhasil disimpan", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Edit_Data_Buku.this, Dashboard_Admin.class));
                        alertDialog.dismiss();
                    }
                });
            }
        });

    }

    private void getID() {
        et_bahasa = findViewById(R.id.et_bahasa);
        et_halaman = findViewById(R.id.et_halaman);
        et_judul = findViewById(R.id.et_judulBuku);
        et_pengarang = findViewById(R.id.et_pengarang);
        et_stok = findViewById(R.id.et_stok);
        et_terbit = findViewById(R.id.et_terbit);
        spin_kategori = findViewById(R.id.spin_kategori);
        btn_submit = findViewById(R.id.btn_submit);
    }

    private void setString() {
        Bahasa = et_bahasa.getText().toString();
        Halaman = et_halaman.getText().toString();
        Judul = et_judul.getText().toString();
        Pengarang = et_pengarang.getText().toString();
        Stok = et_stok.getText().toString();
        Terbit = et_terbit.getText().toString();
    }

    private void spinKategori(){
        adapterKategori = ArrayAdapter.createFromResource(this, R.array.KategoriBuku, android.R.layout.simple_spinner_item);
        adapterKategori.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_kategori.setAdapter(adapterKategori);
        spin_kategori.setOnItemSelectedListener(this);
    }

    private void getDataBuku() {
        kodeBuku = Edit_Buku.kodeBuku;
        dbBuku.child("Buku").child(kodeBuku).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    et_bahasa.setText(snapshot.child("bahasa").getValue().toString());
                    et_halaman.setText(snapshot.child("jumlahHalaman").getValue().toString());
                    et_judul.setText(snapshot.child("judulBuku").getValue().toString());
                    et_stok.setText(snapshot.child("stokBuku").getValue().toString());
                    et_pengarang.setText(snapshot.child("pengarang").getValue().toString());
                    et_terbit.setText(snapshot.child("tahunTerbit").getValue().toString());
                    penerbitBuku = snapshot.child("namaPenerbit").getValue().toString();
                    Gambar = snapshot.child("gambar").getValue().toString();
                    int spinnerPosition = adapterKategori.getPosition(snapshot.child("kategori").getValue().toString());
                    spin_kategori.setSelection(spinnerPosition);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        spin_kategori = (Spinner) adapterView;
        Kategori = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void inputData(){
        setString();
        Buku_Model bukuModel = new Buku_Model(Judul, Stok, Pengarang, Gambar, Bahasa, String.valueOf(kodeBuku), Terbit,penerbitBuku, Kategori, Halaman);
        dbBuku.child("Buku").child(kodeBuku).setValue(bukuModel);
    }

    private void clickAnimation(View view){
        Context context = this;
        view.startAnimation(AnimationUtils.loadAnimation(context,R.anim.animation_click));
    }
}