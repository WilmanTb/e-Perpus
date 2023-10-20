package com.perpustakaan.eperpus.Admin.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.perpustakaan.eperpus.R;

import java.util.HashMap;

public class Tambah_Kategori extends AppCompatActivity {

    private EditText et_kategori;
    private Button btn_submit;
    private String Kategori, childCount;
    private DatabaseReference dbKategori;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_kategori);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbKategori = FirebaseDatabase
                .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Daftar Kategori");

        getID();

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(Tambah_Kategori.this)
                        .setMessage("Yakin menyimpan data buku ini ?")
                        .setPositiveButton("Ya", null)
                        .setNegativeButton("Tidak", null)
                        .show();

                Button positiveButton = alertDialog.getButton(alertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        inputKategori();
                        Toast.makeText(Tambah_Kategori.this, "Kategori berhasil ditambah", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                        et_kategori.setText("");
                    }
                });
            }
        });
    }

    private void getID() {
        et_kategori = findViewById(R.id.et_kategori);
        btn_submit = findViewById(R.id.btn_submit);
    }

    private void inputKategori() {
        Kategori = et_kategori.getText().toString();
        if (Kategori.isEmpty()) {
            Toast.makeText(this, "Form tidak boleh kosong", Toast.LENGTH_SHORT).show();
        } else {
            dbKategori.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    childCount = String.valueOf(snapshot.getChildrenCount() + 1);
                    HashMap hashMap = new HashMap<>();
                    hashMap.put("kategori", Kategori);
                    dbKategori.child(childCount).setValue(hashMap);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}